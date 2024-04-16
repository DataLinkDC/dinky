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
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.alert.AlertHistory;
import org.dinky.data.result.Result;
import org.dinky.service.AlertHistoryService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AlertHistoryController
 *
 * @since 2022/2/24 20:43
 */
@Slf4j
@RestController
@Api(tags = "Alert History Controller")
@RequestMapping("/api/alertHistory")
@RequiredArgsConstructor
@SaCheckLogin
public class AlertHistoryController {

    private final AlertHistoryService alertHistoryService;

    /**
     * Query Alert History By Job Instance Id
     * @param jobInstanceId
     * @return List<AlertHistory>
     */
    @GetMapping("/list")
    @ApiOperation("Query Alert History")
    @ApiImplicitParam(
            name = "jobInstanceId",
            value = "Query Alert History By Job Instance Id",
            dataTypeClass = Integer.class,
            required = true,
            dataType = "Integer")
    @Log(title = "Query Alert History", businessType = BusinessType.QUERY)
    public Result<List<AlertHistory>> listAlertHistoryRecord(@RequestParam("jobInstanceId") Integer jobInstanceId) {
        return Result.succeed(alertHistoryService.queryAlertHistoryRecordByJobInstanceId(jobInstanceId));
    }

    /**
     * delete AlertInstance by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    @Log(title = "Delete Alert History By Id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete Alert History By Id")
    @ApiImplicitParam(
            name = "id",
            value = "Alert History Id",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class)
    public Result<Void> deleteAlertInstanceById(@RequestParam("id") Integer id) {
        if (alertHistoryService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }
}
