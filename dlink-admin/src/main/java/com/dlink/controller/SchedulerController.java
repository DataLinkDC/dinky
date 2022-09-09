package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.scheduler.client.ProcessClient;
import com.dlink.scheduler.client.ProjectClient;
import com.dlink.scheduler.client.TaskClient;
import com.dlink.scheduler.model.ProcessDefinition;
import com.dlink.scheduler.model.Project;
import com.dlink.scheduler.model.TaskDefinitionLog;
import com.dlink.scheduler.model.TaskMainInfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private ProjectClient projectClient;
    @Autowired
    private TaskClient taskClient;

    /**
     * 获取工作流程定义
     */
    @GetMapping("/process")
    public Result<List<ProcessDefinition>> getProcessDefinition() {
        List<ProcessDefinition> lists = new ArrayList<>();
        Project dinkyProject = projectClient.getDinkyProject();
        if (dinkyProject == null) {
            return Result.succeed(lists);
        }
        long projectCode = dinkyProject.getCode();
        lists = processClient.getProcessDefinition(projectCode, "");
        return Result.succeed(lists);
    }

    /**
     * 创建工作流定义
     */
    @PostMapping("/process")
    public Result<ProcessDefinition> createProcessDefinition(@RequestParam String processName,
                                                             @RequestParam String taskName,
                                                             @RequestParam String dinkyTaskId) {
        Project dinkyProject = projectClient.getDinkyProject();
        if (dinkyProject == null) {
            dinkyProject = projectClient.createDinkyProject();
        }
        long projectCode = dinkyProject.getCode();
        Long taskCode = taskClient.genTaskCode(projectCode);
        ProcessDefinition processDefinition = processClient.createProcessDefinition(projectCode, processName, taskCode,
            taskName, dinkyTaskId);
        return Result.succeed(processDefinition);
    }

    /**
     * 获取任务定义
     */
    @GetMapping("/task")
    public Result<List<TaskMainInfo>> getTaskDefinition(@RequestParam Long projectCode,
                                                        @RequestParam String processName) {
        List<TaskMainInfo> lists = taskClient.getTaskDefinition(projectCode, processName, "");
        return Result.succeed(lists);
    }

    /**
     * 创建任务定义
     */
    @PostMapping("/task")
    public Result<TaskDefinitionLog> createTaskDefinition(@RequestParam Long projectCode,
                                                          @RequestParam Long processCode,
                                                          @RequestParam String processName,
                                                          @RequestParam(required = false) Long upstreamCodes,
                                                          @RequestParam String taskName,
                                                          @RequestParam String dinkyTaskId) {

        TaskMainInfo taskDefinitionInfo = taskClient.getTaskDefinitionInfo(projectCode, processName, taskName);
        if (taskDefinitionInfo != null) {
            return Result.failed("添加失败,工作流定义[" + processName + "]已存在任务定义[" + taskName + "]");
        }
        TaskDefinitionLog taskMainInfo = taskClient.createTaskDefinition(projectCode, processCode, upstreamCodes, taskName, dinkyTaskId);
        return Result.succeed(taskMainInfo);
    }

}
