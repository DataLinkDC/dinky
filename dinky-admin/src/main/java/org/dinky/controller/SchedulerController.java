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
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.Result;
import org.dinky.init.SystemInit;
import org.dinky.scheduler.client.ProcessClient;
import org.dinky.scheduler.client.TaskClient;
import org.dinky.scheduler.enums.ReleaseState;
import org.dinky.scheduler.exception.SchedulerException;
import org.dinky.scheduler.model.DagData;
import org.dinky.scheduler.model.DinkyTaskParams;
import org.dinky.scheduler.model.ProcessDefinition;
import org.dinky.scheduler.model.Project;
import org.dinky.scheduler.model.TaskDefinition;
import org.dinky.scheduler.model.TaskMainInfo;
import org.dinky.scheduler.model.TaskRequest;
import org.dinky.service.CatalogueService;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Strings;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scheduler")
@Api(tags = "DolphinScheduler Controller")
@RequiredArgsConstructor
public class SchedulerController {

    public static final String TASK_TYPE = "DINKY";
    private final ProcessClient processClient;
    private final TaskClient taskClient;
    private final CatalogueService catalogueService;

    /** 获取任务定义 */
    @GetMapping("/task")
    @ApiOperation("Get Task Definition")
    @ApiImplicitParam(
            name = "dinkyTaskId",
            value = "Dinky Task id",
            required = true,
            dataType = "Long",
            paramType = "query",
            example = "1")
    public Result<TaskDefinition> getTaskDefinition(@ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId) {
        Catalogue catalogue =
                catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed(Status.DS_GET_NODE_LIST_ERROR);
        }

        Project dinkyProject = SystemInit.getProject();
        long projectCode = dinkyProject.getCode();

