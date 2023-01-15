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

import org.dinky.common.result.Result;
import org.dinky.db.service.ISuperService;
import org.dinky.dto.LoginDTO;
import org.dinky.dto.UserDTO;
import org.dinky.model.Tenant;
import org.dinky.model.User;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * UserService
 *
 * @author wenmo
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
     * @return boolean
     */
    boolean modifyUser(User user);

    /**
     * modifyPassword
     *
     * @param username username
     * @param password password
     * @param newPassword newPassword
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> modifyPassword(String username, String password, String newPassword);

    /**
     * removeUser
     *
     * @param id id
     * @return boolean
     */
    boolean removeUser(Integer id);

    /**
     * loginUser
     *
     * @param loginDTO loginDTO
     * @return {@link Result}<{@link UserDTO}>
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
     * grantRole
     *
     * @param param param
     * @return {@link Result}<{@link Void}>
     */
    Result<Void> grantRole(JsonNode param);

    /**
     * getTenants
     *
     * @param username username
     * @return {@link Result}<{@link List}<{@link Tenant}>>
     */
    Result<List<Tenant>> getTenants(String username);
}
