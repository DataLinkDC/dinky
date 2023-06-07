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
import org.dinky.data.model.UserRole;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

public interface UserRoleService extends ISuperService<UserRole> {

    /**
     * delete user role relation by user id
     *
     * @param userId user id
     * @return delete row num
     */
    int delete(int userId);

    /**
     * query user role relation by userId
     *
     * @param userId user id
     * @return delete row num
     */
    List<UserRole> getUserRoleByUserId(int userId);

    /**
     * delete user role relation by userId and roleId
     *
     * @param userRoleList
     * @return
     */
    int deleteBathRelation(List<UserRole> userRoleList);

    /**
     * delete user role relation by role id
     *
     * @param roleIds role id
     * @return
     */
    boolean deleteByRoleIds(List<Integer> roleIds);

    /**
     * query roles by user id
     *
     * @param userId user id
     * @return role list
     */
    List<Role> getRoleByUserId(Integer userId);
}
