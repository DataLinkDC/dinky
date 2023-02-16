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
import org.dinky.common.result.Result;
import org.dinky.context.TenantContextHolder;
import org.dinky.db.service.impl.SuperServiceImpl;
import org.dinky.dto.LoginDTO;
import org.dinky.dto.UserDTO;
import org.dinky.mapper.UserMapper;
import org.dinky.model.Role;
import org.dinky.model.Tenant;
import org.dinky.model.User;
import org.dinky.model.UserRole;
import org.dinky.model.UserTenant;
import org.dinky.service.RoleService;
import org.dinky.service.TenantService;
import org.dinky.service.UserRoleService;
import org.dinky.service.UserService;
import org.dinky.service.UserTenantService;
import org.dinky.utils.MessageResolverUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;

/**
 * UserServiceImpl
 *
 * @author wenmo
 * @since 2021/11/28 13:39
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    private static UserDTO currentUserDTO = new UserDTO();

    private static Tenant currentTenant = new Tenant();

    private final UserRoleService userRoleService;

    private final UserTenantService userTenantService;

    private final RoleService roleService;

    private final TenantService tenantService;

    @Override
    public Result<Void> registerUser(User user) {
        User userByUsername = getUserByUsername(user.getUsername());
        if (Asserts.isNotNull(userByUsername)) {
            return Result.failed(MessageResolverUtils.getMessage("user.register.account.exists"));
        }
        if (Asserts.isNullString(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        user.setEnabled(true);
        user.setIsDelete(false);
        if (save(user)) {
            return Result.succeed(MessageResolverUtils.getMessage("user.register.success"));
        } else {
            return Result.failed(MessageResolverUtils.getMessage("user.register.account.exists"));
        }
    }

    @Override
    public boolean modifyUser(User user) {
        if (Asserts.isNull(user.getId())) {
            return false;
        }
        return updateById(user);
    }

    @Override
    public Result<Void> modifyPassword(String username, String password, String newPassword) {
        User user = getUserByUsername(username);
        if (Asserts.isNull(user)) {
            return Result.failed(MessageResolverUtils.getMessage("login.user.not.exists"));
        }
        if (!Asserts.isEquals(SaSecureUtil.md5(password), user.getPassword())) {
            return Result.failed(MessageResolverUtils.getMessage("user.oldpassword.incorrect"));
        }
        user.setPassword(SaSecureUtil.md5(newPassword));
        if (updateById(user)) {
            return Result.succeed(MessageResolverUtils.getMessage("user.change.password.success"));
        }
        return Result.failed(MessageResolverUtils.getMessage("user.change.password.failed"));
    }

    @Override
    public boolean removeUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setIsDelete(true);
        return updateById(user);
    }

    @Override
    public Result<UserDTO> loginUser(LoginDTO loginDTO) {
        User user = getUserByUsername(loginDTO.getUsername());
        if (Asserts.isNull(user)) {
            return Result.failed(MessageResolverUtils.getMessage("login.fail"));
        }
        String userPassword = user.getPassword();
        if (Asserts.isNullString(loginDTO.getPassword())) {
            return Result.failed(MessageResolverUtils.getMessage("login.password.notnull"));
        }
        if (Asserts.isEquals(SaSecureUtil.md5(loginDTO.getPassword()), userPassword)) {
            if (user.getIsDelete()) {
                return Result.failed(MessageResolverUtils.getMessage("login.user.not.exists"));
            }
            if (!user.getEnabled()) {
                return Result.failed(MessageResolverUtils.getMessage("login.user.disabled"));
            }
            // get user tenants and roles
            currentUserDTO = refreshUserInfo(user);

            StpUtil.login(user.getId(), loginDTO.isAutoLogin());

            return Result.succeed(currentUserDTO, MessageResolverUtils.getMessage("login.success"));
        } else {
            return Result.failed(MessageResolverUtils.getMessage("login.fail"));
        }
    }

    @Deprecated
    private UserDTO getUserAllBaseInfo(LoginDTO loginDTO, User user) {
        UserDTO userDTO = new UserDTO();
        List<Role> roleList = new LinkedList<>();
        List<Tenant> tenantList = new LinkedList<>();

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        List<UserTenant> userTenants = userTenantService.getUserTenantByUserId(user.getId());

        Tenant currentTenant = tenantService.getBaseMapper().selectById(loginDTO.getTenantId());

        userRoles.forEach(
                userRole -> {
                    Role role = roleService.getBaseMapper().selectById(userRole.getRoleId());
                    if (Asserts.isNotNull(role)) {
                        roleList.add(role);
                    }
                });

        userTenants.forEach(
                userTenant -> {
                    Tenant tenant =
                            tenantService
                                    .getBaseMapper()
                                    .selectOne(
                                            new QueryWrapper<Tenant>()
                                                    .eq("id", userTenant.getTenantId()));
                    if (Asserts.isNotNull(tenant)) {
                        tenantList.add(tenant);
                    }
                });

        userDTO.setUser(user);
        userDTO.setRoleList(roleList);
        userDTO.setTenantList(tenantList);
        userDTO.setCurrentTenant(currentTenant);
        return userDTO;
    }

    private UserDTO refreshUserInfo(User user) {
        List<Role> roleList = new LinkedList<>();
        List<Tenant> tenantList = new LinkedList<>();

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        List<UserTenant> userTenants = userTenantService.getUserTenantByUserId(user.getId());

        userRoles.forEach(
                userRole -> {
                    Role role = roleService.getBaseMapper().selectById(userRole.getRoleId());
                    if (Asserts.isNotNull(role)) {
                        roleList.add(role);
                    }
                });

        userTenants.forEach(
                userTenant -> {
                    Tenant tenant =
                            tenantService
                                    .getBaseMapper()
                                    .selectOne(
                                            new QueryWrapper<Tenant>()
                                                    .eq("id", userTenant.getTenantId()));
                    if (Asserts.isNotNull(tenant)) {
                        tenantList.add(tenant);
                    }
                });

        currentUserDTO.setUser(user);
        currentUserDTO.setRoleList(roleList);
        currentUserDTO.setTenantList(tenantList);
        return currentUserDTO;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = getOne(new QueryWrapper<User>().eq("username", username));
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
    public Result<Tenant> chooseTenant(Integer tenantId) {
        currentTenant = tenantService.getById(tenantId);
        if (Asserts.isNull(currentTenant)) {
            return Result.failed("Failed to obtain tenant information");
        } else {
            currentUserDTO.setCurrentTenant(currentTenant);
            TenantContextHolder.set(currentTenant.getId());
            return Result.succeed(currentTenant, "Tenant selected successfully");
        }
    }

    @Override
    public Result<UserDTO> queryCurrentUserInfo() {
        if (Asserts.isNotNull(currentUserDTO.getUser())
                && Asserts.isNotNull(currentUserDTO.getRoleList())
                && Asserts.isNotNull(currentUserDTO.getTenantList())
                && Asserts.isNotNull(currentUserDTO.getCurrentTenant())) {
            StpUtil.getSession().set("user", currentUserDTO);
            return Result.succeed(
                    currentUserDTO, MessageResolverUtils.getMessage("response.get.success"));
        } else {
            return Result.failed(
                    currentUserDTO, MessageResolverUtils.getMessage("response.get.failed"));
        }
    }
}
