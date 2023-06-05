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

import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.model.Role;
import org.dinky.data.model.RoleSelectPermissions;
import org.dinky.data.model.Tenant;
import org.dinky.data.model.User;
import org.dinky.data.params.AssignRoleParams;
import org.dinky.data.result.Result;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * UserService
 *
 * @since 2021/11/28 13:39
 */
public interface UserService extends ISuperService<User> {

    /**
     * registerUser
     *
     * @param user user
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> registerUser(User user);

    /**
     * modifyUser
     *
     * @param user user
     * @return {@link Boolean}
     */
    Boolean modifyUser(User user);

    /**
     * modifyPassword
     *
     * @param modifyPasswordDTO {@link ModifyPasswordDTO}
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> modifyPassword(ModifyPasswordDTO modifyPasswordDTO);

    /**
     * removeUser
     *
     * @param id id
     * @return {@link Boolean}
     */
    Boolean removeUser(Integer id);

    /**
     * loginUser
     *
     * @param loginDTO basic information for user login
     * @return {@link Result}{@link UserDTO} obtain the user's UserDTO
     */
    Result<UserDTO> loginUser(LoginDTO loginDTO);

    /**
     * getUserByUsername
     *
     * @param username username
     * @return {@link User}
     */
    User getUserByUsername(String username);

    /**
     * grantRole will be {@link Deprecated} please use {@link
     * UserService#assignRole(AssignRoleParams)}
     *
     * @param param param
     * @return {@link Result}<{@link Void}>
     */
    @Deprecated
    Result<Void> grantRole(JsonNode param);

    /**
     * grantRole
     *
     * @param assignRoleParams {@link AssignRoleParams}
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> assignRole(AssignRoleParams assignRoleParams);

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
    Boolean enable(Integer id);

    /**
     * check user is admin
     *
     * @param id
     * @return {@link Boolean}
     */
    Boolean checkAdmin(Integer id);

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
    List<RoleSelectPermissions> getCurrentRoleSelectPermissions();

    /** user loginout */
    void outLogin();
}
