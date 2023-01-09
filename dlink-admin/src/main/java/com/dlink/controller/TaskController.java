package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.dto.TaskRollbackVersionDTO;
import com.dlink.job.JobResult;
import com.dlink.model.Task;
import com.dlink.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务 Controller
 *
 * @author wenmo
 * @since 2021-05-24
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Task task) throws Exception {
        if (taskService.saveOrUpdateTask(task)) {
            return Result.succeed("操作成功");
        } else {
            return Result.failed("操作失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<Task> listTasks(@RequestBody JsonNode para) {
        return taskService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            boolean isAdmin = false;
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!taskService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0 && !isAdmin) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 批量执行
     */
    @PostMapping(value = "/submit")
    public Result submit(@RequestBody JsonNode para) throws Exception {
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
                return Result.succeed(results, "执行部分成功，但" + error.toString() + "执行失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要执行的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @GetMapping
    public Result getOneById(@RequestParam Integer id) {
        Task task = taskService.getTaskInfoById(id);
        return Result.succeed(task, "获取成功");
    }

    /**
     * 获取所有可用的 FlinkSQLEnv
     */
    @GetMapping(value = "/listFlinkSQLEnv")
    public Result listFlinkSQLEnv() {
        return Result.succeed(taskService.listFlinkSQLEnv(), "获取成功");
    }

    /**
     * 导出 sql
     */
    @GetMapping(value = "/exportSql")
    public Result exportSql(@RequestParam Integer id) {
        return Result.succeed(taskService.exportSql(id), "获取成功");
    }

    /**
     * 发布任务
     */
    @GetMapping(value = "/releaseTask")
    public Result releaseTask(@RequestParam Integer id) {
        return taskService.releaseTask(id);
    }


    @PostMapping("/rollbackTask")
    public Result rollbackTask(@RequestBody TaskRollbackVersionDTO dto) throws Exception {

       return taskService.rollbackTask(dto);
    }

    /**
     * 维护任务
     */
    @GetMapping(value = "/developTask")
    public Result developTask(@RequestParam Integer id) {
        return Result.succeed(taskService.developTask(id), "操作成功");
    }

    /**
     * 上线任务
     */
    @GetMapping(value = "/onLineTask")
    public Result onLineTask(@RequestParam Integer id) {
        return taskService.onLineTask(id);
    }

    /**
     * 下线任务
     */
    @GetMapping(value = "/offLineTask")
    public Result offLineTask(@RequestParam Integer id, @RequestParam String type) {
        return taskService.offLineTask(id, type);
    }

    /**
     * 注销任务
     */
    @GetMapping(value = "/cancelTask")
    public Result cancelTask(@RequestParam Integer id) {
        return taskService.cancelTask(id);
    }

    /**
     * 恢复任务
     */
    @GetMapping(value = "/recoveryTask")
    public Result recoveryTask(@RequestParam Integer id) {
        return Result.succeed(taskService.recoveryTask(id), "操作成功");
    }

    /**
     * 重启任务
     */
    @GetMapping(value = "/restartTask")
    public Result restartTask(@RequestParam Integer id, @RequestParam Boolean isOnLine) {
        if (isOnLine) {
            return taskService.reOnLineTask(id);
        } else {
            return Result.succeed(taskService.restartTask(id), "重启成功");
        }
    }

    /**
     * 获取当前的 API 的地址
     */
    @GetMapping(value = "/getTaskAPIAddress")
    public Result getTaskAPIAddress() {
        return Result.succeed(taskService.getTaskAPIAddress(), "重启成功");
    }
}

