package com.dlink.controller;

import com.dlink.alert.AlertPool;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertInstanceService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * AlertInstanceController
 *
 * @author wenmo
 * @since 2022/2/24 19:54
 **/
@Slf4j
@RestController
@RequestMapping("/api/alertInstance")
public class AlertInstanceController {
    @Autowired
    private AlertInstanceService alertInstanceService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody AlertInstance alertInstance) throws Exception {
        if (alertInstanceService.saveOrUpdate(alertInstance)) {
            AlertPool.remove(alertInstance.getName());
            return Result.succeed("新增成功");
        } else {
            return Result.failed("新增失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<AlertInstance> listAlertInstances(@RequestBody JsonNode para) {
        return alertInstanceService.selectForProTable(para);
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
                if (!alertInstanceService.removeById(id)) {
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
    public Result getOneById(@RequestBody AlertInstance alertInstance) throws Exception {
        alertInstance = alertInstanceService.getById(alertInstance.getId());
        return Result.succeed(alertInstance, "获取成功");
    }

    /**
     * 获取可用的报警实例列表
     */
    @GetMapping("/listEnabledAll")
    public Result listEnabledAll() {
        return Result.succeed(alertInstanceService.listEnabledAll(), "获取成功");
    }
}
