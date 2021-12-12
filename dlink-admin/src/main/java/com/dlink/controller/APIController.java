package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.*;
import com.dlink.service.APIService;
import com.dlink.service.StudioService;
import com.dlink.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * APIController
 *
 * @author wenmo
 * @since 2021/12/11 21:44
 */
@Slf4j
@RestController
@RequestMapping("/openapi")
public class APIController {

    @Autowired
    private APIService apiService;
    @Autowired
    private StudioService studioService;
    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/submitTask")
    public Result submitTask(@RequestParam Integer id) {
        return Result.succeed(taskService.submitByTaskId(id),"执行成功");
    }

    @PostMapping("/executeSql")
    public Result executeSql(@RequestBody APIExecuteSqlDTO apiExecuteSqlDTO)  {
        return Result.succeed(apiService.executeSql(apiExecuteSqlDTO),"执行成功");
    }

    @PostMapping("/explainSql")
    public Result explainSql(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO)  {
        return Result.succeed(apiService.explainSql(apiExecuteSqlDTO),"执行成功");
    }

    @PostMapping("/getJobPlan")
    public Result getJobPlan(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO)  {
        return Result.succeed(apiService.getJobPlan(apiExecuteSqlDTO),"执行成功");
    }

    @PostMapping("/getStreamGraph")
    public Result getStreamGraph(@RequestBody APIExplainSqlDTO apiExecuteSqlDTO)  {
        return Result.succeed(apiService.getStreamGraph(apiExecuteSqlDTO),"执行成功");
    }

    @GetMapping("/getJobData")
    public Result getJobData(@RequestParam String jobId)  {
        return Result.succeed(studioService.getJobData(jobId),"获取成功");
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestBody APICancelDTO apiCancelDTO)  {
        return Result.succeed(apiService.cancel(apiCancelDTO),"执行成功");
    }

    @PostMapping("/savepoint")
    public Result savepoint(@RequestBody APISavePointDTO apiSavePointDTO)  {
        return Result.succeed(apiService.savepoint(apiSavePointDTO),"执行成功");
    }

    @PostMapping("/executeJar")
    public Result executeJar(@RequestBody APIExecuteJarDTO apiExecuteJarDTO)  {
        return Result.succeed(apiService.executeJar(apiExecuteJarDTO),"执行成功");
    }
}
