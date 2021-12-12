package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.dto.APIExplainSqlDTO;
import com.dlink.service.APIService;
import com.dlink.service.StudioService;
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
}
