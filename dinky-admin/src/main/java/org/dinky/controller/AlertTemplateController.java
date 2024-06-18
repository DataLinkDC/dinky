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
import org.dinky.data.model.alert.AlertTemplate;
import org.dinky.data.result.Result;
import org.dinky.service.AlertTemplateService;

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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alertTemplate")
@Api(tags = "Alert Template Controller")
@SaCheckLogin
public class AlertTemplateController {

    private final AlertTemplateService alertTemplateService;

    @GetMapping
    @ApiOperation("Query alert templates list")
    public Result<List<AlertTemplate>> list() {
        return Result.succeed(alertTemplateService.list());
    }

    @DeleteMapping
    @Log(title = "Delete AlertTemplate ", businessType = BusinessType.DELETE)
    @ApiOperation("Delete alert template")
    @ApiImplicitParam(
            name = "id",
            value = "AlertTemplate ID",
            required = true,
            dataType = "Integer",
            paramType = "query",
            example = "1")
    @SaCheckPermission(PermissionConstants.REGISTRATION_ALERT_TEMPLATE_DELETE)
    public Result<Boolean> deleteAlertTemplateById(@RequestParam Integer id) {
        if (alertTemplateService.removeAlertTemplateById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        }
        return Result.failed(Status.DELETE_FAILED);
    }

    @PutMapping
    @Log(title = "Insert OR Update AlertTemplate ", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert OR Update alert template")
    @ApiImplicitParam(
            name = "alertTemplate",
            value = "AlertTemplate",
            required = true,
            dataType = "AlertTemplate",
            paramType = "body",
            dataTypeClass = AlertTemplate.class)
    @SaCheckPermission(
            value = {
                PermissionConstants.REGISTRATION_ALERT_TEMPLATE_ADD,
                PermissionConstants.REGISTRATION_ALERT_TEMPLATE_EDIT
            },
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateAlertTemplate(@RequestBody AlertTemplate alertTemplate) {
        if (alertTemplateService.saveOrUpdate(alertTemplate)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }
}
