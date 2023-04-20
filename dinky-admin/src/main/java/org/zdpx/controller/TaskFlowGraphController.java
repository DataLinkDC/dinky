package org.zdpx.controller;


import lombok.extern.slf4j.Slf4j;
import org.dinky.common.result.Result;
import org.dinky.model.Task;
import org.zdpx.service.TaskFlowGraphService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/zdpx")
public class TaskFlowGraphController {

    private final TaskFlowGraphService taskFlowGraphService;

    public TaskFlowGraphController(TaskFlowGraphService taskFlowGraphService) {
        this.taskFlowGraphService = taskFlowGraphService;
    }

    @PutMapping
    public Result<Void> submitSql(@RequestBody Task task) {
        if (taskFlowGraphService.saveOrUpdateTask(task)) {
            return Result.succeed("操作成功");
        } else {
            return Result.failed("操作失败");
        }

    }

}
