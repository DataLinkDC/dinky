/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.controller;

import org.dinky.alert.AlertPool;
import org.dinky.alert.AlertResult;
import org.dinky.common.result.ProTableResult;
import org.dinky.common.result.Result;
import org.dinky.model.AlertInstance;
import org.dinky.service.AlertInstanceService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AlertInstanceController
 *
 * @author wenmo
 * @since 2022/2/24 19:54
 */
@Slf4j
@RestController
@RequestMapping("/api/alertInstance")
@RequiredArgsConstructor
public class AlertInstanceController {

    private final AlertInstanceService alertInstanceService;

    /** 新增或者更新 */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody AlertInstance alertInstance) throws Exception {
        if (alertInstanceService.saveOrUpdate(alertInstance)) {
            AlertPool.remove(alertInstance.getName());
            return Result.succeed("新增成功");
        } else {
            return Result.failed("新增失败");
        }
    }

    /** 动态查询列表 */
    @PostMapping
    public ProTableResult<AlertInstance> listAlertInstances(@RequestBody JsonNode param) {
        return alertInstanceService.selectForProTable(param);
    }

    /** 批量删除 */
    @DeleteMapping
    public Result<Void> deleteMul(@RequestBody JsonNode param) {
        return alertInstanceService.deleteAlertInstance(param);
    }

    /** 获取指定ID的信息 */
    @PostMapping("/getOneById")
    public Result<AlertInstance> getOneById(@RequestBody AlertInstance alertInstance)
            throws Exception {
        alertInstance = alertInstanceService.getById(alertInstance.getId());
        return Result.succeed(alertInstance, "获取成功");
    }

    /** 获取可用的报警实例列表 */
    @GetMapping("/listEnabledAll")
    public Result<List<AlertInstance>> listEnabledAll() {
        return Result.succeed(alertInstanceService.listEnabledAll(), "获取成功");
    }

    /** 发送告警实例的测试信息 */
    @PostMapping("/sendTest")
    public Result<Void> sendTest(@RequestBody AlertInstance alertInstance) {
        AlertResult alertResult = alertInstanceService.testAlert(alertInstance);
        if (alertResult.getSuccess()) {
            return Result.succeed("发送成功");
        } else {
            return Result.failed("发送失败");
        }
    }
}
