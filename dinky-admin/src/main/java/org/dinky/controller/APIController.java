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

import org.dinky.data.dto.APICancelDTO;
import org.dinky.data.dto.APIExecuteJarDTO;
import org.dinky.data.dto.APIExecuteSqlDTO;
import org.dinky.data.dto.APIExplainSqlDTO;
import org.dinky.data.dto.APISavePointDTO;
import org.dinky.data.dto.APISavePointTaskDTO;
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
    public Result<JobResult> submitTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.submitTask(id), "执行成功");
    }

    @PostMapping("/executeSql")
    public Result<APIJobResult> executeSql(@RequestBody APIExecuteSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.executeSql(apiExecuteSqlDTO), "执行成功");
    }

    @PostMapping("/explainSql")
    public Result<ExplainResult> explainSql(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.explainSql(apiExecuteSqlDTO), "执行成功");
    }

    @PostMapping("/getJobPlan")
    public Result<ObjectNode> getJobPlan(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.getJobPlan(apiExecuteSqlDTO), "执行成功");
    }

    @PostMapping("/getStreamGraph")
    public Result<ObjectNode> getStreamGraph(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.getStreamGraph(apiExecuteSqlDTO), "执行成功");
    }

    @GetMapping("/getJobData")
    public Result<SelectResult> getJobData(@RequestParam String jobId) {
        return Result.succeed(studioService.getJobData(jobId), "获取成功");
    }

    @PostMapping("/cancel")
    public Result<Boolean> cancel(@RequestBody APICancelDTO apiCancelDTO) {
        return Result.succeed(apiService.cancel(apiCancelDTO), "执行成功");
    }

    @PostMapping("/savepoint")
    public Result<SavePointResult> savepoint(@RequestBody APISavePointDTO apiSavePointDTO) {
        return Result.succeed(apiService.savepoint(apiSavePointDTO), "执行成功");
    }

    @PostMapping("/executeJar")
    public Result<APIJobResult> executeJar(@RequestBody APIExecuteJarDTO apiExecuteJarDTO) {
        return Result.succeed(apiService.executeJar(apiExecuteJarDTO), "执行成功");
    }

    @PostMapping("/savepointTask")
    public Result<Boolean> savepointTask(@RequestBody APISavePointTaskDTO apiSavePointTaskDTO) {
        return Result.succeed(
                taskService.savepointTask(
                        apiSavePointTaskDTO.getTaskId(), apiSavePointTaskDTO.getType()),
                "执行成功");
    }

    /** 重启任务 */
    @GetMapping("/restartTask")
    public Result<JobResult> restartTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.restartTask(id, null), "重启成功");
    }

    /** 选择保存点重启任务 */
    @GetMapping("/selectSavePointRestartTask")
    public Result<JobResult> restartTask(
            @RequestParam Integer id, @RequestParam String savePointPath) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.restartTask(id, savePointPath), "重启成功");
    }

    /** 上线任务 */
    @GetMapping("/onLineTask")
    public Result<JobResult> onLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.onLineTask(id);
    }

    /** 下线任务 */
    @GetMapping("/offLineTask")
    public Result<Void> offLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.offLineTask(id, null);
    }

    /** 重新上线任务 */
    @GetMapping("/reOnLineTask")
    public Result<JobResult> reOnLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.reOnLineTask(id, null);
    }

    /** 选择保存点重新上线任务 */
    @GetMapping("/selectSavePointReOnLineTask")
    public Result<JobResult> selectSavePointReOnLineTask(
            @RequestParam Integer id, @RequestParam String savePointPath) {
        taskService.initTenantByTaskId(id);
        return taskService.reOnLineTask(id, savePointPath);
    }

    /** 获取Job实例的信息 */
    @GetMapping("/getJobInstance")
    public Result<JobInstance> getJobInstance(@RequestParam Integer id) {
        jobInstanceService.initTenantByJobInstanceId(id);
        return Result.succeed(jobInstanceService.getById(id), "获取成功");
    }

    /** 通过 taskId 获取 Task 对应的 Job 实例的信息 */
    @GetMapping("/getJobInstanceByTaskId")
    public Result<JobInstance> getJobInstanceByTaskId(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(jobInstanceService.getJobInstanceByTaskId(id), "获取成功");
    }
}
