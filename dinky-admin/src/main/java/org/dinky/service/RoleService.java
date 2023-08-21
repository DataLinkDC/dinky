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

import org.dinky.data.model.Role;
import org.dinky.data.model.User;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface RoleService extends ISuperService<Role> {

    /**
     * create or update role
     *
     * @param role {@link Role}
     * @return {@link Result} of {@link Void}
     */
    Result<Void> addedOrUpdateRole(Role role);

    @Override
    ProTableResult<Role> selectForProTable(JsonNode para);

    /**
     * get role by id
     *
     * @param id role id
     * @return {@link Result} of {@link Void}
     */
    Result<Void> deleteRoleById(Integer id);

    /**
     * get role list by user id
     *
     * @param userId user id
     * @return role list
     */
    List<Role> getRoleByUserId(Integer userId);

    /**
     * Query role permissions based on user ID.
     *
     * @param userId user ID
     * @return permissions
     */
    Set<String> selectRolePermissionByUserId(Integer userId);

    /**
     * Query user role list by user ID.
     *
     * @param userId user ID
     * @return role list
     */
    List<Integer> selectRoleListByUserId(Integer userId);

    List<User> getUserListByRoleId(Integer roleId);
}
