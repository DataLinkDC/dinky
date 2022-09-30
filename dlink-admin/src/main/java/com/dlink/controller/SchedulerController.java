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
import com.dlink.init.SystemInit;
import com.dlink.model.Catalogue;
import com.dlink.scheduler.client.ProcessClient;
import com.dlink.scheduler.client.TaskClient;
import com.dlink.scheduler.config.DolphinSchedulerProperties;
import com.dlink.scheduler.enums.ReleaseState;
import com.dlink.scheduler.exception.SchedulerException;
import com.dlink.scheduler.model.DagData;
import com.dlink.scheduler.model.DlinkTaskParams;
import com.dlink.scheduler.model.ProcessDefinition;
import com.dlink.scheduler.model.Project;
import com.dlink.scheduler.model.TaskDefinition;
import com.dlink.scheduler.model.TaskMainInfo;
import com.dlink.scheduler.model.TaskRequest;
import com.dlink.service.CatalogueService;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author 郑文豪
 */
@RestController
@RequestMapping("/api/scheduler")
@Api(value = "海豚调度", tags = "海豚调度")
public class SchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @Autowired
    private DolphinSchedulerProperties dolphinSchedulerProperties;
    @Autowired
    private ProcessClient processClient;
    @Autowired
    private TaskClient taskClient;
    @Autowired
    private CatalogueService catalogueService;

    /**
     * 获取任务定义
     */
    @GetMapping("/task")
    @ApiOperation(value = "获取任务定义", notes = "获取任务定义")
    public Result<TaskDefinition> getTaskDefinition(@ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId) {
        TaskDefinition taskDefinition = null;
        Project dinkyProject = SystemInit.getProject();

        Catalogue catalogue = catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed("节点获取失败");
        }

        List<String> lists = Lists.newArrayList();
        getDinkyNames(lists, catalogue, 0);
        Collections.reverse(lists);
        String processName = StringUtils.join(lists, "_");
        String taskName = catalogue.getName() + ":" + catalogue.getId();

        long projectCode = dinkyProject.getCode();
        TaskMainInfo taskMainInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName);

        if (taskMainInfo != null) {
            taskDefinition = taskClient.getTaskDefinition(projectCode, taskMainInfo.getTaskCode());

            if (taskDefinition != null) {
                taskDefinition.setProcessDefinitionCode(taskMainInfo.getProcessDefinitionCode());
                taskDefinition.setProcessDefinitionName(taskMainInfo.getProcessDefinitionName());
                taskDefinition.setProcessDefinitionVersion(taskMainInfo.getProcessDefinitionVersion());
                taskDefinition.setUpstreamTaskMap(taskMainInfo.getUpstreamTaskMap());
            } else {
                return Result.failed("请先工作流保存");
            }
        }
        return Result.succeed(taskDefinition);
    }

    /**
     * 获取前置任务定义集合
     */
    @GetMapping("/upstream/tasks")
    @ApiOperation(value = "获取前置任务定义集合", notes = "获取前置任务定义集合")
    public Result<List<TaskMainInfo>> getTaskMainInfos(@ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId) {

        Project dinkyProject = SystemInit.getProject();

        Catalogue catalogue = catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed("节点获取失败");
        }

        List<String> lists = Lists.newArrayList();
        getDinkyNames(lists, catalogue, 0);
        Collections.reverse(lists);
        String processName = StringUtils.join(lists, "_");

        long projectCode = dinkyProject.getCode();

        List<TaskMainInfo> taskMainInfos = taskClient.getTaskMainInfos(projectCode, processName, "");
        //去掉本身
        taskMainInfos.removeIf(taskMainInfo -> (catalogue.getName() + ":" + catalogue.getId()).equalsIgnoreCase(taskMainInfo.getTaskName()));

        return Result.succeed(taskMainInfos);
    }

    /**
     * 创建任务定义
     */
    @PostMapping("/task")
    @ApiOperation(value = "创建任务定义", notes = "创建任务定义")
    public Result<String> createTaskDefinition(@ApiParam(value = "前置任务编号 逗号隔开") @RequestParam(required = false) String upstreamCodes,
                                               @ApiParam(value = "dinky任务id") @RequestParam Long dinkyTaskId,
                                               @Valid @RequestBody TaskRequest taskRequest) {
        DlinkTaskParams dlinkTaskParams = new DlinkTaskParams();
        dlinkTaskParams.setTaskId(dinkyTaskId.toString());
        dlinkTaskParams.setAddress(dolphinSchedulerProperties.getAddress());
        taskRequest.setTaskParams(JSONUtil.parseObj(dlinkTaskParams).toString());
        taskRequest.setTaskType("DINKY");

        Project dinkyProject = SystemInit.getProject();

        Catalogue catalogue = catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed("节点获取失败");
        }

        List<String> lists = Lists.newArrayList();
        getDinkyNames(lists, catalogue, 0);
        Collections.reverse(lists);
        String processName = StringUtils.join(lists, "_");
        String taskName = catalogue.getName() + ":" + catalogue.getId();

        long projectCode = dinkyProject.getCode();
        ProcessDefinition process = processClient.getProcessDefinitionInfo(projectCode, processName);
        taskRequest.setName(taskName);
        if (process == null) {
            Long taskCode = taskClient.genTaskCode(projectCode);
            taskRequest.setCode(taskCode);
            JSONObject jsonObject = JSONUtil.parseObj(taskRequest);
            JSONArray array = new JSONArray();
            array.set(jsonObject);
            processClient.createProcessDefinition(projectCode, processName, taskCode, array.toString());

            return Result.succeed("添加工作流定义成功");
        } else {
            if (process.getReleaseState() == ReleaseState.ONLINE) {
                return Result.failed("工作流定义 [" + processName + "] 已经上线已经上线");
            }
            long processCode = process.getCode();
            TaskMainInfo taskDefinitionInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName);
            if (taskDefinitionInfo != null) {
                return Result.failed("添加失败,工作流定义[" + processName + "]已存在任务定义[" + taskName + "] 请刷新");
            }

            String taskDefinitionJsonObj = JSONUtil.toJsonStr(taskRequest);
            taskClient.createTaskDefinition(projectCode, processCode, upstreamCodes, taskDefinitionJsonObj);

            return Result.succeed("添加任务定义成功");
        }

    }

    /**
     * 更新任务定义
     */
    @PutMapping("/task")
    @ApiOperation(value = "更新任务定义", notes = "更新任务定义")
    public Result<String> updateTaskDefinition(@ApiParam(value = "项目编号") @RequestParam long projectCode,
                                               @ApiParam(value = "工作流定义编号") @RequestParam long processCode,
                                               @ApiParam(value = "任务定义编号") @RequestParam long taskCode,
                                               @ApiParam(value = "前置任务编号 逗号隔开") @RequestParam(required = false) String upstreamCodes,
                                               @Valid @RequestBody TaskRequest taskRequest) {

        TaskDefinition taskDefinition = taskClient.getTaskDefinition(projectCode, taskCode);
        if (taskDefinition == null) {
            return Result.failed("任务不存在");
        }
        if (!"DINKY".equals(taskDefinition.getTaskType())) {
            return Result.failed("海豚调度类型为[" + taskDefinition.getTaskType() + "] 不支持,非DINKY类型");
        }
        DagData dagData = processClient.getProcessDefinitionInfo(projectCode, processCode);
        if (dagData == null) {
            return Result.failed("工作流定义不存在");
        }
        ProcessDefinition process = dagData.getProcessDefinition();
        if (process == null) {
            return Result.failed("工作流定义不存在");
        }
        if (process.getReleaseState() == ReleaseState.ONLINE) {
            return Result.failed("工作流定义 [" + process.getName() + "] 已经上线");
        }

        taskRequest.setName(taskDefinition.getName());
        taskRequest.setTaskParams(taskDefinition.getTaskParams());
        taskRequest.setTaskType("DINKY");

        String taskDefinitionJsonObj = JSONUtil.toJsonStr(taskRequest);
        taskClient.updateTaskDefinition(projectCode, taskCode, upstreamCodes, taskDefinitionJsonObj);
        return Result.succeed("修改成功");
    }

    private void getDinkyNames(List<String> lists, Catalogue catalogue, int i) {
        if (i == 3) {
            return;
        }
        if (catalogue.getParentId().equals(0)) {
            return;
        }
        catalogue = catalogueService.getById(catalogue.getParentId());
        if (catalogue == null) {
            throw new SchedulerException("节点获取失败");
        }
        if (i == 0) {
            lists.add(catalogue.getName() + ":" + catalogue.getId());
        } else {
            lists.add(catalogue.getName());
        }
        getDinkyNames(lists, catalogue, ++i);
    }
}
