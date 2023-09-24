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

import org.dinky.data.model.LoginLog;
import org.dinky.data.model.OperateLog;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.LoginLogService;
import org.dinky.service.OperateLogService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Log Controller")
@RequestMapping("/api/log")
@RequiredArgsConstructor
@SaCheckLogin
public class LogController {

    private final LoginLogService loginLogService;

    private final OperateLogService operateLogService;

    @GetMapping("/loginRecord/{userId}")
    @ApiOperation("Query Login Log List")
    @ApiImplicitParam(name = "userId", value = "User ID", required = true, dataType = "Integer", paramType = "path")
    public Result<List<LoginLog>> queryLoginLogRecord(@PathVariable Integer userId) {
        return Result.succeed(loginLogService.loginRecord(userId));
    }

    @PostMapping("/operateLog/{userId}")
    @ApiOperation("Query Operate Log List")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "userId",
                value = "User ID",
                required = true,
                dataType = "Integer",
                paramType = "path"),
        @ApiImplicitParam(
                name = "para",
                value = "Query Parameters",
                required = true,
                dataType = "JsonNode",
                paramType = "body")
    })
    public ProTableResult<OperateLog> queryOperateLogRecord(@RequestBody JsonNode para, @PathVariable Integer userId) {
        return operateLogService.operateRecord(para, userId);
    }
}
