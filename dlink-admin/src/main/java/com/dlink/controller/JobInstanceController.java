package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Jar;
import com.dlink.model.JobInstance;
import com.dlink.service.JobInstanceService;
import com.dlink.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * JobInstanceController
 *
 * @author wenmo
 * @since 2022/2/2 14:02
 */
@Slf4j
@RestController
@RequestMapping("/api/jobInstance")
public class JobInstanceController {
    @Autowired
    private JobInstanceService jobInstanceService;
    @Autowired
    private TaskService taskService;

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<JobInstance> listJobInstances(@RequestBody JsonNode para) {
        return jobInstanceService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!jobInstanceService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody JobInstance JobInstance) throws Exception {
        JobInstance = jobInstanceService.getById(JobInstance.getId());
        return Result.succeed(JobInstance, "获取成功");
    }

    /**
     * 获取状态统计信息
     */
    @GetMapping("/getStatusCount")
    public Result getStatusCount() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("history",jobInstanceService.getStatusCount(true));
        result.put("instance",jobInstanceService.getStatusCount(false));
        return Result.succeed(result, "获取成功");
    }

    /**
     * 获取Job实例的所有信息
     */
    @GetMapping("/getJobInfoDetail")
    public Result getJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getJobInfoDetail(id), "获取成功");
    }

    /**
     * 刷新Job实例的所有信息
     */
    @GetMapping("/refreshJobInfoDetail")
    public Result refreshJobInfoDetail(@RequestParam Integer id) {
        return Result.succeed(taskService.refreshJobInfoDetail(id), "刷新成功");
    }

    /**
     * 获取单表的血缘分析
     */
    @GetMapping("/getOneTableColumnCA")
    public Result getOneTableColumnCA(@RequestParam Integer id) {
        return Result.succeed(jobInstanceService.getOneTableColumnCA(id), "刷新成功");
    }
}
