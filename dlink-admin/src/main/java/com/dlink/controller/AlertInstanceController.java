package com.dlink.controller;

import com.dlink.alert.AlertPool;
import com.dlink.alert.AlertResult;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertInstanceService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return alertInstanceService.deleteAlertInstance(para);
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

    /**
     * 发送告警实例的测试信息
     */
    @PostMapping("/sendTest")
    public Result sendTest(@RequestBody AlertInstance alertInstance) throws Exception {
        AlertResult alertResult = alertInstanceService.testAlert(alertInstance);
        if (alertResult.getSuccess()) {
            return Result.succeed("发送成功");
        } else {
            return Result.failed("发送失败");
        }
    }
}
