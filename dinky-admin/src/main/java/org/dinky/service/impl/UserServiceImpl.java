/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.service.impl;

import org.dinky.assertion.Asserts;
import org.dinky.context.TenantContextHolder;
import org.dinky.context.UserInfoContextHolder;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.model.Role;
import org.dinky.data.model.RoleSelectPermissions;
import org.dinky.data.model.Tenant;
import org.dinky.data.model.User;
import org.dinky.data.model.UserRole;
import org.dinky.data.model.UserTenant;
import org.dinky.data.params.AssignRoleParams;
import org.dinky.data.result.Result;
import org.dinky.mapper.UserMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.RoleSelectPermissionsService;
import org.dinky.service.RoleService;
import org.dinky.service.TenantService;
import org.dinky.service.UserRoleService;
import org.dinky.service.UserService;
import org.dinky.service.UserTenantService;
import org.dinky.utils.I18nMsgUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;

/**
 * UserServiceImpl
 *
 * @since 2021/11/28 13:39
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final UserRoleService userRoleService;

    private final UserTenantService userTenantService;

    private final RoleService roleService;

    private final TenantService tenantService;

    private final RoleSelectPermissionsService roleSelectPermissionsService;

    @Override
    public Result<Void> registerUser(User user) {
        User userByUsername = getUserByUsername(user.getUsername());
        if (Asserts.isNotNull(userByUsername)) {
            return Result.failed(I18nMsgUtils.getMsg("user.register.account.exists"));
        }
        if (Asserts.isNullString(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        user.setEnabled(true);
        user.setIsDelete(false);
        if (save(user)) {
            return Result.succeed(I18nMsgUtils.getMsg("create.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("user.register.account.exists"));
        }
    }

    @Override
    public Boolean modifyUser(User user) {
        if (Asserts.isNull(user.getId())) {
            return false;
        }
        return updateById(user);
    }

    @Override
    public Result<Void> modifyPassword(ModifyPasswordDTO modifyPasswordDTO) {
        User user = getUserByUsername(modifyPasswordDTO.getUsername());
        if (Asserts.isNull(user)) {
            return Result.failed(I18nMsgUtils.getMsg("login.user.not.exists"));
        }
        if (!Asserts.isEquals(
                SaSecureUtil.md5(modifyPasswordDTO.getPassword()), user.getPassword())) {
            return Result.failed(I18nMsgUtils.getMsg("user.oldpassword.incorrect"));
        }
        user.setPassword(SaSecureUtil.md5(modifyPasswordDTO.getNewPassword()));
        if (updateById(user)) {
            return Result.succeed(I18nMsgUtils.getMsg("user.change.password.success"));
        }
        return Result.failed(I18nMsgUtils.getMsg("user.change.password.failed"));
    }

    @Override
    public Boolean removeUser(Integer id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public Result<UserDTO> loginUser(LoginDTO loginDTO) {
        User user = getUserByUsername(loginDTO.getUsername());
        if (Asserts.isNull(user)) {
            return Result.failed(I18nMsgUtils.getMsg("login.user.not.exists"));
        }
        String userPassword = user.getPassword();
        if (Asserts.isNullString(loginDTO.getPassword())) {
            return Result.failed(I18nMsgUtils.getMsg("login.password.notnull"));
        }
        if (Asserts.isEquals(SaSecureUtil.md5(loginDTO.getPassword()), userPassword)) {
            if (!user.getEnabled()) {
                return Result.failed(I18nMsgUtils.getMsg("login.user.disabled"));
            }
            // get user tenants and roles
            UserDTO userInfo = refreshUserInfo(user);
            if (Asserts.isNullCollection(userInfo.getTenantList())) {
                return Result.failed(I18nMsgUtils.getMsg("login.user.not.binding"));
            }
            StpUtil.login(user.getId(), loginDTO.isAutoLogin());
            return Result.succeed(userInfo, I18nMsgUtils.getMsg("login.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("login.fail"));
        }
    }

    private UserDTO refreshUserInfo(User user) {
        List<Role> roleList = new LinkedList<>();
        List<Tenant> tenantList = new LinkedList<>();

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        List<UserTenant> userTenants = userTenantService.getUserTenantByUserId(user.getId());

        userRoles.stream()
                .forEach(
                        userRole -> {
                            Role role =
                                    roleService.getBaseMapper().selectById(userRole.getRoleId());
                            if (Asserts.isNotNull(role)) {
                                roleList.add(role);
                            }
                        });

        userTenants.stream()
                .forEach(
                        userTenant -> {
                            Tenant tenant = tenantService.getById(userTenant.getTenantId());
                            if (Asserts.isNotNull(tenant)) {
                                tenantList.add(tenant);
                            }
                        });

        UserDTO userInfo = new UserDTO();
        userInfo.setUser(user);
        userInfo.setRoleList(roleList);
        userInfo.setTenantList(tenantList);
        UserInfoContextHolder.set(user.getId(), userInfo);
        return userInfo;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (Asserts.isNotNull(user)) {
            user.setIsAdmin(Asserts.isEqualsIgnoreCase(username, "admin"));
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> grantRole(JsonNode param) {
        if (param.size() > 0) {
            List<UserRole> userRoleList = new ArrayList<>();
            Integer userId = param.get("userId").asInt();
            userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));
            JsonNode userRoleJsonNode = param.get("roles");
            for (JsonNode ids : userRoleJsonNode) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(ids.asInt());
                userRoleList.add(userRole);
            }
            // save or update user role
            boolean result = userRoleService.saveOrUpdateBatch(userRoleList, 1000);
            if (result) {
                return Result.succeed("用户授权角色成功");
            } else {
                if (userRoleList.size() == 0) {
                    return Result.succeed("该用户绑定的角色已被全部删除");
                }
                return Result.failed("用户授权角色失败");
            }
        } else {
            return Result.failed("请选择要授权的角色");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignRole(AssignRoleParams assignRoleParams) {
        List<UserRole> userRoleList = new ArrayList<>();
        userRoleService.remove(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, assignRoleParams.getUserId()));
        for (Integer roleId : assignRoleParams.getRoleIds()) {
            UserRole userRole = new UserRole();
            userRole.setUserId(assignRoleParams.getUserId());
            userRole.setRoleId(roleId);
            userRoleList.add(userRole);
        }
        // save or update user role
        boolean result = userRoleService.saveOrUpdateBatch(userRoleList, 1000);
        if (result) {
            return Result.succeed(I18nMsgUtils.getMsg("user.assign.role.success"));
        } else {
            if (userRoleList.size() == 0) {
                return Result.succeed(I18nMsgUtils.getMsg("user.binding.role.deleteAll"));
            }
            return Result.failed(I18nMsgUtils.getMsg("user.assign.role.failed"));
        }
    }

    @Override
    public Result<Tenant> chooseTenant(Integer tenantId) {
        Tenant currentTenant = tenantService.getById(tenantId);
        if (Asserts.isNull(currentTenant)) {
            return Result.failed(I18nMsgUtils.getMsg("user.get.tenant.failed"));
        } else {
            UserDTO userInfo = UserInfoContextHolder.get(StpUtil.getLoginIdAsInt());
            userInfo.setCurrentTenant(currentTenant);
            UserInfoContextHolder.refresh(StpUtil.getLoginIdAsInt(), userInfo);
            TenantContextHolder.set(currentTenant.getId());

            return Result.succeed(currentTenant, I18nMsgUtils.getMsg("user.select.tenant.success"));
        }
    }

    @Override
    public Result<UserDTO> queryCurrentUserInfo() {
        UserDTO userInfo = UserInfoContextHolder.get(StpUtil.getLoginIdAsInt());
        if (Asserts.isNotNull(userInfo)
                && Asserts.isNotNull(userInfo.getUser())
                && Asserts.isNotNull(userInfo.getRoleList())
                && Asserts.isNotNull(userInfo.getTenantList())
                && Asserts.isNotNull(userInfo.getCurrentTenant())) {
            StpUtil.getSession().set("user", userInfo);
            return Result.succeed(userInfo, I18nMsgUtils.getMsg("response.get.success"));
        } else {
            return Result.failed(userInfo, I18nMsgUtils.getMsg("response.get.failed"));
        }
    }

    @Override
    public Boolean enable(Integer id) {
        User user = getById(id);
        user.setEnabled(!user.getEnabled());
        return updateById(user);
    }

    @Override
    public Boolean checkAdmin(Integer id) {

        User user = getById(id);
        return "admin".equals(user.getUsername());
    }

    @Override
    public List<Role> getCurrentRole() {
        return roleService.getRoleByUserId(StpUtil.getLoginIdAsInt());
    }

    @Override
    public List<RoleSelectPermissions> getCurrentRoleSelectPermissions() {
        List<Role> currentRole = getCurrentRole();
        if (Asserts.isNullCollection(currentRole)) {
            return new ArrayList<>();
        }
        List<Integer> roleIds = currentRole.stream().map(Role::getId).collect(Collectors.toList());
        return roleSelectPermissionsService.listRoleSelectPermissionsByRoleIds(roleIds);
    }

    @Override
    public void outLogin() {
        StpUtil.logout();
    }
}
