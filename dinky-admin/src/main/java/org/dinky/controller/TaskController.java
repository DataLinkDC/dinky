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

import org.dinky.data.annotations.ExecuteProcess;
import org.dinky.data.annotations.Log;
import org.dinky.data.annotations.ProcessId;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.dto.TaskSaveDTO;
import org.dinky.data.dto.TaskSubmitDto;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.ProcessType;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.exception.SqlExplainExcepition;
import org.dinky.data.model.Task;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobResult;
import org.dinky.service.TaskService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Task Controller")
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/submitTask")
    @ApiOperation("Submit Task")
    @Log(title = "Submit Task", businessType = BusinessType.SUBMIT)
    @ExecuteProcess(type = ProcessType.FLINK_SUBMIT)
    public Result<JobResult> submitTask(@ProcessId @RequestParam Integer id) throws Exception {
        JobResult jobResult =
                taskService.submitTask(TaskSubmitDto.builder().id(id).build());
        if (jobResult.isSuccess()) {
            return Result.succeed(jobResult, Status.EXECUTE_SUCCESS);
        } else {
            return Result.failed(jobResult, jobResult.getError());
        }
    }

    @PostMapping("/debugTask")
    @ApiOperation("Debug Task")
    @Log(title = "Debug Task", businessType = BusinessType.DEBUG)
    @ApiImplicitParam(
            name = "debugTask",
            value = "Debug Task",
            required = true,
            dataType = "DebugDTO",
            paramType = "body")
    @ExecuteProcess(type = ProcessType.FLINK_SUBMIT)
    public Result<JobResult> debugTask(@RequestBody TaskDTO task) throws Exception {
        JobResult result = taskService.debugTask(task);
        if (result.isSuccess()) {
            return Result.succeed(result, Status.DEBUG_SUCCESS);
        }
        return Result.failed(result, Status.DEBUG_FAILED);
    }

    @GetMapping("/cancel")
    @Log(title = "Cancel Flink Job", businessType = BusinessType.TRIGGER)
    @ApiOperation("Cancel Flink Job")
    public Result<Void> cancel(
            @RequestParam Integer id,
            @RequestParam(defaultValue = "false") boolean withSavePoint,
            @RequestParam(defaultValue = "false") boolean forceCancel) {
        if (taskService.cancelTaskJob(taskService.getTaskInfoById(id), withSavePoint, forceCancel)) {
            return Result.succeed(Status.EXECUTE_SUCCESS);
        } else {
            return Result.failed(Status.EXECUTE_FAILED);
        }
    }

    /**
     * 重启任务
     */
    @GetMapping(value = "/restartTask")
    @ApiOperation("Restart Task")
    @Log(title = "Restart Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<JobResult> restartTask(@RequestParam Integer id, String savePointPath) throws Exception {
        JobResult jobResult = taskService.restartTask(id, savePointPath);
        if (jobResult.isSuccess()) {
            return Result.succeed(jobResult, Status.RESTART_SUCCESS);
        }
        return Result.failed(jobResult, Status.RESTART_FAILED);
    }

    @GetMapping("/savepoint")
    @Log(title = "Savepoint Trigger", businessType = BusinessType.TRIGGER)
    @ApiOperation("Savepoint Trigger")
    public Result<SavePointResult> savepoint(@RequestParam Integer taskId, @RequestParam String savePointType) {
        return Result.succeed(
                taskService.savepointTaskJob(
                        taskService.getTaskInfoById(taskId), SavePointType.valueOf(savePointType.toUpperCase())),
                Status.EXECUTE_SUCCESS);
    }

    @GetMapping("/changeTaskLife")
    @Log(title = "changeTaskLife", businessType = BusinessType.TRIGGER)
    @ApiOperation("changeTaskLife")
    public Result<Boolean> changeTaskLife(@RequestParam Integer taskId, @RequestParam Integer lifeCycle)
            throws SqlExplainExcepition {
        if (taskService.changeTaskLifeRecyle(taskId, JobLifeCycle.get(lifeCycle))) {
            return Result.succeed(lifeCycle == 2 ? Status.PUBLISH_SUCCESS : Status.OFFLINE_SUCCESS);
        } else {
            return Result.failed(lifeCycle == 2 ? Status.PUBLISH_FAILED : Status.OFFLINE_FAILED);
        }
    }

    @PostMapping("/explainSql")
    @ApiOperation("Explain Sql")
    public Result<List<SqlExplainResult>> explainSql(@RequestBody TaskDTO taskDTO) throws NotSupportExplainExcepition {
        return Result.succeed(taskService.explainTask(taskDTO), Status.EXECUTE_SUCCESS);
    }

    @PostMapping("/getJobPlan")
    @ApiOperation("Get Job Plan")
    @ExecuteProcess(type = ProcessType.FLINK_JOB_PLAN)
    public Result<ObjectNode> getJobPlan(@RequestBody TaskDTO taskDTO) {
        ObjectNode jobPlan = null;
        jobPlan = taskService.getJobPlan(taskDTO);
        return Result.succeed(jobPlan, Status.EXECUTE_SUCCESS);
    }

    @PutMapping
    @ApiOperation("Insert Or Update Task")
    @Log(title = "Insert Or Update Task", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "task",
            value = "Task",
            required = true,
            dataType = "TaskSaveDTO",
            paramType = "body",
            dataTypeClass = TaskSaveDTO.class)
    public Result<Void> saveOrUpdateTask(@RequestBody TaskSaveDTO task) {
        if (taskService.saveOrUpdateTask(task.toTaskEntity())) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    @PostMapping
    @ApiOperation("Query Task List")
    @ApiImplicitParam(
            name = "para",
            value = "Query Condition",
            required = true,
            dataType = "JsonNode",
            paramType = "body",
            dataTypeClass = JsonNode.class)
    public ProTableResult<Task> listTasks(@RequestBody JsonNode para) {
        return taskService.selectForProTable(para);
    }

    @GetMapping
    @ApiOperation("Get Task Info By Id")
    @ApiImplicitParam(
            name = "id",
            value = "Task Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    public Result<TaskDTO> getOneById(@RequestParam Integer id) {
        return Result.succeed(taskService.getTaskInfoById(id));
    }

    @GetMapping(value = "/listFlinkSQLEnv")
    @ApiOperation("Get All FlinkSQLEnv")
    public Result<List<Task>> listFlinkSQLEnv() {
        return Result.succeed(taskService.listFlinkSQLEnv());
    }

    @PostMapping("/rollbackTask")
    @ApiOperation("Rollback Task")
    @Log(title = "Rollback Task", businessType = BusinessType.UPDATE)
    public Result<Void> rollbackTask(@RequestBody TaskRollbackVersionDTO dto) {
        if (taskService.rollbackTask(dto)) {
            return Result.succeed(Status.VERSION_ROLLBACK_SUCCESS);
        }
        return Result.failed(Status.VERSION_ROLLBACK_FAILED);
    }

    @GetMapping(value = "/getTaskAPIAddress")
    @ApiOperation("Get Task API Address")
    public Result<String> getTaskAPIAddress() {
        return Result.succeed(taskService.getTaskAPIAddress(), Status.RESTART_SUCCESS);
    }

    @GetMapping(value = "/exportJsonByTaskId")
    @ApiOperation("Export Task To Sign Json")
    @Log(title = "Export Task To Sign Json", businessType = BusinessType.EXPORT)
    public Result<String> exportJsonByTaskId(@RequestParam Integer id) {
        return Result.succeed(taskService.exportJsonByTaskId(id));
    }

    @PostMapping(value = "/exportJsonByTaskIds")
    @ApiOperation("Export Task To Array Json")
    @Log(title = "Export Task To Array Json", businessType = BusinessType.EXPORT)
    public Result<String> exportJsonByTaskIds(@RequestBody JsonNode para) {
        return Result.succeed(taskService.exportJsonByTaskIds(para));
    }

    @PostMapping(value = "/uploadTaskJson")
    @ApiOperation("Upload Task Json")
    @Log(title = "Upload Task Json", businessType = BusinessType.UPLOAD)
    public Result<Void> uploadTaskJson(@RequestParam("file") MultipartFile file) throws Exception {
        return taskService.uploadTaskJson(file);
    }

    @GetMapping("/queryAllCatalogue")
    @ApiOperation("Query All Catalogue")
    public Result<Tree<Integer>> queryAllCatalogue() {
        return taskService.queryAllCatalogue();
    }
}
