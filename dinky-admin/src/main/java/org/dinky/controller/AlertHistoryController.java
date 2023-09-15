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

import org.dinky.data.annotation.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.model.AlertHistory;
import org.dinky.data.result.ProTableResult;
import org.dinky.service.AlertHistoryService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

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
public class AlertHistoryController {

    private final AlertHistoryService alertHistoryService;

    /** 动态查询列表 */
    @PostMapping
    @ApiOperation("Query Alert History")
    @ApiImplicitParam(
            name = "para",
            value = "Query Alert History",
            dataTypeClass = JsonNode.class,
            paramType = "body",
            required = true,
            dataType = "JsonNode")
    @Log(title = "Query Alert History", businessType = BusinessType.QUERY)
    public ProTableResult<AlertHistory> listAlertHistoryRecord(@RequestBody JsonNode para) {
        return alertHistoryService.selectForProTable(para);
    }
}
