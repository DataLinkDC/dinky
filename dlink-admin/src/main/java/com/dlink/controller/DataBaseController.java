package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.DataBase;
import com.dlink.service.DataBaseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DataBaseController
 *
 * @author wenmo
 * @since 2021/7/20 23:48
 */
@Slf4j
@RestController
@RequestMapping("/api/database")
public class DataBaseController {
    @Autowired
    private DataBaseService databaseService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody DataBase database) {
        if(databaseService.saveOrUpdateDataBase(database)){
            return Result.succeed("更新成功");
        }else {
            return Result.failed("更新失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<DataBase> listDataBases(@RequestBody JsonNode para) {
        return databaseService.selectForProTable(para);
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
                if(!databaseService.removeById(id)){
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
    public Result getOneById(@RequestBody DataBase database) {
        database = databaseService.getById(database.getId());
        return Result.succeed(database,"获取成功");
    }

    /**
     * 全部心跳监测
     */
    @PostMapping("/testConnect")
    public Result testConnect(@RequestBody DataBase database) {
        return Result.succeed(databaseService.checkHeartBeat(database),"获取成功");
    }
}