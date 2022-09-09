package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.init.SystemInit;
import com.dlink.model.Catalogue;
import com.dlink.scheduler.client.ProcessClient;
import com.dlink.scheduler.client.TaskClient;
import com.dlink.scheduler.exception.SchedulerException;
import com.dlink.scheduler.model.ProcessDefinition;
import com.dlink.scheduler.model.Project;
import com.dlink.scheduler.model.TaskDefinition;
import com.dlink.scheduler.model.TaskMainInfo;
import com.dlink.service.CatalogueService;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starrocks.shade.com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 郑文豪
 */
@Slf4j
@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    @Autowired
    private ProcessClient processClient;

    @Autowired
    private TaskClient taskClient;
    @Autowired
    private CatalogueService catalogueService;

    @GetMapping("/task")
    public Result<TaskDefinition> getTaskDefinition(@RequestParam Long dinkyTaskId) {
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
        String taskName = catalogue.getName();

        long projectCode = dinkyProject.getCode();
        TaskMainInfo taskDefinitionInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName);

        if (taskDefinitionInfo != null) {
            taskDefinition = taskClient.getTaskDefinition(projectCode, taskDefinitionInfo.getTaskCode());
        }
        return Result.succeed(taskDefinition);
    }

    /**
     * 获取任务定义集合
     */
    @GetMapping("/tasks")
    public Result<List<TaskMainInfo>> getTaskMainInfos(@RequestParam Long dinkyTaskId) {

        List<TaskMainInfo> taskMainInfos = Lists.newArrayList();
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

        taskMainInfos = taskClient.getTaskMainInfos(projectCode, processName, "");
        return Result.succeed(taskMainInfos);
    }

    /**
     * 创建任务定义
     */
    @PostMapping("/task")
    public Result<String> createTaskDefinition(@RequestParam(required = false) Long upstreamCodes,
                                               @RequestParam Long dinkyTaskId) {

        Project dinkyProject = SystemInit.getProject();

        Catalogue catalogue = catalogueService.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, dinkyTaskId));
        if (catalogue == null) {
            return Result.failed("节点获取失败");
        }

        List<String> lists = Lists.newArrayList();
        getDinkyNames(lists, catalogue, 0);
        Collections.reverse(lists);
        String processName = StringUtils.join(lists, "_");
        String taskName = catalogue.getName();

        long projectCode = dinkyProject.getCode();
        ProcessDefinition processDefinition = processClient.getProcessDefinitionInfo(projectCode, processName);
        if (processDefinition == null) {
            Long taskCode = taskClient.genTaskCode(projectCode);
            processDefinition = processClient.createProcessDefinition(projectCode, processName, taskCode, taskName, dinkyTaskId);
        }
        long processCode = processDefinition.getCode();
        TaskMainInfo taskDefinitionInfo = taskClient.getTaskMainInfo(projectCode, processName, taskName);
        if (taskDefinitionInfo != null) {
            return Result.failed("添加失败,工作流定义[" + processName + "]已存在任务定义[" + taskName + "] 请刷新");
        }
        taskClient.createTaskDefinition(projectCode, processCode, upstreamCodes, taskName, dinkyTaskId);

        return Result.succeed("添加成功");
    }

    /**
     * 更新任务定义
     */
    @PutMapping("/task")
    public Result<String> updateTaskDefinition(@RequestParam long projectCode, @RequestParam long taskCode, @RequestParam String taskDefinitionJsonObj) {
        taskClient.updateTaskDefinition(projectCode, taskCode, taskDefinitionJsonObj);
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
