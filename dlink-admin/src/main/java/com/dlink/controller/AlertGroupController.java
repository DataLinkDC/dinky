package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.AlertGroup;
import com.dlink.service.AlertGroupService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * AlertGroupController
 *
 * @author wenmo
 * @since 2022/2/24 20:02
 **/
@Slf4j
@RestController
@RequestMapping("/api/alertGroup")
public class AlertGroupController {
    @Autowired
    private AlertGroupService alertGroupService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody AlertGroup alertGroup) throws Exception {
        if(alertGroupService.saveOrUpdate(alertGroup)){
            return Result.succeed("新增成功");
        }else {
            return Result.failed("新增失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<AlertGroup> listAlertGroups(@RequestBody JsonNode para) {
        return alertGroupService.selectForProTable(para);
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
                if(!alertGroupService.removeById(id)){
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
    public Result getOneById(@RequestBody AlertGroup alertGroup) throws Exception {
        alertGroup = alertGroupService.getById(alertGroup.getId());
        return Result.succeed(alertGroup,"获取成功");
    }
}
