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

import org.dinky.context.ConsoleContextHolder;
import org.dinky.data.enums.Status;
import org.dinky.data.model.ProcessEntity;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;

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

/**
 * ProcessController
 *
 * @since 2022/10/16 22:53
 */
@RestController
@Api(tags = "Process Controller")
@RequestMapping("/api/process")
@SaCheckLogin
@RequiredArgsConstructor
public class ProcessController {

    /**
     * List all process
     *
     * @param active true: list active process, false: list inactive process {@link Boolean}
     * @return {@link ProTableResult}<{@link ProcessEntity}>
     */
    @GetMapping("/listAllProcess")
    @ApiOperation("List all process")
    @ApiImplicitParam(
            name = "active",
            value = "true: list active process, false: list inactive process",
            dataType = "Boolean")
    public ProTableResult<ProcessEntity> listAllProcess(@RequestParam boolean active) {
        return ProTableResult.<ProcessEntity>builder()
                .success(true)
                .data(ConsoleContextHolder.getInstances().list())
                .build();
    }

    @GetMapping("/getProcess")
    @ApiOperation("get process")
    @ApiImplicitParam(name = "processName", value = "process name", dataType = "ProcessEntity")
    public Result<ProcessEntity> getProcessByProcessName(@RequestParam String processName) {
        return Result.succeed(ConsoleContextHolder.getInstances().getProcess(processName));
    }

    @DeleteMapping("/clearProcessLog")
    @ApiOperation("Clear Process")
    @ApiImplicitParam(name = "processName", value = "process name", dataType = "ProcessEntity")
    public Result<Void> clearProcessLog(@RequestParam String processName) {
        boolean clearProcessLog = ConsoleContextHolder.getInstances().clearProcessLog(processName);
        if (!clearProcessLog) {
            return Result.failed(Status.PROCESS_CLEAR_LOG_FAILED);
        }
        return Result.succeed(Status.PROCESS_CLEAR_LOG_SUCCESS);
    }

    @GetMapping("/killProcess")
    @ApiOperation("killProcess ")
    @ApiImplicitParam(name = "processName", value = "process name", dataType = "ProcessEntity")
    public Result<ProcessEntity> stopProcess(@RequestParam String processName) {
        ProcessEntity process = ConsoleContextHolder.getInstances().killProcess(processName);
        return Result.succeed(process);
    }
}
