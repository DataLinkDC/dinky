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
import org.dinky.data.annotation.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.JobInfoDetail;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.JobManagerConfiguration;
import org.dinky.data.model.TaskManagerConfiguration;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.job.BuildConfiguration;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.lang.Dict;
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
@RequestMapping("/api/jobInstance")
@RequiredArgsConstructor
public class JobInstanceController {

    private final JobInstanceService jobInstanceService;
    private final TaskService taskService;

    /** 动态查询列表 */
    @PostMapping
    public ProTableResult<JobInstance> listJobInstances(@RequestBody JsonNode para) {
        return jobInstanceService.listJobInstances(para);
    }

    /** 获取状态统计信息 */
    @GetMapping("/getStatusCount")
    @ApiOperation("Get status count")
    public Result<Dict> getStatusCount() {
        Dict result = Dict.create()
                .set("history", jobInstanceService.getStatusCount(true))
                .set("instance", jobInstanceService.getStatusCount(false));
        return Result.succeed(result);
    }

    /** 获取Job实例的所有信息 */
    @GetMapping("/getJobInfoDetail")
    @ApiOperation("Get job info detail")
    public Result<JobInfoDetail> getJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getJobInfoDetail(id));
    }

    /** 刷新Job实例的所有信息 */
    @GetMapping("/refreshJobInfoDetail")
    @ApiOperation("Refresh job info detail")
    @Log(title = "Refresh job info detail", businessType = BusinessType.UPDATE)
    public Result<JobInfoDetail> refreshJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(taskService.refreshJobInfoDetail(id), Status.RESTART_SUCCESS);
    }

    /** 获取单任务实例的血缘分析 */
    @GetMapping("/getLineage")
    @ApiOperation("Get lineage of a single task instance")
    public Result<LineageResult> getLineage(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getLineage(id), Status.RESTART_SUCCESS);
    }

    /** 获取 JobManager 的信息 */
    @GetMapping("/getJobManagerInfo")
    @ApiOperation("Get job manager info")
    public Result<JobManagerConfiguration> getJobManagerInfo(@RequestParam String address) {
        JobManagerConfiguration jobManagerConfiguration = new JobManagerConfiguration();
        if (Asserts.isNotNullString(address)) {
            BuildConfiguration.buildJobManagerConfiguration(jobManagerConfiguration, FlinkAPI.build(address));
        }
        return Result.succeed(jobManagerConfiguration);
    }

    @GetMapping("/getJobManagerLog")
    @ApiOperation("Get job manager log")
    public Result<String> getJobManagerLog(@RequestParam String address) {
        return Result.succeed(FlinkAPI.build(address).getJobManagerLog(),"");
    }

    @GetMapping("/getJobManagerStdOut")
    @ApiOperation("Get job manager stdout")
    public Result<String> getJobManagerStdOut(@RequestParam String address) {
        return Result.succeed(FlinkAPI.build(address).getJobManagerStdOut(),"");
    }


    @GetMapping("/getJobManagerThreadDump")
    @ApiOperation("Get job manager ThreadDump")
    public Result<String> getJobManagerThreadDump(@RequestParam String address) {
        return Result.succeed(FlinkAPI.build(address).getJobManagerThreadDump(),"");
    }

    @GetMapping("/getTaskManagerList")
    @ApiOperation("Get task manager List")
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

    /** 获取 TaskManager 的信息 */
    @GetMapping("/getTaskManagerLog")
    @ApiOperation("Get task manager log")
    public Result<String> getTaskManagerLog(@RequestParam String address,@RequestParam String containerId) {
        return Result.succeed(FlinkAPI.build(address).getTaskManagerLog(containerId),"");
    }
}
