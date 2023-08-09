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
import org.dinky.data.dto.TaskRollbackVersionDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.TaskOperatingSavepointSelect;
import org.dinky.data.model.Task;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.job.JobResult;
import org.dinky.service.TaskService;
import org.dinky.utils.TaskOneClickOperatingUtil;

import java.util.ArrayList;
import java.util.Collections;
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

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务 Controller
 *
 * @since 2021-05-24
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** 新增或者更新 */
    @PutMapping
    @ApiOperation("Insert Or Update Task")
    @Log(title = "Insert Or Update Task", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result<Void> saveOrUpdateTask(@RequestBody Task task) throws Exception {
        if (taskService.saveOrUpdateTask(task)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /** 动态查询列表 */
    @PostMapping
    @ApiOperation("Query Task List")
    public ProTableResult<Task> listTasks(@RequestBody JsonNode para) {
        return taskService.selectForProTable(para);
    }

    /** 批量执行 */
    @PostMapping(value = "/submit")
    @ApiOperation("Batch Execute Task")
    public Result<List<JobResult>> submit(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<JobResult> results = new ArrayList<>();
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                JobResult result = taskService.submitTask(id);
                if (!result.isSuccess()) {
                    error.add(id);
                }
                results.add(result);
            }
            if (error.size() == 0) {
                return Result.succeed(results, "执行成功");
            } else {
                return Result.succeed(
                        results, "执行部分成功，但" + error + "执行失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要执行的记录");
        }
    }

    /** 获取指定ID的信息 */
    @GetMapping
    @ApiOperation("Get Task Info By Id")
    public Result<Task> getOneById(@RequestParam Integer id) {
        Task task = taskService.getTaskInfoById(id);
        return Result.succeed(task);
    }

    /** 获取所有可用的 FlinkSQLEnv */
    @GetMapping(value = "/listFlinkSQLEnv")
    @ApiOperation("Get All FlinkSQLEnv")
    public Result<List<Task>> listFlinkSQLEnv() {
        return Result.succeed(taskService.listFlinkSQLEnv());
    }

    /** 导出 sql */
    @GetMapping(value = "/exportSql")
    @ApiOperation("Export Sql")
    @Log(title = "Export Sql", businessType = BusinessType.EXPORT)
    public Result<String> exportSql(@RequestParam Integer id) {
        return Result.succeed(taskService.exportSql(id));
    }

    /** 发布任务 */
    @GetMapping(value = "/releaseTask")
    @ApiOperation("Release Task")
    @Log(title = "Release Task", businessType = BusinessType.UPDATE)
    public Result<Void> releaseTask(@RequestParam Integer id) {
        return taskService.releaseTask(id);
    }

    @PostMapping("/rollbackTask")
    @ApiOperation("Rollback Task")
    @Log(title = "Rollback Task", businessType = BusinessType.UPDATE)
    public Result<Void> rollbackTask(@RequestBody TaskRollbackVersionDTO dto) {
        return taskService.rollbackTask(dto);
    }

    /** 维护任务 */
    @GetMapping(value = "/developTask")
    @ApiOperation("Develop Task")
    @Log(title = "Develop Task", businessType = BusinessType.UPDATE)
    public Result<Boolean> developTask(@RequestParam Integer id) {
        return Result.succeed(taskService.developTask(id), Status.OPERATE_SUCCESS);
    }

    /** 上线任务 */
    @GetMapping(value = "/onLineTask")
    @ApiOperation("OnLine Task")
    @Log(title = "OnLine Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<JobResult> onLineTask(@RequestParam Integer id) {
        return taskService.onLineTask(id);
    }

    /** 下线任务 */
    @GetMapping(value = "/offLineTask")
    @ApiOperation("OffLine Task")
    @Log(title = "OffLine Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<Void> offLineTask(@RequestParam Integer id, @RequestParam String type) {
        return taskService.offLineTask(id, type);
    }

    /** 注销任务 */
    @GetMapping(value = "/cancelTask")
    @ApiOperation("Cancel Task")
    @Log(title = "Cancel Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<Void> cancelTask(@RequestParam Integer id) {
        return taskService.cancelTask(id);
    }

    /** 恢复任务 */
    @GetMapping(value = "/recoveryTask")
    @ApiOperation("Recovery Task")
    @Log(title = "Recovery Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<Boolean> recoveryTask(@RequestParam Integer id) {
        return Result.succeed(taskService.recoveryTask(id), Status.OPERATE_SUCCESS);
    }

    /** 重启任务 */
    @GetMapping(value = "/restartTask")
    @ApiOperation("Restart Task")
    @Log(title = "Restart Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<JobResult> restartTask(@RequestParam Integer id, @RequestParam Boolean isOnLine) {
        if (isOnLine) {
            return taskService.reOnLineTask(id, null);
        } else {
            return Result.succeed(taskService.restartTask(id, null), Status.RESTART_SUCCESS);
        }
    }

    /** 选择保存点重启任务 */
    @GetMapping(value = "/selectSavePointRestartTask")
    @ApiOperation("Select Save Point Restart Task")
    @Log(title = "Select Save Point Restart Task", businessType = BusinessType.REMOTE_OPERATION)
    public Result<JobResult> selectSavePointRestartTask(
            @RequestParam Integer id,
            @RequestParam Boolean isOnLine,
            @RequestParam String savePointPath) {
        if (isOnLine) {
            return taskService.reOnLineTask(id, savePointPath);
        } else {
            return Result.succeed(
                    taskService.restartTask(id, savePointPath), Status.RESTART_SUCCESS);
        }
    }

    /** 获取当前的 API 的地址 */
    @GetMapping(value = "/getTaskAPIAddress")
    @ApiOperation("Get Task API Address")
    public Result<String> getTaskAPIAddress() {
        return Result.succeed(taskService.getTaskAPIAddress(), Status.RESTART_SUCCESS);
    }

    /** 导出json */
    @GetMapping(value = "/exportJsonByTaskId")
    @ApiOperation("Export Task To Sign Json")
    @Log(title = "Export Task To Sign Json", businessType = BusinessType.EXPORT)
    public Result<String> exportJsonByTaskId(@RequestParam Integer id) {
        return Result.succeed(taskService.exportJsonByTaskId(id));
    }

    /** 导出json数组 */
    @PostMapping(value = "/exportJsonByTaskIds")
    @ApiOperation("Export Task To Array Json")
    @Log(title = "Export Task To Array Json", businessType = BusinessType.EXPORT)
    public Result<String> exportJsonByTaskIds(@RequestBody JsonNode para) {
        return Result.succeed(taskService.exportJsonByTaskIds(para));
    }

    /** json文件上传 导入task */
    @PostMapping(value = "/uploadTaskJson")
    @ApiOperation("Upload Task Json")
    @Log(title = "Upload Task Json", businessType = BusinessType.UPLOAD)
    public Result<Void> uploadTaskJson(@RequestParam("file") MultipartFile file) throws Exception {
        return taskService.uploadTaskJson(file);
    }

    /**
     * 查询所有目录
     *
     * @return {@link Result}<{@link Tree}<{@link Integer}>>
     */
    @GetMapping("/queryAllCatalogue")
    @ApiOperation("Query All Catalogue")
    public Result<Tree<Integer>> queryAllCatalogue() {
        return taskService.queryAllCatalogue();
    }

    /**
     * 查询对应操作的任务列表
     *
     * @param operating 操作
     * @param catalogueId 目录id
     * @return {@link Result}<{@link List}<{@link Task}>>
     */
    @GetMapping("/queryOnClickOperatingTask")
    @ApiOperation("Query On Click Operating Task")
    public Result<List<Task>> queryOnClickOperatingTask(
            @RequestParam("operating") Integer operating,
            @RequestParam("catalogueId") Integer catalogueId) {
        if (operating == null) {
            return Result.failed(Status.OPERATE_FAILED);
        }
        switch (operating) {
            case 1:
                return taskService.queryOnLineTaskByDoneStatus(
                        Collections.singletonList(JobLifeCycle.RELEASE),
                        JobStatus.getAllDoneStatus(),
                        true,
                        catalogueId);
            case 2:
                return taskService.queryOnLineTaskByDoneStatus(
                        Collections.singletonList(JobLifeCycle.ONLINE),
                        Collections.singletonList(JobStatus.RUNNING),
                        false,
                        catalogueId);
            default:
                return Result.failed(Status.OPERATE_FAILED);
        }
    }

    /**
     * 一键操作任务
     *
     * @param operating 操作
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/onClickOperatingTask")
    @ApiOperation("On Click Operating Task")
    public Result<Void> onClickOperatingTask(@RequestBody JsonNode operating) {
        if (operating == null || operating.get("operating") == null) {
            return Result.failed(Status.OPERATE_FAILED);
        }
        switch (operating.get("operating").asInt()) {
            case 1:
                final JsonNode savepointSelect = operating.get("taskOperatingSavepointSelect");
                return TaskOneClickOperatingUtil.oneClickOnline(
                        TaskOneClickOperatingUtil.parseJsonNode(operating),
                        TaskOperatingSavepointSelect.valueByCode(
                                savepointSelect == null ? 0 : savepointSelect.asInt()));
            case 2:
                return TaskOneClickOperatingUtil.onClickOffline(
                        TaskOneClickOperatingUtil.parseJsonNode(operating));
            default:
                return Result.failed(Status.OPERATE_FAILED);
        }
    }

    /**
     * 查询一键操作任务状态
     *
     * @return {@link Result}<{@link Dict}>
     */
    @GetMapping("/queryOneClickOperatingTaskStatus")
    @ApiOperation("Query One Click Operating Task Status")
    public Result<Dict> queryOneClickOperatingTaskStatus() {
        return TaskOneClickOperatingUtil.queryOneClickOperatingTaskStatus();
    }
}
