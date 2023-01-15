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

import org.dinky.common.result.ProTableResult;
import org.dinky.common.result.Result;
import org.dinky.model.Tenant;
import org.dinky.service.TenantService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * create or update tenant
     *
     * @return delete result code
     */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody Tenant tenant) {
        return tenantService.saveOrUpdateTenant(tenant);
    }

    /**
     * delete tenant by id
     *
     * @return delete result code
     */
    @DeleteMapping()
    public Result<Void> deleteTenantById(@RequestBody JsonNode para) {
        return tenantService.deleteTenantById(para);
    }

    /** query tenant list */
    @PostMapping
    public ProTableResult<Tenant> listTenants(@RequestBody JsonNode para) {
        return tenantService.selectForProTable(para, true);
    }

    /**
     * give tenant grant user
     *
     * @param para 帕拉
     * @return {@link Result}
     */
    @PutMapping(value = "/grantTenantToUser")
    public Result<Void> distributeUser(@RequestBody JsonNode para) {
        return tenantService.distributeUsers(para);
    }

    @PostMapping(value = "/switchTenant")
    public Result<Void> switchTenant(@RequestBody JsonNode para) {
        return tenantService.switchTenant(para);
    }
}
