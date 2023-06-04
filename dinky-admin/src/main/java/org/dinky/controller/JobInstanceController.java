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
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.job.BuildConfiguration;
import org.dinky.model.JobInfoDetail;
import org.dinky.model.JobInstance;
import org.dinky.model.JobManagerConfiguration;
import org.dinky.model.TaskManagerConfiguration;
import org.dinky.result.ProTableResult;
import org.dinky.result.Result;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.lang.Dict;
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

    /** 批量删除 */
    @DeleteMapping
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!jobInstanceService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /** 获取指定ID的信息 */
    @PostMapping("/getOneById")
    public Result<JobInstance> getOneById(@RequestBody JobInstance jobInstance) throws Exception {
        jobInstance = jobInstanceService.getById(jobInstance.getId());
        return Result.succeed(jobInstance, "获取成功");
    }

    /** 获取状态统计信息 */
    @GetMapping("/getStatusCount")
    public Result<Dict> getStatusCount() {
        Dict result =
                Dict.create()
                        .set("history", jobInstanceService.getStatusCount(true))
                        .set("instance", jobInstanceService.getStatusCount(false));
        return Result.succeed(result, "获取成功");
    }

    /** 获取Job实例的所有信息 */
    @GetMapping("/getJobInfoDetail")
    public Result<JobInfoDetail> getJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getJobInfoDetail(id), "获取成功");
    }

    /** 刷新Job实例的所有信息 */
    @GetMapping("/refreshJobInfoDetail")
    public Result<JobInfoDetail> refreshJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(taskService.refreshJobInfoDetail(id), "刷新成功");
    }

    /** 获取单任务实例的血缘分析 */
    @GetMapping("/getLineage")
    public Result<LineageResult> getLineage(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getLineage(id), "刷新成功");
    }

    /** 获取 JobManager 的信息 */
    @GetMapping("/getJobManagerInfo")
    public Result<JobManagerConfiguration> getJobManagerInfo(@RequestParam String address) {
        JobManagerConfiguration jobManagerConfiguration = new JobManagerConfiguration();
        if (Asserts.isNotNullString(address)) {
            BuildConfiguration.buildJobManagerConfiguration(
                    jobManagerConfiguration, FlinkAPI.build(address));
        }
        return Result.succeed(jobManagerConfiguration, "获取成功");
    }

    /** 获取 TaskManager 的信息 */
    @GetMapping("/getTaskManagerInfo")
    public Result<Set<TaskManagerConfiguration>> getTaskManagerInfo(@RequestParam String address) {
        Set<TaskManagerConfiguration> taskManagerConfigurationList = new HashSet<>();
        if (Asserts.isNotNullString(address)) {
            FlinkAPI flinkAPI = FlinkAPI.build(address);
            JsonNode taskManagerContainers = flinkAPI.getTaskManagers();
            BuildConfiguration.buildTaskManagerConfiguration(
                    taskManagerConfigurationList, flinkAPI, taskManagerContainers);
        }
        return Result.succeed(taskManagerConfigurationList, "获取成功");
    }
}
