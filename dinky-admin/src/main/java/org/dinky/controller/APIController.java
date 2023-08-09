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
import org.dinky.data.dto.APICancelDTO;
import org.dinky.data.dto.APIExecuteJarDTO;
import org.dinky.data.dto.APIExecuteSqlDTO;
import org.dinky.data.dto.APIExplainSqlDTO;
import org.dinky.data.dto.APISavePointDTO;
import org.dinky.data.dto.APISavePointTaskDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.JobInstance;
import org.dinky.data.result.APIJobResult;
import org.dinky.data.result.ExplainResult;
import org.dinky.data.result.Result;
import org.dinky.data.result.SelectResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobResult;
import org.dinky.service.APIService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.StudioService;
import org.dinky.service.TaskService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * APIController
 *
 * @since 2021/12/11 21:44
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
@RestController
@RequestMapping("/openapi")
@RequiredArgsConstructor
public class APIController {

    private final APIService apiService;
    private final StudioService studioService;
    private final TaskService taskService;
    private final JobInstanceService jobInstanceService;

    @GetMapping("/submitTask")
    @ApiOperation("Submit task")
    @Log(title = "Submit task", businessType = BusinessType.SUBMIT)
    public Result<JobResult> submitTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.submitTask(id), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/executeSql")
    @Log(title = "Execute Sql", businessType = BusinessType.EXECUTE)
    @ApiOperation("Execute sql")
    public Result<APIJobResult> executeSql(@RequestBody APIExecuteSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.executeSql(apiExecuteSqlDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/explainSql")
    @Log(title = "Explain Sql", businessType = BusinessType.EXECUTE)
    @ApiOperation("Explain sql")
    public Result<ExplainResult> explainSql(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.explainSql(apiExecuteSqlDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/getJobPlan")
    @Log(title = "Get Job Plan", businessType = BusinessType.EXECUTE)
    @ApiOperation("Get job plan")
    public Result<ObjectNode> getJobPlan(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.getJobPlan(apiExecuteSqlDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/getStreamGraph")
    @Log(title = "Get Stream Graph", businessType = BusinessType.EXECUTE)
    @ApiOperation("Get stream graph")
    public Result<ObjectNode> getStreamGraph(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.getStreamGraph(apiExecuteSqlDTO), Status.EXECUTE_SUCCESS);
    }

    @GetMapping("/getJobData")
    @Log(title = "Get Job Data", businessType = BusinessType.EXECUTE)
    @ApiOperation("Get job data")
    public Result<SelectResult> getJobData(@RequestParam String jobId) {
        return Result.succeed(studioService.getJobData(jobId));
    }

    @PostMapping("/cancel")
    @Log(title = "Cancel Flink Job", businessType = BusinessType.TRIGGER)
    @ApiOperation("Cancel Flink Job")
    public Result<Boolean> cancel(@RequestBody APICancelDTO apiCancelDTO) {
        return Result.succeed(apiService.cancel(apiCancelDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/savepoint")
    @Log(title = "Savepoint trigger", businessType = BusinessType.TRIGGER)
    @ApiOperation("Savepoint trigger")
    public Result<SavePointResult> savepoint(@RequestBody APISavePointDTO apiSavePointDTO) {
        return Result.succeed(apiService.savepoint(apiSavePointDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/executeJar")
    @Log(title = "Execute Jar", businessType = BusinessType.EXECUTE)
    @ApiOperation("Execute jar")
    public Result<APIJobResult> executeJar(@RequestBody APIExecuteJarDTO apiExecuteJarDTO) {
        return Result.succeed(apiService.executeJar(apiExecuteJarDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/savepointTask")
    @Log(title = "Savepoint task", businessType = BusinessType.TRIGGER)
    @ApiOperation("Savepoint task")
    public Result<Boolean> savepointTask(@RequestBody APISavePointTaskDTO apiSavePointTaskDTO) {
        return Result.succeed(
                taskService.savepointTask(
                        apiSavePointTaskDTO.getTaskId(), apiSavePointTaskDTO.getType()),
                "执行成功");
    }

    /** 重启任务 */
    @GetMapping("/restartTask")
    @ApiOperation("Restart task")
    @Log(title = "Restart task", businessType = BusinessType.EXECUTE)
    public Result<JobResult> restartTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.restartTask(id, null), Status.RESTART_SUCCESS);
    }

    /** 选择保存点重启任务 */
    @GetMapping("/selectSavePointRestartTask")
    @ApiOperation("Select save point restart task")
    @Log(title = "Select save point restart task", businessType = BusinessType.EXECUTE)
    public Result<JobResult> restartTask(
            @RequestParam Integer id, @RequestParam String savePointPath) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.restartTask(id, savePointPath), Status.RESTART_SUCCESS);
    }

    /** 上线任务 */
    @GetMapping("/onLineTask")
    @ApiOperation("On line task")
    @Log(title = "On line task", businessType = BusinessType.EXECUTE)
    public Result<JobResult> onLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.onLineTask(id);
    }

    /** 下线任务 */
    @GetMapping("/offLineTask")
    @ApiOperation("Off line task")
    @Log(title = "Off line task", businessType = BusinessType.EXECUTE)
    public Result<Void> offLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.offLineTask(id, null);
    }

    /** 重新上线任务 */
    @GetMapping("/reOnLineTask")
    @ApiOperation("Re on line task")
    @Log(title = "Re on line task", businessType = BusinessType.EXECUTE)
    public Result<JobResult> reOnLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.reOnLineTask(id, null);
    }

    /** 选择保存点重新上线任务 */
    @GetMapping("/selectSavePointReOnLineTask")
    @ApiOperation("Select save point re on line task")
    @Log(title = "Select save point re on line task", businessType = BusinessType.EXECUTE)
    public Result<JobResult> selectSavePointReOnLineTask(
            @RequestParam Integer id, @RequestParam String savePointPath) {
        taskService.initTenantByTaskId(id);
        return taskService.reOnLineTask(id, savePointPath);
    }

    /** 获取Job实例的信息 */
    @GetMapping("/getJobInstance")
    @Log(title = "Get Job Instance", businessType = BusinessType.QUERY)
    @ApiOperation("Get job instance")
    public Result<JobInstance> getJobInstance(@RequestParam Integer id) {
        jobInstanceService.initTenantByJobInstanceId(id);
        return Result.succeed(jobInstanceService.getById(id));
    }

    /** 通过 taskId 获取 Task 对应的 Job 实例的信息 */
    @GetMapping("/getJobInstanceByTaskId")
    @Log(title = "Get Job Instance By Task Id", businessType = BusinessType.QUERY)
    @ApiOperation("Get job instance by task id")
    public Result<JobInstance> getJobInstanceByTaskId(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(jobInstanceService.getJobInstanceByTaskId(id));
    }
}
