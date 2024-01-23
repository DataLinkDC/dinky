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
import org.dinky.context.RowLevelPermissionsContext;
import org.dinky.context.TenantContextHolder;
import org.dinky.context.UserInfoContextHolder;
import org.dinky.data.dto.AssignRoleDTO;
import org.dinky.data.dto.AssignUserToTenantDTO;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.UserType;
import org.dinky.data.exception.AuthException;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.SysToken;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.rbac.Menu;
import org.dinky.data.model.rbac.Role;
import org.dinky.data.model.rbac.RoleMenu;
import org.dinky.data.model.rbac.RowPermissions;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.model.rbac.User;
import org.dinky.data.model.rbac.UserRole;
import org.dinky.data.model.rbac.UserTenant;
import org.dinky.data.result.Result;
import org.dinky.data.vo.UserVo;
import org.dinky.mapper.TokenMapper;
import org.dinky.mapper.UserMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.MenuService;
import org.dinky.service.RoleMenuService;
import org.dinky.service.RoleService;
import org.dinky.service.RowPermissionsService;
import org.dinky.service.TenantService;
import org.dinky.service.UserRoleService;
import org.dinky.service.UserService;
import org.dinky.service.UserTenantService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
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

    private final LoginLogServiceImpl loginLogService;

    private final RoleMenuService roleMenuService;

    private final MenuService menuService;
    private final TokenService tokenService;
    private final TokenMapper tokenMapper;

    private final ReentrantLock lock = new ReentrantLock();

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
        if (!Asserts.isEquals(SaSecureUtil.md5(modifyPasswordDTO.getPassword()), user.getPassword())) {
            return Result.failed(Status.USER_OLD_PASSWORD_INCORRECT);
        }
        user.setPassword(SaSecureUtil.md5(modifyPasswordDTO.getNewPassword()));
        if (updateById(user)) {
            StpUtil.logout(user.getId());
            return Result.succeed(Status.CHANGE_PASSWORD_SUCCESS);
        }
        return Result.failed(Status.CHANGE_PASSWORD_FAILED);
    }

    @Override
    public Boolean removeUser(Integer id) {
        User user = getById(id);
        if (user.getSuperAdminFlag()) {
            throw new BusException(Status.USER_SUPERADMIN_CANNOT_DELETE);
        }
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * The user login method is to determine whether to use ldap authentication or local
     * authentication according to the field isLdapLogin. After the ldap authentication is
     * successful, it will automatically find the local mapped data to return
     *
     * @param loginDTO a user based on the provided login credentials.
     * @return a Result object containing the user information if the login is successful, or an
     * appropriate error status if the login fails.
     */
    @Override
    public Result<UserDTO> loginUser(LoginDTO loginDTO) {
        User user = null;
        try {
            // Determine the login method (LDAP or local) based on the flag in loginDTO
            user = loginDTO.isLdapLogin() ? ldapLogin(loginDTO) : localLogin(loginDTO);
        } catch (AuthException e) {
            // Handle authentication exceptions and return the corresponding error status
            return Result.authorizeFailed(e.getStatus() + e.getMessage());
        }

        // Check if the user is enabled
        if (!user.getEnabled()) {
            loginLogService.saveLoginLog(user, Status.USER_DISABLED_BY_ADMIN);
            return Result.authorizeFailed(Status.USER_DISABLED_BY_ADMIN);
        }

        UserDTO userInfo = refreshUserInfo(user);
        if (Asserts.isNullCollection(userInfo.getTenantList())) {
            loginLogService.saveLoginLog(user, Status.USER_NOT_BINDING_TENANT);
            return Result.authorizeFailed(Status.USER_NOT_BINDING_TENANT);
        }

        // Perform login using StpUtil (Assuming it handles the session management)
        Integer userId = user.getId();
        StpUtil.login(userId, loginDTO.isAutoLogin());

        // save login log record
        loginLogService.saveLoginLog(user, Status.LOGIN_SUCCESS);

        upsertToken(userInfo);

        // Return the user information along with a success status
        return Result.succeed(userInfo, Status.LOGIN_SUCCESS);
    }

    private void upsertToken(UserDTO userInfo) {
        Integer userId = userInfo.getUser().getId();
        SysToken sysToken = new SysToken();
        String tokenValue = StpUtil.getTokenValueByLoginId(userId);
        sysToken.setTokenValue(tokenValue);
        sysToken.setUserId(userId);
        // todo 权限和租户暂未接入
        sysToken.setRoleId(1);
        sysToken.setTenantId(1);
        sysToken.setExpireType(3);
        DateTime date = DateUtil.date();
        sysToken.setExpireStartTime(date);
        sysToken.setExpireEndTime(DateUtil.offsetDay(date, 1));
        sysToken.setCreator(userId);
        sysToken.setUpdater(userId);
        sysToken.setSource(SysToken.Source.LOGIN);

        try {
            lock.lock();
            SysToken lastSysToken =
                    tokenMapper.selectOne(new LambdaQueryWrapper<SysToken>().eq(SysToken::getTokenValue, tokenValue));
            if (Asserts.isNull(lastSysToken)) {
                tokenMapper.insert(sysToken);
            } else {
                sysToken.setId(lastSysToken.getId());
                tokenMapper.updateById(sysToken);
            }
        } catch (Exception e) {
            log.error("update token info failed", e);
        } finally {
            lock.unlock();
        }
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
            loginLogService.saveLoginLog(user, Status.LOGIN_PASSWORD_NOT_NULL);
            throw new AuthException(Status.LOGIN_PASSWORD_NOT_NULL);
        }

        // Compare the hashed form of the provided password with the stored password
        if (Asserts.isEquals(SaSecureUtil.md5(loginDTO.getPassword()), userPassword)) {
            return user;
        } else {
            loginLogService.saveLoginLog(user, Status.USER_NAME_PASSWD_ERROR);
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
            userFromLdap.setSuperAdminFlag(false);
            //            userFromLdap.setTenantAdminFlag(false);
            userFromLdap.setIsDelete(false);
            save(userFromLdap);

            // Assign the user to the default tenant
            List<Integer> userIds = getUserIdsByTenantId(tenant.getId());
            User user = getUserByUsername(loginDTO.getUsername());
            userIds.add(user.getId());
            tenantService.assignUserToTenant(new AssignUserToTenantDTO(tenant.getId(), userIds));
            return user;
        } else if (userFromLocal.getUserType() != UserType.LDAP.getCode()) {
            loginLogService.saveLoginLog(userFromLocal, Status.LDAP_LOGIN_FORBID);
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
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignRole(AssignRoleDTO assignRoleDTO) {
        List<UserRole> userRoleList = new ArrayList<>();
        userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, assignRoleDTO.getUserId()));
        for (Integer roleId : assignRoleDTO.getRoleIds()) {
            UserRole userRole = new UserRole();
            userRole.setUserId(assignRoleDTO.getUserId());
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
            if (userInfoDto != null) {
                userInfoDto.setCurrentTenant(userInfo.getCurrentTenant());
            }
            UserInfoContextHolder.refresh(StpUtil.getLoginIdAsInt(), userInfoDto);
            return Result.succeed(userInfoDto);
        } else {
            return Result.failed();
        }
    }

    @Override
    public Boolean modifyUserStatus(Integer id) {
        User user = getById(id);
        user.setEnabled(!user.getEnabled());
        return updateById(user);
    }

    @Override
    public Boolean checkSuperAdmin(Integer id) {
        User user = getById(id);
        return user.getSuperAdminFlag();
    }

    /**
     * check user is tenant admin
     *
     * @param id
     * @return {@link Boolean}
     */
    @Override
    public Boolean checkTenantAdmin(Integer id) {
        User user = getById(id);
        return user.getTenantAdminFlag();
    }

    @Override
    public List<Role> getCurrentRole() {
        if (StpUtil.isLogin()) {
            return roleService.getRoleByUserId(StpUtil.getLoginIdAsInt());
        }
        return new ArrayList<>();
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
    public void buildRowPermission() {
        List<RowPermissions> currentRoleSelectPermissions = getCurrentRoleSelectPermissions();
        if (Asserts.isNotNullCollection(currentRoleSelectPermissions)) {
            ConcurrentHashMap<String, String> permission = new ConcurrentHashMap<>();
            for (RowPermissions roleSelectPermissions : currentRoleSelectPermissions) {
                if (Asserts.isAllNotNullString(
                        roleSelectPermissions.getTableName(), roleSelectPermissions.getExpression())) {
                    permission.put(roleSelectPermissions.getTableName(), roleSelectPermissions.getExpression());
                }
            }
            RowLevelPermissionsContext.set(permission);
        }
    }

    @Override
    public void outLogin() {
        StpUtil.logout(StpUtil.getLoginIdAsInt());
    }

    @Override
    public List<Integer> getUserIdsByTenantId(int id) {
        List<UserTenant> userTenants = userTenantService
                .getBaseMapper()
                .selectList(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getTenantId, id));
        List<Integer> userIds = new ArrayList<>();
        for (UserTenant userTenant : userTenants) {
            userIds.add(userTenant.getUserId());
        }
        return userIds;
    }

    /**
     * get user list by tenant id
     *
     * @param id
     * @return role select permissions list
     */
    @Override
    public List<User> getUserListByTenantId(int id) {
        List<User> userList = new ArrayList<>();
        List<UserTenant> userTenants =
                userTenantService.list(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getTenantId, id));
        userTenants.forEach(userTenant -> {
            User user = getById(userTenant.getUserId());
            user.setTenantAdminFlag(userTenant.getTenantAdminFlag());
            userList.add(user);
        });
        return userList;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Result<Void> modifyUserToTenantAdmin(Integer userId, Integer tenantId, Boolean tenantAdminFlag) {
        // query tenant admin user count
        long queryAdminUserByTenantCount = userTenantService.count(new LambdaQueryWrapper<UserTenant>()
                .eq(UserTenant::getTenantId, tenantId)
                .eq(UserTenant::getTenantAdminFlag, 1));
        if (queryAdminUserByTenantCount >= 1 && !tenantAdminFlag) {
            return Result.failed(Status.TENANT_ADMIN_ALREADY_EXISTS);
        }
        UserTenant userTenant = userTenantService.getOne(new LambdaQueryWrapper<UserTenant>()
                .eq(UserTenant::getTenantId, tenantId)
                .eq(UserTenant::getUserId, userId));
        userTenant.setTenantAdminFlag(!userTenant.getTenantAdminFlag());
        if (userTenantService.updateById(userTenant)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        }
        return Result.failed(Status.MODIFY_FAILED);
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Result<Void> recoveryUser(Integer userId) {

        Integer recoveryUser = baseMapper.recoveryUser(userId);
        return recoveryUser > 0 ? Result.succeed(Status.MODIFY_SUCCESS) : Result.failed(Status.MODIFY_FAILED);
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Result<UserVo> resetPassword(Integer userId) {
        String randomPassword = RandomUtil.randomStringUpper(6) + RandomUtil.randomNumber();
        String resetPassword = SaSecureUtil.md5(randomPassword);
        User user = getById(userId);
        user.setPassword(resetPassword);
        UserVo userVo = new UserVo(user, randomPassword);
        return updateById(user) ? Result.succeed(userVo) : Result.failed(Status.MODIFY_FAILED);
    }

    /**
     * build user info
     *
     * @param userId
     * @return
     */
    public UserDTO buildUserInfo(Integer userId) {

        User user = getById(userId);
        if (Asserts.isNull(user)) {
            return null;
        }

        List<Role> roleList = new LinkedList<>();
        List<Tenant> tenantList = new LinkedList<>();
        List<Menu> menuList = new LinkedList<>();

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        List<UserTenant> userTenants = userTenantService.getUserTenantByUserId(user.getId());

        userRoles.forEach(userRole -> {
            Role role = roleService.getBaseMapper().selectById(userRole.getRoleId());
            if (Asserts.isNotNull(role)) {
                roleList.add(role);
                // query role menu
                List<RoleMenu> roleMenus =
                        roleMenuService.list(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, role.getId()));
                roleMenus.forEach(roleMenu -> {
                    Menu menu = menuService.getById(roleMenu.getMenuId());
                    if (Asserts.isNotNull(menu) && !StrUtil.equals("M", menu.getType())) {
                        menuList.add(menu);
                    }
                });
            }
        });

        userTenants.forEach(userTenant -> {
            Tenant tenant = tenantService.getById(userTenant.getTenantId());
            if (Asserts.isNotNull(tenant)) {
                tenantList.add(tenant);
            }
        });

        UserDTO userInfo = new UserDTO();
        userInfo.setUser(user);
        userInfo.setRoleList(roleList);
        userInfo.setTenantList(tenantList);
        userInfo.setMenuList(menuList);
        userInfo.setTokenInfo(StpUtil.getTokenInfo());
        return userInfo;
    }
}
