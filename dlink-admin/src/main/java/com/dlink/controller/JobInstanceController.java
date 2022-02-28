package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Jar;
import com.dlink.model.JobInstance;
import com.dlink.service.JobInstanceService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        if (para.size()>0){
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para){
                Integer id = item.asInt();
                if(!jobInstanceService.removeById(id)){
                    error.add(id);
                }
            }
            if(error.size()==0) {
                return Result.succeed("删除成功");
            }else {
                return Result.succeed("删除部分成功，但"+error.toString()+"删除失败，共"+error.size()+"次失败。");
            }
        }else{
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody JobInstance JobInstance) throws Exception {
        JobInstance = jobInstanceService.getById(JobInstance.getId());
        return Result.succeed(JobInstance,"获取成功");
    }

    /**
     * 获取状态统计信息
     */
    @GetMapping("/getStatusCount")
    public Result getStatusCount() {
        return Result.succeed(jobInstanceService.getStatusCount(),"获取成功");
    }
}
