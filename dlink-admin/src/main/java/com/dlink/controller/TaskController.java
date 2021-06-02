package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Task;
import com.dlink.result.SubmitResult;
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
        if(taskService.saveOrUpdateTask(task)){
            return Result.succeed("操作成功");
        }else {
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
        if (para.size()>0){
            boolean isAdmin = false;
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para){
                Integer id = item.asInt();
                if(!taskService.removeById(id)){
                    error.add(id);
                }
            }
            if(error.size()==0&&!isAdmin) {
                return Result.succeed("删除成功");
            }else {
                return Result.succeed("删除部分成功，但"+error.toString()+"删除失败，共"+error.size()+"次失败。");
            }
        }else{
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 批量执行
     */
    @PostMapping(value = "/submit")
    public Result submit(@RequestBody JsonNode para) throws Exception {
        if (para.size()>0){
            List<SubmitResult> results = new ArrayList<>();
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para){
                Integer id = item.asInt();
                SubmitResult result = taskService.submitByTaskId(id);
                if(!result.isSuccess()){
                    error.add(id);
                }
                results.add(result);
            }
            if(error.size()==0) {
                return Result.succeed(results,"执行成功");
            }else {
                return Result.succeed(results,"执行部分成功，但"+error.toString()+"执行失败，共"+error.size()+"次失败。");
            }
        }else{
            return Result.failed("请选择要执行的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @GetMapping
    public Result getOneById(@RequestParam Integer id) {
        Task task = taskService.getTaskInfoById(id);
        return Result.succeed(task,"获取成功");
    }
}

