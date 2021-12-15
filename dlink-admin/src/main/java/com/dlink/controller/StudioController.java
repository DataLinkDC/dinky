package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioCADTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.job.JobResult;
import com.dlink.result.IResult;
import com.dlink.service.StudioService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * StudioController
 *
 * @author wenmo
 * @since 2021/5/30 11:05
 */
@Slf4j
@RestController
@RequestMapping("/api/studio")
public class StudioController {

    @Autowired
    private StudioService studioService;

    /**
     * 执行Sql
     */
    @PostMapping("/executeSql")
    public Result executeSql(@RequestBody StudioExecuteDTO studioExecuteDTO)  {
        JobResult jobResult = studioService.executeSql(studioExecuteDTO);
        return Result.succeed(jobResult,"执行成功");
    }

    /**
     * 解释Sql
     */
    @PostMapping("/explainSql")
    public Result explainSql(@RequestBody StudioExecuteDTO studioExecuteDTO)  {
        return Result.succeed(studioService.explainSql(studioExecuteDTO),"解释成功");
    }

    /**
     * 获取执行图
     */
    @PostMapping("/getStreamGraph")
    public Result getStreamGraph(@RequestBody StudioExecuteDTO studioExecuteDTO)  {
        return Result.succeed(studioService.getStreamGraph(studioExecuteDTO),"获取执行图成功");
    }

    /**
     * 获取sql的jobplan
     */
    @PostMapping("/getJobPlan")
    public Result getJobPlan(@RequestBody StudioExecuteDTO studioExecuteDTO)  {
        return Result.succeed(studioService.getJobPlan(studioExecuteDTO),"获取作业计划成功");
    }

    /**
     * 进行DDL操作
     */
    @PostMapping("/executeDDL")
    public Result executeDDL(@RequestBody StudioDDLDTO studioDDLDTO)  {
        IResult result = studioService.executeDDL(studioDDLDTO);
        return Result.succeed(result,"执行成功");
    }

    /**
     * 根据jobId获取数据
     */
    @GetMapping("/getJobData")
    public Result getJobData(@RequestParam String jobId)  {
        return Result.succeed(studioService.getJobData(jobId),"获取成功");
    }

    /**
     * 获取单表的血缘分析
     */
    @PostMapping("/getCAByStatement")
    public Result getCAByStatement(@RequestBody StudioCADTO studioCADTO)  {
        switch (studioCADTO.getType()){
            case 1:return Result.succeed(studioService.getOneTableColumnCAByStatement(studioCADTO.getStatement()),"执行成功");
            case 2:return Result.succeed(studioService.getColumnCAByStatement(studioCADTO.getStatement()),"执行成功");
            default:return Result.failed("敬请期待");
        }
    }

    /**
     * 创建session
     */
    @PutMapping("/createSession")
    public Result createSession(@RequestBody SessionDTO sessionDTO)  {
        return Result.succeed(studioService.createSession(sessionDTO,"admin"),"创建成功");
    }

    /**
     * 清除指定session
     */
    @DeleteMapping("/clearSession")
    public Result clearSession(@RequestBody JsonNode para) {
        if (para.size()>0){
            List<String> error = new ArrayList<>();
            for (final JsonNode item : para){
                String session = item.asText();
                if(!studioService.clearSession(session)){
                    error.add(session);
                }
            }
            if(error.size()==0) {
                return Result.succeed("清除成功");
            }else {
                return Result.succeed("清除部分成功，但"+error.toString()+"清除失败，共"+error.size()+"次失败。");
            }
        }else{
            return Result.failed("请选择要清除的记录");
        }
    }

    /**
     * 获取session列表
     */
    @GetMapping("/listSession")
    public Result listSession()  {
        return Result.succeed(studioService.listSession("admin"),"获取成功");
    }

    /**
     * 获取flinkjobs列表
     */
    @GetMapping("/listJobs")
    public Result listJobs(@RequestParam Integer clusterId)  {
        List<JsonNode> jobs = studioService.listJobs(clusterId);
        return Result.succeed(jobs.toArray(),"获取成功");
    }

    /**
     * 停止任务
     */
    @GetMapping("/cancel")
    public Result cancel(@RequestParam Integer clusterId,@RequestParam String jobId)  {
        return Result.succeed(studioService.cancel(clusterId,jobId),"停止成功");
    }

    /**
     * savepoint
     */
    @GetMapping("/savepoint")
    public Result savepoint(@RequestParam Integer clusterId,@RequestParam String jobId,
                            @RequestParam String savePointType,@RequestParam String name)  {
        return Result.succeed(studioService.savepoint(clusterId,jobId,savePointType,name),"savepoint 成功");
    }
}
