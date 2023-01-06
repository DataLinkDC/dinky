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

package com.dlink.service;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Role;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface RoleService extends ISuperService<Role> {
    /**
     * delete role
     *
     * @param para role id
     * @return delete result code
     */
    Result deleteRoles(JsonNode para);

    Result saveOrUpdateRole(Role role);

    List<Role> getRoleByIds(Set<Integer> roleIds);

    List<Role> getRoleByTenantIdAndIds(String tenantId, Set<Integer> roleIds);

    boolean deleteByIds(List<Integer> ids);

    @Override
    ProTableResult<Role> selectForProTable(JsonNode para);
}