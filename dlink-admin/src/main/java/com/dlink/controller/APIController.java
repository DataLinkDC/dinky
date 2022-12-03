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

package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.APICancelDTO;
import com.dlink.dto.APIExecuteJarDTO;
import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.dto.APIExplainSqlDTO;
import com.dlink.dto.APISavePointDTO;
import com.dlink.dto.APISavePointTaskDTO;
import com.dlink.service.APIService;
import com.dlink.service.JobInstanceService;
import com.dlink.service.StudioService;
import com.dlink.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * APIController
 *
 * @author wenmo
 * @since 2021/12/11 21:44
 */
@Slf4j
@RestController
@RequestMapping("/openapi")
public class APIController {

    @Autowired
    private APIService apiService;
    @Autowired
    private StudioService studioService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private JobInstanceService jobInstanceService;

    @GetMapping("/submitTask")
    public Result submitTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.submitTask(id), "执行成功");
    }

    @PostMapping("/executeSql")
    public Result executeSql(@RequestBody APIExecuteSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.executeSql(apiExecuteSqlDTO), "执行成功");
    }

    @PostMapping("/explainSql")
    public Result explainSql(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.explainSql(apiExecuteSqlDTO), "执行成功");
    }

    @PostMapping("/getJobPlan")
    public Result getJobPlan(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.getJobPlan(apiExecuteSqlDTO), "执行成功");
    }

    @PostMapping("/getStreamGraph")
    public Result getStreamGraph(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO) {
        return Result.succeed(apiService.getStreamGraph(apiExecuteSqlDTO), "执行成功");
    }

    @GetMapping("/getJobData")
    public Result getJobData(@RequestParam String jobId) {
        return Result.succeed(studioService.getJobData(jobId), "获取成功");
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestBody APICancelDTO apiCancelDTO) {
        return Result.succeed(apiService.cancel(apiCancelDTO), "执行成功");
    }

    @PostMapping("/savepoint")
    public Result savepoint(@RequestBody APISavePointDTO apiSavePointDTO) {
        return Result.succeed(apiService.savepoint(apiSavePointDTO), "执行成功");
    }

    @PostMapping("/executeJar")
    public Result executeJar(@RequestBody APIExecuteJarDTO apiExecuteJarDTO) {
        return Result.succeed(apiService.executeJar(apiExecuteJarDTO), "执行成功");
    }

    @PostMapping("/savepointTask")
    public Result savepointTask(@RequestBody APISavePointTaskDTO apiSavePointTaskDTO) {
        return Result.succeed(taskService.savepointTask(apiSavePointTaskDTO.getTaskId(), apiSavePointTaskDTO.getType()),
                "执行成功");
    }

    /**
     * 重启任务
     */
    @GetMapping("/restartTask")
    public Result restartTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.restartTask(id, null), "重启成功");
    }

    /**
     * 选择保存点重启任务
     */
    @GetMapping("/selectSavePointRestartTask")
    public Result restartTask(@RequestParam Integer id, @RequestParam String savePointPath) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(taskService.restartTask(id, savePointPath), "重启成功");
    }

    /**
     * 上线任务
     */
    @GetMapping("/onLineTask")
    public Result onLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.onLineTask(id);
    }

    /**
     * 下线任务
     */
    @GetMapping("/offLineTask")
    public Result offLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.offLineTask(id, null);
    }

    /**
     * 重新上线任务
     */
    @GetMapping("/reOnLineTask")
    public Result reOnLineTask(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return taskService.reOnLineTask(id, null);
    }

    /**
     * 选择保存点重新上线任务
     */
    @GetMapping("/selectSavePointReOnLineTask")
    public Result selectSavePointReOnLineTask(@RequestParam Integer id, @RequestParam String savePointPath) {
        taskService.initTenantByTaskId(id);
        return taskService.reOnLineTask(id, savePointPath);
    }

    /**
     * 获取Job实例的信息
     */
    @GetMapping("/getJobInstance")
    public Result getJobInstance(@RequestParam Integer id) {
        jobInstanceService.initTenantByJobInstanceId(id);
        return Result.succeed(jobInstanceService.getById(id), "获取成功");
    }

    /**
     * 通过 taskId 获取 Task 对应的 Job 实例的信息
     */
    @GetMapping("/getJobInstanceByTaskId")
    public Result getJobInstanceByTaskId(@RequestParam Integer id) {
        taskService.initTenantByTaskId(id);
        return Result.succeed(jobInstanceService.getJobInstanceByTaskId(id), "获取成功");
    }
}
