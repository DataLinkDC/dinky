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

package org.dinky.mapper;

import org.dinky.data.model.Role;
import org.dinky.mybatis.mapper.SuperMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/** role mapper interface */
@Mapper
public interface RoleMapper extends SuperMapper<Role> {

    List<Role> getRoleByIds(@Param("roleIds") Set<Integer> roleIds);

    List<Role> getRoleByTenantIdAndIds(
            @Param("tenantId") String tenantId, @Param("roleIds") Set<Integer> roleIds);

    /**
     * Query roles by user ID.
     *
     * @param userId user ID
     * @return role list
     */
    List<Role> selectRolePermissionByUserId(Integer userId);

    /**
     * Obtain a list of role selection boxes by user ID.
     *
     * @param userId user ID
     * @return result
     */
    List<Integer> selectRoleListByUserId(Integer userId);
}
