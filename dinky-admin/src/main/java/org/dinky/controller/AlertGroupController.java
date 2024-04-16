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
import org.dinky.data.model.alert.AlertGroup;
import org.dinky.data.result.Result;
import org.dinky.service.AlertGroupService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** AlertGroupController */
@Slf4j
@RestController
@Api(tags = "Alert Group Controller")
@RequestMapping("/api/alertGroup")
@RequiredArgsConstructor
@SaCheckLogin
public class AlertGroupController {

    private final AlertGroupService alertGroupService;

    /**
     * save or update alert Group
     *
     * @param alertGroup {@link AlertGroup}
     * @return {@link Result} with {@link Void}
     */
    @PutMapping("/addOrUpdate")
    @SaCheckPermission(
            value = {PermissionConstants.REGISTRATION_ALERT_GROUP_ADD, PermissionConstants.REGISTRATION_ALERT_GROUP_EDIT
            },
            mode = SaMode.OR)
    @Log(title = "Insert OR Update AlertGroup ", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "alertGroup",
            value = "AlertGroup",
            required = true,
            dataType = "AlertGroup",
            dataTypeClass = AlertGroup.class)
    @ApiOperation("Insert OR Update AlertGroup")
    public Result<Void> saveOrUpdateAlertGroup(@RequestBody AlertGroup alertGroup) {
        if (alertGroupService.saveOrUpdate(alertGroup)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * list alert groups
     *
     * @param keyword {@link String}
     * @return {@link Result} with {@link AlertGroup}
     */
    @GetMapping("/list")
    @ApiOperation("Query AlertGroup List")
    public Result<List<AlertGroup>> listAlertGroups(@RequestParam String keyword) {
        return Result.succeed(alertGroupService.selectListByKeyWord(keyword));
    }

    /**
     * get all enabled alert group
     *
     * @return {@link Result} with {@link List} of {@link AlertGroup}
     */
    @GetMapping("/listEnabledAll")
    @ApiOperation("Query AlertGroup List Of Enable")
    public Result<List<AlertGroup>> listEnabledAllAlertGroups() {
        return Result.succeed(alertGroupService.listEnabledAllAlertGroups());
    }

    /**
     * enable or disable alert group
     *
     * @return {@link Result} with {@link List} of {@link AlertGroup}
     */
    @PutMapping("/enable")
    @ApiOperation("Update AlertGroup Status")
    @ApiImplicitParam(
            name = "id",
            value = "AlertGroup Id",
            required = true,
            dataTypeClass = Integer.class,
            dataType = "Integer")
    @Log(title = "Update AlertGroup Status", businessType = BusinessType.UPDATE)
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_ALERT_GROUP_EDIT})
    public Result<Void> modifyAlertGroupStatus(@RequestParam("id") Integer id) {
        if (alertGroupService.modifyAlertGroupStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * delete alert group by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete AlertGroup By Id")
    @ApiImplicitParam(
            name = "id",
            value = "AlertGroup Id",
            required = true,
            dataTypeClass = Integer.class,
            dataType = "Integer")
    @Log(title = "Delete AlertGroup By Id", businessType = BusinessType.DELETE)
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_ALERT_GROUP_DELETE})
    public Result<Void> deleteGroupById(@RequestParam("id") Integer id) {
        if (alertGroupService.deleteGroupById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }
}
