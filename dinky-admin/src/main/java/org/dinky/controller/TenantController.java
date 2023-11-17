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

import org.dinky.data.annotations.Log;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.AssignUserToTenantDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.TenantService;
import org.dinky.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** tenant controller */
@Slf4j
@RestController
@Api(tags = "Tenant Controller")
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    private final UserService userService;

    /**
     * save or update tenant
     *
     * @param tenant {@link Tenant}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping
    @ApiOperation("Insert Or Update Tenant")
    @Log(title = "Insert Or Update Tenant", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "tenant",
            value = "tenant entity",
            required = true,
            dataType = "Tenant",
            paramType = "body")
    @SaCheckPermission(
            value = {PermissionConstants.AUTH_TENANT_ADD, PermissionConstants.AUTH_TENANT_EDIT},
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateTenant(@RequestBody Tenant tenant) {
        return tenantService.saveOrUpdateTenant(tenant);
    }

    /**
     * delete tenant by id
     *
     * @param tenantId tenant id
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete Tenant By Id")
    @Log(title = "Delete Tenant By Id", businessType = BusinessType.DELETE)
    @ApiImplicitParam(name = "id", value = "tenant id", required = true, dataType = "Integer", paramType = "query")
    @SaCheckPermission(value = PermissionConstants.AUTH_TENANT_DELETE)
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
    @ApiOperation("List Tenants")
    @ApiImplicitParam(
            name = "para",
            value = "ProTable request params",
            required = true,
            dataType = "JsonNode",
            paramType = "body")
    public ProTableResult<Tenant> listTenants(@RequestBody JsonNode para) {
        return tenantService.selectForProTable(para, true);
    }

    /**
     * assign user to tenant
     *
     * @param assignUserToTenantDTO {@link AssignUserToTenantDTO}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping(value = "/assignUserToTenant")
    @ApiOperation("Assign User To Tenant")
    @ApiImplicitParam(
            name = "assignUserToTenantDTO",
            value = "assign user to tenant params",
            required = true,
            dataType = "AssignUserToTenantDTO",
            paramType = "body")
    @Log(title = "Assign User To Tenant", businessType = BusinessType.INSERT)
    @SaCheckPermission(value = PermissionConstants.AUTH_TENANT_ASSIGN_USER)
    public Result<Void> assignUserToTenant(@RequestBody AssignUserToTenantDTO assignUserToTenantDTO) {
        return tenantService.assignUserToTenant(assignUserToTenantDTO);
    }

    /**
     * get user list by tenant id
     *
     * @param id
     * @return {@link Result} with {@link Dict}
     */
    @GetMapping("/getUsersByTenantId")
    @ApiImplicitParam(name = "id", value = "tenant id", required = true, dataType = "Integer", paramType = "query")
    @ApiOperation("Get User List By Tenant Id")
    @SaCheckPermission(value = PermissionConstants.AUTH_TENANT_VIEW_USER)
    public Result<List<User>> getUserListByTenantId(@RequestParam("id") Integer id) {
        return Result.succeed(userService.getUserListByTenantId(id));
    }

    @GetMapping("/getTenantListByUserId")
    @ApiOperation("Get Tenant List By User Id")
    @ApiImplicitParam(
            name = "id",
            value = "user id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            example = "1")
    public Result<List<Tenant>> getTenantListByUserId(@RequestParam("id") Integer userId) {
        return Result.succeed(tenantService.getTenantListByUserId(userId));
    }
}
