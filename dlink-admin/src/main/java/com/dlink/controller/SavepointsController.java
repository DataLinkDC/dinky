package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Savepoints;
import com.dlink.service.SavepointsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * SavepointsController
 *
 * @author wenmo
 * @since 2021/11/21
 **/
@Slf4j
@RestController
@RequestMapping("/api/savepoints")
public class SavepointsController {
    @Autowired
    private SavepointsService savepointsService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Savepoints savepoints) throws Exception {
        if(savepointsService.saveOrUpdate(savepoints)){
            return Result.succeed("新增成功");
        }else {
            return Result.failed("新增失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<Savepoints> listSavepointss(@RequestBody JsonNode para) {
        return savepointsService.selectForProTable(para);
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
                if(!savepointsService.removeById(id)){
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
    public Result getOneById(@RequestBody Savepoints savepoints) throws Exception {
        savepoints = savepointsService.getById(savepoints.getId());
        return Result.succeed(savepoints,"获取成功");
    }

    /**
     * 获取指定作业ID的所有savepoint
     */
    @GetMapping("/listSavepointsByTaskId")
    public Result listSavepointsByTaskId(@RequestParam Integer taskID) throws Exception {
        return Result.succeed(savepointsService.listSavepointsByTaskId(taskID),"获取成功");
    }
}
