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

package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.context.TenantContextHolder;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.dto.LoginUTO;
import com.dlink.dto.UserDTO;
import com.dlink.mapper.UserMapper;
import com.dlink.model.Role;
import com.dlink.model.Tenant;
import com.dlink.model.User;
import com.dlink.model.UserRole;
import com.dlink.model.UserTenant;
import com.dlink.service.RoleService;
import com.dlink.service.TenantService;
import com.dlink.service.UserRoleService;
import com.dlink.service.UserService;
import com.dlink.service.UserTenantService;
import com.dlink.utils.MessageResolverUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;

/**
 * UserServiceImpl
 *
 * @author wenmo
 * @since 2021/11/28 13:39
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserTenantService userTenantService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TenantService tenantService;

    @Override
    public Result registerUser(User user) {
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
    public Result modifyPassword(String username, String password, String newPassword) {
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
    public Result loginUser(LoginUTO loginUTO) {
        User user = getUserByUsername(loginUTO.getUsername());
        if (Asserts.isNull(user)) {
            return Result.failed(MessageResolverUtils.getMessage("login.fail"));
        }
        String userPassword = user.getPassword();
        if (Asserts.isNullString(loginUTO.getPassword())) {
            return Result.failed(MessageResolverUtils.getMessage("login.password.notnull"));
        }
        if (Asserts.isEquals(SaSecureUtil.md5(loginUTO.getPassword()), userPassword)) {
            if (user.getIsDelete()) {
                return Result.failed(MessageResolverUtils.getMessage("login.user.not.exists"));
            }
            if (!user.getEnabled()) {
                return Result.failed(MessageResolverUtils.getMessage("login.user.disabled"));
            }

            // 将前端入参 租户id 放入上下文
            TenantContextHolder.set(loginUTO.getTenantId());

            // get user tenants and roles
            UserDTO userDTO = getUserALLBaseInfo(loginUTO, user);

            StpUtil.login(user.getId(), loginUTO.isAutoLogin());
            StpUtil.getSession().set("user", userDTO);
            return Result.succeed(userDTO, MessageResolverUtils.getMessage("login.success"));
        } else {
            return Result.failed(MessageResolverUtils.getMessage("login.fail"));
        }
    }

    private UserDTO getUserALLBaseInfo(LoginUTO loginUTO, User user) {
        UserDTO userDTO = new UserDTO();
        List<Role> roleList = new LinkedList<>();
        List<Tenant> tenantList = new LinkedList<>();

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        List<UserTenant> userTenants = userTenantService.getUserTenantByUserId(user.getId());

        Tenant currentTenant = tenantService.getBaseMapper().selectById(loginUTO.getTenantId());

        userRoles.forEach(userRole -> {
            Role role = roleService.getBaseMapper().selectById(userRole.getRoleId());
            if (Asserts.isNotNull(role)) {
                roleList.add(role);
            }
        });

        userTenants.forEach(userTenant -> {
            Tenant tenant = tenantService.getBaseMapper()
                .selectOne(new QueryWrapper<Tenant>().eq("id", userTenant.getTenantId()));
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
    public Result grantRole(JsonNode para) {
        if (para.size() > 0) {
            List<UserRole> userRoleList = new ArrayList<>();
            Integer userId = para.get("userId").asInt();
            userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));
            JsonNode userRoleJsonNode = para.get("roles");
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
    public Result getTenants(String username) {
        User user = getUserByUsername(username);
        if (Asserts.isNull(user)) {
            return Result.failed("该账号不存在,获取租户失败");
        }

        List<UserTenant> userTenants = userTenantService.getUserTenantByUserId(user.getId());
        if (userTenants.size() == 0) {
            return Result.failed("用户未绑定租户,获取租户失败");
        }

        Set<Integer> tenantIds = new HashSet<>();
        userTenants.forEach(userTenant -> tenantIds.add(userTenant.getTenantId()));
        List<Tenant> tenants = tenantService.getTenantByIds(tenantIds);
        return Result.succeed(tenants, MessageResolverUtils.getMessage("response.get.success"));
    }

}
