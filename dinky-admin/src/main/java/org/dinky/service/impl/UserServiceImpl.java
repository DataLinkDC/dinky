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
import org.dinky.data.enums.Status;
import org.dinky.data.enums.UserType;
import org.dinky.data.exception.AuthException;
import org.dinky.data.model.Role;
import org.dinky.data.model.RowPermissions;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.Tenant;
import org.dinky.data.model.User;
import org.dinky.data.model.UserRole;
import org.dinky.data.model.UserTenant;
import org.dinky.data.params.AssignRoleParams;
import org.dinky.data.params.AssignUserToTenantParams;
import org.dinky.data.result.Result;
import org.dinky.mapper.UserMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.RoleService;
import org.dinky.service.RowPermissionsService;
import org.dinky.service.TenantService;
import org.dinky.service.UserRoleService;
import org.dinky.service.UserService;
import org.dinky.service.UserTenantService;

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

    private final RowPermissionsService roleSelectPermissionsService;

    private final LdapServiceImpl ldapService;

    @Override
    public Result<Void> registerUser(User user) {
        User userByUsername = getUserByUsername(user.getUsername());
        if (Asserts.isNotNull(userByUsername)) {
            return Result.failed(Status.USER_ALREADY_EXISTS);
        }
        if (Asserts.isNullString(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        user.setEnabled(true);
        user.setIsDelete(false);
        user.setUserType(UserType.LOCAL.getCode());
        if (save(user)) {
            return Result.succeed(Status.ADDED_SUCCESS);
        } else {
            return Result.failed(Status.ADDED_FAILED);
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
        User user = getById(modifyPasswordDTO.getId());
        if (Asserts.isNull(user)) {
            return Result.failed(Status.USER_NOT_EXIST);
        }
        if (!Asserts.isEquals(
                SaSecureUtil.md5(modifyPasswordDTO.getPassword()), user.getPassword())) {
            return Result.failed(Status.USER_OLD_PASSWORD_INCORRECT);
        }
        user.setPassword(SaSecureUtil.md5(modifyPasswordDTO.getNewPassword()));
        if (updateById(user)) {
            return Result.succeed(Status.CHANGE_PASSWORD_SUCCESS);
        }
        return Result.failed(Status.CHANGE_PASSWORD_FAILED);
    }

    @Override
    public Boolean removeUser(Integer id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * The user login method is to determine whether to use ldap authentication or local
     * authentication according to the field isLdapLogin. After the ldap authentication is
     * successful, it will automatically find the local mapped data to return
     *
     * @param loginDTO a user based on the provided login credentials.
     * @return a Result object containing the user information if the login is successful, or an
     *     appropriate error status if the login fails.
     */
    @Override
    public Result<UserDTO> loginUser(LoginDTO loginDTO) {
        User user = null;
        try {
            // Determine the login method (LDAP or local) based on the flag in loginDTO
            user = loginDTO.isLdapLogin() ? ldapLogin(loginDTO) : localLogin(loginDTO);
        } catch (AuthException e) {
            // Handle authentication exceptions and return the corresponding error status
            return Result.failed(e.getStatus());
        }

        // Check if the user is enabled
        if (!user.getEnabled()) {
            return Result.failed(Status.USER_DISABLED_BY_ADMIN);
        }

        UserDTO userInfo = refreshUserInfo(user);
        if (Asserts.isNullCollection(userInfo.getTenantList())) {
            return Result.failed(Status.USER_NOT_BINDING_TENANT);
        }

        // Perform login using StpUtil (Assuming it handles the session management)
        StpUtil.login(user.getId(), loginDTO.isAutoLogin());

        // Return the user information along with a success status
        return Result.succeed(userInfo, Status.LOGIN_SUCCESS);
    }

    private User localLogin(LoginDTO loginDTO) throws AuthException {
        // Get user from local database by username
        User user = getUserByUsername(loginDTO.getUsername());
        if (Asserts.isNull(user)) {
            // User doesn't exist
            throw new AuthException(Status.USER_NOT_EXIST);
        }

        String userPassword = user.getPassword();
        // Check if the provided password is null
        if (Asserts.isNullString(loginDTO.getPassword())) {
            throw new AuthException(Status.LOGIN_PASSWORD_NOT_NULL);
        }

        // Compare the hashed form of the provided password with the stored password
        if (Asserts.isEquals(SaSecureUtil.md5(loginDTO.getPassword()), userPassword)) {
            return user;
        } else {
            throw new AuthException(Status.USER_NAME_PASSWD_ERROR);
        }
    }

    private User ldapLogin(LoginDTO loginDTO) throws AuthException {
        // Authenticate user against LDAP
        User userFromLdap = ldapService.authenticate(loginDTO);
        // Get user from local database
        User userFromLocal = getUserByUsername(loginDTO.getUsername());

        if (Asserts.isNull(userFromLocal)) {
            // User doesn't exist locally
            // Check if LDAP user autoload is enabled
            if (!SystemConfiguration.getInstances().getLdapAutoload().getValue()) {
                throw new AuthException(Status.LDAP_USER_AUTOLOAD_FORBAID);
            }

            // Get default tenant from system configuration
            String defaultTeantCode =
                    SystemConfiguration.getInstances().getLdapDefaultTeant().getValue();
            Tenant tenant = tenantService.getTenantByTenantCode(defaultTeantCode);
            if (Asserts.isNull(tenant)) {
                throw new AuthException(Status.LDAP_DEFAULT_TENANT_NOFOUND);
            }

            // Update LDAP user properties and save
            userFromLdap.setUserType(UserType.LDAP.getCode());
            userFromLdap.setEnabled(true);
            userFromLdap.setIsAdmin(false);
            userFromLdap.setIsDelete(false);
            save(userFromLdap);

            // Assign the user to the default tenant
            List<Integer> userIds = getUserIdsByTeantId(tenant.getId());
            User user = getUserByUsername(loginDTO.getUsername());
            userIds.add(user.getId());
            tenantService.assignUserToTenant(new AssignUserToTenantParams(tenant.getId(), userIds));
            return user;
        } else if (userFromLocal.getUserType() != UserType.LDAP.getCode()) {
            throw new AuthException(Status.LDAP_LOGIN_FORBID);
        }

        // If local database have the user and ldap login is pass,
        // Return user from local database
        return userFromLocal;
    }

    private UserDTO refreshUserInfo(User user) {
        UserDTO reBuildUserInfo = buildUserInfo(user.getId());
        UserInfoContextHolder.set(user.getId(), reBuildUserInfo);
        return reBuildUserInfo;
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
            return Result.succeed(Status.USER_ASSIGN_ROLE_SUCCESS);
        } else {
            if (userRoleList.size() == 0) {
                return Result.succeed(Status.USER_BINDING_ROLE_DELETE_ALL);
            }
            return Result.failed(Status.USER_ASSIGN_ROLE_FAILED);
        }
    }

    @Override
    public Result<Tenant> chooseTenant(Integer tenantId) {
        Tenant currentTenant = tenantService.getById(tenantId);
        if (Asserts.isNull(currentTenant)) {
            return Result.failed(Status.GET_TENANT_FAILED);
        } else {
            UserDTO userInfo = UserInfoContextHolder.get(StpUtil.getLoginIdAsInt());
            userInfo.setCurrentTenant(currentTenant);
            UserInfoContextHolder.refresh(StpUtil.getLoginIdAsInt(), userInfo);
            TenantContextHolder.set(currentTenant.getId());

            return Result.succeed(currentTenant, Status.SWITCHING_TENANT_SUCCESS);
        }
    }

    @Override
    public Result<UserDTO> queryCurrentUserInfo() {
        UserDTO userInfo = UserInfoContextHolder.get(StpUtil.getLoginIdAsInt());

        if (Asserts.isNotNull(userInfo)) {
            UserDTO userInfoDto = buildUserInfo(userInfo.getUser().getId());
            userInfoDto.setCurrentTenant(userInfo.getCurrentTenant());
            UserInfoContextHolder.refresh(StpUtil.getLoginIdAsInt(), userInfoDto);
            return Result.succeed(userInfoDto);
        } else {
            return Result.failed();
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
    public List<RowPermissions> getCurrentRoleSelectPermissions() {
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

    @Override
    public List<Integer> getUserIdsByTeantId(int id) {
        List<UserTenant> userTenants =
                userTenantService
                        .getBaseMapper()
                        .selectList(
                                new LambdaQueryWrapper<UserTenant>()
                                        .eq(UserTenant::getTenantId, id));
        List<Integer> userIds = new ArrayList<>();
        for (UserTenant userTenant : userTenants) {
            userIds.add(userTenant.getUserId());
        }
        return userIds;
    }

    /**
     * build user info
     *
     * @param userId
     * @return
     */
    private UserDTO buildUserInfo(Integer userId) {

        User user = getById(userId);
        if (Asserts.isNull(user)) {
            return null;
        }

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
        return userInfo;
    }
}
