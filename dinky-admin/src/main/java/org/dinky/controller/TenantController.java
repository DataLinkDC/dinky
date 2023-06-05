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

package org.dinky.controller;

import org.dinky.data.model.Tenant;
import org.dinky.data.params.AssignUserToTenantParams;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.TenantService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** tenant controller */
@Slf4j
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * save or update tenant
     *
     * @param tenant {@link Tenant}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody Tenant tenant) {
        return tenantService.saveOrUpdateTenant(tenant);
    }

    /**
     * delete tenant by id , this method will be {@link Deprecated} in the future, please use {@link
     * #removeTenantById(Integer)}
     *
     * @return delete result code
     */
    @DeleteMapping()
    @Deprecated
    public Result<Void> deleteTenantById(@RequestBody JsonNode para) {
        return tenantService.deleteTenantById(para);
    }

    /**
     * delete tenant by id
     *
     * @param tenantId tenant id
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    public Result<Void> removeTenantById(@RequestParam("id") Integer tenantId) {
        return tenantService.removeTenantById(tenantId);
    }

    /**
     * list tenants
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link Tenant}
     */
    @PostMapping
    public ProTableResult<Tenant> listTenants(@RequestBody JsonNode para) {
        return tenantService.selectForProTable(para, true);
    }

    /**
     * give tenant grant user, this method is {@link @Deprecated} in the future, please use {@link
     * #assignUserToTenant}
     *
     * @param para para
     * @return {@link Result}
     */
    @PutMapping(value = "/grantTenantToUser")
    @Deprecated
    public Result<Void> distributeUser(@RequestBody JsonNode para) {
        return tenantService.distributeUsers(para);
    }

    /**
     * assign user to tenant
     *
     * @param assignUserToTenantParams {@link AssignUserToTenantParams}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping(value = "/assignUserToTenant")
    public Result<Void> assignUserToTenant(
            @RequestBody AssignUserToTenantParams assignUserToTenantParams) {
        return tenantService.assignUserToTenant(assignUserToTenantParams);
    }
}
