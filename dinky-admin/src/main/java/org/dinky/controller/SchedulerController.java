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

import org.dinky.data.enums.Status;
import org.dinky.data.result.Result;
import org.dinky.scheduler.model.DinkyTaskRequest;
import org.dinky.scheduler.model.TaskDefinition;
import org.dinky.scheduler.model.TaskMainInfo;
import org.dinky.service.SchedulerService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scheduler")
@Api(tags = "DolphinScheduler Controller")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;

    /**
     * get task definition
     */
    @GetMapping("/queryTaskDefinition")
    @ApiOperation("Get Task Definition")
    @ApiImplicitParam(
            name = "dinkyTaskId",
            value = "Dinky Task id",
            required = true,
            dataType = "Long",
            paramType = "query",
            example = "1")
    public Result<TaskDefinition> getTaskDefinition(@ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId) {
        TaskDefinition taskDefinitionInfo = schedulerService.getTaskDefinitionInfo(dinkyTaskId);
        if (taskDefinitionInfo == null) {
            return Result.failed(Status.DS_TASK_NOT_EXIST);
        }
        return Result.succeed(taskDefinitionInfo);
    }

    /**
     * query upstream task
     */
    @GetMapping("/queryUpstreamTasks")
    @ApiOperation("Get Upstream Task Definition")
    @ApiImplicitParam(
            name = "dinkyTaskId",
            value = "Dinky Task id",
            required = true,
            dataType = "Long",
            paramType = "query",
            example = "1")
    public Result<List<TaskMainInfo>> getTaskMainInfos(@ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId) {
        List<TaskMainInfo> taskMainInfos = schedulerService.getTaskMainInfos(dinkyTaskId);
        return Result.succeed(taskMainInfos);
    }

    /**
     * create or update
     */
    @PostMapping("/createOrUpdateTaskDefinition")
    @ApiOperation("Create or Update Task Definition")
    public Result<String> createOrUpdateTaskDefinition(@RequestBody DinkyTaskRequest dinkyTaskRequest) {
        if (schedulerService.pushAddTask(dinkyTaskRequest)) {
            return Result.succeed(Status.DS_ADD_TASK_DEFINITION_SUCCESS);
        }
        return Result.succeed(Status.DS_ADD_TASK_DEFINITION_SUCCESS);
    }
}
