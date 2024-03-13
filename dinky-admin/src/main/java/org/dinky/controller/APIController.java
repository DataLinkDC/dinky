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

import org.dinky.DinkyVersion;
import org.dinky.data.annotations.Log;
import org.dinky.data.dto.APISavePointTaskDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskSubmitDto;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobResult;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * APIController
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
@RestController
@Api(tags = "OpenAPI & Task API Controller")
@RequestMapping("/openapi")
@RequiredArgsConstructor
public class APIController {

    private final TaskService taskService;
    private final JobInstanceService jobInstanceService;

    @GetMapping("/version")
    @ApiOperation(value = "Query Service Version", notes = "Query Dinky Service Version Number")
    public Result<String> getVersionInfo() {
        return Result.succeed(DinkyVersion.getVersion(), "Get success");
    }

    @PostMapping("/submitTask")
    @ApiOperation("Submit Task")
    //    @Log(title = "Submit Task", businessType = BusinessType.SUBMIT)
    public Result<JobResult> submitTask(@RequestBody TaskSubmitDto submitDto) throws Exception {
        taskService.initTenantByTaskId(submitDto.getId());
        JobResult jobResult = taskService.submitTask(submitDto);
        if (jobResult.isSuccess()) {
            return Result.succeed(jobResult, Status.EXECUTE_SUCCESS);
        } else {
            return Result.failed(jobResult, jobResult.getError());
        }
    }

    @PostMapping("/savepointTask")
    public Result savepointTask(@RequestBody APISavePointTaskDTO apiSavePointTaskDTO) {
        return Result.succeed(
                taskService.savepointTaskJob(
                        taskService.getTaskInfoById(apiSavePointTaskDTO.getTaskId()),
                        SavePointType.get(apiSavePointTaskDTO.getType())),
                Status.EXECUTE_SUCCESS);
    }

    @GetMapping("/cancel")
    //    @Log(title = "Cancel Flink Job", businessType = BusinessType.TRIGGER)
    @ApiOperation("Cancel Flink Job")
    public Result<Boolean> cancel(
            @RequestParam Integer id,
            @RequestParam(defaultValue = "false") boolean withSavePoint,
            @RequestParam(defaultValue = "true") boolean forceCancel) {
        return Result.succeed(
                taskService.cancelTaskJob(taskService.getTaskInfoById(id), withSavePoint, forceCancel),
                Status.EXECUTE_SUCCESS);
    }

    /**
     * 重启任务
     */
    @GetMapping(value = "/restartTask")
    @ApiOperation("Restart Task")
    //    @Log(title = "Restart Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<JobResult> restartTask(@RequestParam Integer id, String savePointPath) throws Exception {
        return Result.succeed(taskService.restartTask(id, savePointPath));
    }

    @PostMapping("/savepoint")
    //    @Log(title = "Savepoint Trigger", businessType = BusinessType.TRIGGER)
    @ApiOperation("Savepoint Trigger")
    public Result<SavePointResult> savepoint(@RequestParam Integer taskId, @RequestParam String savePointType) {
        return Result.succeed(
                taskService.savepointTaskJob(
                        taskService.getTaskInfoById(taskId), SavePointType.valueOf(savePointType.toUpperCase())),
                Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/explainSql")
    @ApiOperation("Explain Sql")
    public Result<List<SqlExplainResult>> explainSql(@RequestBody TaskDTO taskDTO) throws NotSupportExplainExcepition {
        return Result.succeed(taskService.explainTask(taskDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/getJobPlan")
    @ApiOperation("Get Job Plan")
    public Result<ObjectNode> getJobPlan(@RequestBody TaskDTO taskDTO) {
        return Result.succeed(taskService.getJobPlan(taskDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/getStreamGraph")
    @ApiOperation("Get Stream Graph")
    public Result<ObjectNode> getStreamGraph(@RequestBody TaskDTO taskDTO) {
        return Result.succeed(taskService.getStreamGraph(taskDTO), Status.EXECUTE_SUCCESS);
    }

    /**
     * 获取Job实例的信息
     */
    @GetMapping("/getJobInstance")
    @ApiOperation("Get Job Instance")
    @ApiImplicitParam(
            name = "id",
            value = "Job Instance Id",
            required = true,
            dataType = "Integer",
            dataTypeClass = Integer.class)
    public Result<JobInstance> getJobInstance(@RequestParam Integer id) {
        jobInstanceService.initTenantByJobInstanceId(id);
        return Result.succeed(jobInstanceService.getById(id));
    }

    @GetMapping("/getJobInstanceByTaskId")
    @ApiOperation("Get Job Instance By Task Id")
    @ApiImplicitParam(
            name = "id",
            value = "Task Id",
            required = true,
            dataType = "Integer",
            dataTypeClass = Integer.class)
    public Result<JobInstance> getJobInstanceByTaskId(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(jobInstanceService.getJobInstanceByTaskId(id));
    }

    @GetMapping(value = "/exportSql")
    @ApiOperation("Export Sql")
    @Log(title = "Export Sql", businessType = BusinessType.EXPORT)
    @ApiImplicitParam(
            name = "id",
            value = "Task Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    public Result<String> exportSql(@RequestParam Integer id) {
        return Result.succeed(taskService.exportSql(id));
    }

    @GetMapping("/getTaskLineage")
    @ApiOperation("Get Task Lineage")
    @Log(title = "Get Task Lineage", businessType = BusinessType.OTHER)
    @ApiImplicitParam(
            name = "id",
            value = "Task Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    public Result getTaskLineage(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.getTaskLineage(id), "获取成功");
    }
}
