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
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.rbac.RowPermissions;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.RowPermissionsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "RowPermissions Controller")
@RestController
@RequestMapping("/api/rowPermissions")
@SaCheckLogin
public class RowPermissionsController {

    @Autowired
    private RowPermissionsService roleSelectPermissionsService;

    /**
     * save or update roleSelectPermissions
     *
     * @param roleSelectPermissions {@link RowPermissions}
     * @return {@link Result}
     */
    @PutMapping
    @ApiOperation("Insert Or Update RowPermissions")
    @Log(title = "Insert Or Update RowPermissions", businessType = BusinessType.INSERT)
    @ApiImplicitParam(
            name = "roleSelectPermissions",
            value = "RowPermissions",
            dataType = "RowPermissions",
            paramType = "body",
            required = true,
            dataTypeClass = RowPermissions.class)
    @SaCheckPermission(
            value = {PermissionConstants.AUTH_ROW_PERMISSIONS_ADD, PermissionConstants.AUTH_ROW_PERMISSIONS_EDIT},
            mode = SaMode.OR)
    public Result saveOrUpdateRowPermissions(@RequestBody RowPermissions roleSelectPermissions) {
        if (roleSelectPermissionsService.saveOrUpdate(roleSelectPermissions)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * delete rowPermissions by id
     *
     * @param id {@link Integer}
     * @return {@link Result}
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete RowPermissions By Id")
    @Log(title = "Delete RowPermissions By Id", businessType = BusinessType.DELETE)
    @ApiImplicitParam(
            name = "id",
            value = "RowPermissions Id",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.AUTH_ROW_PERMISSIONS_DELETE)
    public Result deleteRowPermissions(@RequestParam("id") Integer id) {

        if (roleSelectPermissionsService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        }
        return Result.failed(Status.DELETE_FAILED);
    }

    /**
     * query roleSelectPermissions list
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link RowPermissions}
     */
    @PostMapping
    @ApiOperation("Query RowPermissions List")
    @ApiImplicitParam(
            name = "para",
            value = "Query Condition",
            dataType = "JsonNode",
            paramType = "body",
            required = true,
            dataTypeClass = JsonNode.class)
    public ProTableResult<RowPermissions> listRowPermissions(@RequestBody JsonNode para) {
        return roleSelectPermissionsService.selectForProTable(para);
    }
}
