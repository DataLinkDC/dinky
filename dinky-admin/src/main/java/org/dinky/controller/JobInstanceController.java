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

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.data.annotations.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.ID;
import org.dinky.data.model.devops.TaskManagerConfiguration;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.home.JobInstanceStatus;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.data.vo.task.JobInstanceVo;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.service.JobInstanceService;
import org.dinky.utils.BuildConfiguration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JobInstanceController
 *
 * @since 2022/2/2 14:02
 */
@Slf4j
@RestController
@Api(tags = "Job Instance Controller")
@RequestMapping("/api/jobInstance")
@RequiredArgsConstructor
public class JobInstanceController {
    private final JobInstanceService jobInstanceService;

    @PutMapping
    @Log(title = "update JobInstance Job Id", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("update JobInstance Job Id")
    public Result<Void> updateJobInstanceJobId(@RequestBody JobInstance jobInstance) {
        boolean updated = jobInstanceService.updateById(jobInstance);
        if (updated) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    @ApiImplicitParam(
            name = "para",
            value = "Query parameters",
            dataType = "JsonNode",
            paramType = "body",
            required = true,
            dataTypeClass = JsonNode.class)
    public ProTableResult<JobInstanceVo> listJobInstances(@RequestBody JsonNode para) {
        return jobInstanceService.listJobInstances(para);
    }

    /**
     * query job instance by task id
     * @param taskId task id
     * @return {@link Result}< {@link JobInstance} >
     */
    @GetMapping("/getJobInstanceByTaskId")
    @ApiImplicitParam(
            name = "TaskId",
            value = "TaskId",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class)
    public Result<JobInstance> getJobInstanceByTaskId(@RequestParam("taskId") Integer taskId) {
        return Result.succeed(jobInstanceService.getJobInstanceByTaskId(taskId));
    }

    /**
     * 获取状态统计信息
     */
    @GetMapping("/getStatusCount")
    @ApiOperation("Get status count")
    public Result<JobInstanceStatus> getStatusCount() {
        return Result.succeed(jobInstanceService.getStatusCount());
    }

    /**
     * 获取Job实例的所有信息
     */
    @GetMapping("/getJobInfoDetail")
    @ApiOperation("Get job info detail")
    @ApiImplicitParam(
            name = "id",
            value = "Job instance id",
            dataType = "Integer",
            paramType = "query",
            required = true)
    public Result<JobInfoDetail> getJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getJobInfoDetail(id));
    }

    @PostMapping("/getOneById")
    @ApiOperation("Get job instance info by job instance id")
    @ApiImplicitParam(
            name = "id",
            value = "Job instance id",
            dataType = "Integer",
            paramType = "query",
            required = true)
    public Result getOneById(@RequestBody ID id) {
        return Result.succeed(jobInstanceService.getById(id.getId()));
    }

    /**
     * 刷新Job实例的所有信息
     */
    @GetMapping("/refreshJobInfoDetail")
    @ApiOperation("Refresh job info detail")
    @Log(title = "Refresh job info detail", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(
            name = "id",
            value = "Job instance id",
            dataType = "Integer",
            paramType = "query",
            required = true)
    public Result<JobInfoDetail> refreshJobInfoDetail(
            @RequestParam Integer id, @RequestParam(defaultValue = "false") boolean isForce) {
        return Result.succeed(jobInstanceService.refreshJobInfoDetail(id, isForce));
    }

    /**
     * 获取单任务实例的血缘分析
     */
    @GetMapping("/getLineage")
    @ApiOperation("Get lineage of a single task instance")
    @ApiImplicitParam(
            name = "id",
            value = "Task instance id",
            dataType = "Integer",
            paramType = "query",
            required = true)
    public Result<LineageResult> getLineage(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getLineage(id));
    }

    @GetMapping("/getJobManagerLog")
    @ApiOperation("Get job manager log")
    @ApiImplicitParam(
            name = "address",
            value = "JobManager address",
            dataType = "String",
            paramType = "query",
            required = true)
    public Result<String> getJobManagerLog(@RequestParam String address) {
        return Result.succeed(FlinkAPI.build(address).getJobManagerLog(), "");
    }

