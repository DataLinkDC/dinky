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

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.context.RequestContext;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.dto.LoginUTO;
import com.dlink.dto.RoleDTO;
import com.dlink.dto.UserDTO;
import com.dlink.mapper.UserMapper;
import com.dlink.model.Role;
import com.dlink.model.Tenant;
import com.dlink.model.User;
import com.dlink.model.UserRole;
import com.dlink.service.RoleService;
import com.dlink.service.TenantService;
import com.dlink.service.UserRoleService;
import com.dlink.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private RoleService roleService;

    @Autowired
    private TenantService tenantService;


    @Override
    public Result registerUser(User user) {
        User userByUsername = getUserByUsername(user.getUsername());
        if (Asserts.isNotNull(userByUsername)) {
            return Result.failed("该账号已存在");
        }
        if (Asserts.isNullString(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        user.setEnabled(true);
        user.setIsDelete(false);
        if (save(user)) {
            return Result.succeed("注册成功");
        } else {
            return Result.failed("该账号已存在");
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
            return Result.failed("该账号不存在");
        }
        if (!Asserts.isEquals(SaSecureUtil.md5(password), user.getPassword())) {
            return Result.failed("原密码错误");
        }
        user.setPassword(SaSecureUtil.md5(newPassword));
        if (updateById(user)) {
            return Result.succeed("密码修改成功");
        }
        return Result.failed("密码修改失败");
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
            return Result.failed("账号或密码错误");
        }
        String userPassword = user.getPassword();
        if (Asserts.isNullString(loginUTO.getPassword())) {
            return Result.failed("密码不能为空");
        }
        if (Asserts.isEquals(SaSecureUtil.md5(loginUTO.getPassword()), userPassword)) {
            if (user.getIsDelete()) {
                return Result.failed("账号不存在");
            }
            if (!user.getEnabled()) {
                return Result.failed("账号已被禁用");
            }
            // 将前端入参 租户id 放入上下文
            RequestContext.set(loginUTO.getTenantId());

            UserDTO userDTO = new UserDTO();
            Set<RoleDTO> roleDTOList = new HashSet<>();
            List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());

            Tenant currentTenant = tenantService.getBaseMapper().selectById(loginUTO.getTenantId());

            //List<Tenant> tenantList = tenantService.list();
            //Map<Integer, List<Tenant>> listMap = tenantList.stream().filter(item -> item.getIsDelete()).collect(Collectors.groupingBy(Tenant::getId));
            //Set<Integer> tenantIds = listMap.keySet();
            for (UserRole userRole : userRoles) {
                Role role = roleService.getBaseMapper().selectById(userRole.getRoleId());
                Tenant tenant = tenantService.getBaseMapper().selectOne(new QueryWrapper<Tenant>().eq("id", role.getTenantId()));
                roleDTOList.add(new RoleDTO(role, tenant));
                /*for (Integer tenantId : tenantIds) {
                    if (Asserts.isNotNull(role)) {
                    }
                }*/
            }
            userDTO.setUser(user);
            userDTO.setRoleDTOList(roleDTOList);
            userDTO.setCurrentTenant(currentTenant);
            StpUtil.login(user.getId(), loginUTO.isAutoLogin());
            StpUtil.getSession().set("user", userDTO);
            return Result.succeed(userDTO, "登录成功");
        } else {
            return Result.failed("账号或密码错误");
        }
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

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        if (userRoles.size() == 0) {
            return Result.failed("用户未绑定角色,获取租户失败");
        }
        Set<Integer> roleIds = new HashSet<>();
        userRoles.forEach(userRole -> roleIds.add(userRole.getRoleId()));

        List<Role> roles = roleService.getRoleByIds(roleIds);
        Set<Integer> tenantIds = new HashSet<>();
        roles.forEach(role -> tenantIds.add(role.getTenantId()));
        List<Tenant> tenants = tenantService.getTenantByIds(tenantIds);
        return Result.succeed(tenants, "获取成功");
    }

}
