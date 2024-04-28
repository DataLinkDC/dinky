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
import org.dinky.data.model.alert.AlertRule;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.job.handler.JobAlertHandler;
import org.dinky.service.AlertRuleService;

import java.util.List;
import java.util.stream.Collectors;

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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alertRule")
@Api(tags = "Alert Rule Controller")
@SaCheckLogin
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @PostMapping("/list")
    @ApiOperation("Query alert rules list")
    public ProTableResult<AlertRule> list(@RequestBody JsonNode para) {
        ProTableResult<AlertRule> result = alertRuleService.selectForProTable(para);
        // The reason for this is to deal with internationalization
        List<AlertRule> list = result.getData().stream()
                .peek(rule -> rule.setName(Status.findMessageByKey(rule.getName())))
                .peek(rule -> rule.setDescription(Status.findMessageByKey(rule.getDescription())))
                .collect(Collectors.toList());
        result.setData(list);
        return result;
    }

    @PutMapping
    @ApiImplicitParam(
            name = "alertRule",
            value = "alertRule",
            required = true,
            dataType = "AlertRule",
            paramType = "body",
            dataTypeClass = AlertRule.class)
    @ApiOperation("Save or update alert rule")
    @Log(title = "Save or update alert rule", businessType = BusinessType.INSERT_OR_UPDATE)
    @SaCheckPermission(
            value = {PermissionConstants.SYSTEM_ALERT_RULE_ADD, PermissionConstants.SYSTEM_ALERT_RULE_EDIT},
            mode = SaMode.OR)
    public Result<Boolean> saveOrUpdateAlertRule(@RequestBody AlertRule alertRule) {
        boolean saved = alertRuleService.saveOrUpdate(alertRule);
        if (saved) {
            JobAlertHandler.getInstance().refreshRulesData();
            return Result.succeed(Status.MODIFY_SUCCESS);
        }
        return Result.failed(Status.MODIFY_FAILED);
    }

    @DeleteMapping
    @ApiImplicitParam(
            name = "id",
            value = "id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class,
            example = "1")
    @ApiOperation("Delete alert rule")
    @Log(title = "Delete alert rule", businessType = BusinessType.DELETE)
    @SaCheckPermission(PermissionConstants.SYSTEM_ALERT_RULE_DELETE)
    public Result<Boolean> deleteAlertRuleById(@RequestParam Integer id) {
        if (alertRuleService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        }
        return Result.failed(Status.DELETE_FAILED);
    }
}