    @GetMapping("/getJobManagerStdOut")
    @ApiOperation("Get job manager stdout")
    @ApiImplicitParam(
            name = "address",
            value = "JobManager address",
            dataType = "String",
            paramType = "query",
            required = true)
    public Result<String> getJobManagerStdOut(@RequestParam String address) {
        return Result.succeed(FlinkAPI.build(address).getJobManagerStdOut(), "");
    }

    @GetMapping("/getJobManagerThreadDump")
    @ApiOperation("Get job manager ThreadDump")
    @ApiImplicitParam(
            name = "address",
            value = "JobManager address",
            dataType = "String",
            paramType = "query",
            required = true)
    public Result<String> getJobManagerThreadDump(@RequestParam String address) {
        return Result.succeed(FlinkAPI.build(address).getJobManagerThreadDump(), "");
    }

    @GetMapping("/getTaskManagerList")
    @ApiOperation("Get task manager List")
    @ApiImplicitParam(
            name = "address",
            value = "JobManager address",
            dataType = "String",
            paramType = "query",
            required = true)
    public Result<Set<TaskManagerConfiguration>> getTaskManagerList(@RequestParam String address) {
        Set<TaskManagerConfiguration> taskManagerConfigurationList = new HashSet<>();
        if (Asserts.isNotNullString(address)) {
            FlinkAPI flinkAPI = FlinkAPI.build(address);
            JsonNode taskManagerContainers = flinkAPI.getTaskManagers();
            BuildConfiguration.buildTaskManagerConfiguration(
                    taskManagerConfigurationList, flinkAPI, taskManagerContainers);
        }
        return Result.succeed(taskManagerConfigurationList);
    }

    /**
     * 获取 TaskManager 的信息
     */
    @GetMapping("/getTaskManagerLog")
    @ApiOperation("Get task manager log")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "address",
                value = "JobManager address",
                dataType = "String",
                paramType = "query",
                required = true),
        @ApiImplicitParam(
                name = "containerId",
                value = "TaskManager container id",
                dataType = "String",
                paramType = "query",
                required = true)
    })
    public Result<String> getTaskManagerLog(@RequestParam String address, @RequestParam String containerId) {
        return Result.succeed(FlinkAPI.build(address).getTaskManagerLog(containerId), "");
    }

    @GetMapping("/getJobMetricsItems")
    @ApiOperation(" getJobMetricsItems List")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "address",
                value = "JobManager address",
                dataType = "String",
                paramType = "query",
                required = true),
        @ApiImplicitParam(name = "jobId", value = "Job id", dataType = "String", paramType = "query", required = true),
        @ApiImplicitParam(
                name = "verticeId",
                value = "Vertice id",
                dataType = "String",
                paramType = "query",
                required = true)
    })
    public Result<JsonNode> getJobMetricsItems(
            @RequestParam String address, @RequestParam String jobId, @RequestParam String verticeId) {
        return Result.succeed(FlinkAPI.build(address).getJobMetricsItems(jobId, verticeId));
    }

    @GetMapping("/getJobMetricsData")
    @ApiOperation(" getJobMetrics Data")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "address",
                value = "JobManager address",
                dataType = "String",
                paramType = "query",
                required = true),
        @ApiImplicitParam(name = "jobId", value = "Job id", dataType = "String", paramType = "query", required = true),
        @ApiImplicitParam(
                name = "verticeId",
                value = "Vertice id",
                dataType = "String",
                paramType = "query",
                required = true),
        @ApiImplicitParam(
                name = "metrics",
                value = "Metrics",
                dataType = "String",
                paramType = "query",
                required = true)
    })
    public Result<JsonNode> getJobMetricsItems(
            @RequestParam String address,
            @RequestParam String jobId,
            @RequestParam String verticeId,
            @RequestParam String metrics) {
        return Result.succeed(FlinkAPI.build(address).getJobMetricsData(jobId, verticeId, metrics));
    }

    @GetMapping("/hookJobDone")
    @ApiOperation("hookJobDone")
    @SaIgnore
    public Result<Dict> hookJobDone(@RequestParam String jobId, @RequestParam Integer taskId) {
        boolean done = jobInstanceService.hookJobDone(jobId, taskId);
        if (done) {
            return Result.succeed();
        } else {
            return Result.failed();
        }
    }
}