        String processName = getDinkyNames(catalogue, 0);
        String taskName = catalogue.getName() + ":" + catalogue.getId();
        TaskMainInfo taskMainInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName);
        TaskDefinition taskDefinition = null;
        if (taskMainInfo == null) {
            return Result.succeed(taskDefinition);
        }

        taskDefinition = taskClient.getTaskDefinition(projectCode, taskMainInfo.getTaskCode());
        if (taskDefinition == null) {
            return Result.failed(Status.DS_WORK_FLOW_NOT_SAVE);
        }

        taskDefinition.setProcessDefinitionCode(taskMainInfo.getProcessDefinitionCode());
        taskDefinition.setProcessDefinitionName(taskMainInfo.getProcessDefinitionName());
        taskDefinition.setProcessDefinitionVersion(taskMainInfo.getProcessDefinitionVersion());
        taskDefinition.setUpstreamTaskMap(taskMainInfo.getUpstreamTaskMap());
        return Result.succeed(taskDefinition);
    }

    /** 获取前置任务定义集合 */
    @GetMapping("/upstream/tasks")
    @ApiOperation("Get Upstream Task Definition")
    @ApiImplicitParam(
            name = "dinkyTaskId",
            value = "Dinky Task id",
            required = true,
            dataType = "Long",
            paramType = "query",
            example = "1")
    public Result<List<TaskMainInfo>> getTaskMainInfos(@ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId) {
        Catalogue catalogue =
                catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed(Status.DS_GET_NODE_LIST_ERROR);
        }

        long projectCode = SystemInit.getProject().getCode();
        String processName = getDinkyNames(catalogue, 0);
        List<TaskMainInfo> taskMainInfos = taskClient.getTaskMainInfos(projectCode, processName, "");
        // 去掉本身
        taskMainInfos.removeIf(taskMainInfo ->
                (catalogue.getName() + ":" + catalogue.getId()).equalsIgnoreCase(taskMainInfo.getTaskName()));

        return Result.succeed(taskMainInfos);
    }

    /** 创建任务定义 */
    @PostMapping("/task")
    @ApiOperation("Create Task Definition")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "dinkyTaskId",
                value = "Dinky Task id",
                required = true,
                dataType = "Long",
                paramType = "query",
                example = "1"),
        @ApiImplicitParam(
                name = "upstreamCodes",
                value = "Upstream Task Codes",
                required = false,
                dataType = "String",
                paramType = "query",
                example = "1,2,3"),
        @ApiImplicitParam(
                name = "taskRequest",
                value = "Task Request",
                required = true,
                dataType = "TaskRequest",
                paramType = "body"),
    })
    public Result<String> createTaskDefinition(
            @ApiParam(value = "前置任务编号 逗号隔开") @RequestParam(required = false) String upstreamCodes,
            @ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId,
            @Valid @RequestBody TaskRequest taskRequest) {
        DinkyTaskParams dinkyTaskParams = new DinkyTaskParams();
        dinkyTaskParams.setTaskId(dinkyTaskId.toString());
        dinkyTaskParams.setAddress(
                SystemConfiguration.getInstances().getDinkyAddr().getValue());
        taskRequest.setTaskParams(JSONUtil.parseObj(dinkyTaskParams).toString());
        taskRequest.setTaskType(TASK_TYPE);

        Catalogue catalogue =
                catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed(Status.DS_GET_NODE_LIST_ERROR);
        }

        String processName = getDinkyNames(catalogue, 0);
        long projectCode = SystemInit.getProject().getCode();
        ProcessDefinition process = processClient.getProcessDefinitionInfo(projectCode, processName);

        String taskName = catalogue.getName() + ":" + catalogue.getId();
        taskRequest.setName(taskName);
        if (process == null) {
            Long taskCode = taskClient.genTaskCode(projectCode);
            taskRequest.setCode(taskCode);
            JSONObject jsonObject = JSONUtil.parseObj(taskRequest);
            JSONArray array = new JSONArray();
            array.set(jsonObject);
            processClient.createProcessDefinition(projectCode, processName, taskCode, array.toString());
            return Result.succeed(Status.DS_ADD_WORK_FLOW_DEFINITION_SUCCESS);
        }

        if (process.getReleaseState() == ReleaseState.ONLINE) {
            return Result.failed(Status.DS_WORK_FLOW_DEFINITION_ONLINE, (Object) processName);
        }

        TaskMainInfo taskDefinitionInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName);
        if (taskDefinitionInfo != null) {
            return Result.failed(Status.DS_WORK_FLOW_DEFINITION_TASK_NAME_EXIST, processName, taskName);
        }

        Long taskCode = taskClient.genTaskCode(projectCode);
        taskRequest.setCode(taskCode);

        String taskDefinitionJsonObj = JSONUtil.toJsonStr(taskRequest);
        taskClient.createTaskDefinition(projectCode, process.getCode(), upstreamCodes, taskDefinitionJsonObj);

        return Result.succeed(Status.DS_ADD_TASK_DEFINITION_SUCCESS);
    }

    /** 更新任务定义 */
    @PutMapping("/task")
    @ApiOperation("Update Task Definition")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "projectCode",
                value = "Project Code",
                required = true,
                dataType = "Long",
                paramType = "query",
                example = "1"),
        @ApiImplicitParam(
                name = "processCode",
                value = "Process Code",
                required = true,
                dataType = "Long",
                paramType = "query",
                example = "1"),
        @ApiImplicitParam(
                name = "taskCode",
                value = "Task Code",
                required = true,
                dataType = "Long",
                paramType = "query",
                example = "1"),
        @ApiImplicitParam(
                name = "upstreamCodes",
                value = "Upstream Task Codes",
                required = false,
                dataType = "String",
                paramType = "query",
                example = "1,2,3")
    })
    public Result<String> updateTaskDefinition(
            @ApiParam(value = "项目编号") @RequestParam long projectCode,
            @ApiParam(value = "工作流定义编号") @RequestParam long processCode,
            @ApiParam(value = "任务定义编号") @RequestParam long taskCode,
            @ApiParam(value = "前置任务编号 逗号隔开") @RequestParam(required = false) String upstreamCodes,
            @Valid @RequestBody TaskRequest taskRequest) {

        TaskDefinition taskDefinition = taskClient.getTaskDefinition(projectCode, taskCode);
        if (taskDefinition == null) {
            return Result.failed(Status.DS_TASK_NOT_EXIST);
        }

        if (!TASK_TYPE.equals(taskDefinition.getTaskType())) {
            return Result.failed(Status.DS_TASK_TYPE_NOT_SUPPORT, (Object) taskDefinition.getTaskType());
        }

        DagData dagData = processClient.getProcessDefinitionInfo(projectCode, processCode);
        if (dagData == null) {
            return Result.failed(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST);
        }

        ProcessDefinition process = dagData.getProcessDefinition();
        if (process == null) {
            return Result.failed(Status.DS_WORK_FLOW_DEFINITION_NOT_EXIST);
        }

        if (process.getReleaseState() == ReleaseState.ONLINE) {
            return Result.failed(Status.DS_WORK_FLOW_DEFINITION_ONLINE, (Object) process.getName());
        }

        taskRequest.setName(taskDefinition.getName());
        taskRequest.setTaskParams(taskDefinition.getTaskParams());
        taskRequest.setTaskType(TASK_TYPE);

        String taskDefinitionJsonObj = JSONUtil.toJsonStr(taskRequest);
        taskClient.updateTaskDefinition(projectCode, taskCode, upstreamCodes, taskDefinitionJsonObj);
        return Result.succeed(Status.MODIFY_SUCCESS);
    }

    private String getDinkyNames(Catalogue catalogue, int i) {
        if (i == 3 || catalogue.getParentId().equals(0)) {
            return "";
        }

        catalogue = catalogueService.getById(catalogue.getParentId());
        if (catalogue == null) {
            throw new SchedulerException("Get Node List Error");
        }

        String name = i == 0 ? catalogue.getName() + ":" + catalogue.getId() : catalogue.getName();
        String next = getDinkyNames(catalogue, ++i);

        if (Strings.isNullOrEmpty(next)) {
            return name;
        }
        return name + "_" + next;
    }
}
