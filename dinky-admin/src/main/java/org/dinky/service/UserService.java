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

package org.dinky.service;

import org.dinky.data.dto.AssignRoleDTO;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.model.rbac.Role;
import org.dinky.data.model.rbac.RowPermissions;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.Result;
import org.dinky.data.vo.UserVo;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/**
 * UserService
 *
 * @since 2021/11/28 13:39
 */
public interface UserService extends ISuperService<User> {

    /**
     * register user
     *
     * @param user user
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> registerUser(User user);

    /**
     * modify user
     *
     * @param user user
     * @return {@link Boolean}
     */
    Boolean modifyUser(User user);

    /**
     * modify password
     *
     * @param modifyPasswordDTO {@link ModifyPasswordDTO}
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> modifyPassword(ModifyPasswordDTO modifyPasswordDTO);

    /**
     * remove user
     *
     * @param id id
     * @return {@link Boolean}
     */
    Boolean removeUser(Integer id);

    /**
     * login user
     *
     * @param loginDTO basic information for user login
     * @return {@link Result}{@link UserDTO} obtain the user's UserDTO
     */
    Result<UserDTO> loginUser(LoginDTO loginDTO);

    /**
     * get user by username
     *
     * @param username username
     * @return {@link User}
     */
    User getUserByUsername(String username);

    /**
     * grant role
     *
     * @param assignRoleDTO {@link AssignRoleDTO}
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> assignRole(AssignRoleDTO assignRoleDTO);

    /**
     * choose tenant
     *
     * @param tenantId
     * @return {@link Result}{@link Tenant} the specified tenant
     */
    Result<Tenant> chooseTenant(Integer tenantId);

    /**
     * get current user base info
     *
     * @return {@link Result}{@link UserDTO} obtain the current user's UserDTO
     */
    Result<UserDTO> queryCurrentUserInfo();

    /**
     * user enable or disable
     *
     * @param id
     * @return {@link Boolean}
     */
    Boolean modifyUserStatus(Integer id);

    /**
     * check user is admin
     *
     * @param id
     * @return {@link Boolean}
     */
    Boolean checkSuperAdmin(Integer id);

    /**
     * check user is tenant admin
     *
     * @param id
     * @return {@link Boolean}
     */
    @Deprecated
    Boolean checkTenantAdmin(Integer id);

    /**
     * get role by current user
     *
     * @return role list
     */
    List<Role> getCurrentRole();

    /**
     * get role select permissions by current user
     *
     * @return role select permissions list
     */
    List<RowPermissions> getCurrentRoleSelectPermissions();

    /**
     * Builds row-level permissions.
     *
     */
    void buildRowPermission();

    /**
     * User logout function.
     *
     */
    void outLogin();

    /**
     * get user ids where user in given tenant id
     *
     * @return role select permissions list
     */
    List<Integer> getUserIdsByTenantId(int id);

    /**
     * Returns a list of users based on the provided tenant id.
     *
     * @param id The id of the tenant.
     * @return A list of users that belong to the tenant with the provided id.
     */
    List<User> getUserListByTenantId(int id);

    /**
     * Modifies the user's role permissions based on the provided parameters.
     *
     * @param userId The id of the user to modify.
     * @param tenantId The id of the tenant to modify.
     * @param tenantAdminFlag Whether the user should be a tenant admin.
     * @return A result object indicating the success or failure of the operation.
     */
    Result<Void> modifyUserToTenantAdmin(Integer userId, Integer tenantId, Boolean tenantAdminFlag);

    /**
     * Restores a user's account to its original status after it has been disabled or locked.
     *
     * @param userId The id of the user to restore.
     * @return A result object indicating the success or failure of the operation.
     */
    Result<Void> recoveryUser(Integer userId);

    /**
     * Resets a user's password to a new value.
     *
     * @param userId The id of the user to reset password.
     * @return A result object containing the user's new password.
     */
    Result<UserVo> resetPassword(Integer userId);
}
