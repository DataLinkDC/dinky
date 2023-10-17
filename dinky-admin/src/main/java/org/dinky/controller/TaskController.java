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
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.NotSupportExplainExcepition;
import org.dinky.data.model.Task;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.job.JobResult;
import org.dinky.process.annotations.ExecuteProcess;
import org.dinky.process.annotations.ProcessId;
import org.dinky.process.enums.ProcessType;
import org.dinky.service.TaskService;
import org.dinky.utils.JsonUtils;

import java.util.List;
import java.util.Map;

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
        JobResult jobResult = taskService.submitTask(id, null);
        if (jobResult.isSuccess()) {
            return Result.succeed(jobResult, Status.EXECUTE_SUCCESS);
        } else {
            return Result.failed(jobResult, jobResult.getError());
        }
    }

    @GetMapping("/cancel")
    @Log(title = "Cancel Flink Job", businessType = BusinessType.TRIGGER)
    @ApiOperation("Cancel Flink Job")
    public Result<Boolean> cancel(@RequestParam Integer id) {
        return Result.succeed(taskService.cancelTaskJob(taskService.getTaskInfoById(id)), Status.EXECUTE_SUCCESS);
    }

    /**
     * 重启任务
     */
    @GetMapping(value = "/restartTask")
    @ApiOperation("Restart Task")
    @Log(title = "Restart Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<JobResult> restartTask(@RequestParam Integer id, String savePointPath) throws Exception {
        return Result.succeed(taskService.restartTask(id, savePointPath));
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

    @GetMapping("/onLineTask")
    @Log(title = "onLineTask", businessType = BusinessType.TRIGGER)
    @ApiOperation("onLineTask")
    public Result<Boolean> onLineTask(@RequestParam Integer taskId) {
        return Result.succeed(taskService.changeTaskLifeRecyle(taskId, JobLifeCycle.ONLINE));
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

    @PutMapping
    @ApiOperation("Insert Or Update Task")
    @Log(title = "Insert Or Update Task", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "task",
            value = "Task",
            required = true,
            dataType = "Task",
            paramType = "body",
            dataTypeClass = Task.class)
    public Result<Void> saveOrUpdateTask(@RequestBody Task task) {
        if (taskService.saveOrUpdateTask(task)) {
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

    @PostMapping("/getPrintTables")
    @ApiOperation("Get Print Tables")
    @SuppressWarnings("unchecked")
    @ApiImplicitParam(name = "statement", value = "Statement", dataType = "String", paramType = "body", required = true)
    public Result<List<String>> getPrintTables(@RequestBody String statement) {
        try {
            Map<String, String> data = JsonUtils.toMap(statement);
            String ss = data.get("statement");
            return Result.succeed(taskService.getPrintTables(ss));
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
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
        return taskService.rollbackTask(dto);
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
