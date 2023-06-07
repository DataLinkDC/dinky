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

import org.dinky.data.model.Tenant;
import org.dinky.data.params.AssignUserToTenantParams;
import org.dinky.data.result.Result;
import org.dinky.mybatis.service.ISuperService;

import com.fasterxml.jackson.databind.JsonNode;

public interface TenantService extends ISuperService<Tenant> {

    /**
     * delete tenant by id, this method will be {@link Deprecated} in the future, please use {@link
     * #removeTenantById(Integer)}
     *
     * @param para tenant id
     * @return delete result code
     */
    @Deprecated
    Result<Void> deleteTenantById(JsonNode para);

    /**
     * remove tenant by id
     *
     * @param tenantId tenant id
     * @return delete result code
     */
    Result<Void> removeTenantById(Integer tenantId);

    /**
     * add or update tenant
     *
     * @param tenant tenant info
     * @return add or update code
     */
    Result<Void> saveOrUpdateTenant(Tenant tenant);

    /**
     * @param tenantCode tenant code
     * @return Tenant
     */
    Tenant getTenantByTenantCode(String tenantCode);

    /**
     * @param tenant tenant info
     * @return modify code
     */
    boolean modifyTenant(Tenant tenant);

    /**
     * distribute users to tenant , this method will be {@link Deprecated} in the future, please use
     * {@link #assignUserToTenant(AssignUserToTenantParams)}
     *
     * @param para {@link JsonNode}
     * @return {@link Result} of {@link Void}
     */
    @Deprecated
    Result<Void> distributeUsers(JsonNode para);

    /**
     * assignUserToTenant users to tenant
     *
     * @param assignUserToTenantParams {@link AssignUserToTenantParams}
     * @return {@link Result} of {@link Void}
     */
    Result<Void> assignUserToTenant(AssignUserToTenantParams assignUserToTenantParams);
}
