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
import org.dinky.utils.I18nMsgUtils;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** AlertInstanceController */
@Slf4j
@RestController
@RequestMapping("/api/alertInstance")
@RequiredArgsConstructor
public class AlertInstanceController {

    private final AlertInstanceService alertInstanceService;

    /**
     * saveOrUpdate
     *
     * @param alertInstance {@link AlertInstance}
     * @return {@link Result} of {@link Void}
     * @throws Exception {@link Exception}
     */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody AlertInstance alertInstance) throws Exception {
        if (alertInstanceService.saveOrUpdate(alertInstance)) {
            AlertPool.remove(alertInstance.getName());
            return Result.succeed(I18nMsgUtils.getMsg("save.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("save.failed"));
        }
    }

    /**
     * listAlertInstances
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link AlertInstance}
     */
    @PostMapping
    public ProTableResult<AlertInstance> listAlertInstances(@RequestBody JsonNode para) {
        return alertInstanceService.selectForProTable(para);
    }

    /**
     * batch Delete AlertInstance, this method is {@link Deprecated} and will be removed in the
     * future, please use {@link #deleteInstanceById(Integer)} instead.
     *
     * @param para
     * @return
     */
    @DeleteMapping
    @Deprecated
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        return alertInstanceService.deleteAlertInstance(para);
    }

    /**
     * delete AlertInstance by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteInstanceById(@RequestParam("id") Integer id) {
        if (alertInstanceService.deleteAlertInstance(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    /**
     * delete AlertInstance by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/enable")
    public Result<Void> enable(@RequestParam("id") Integer id) {
        if (alertInstanceService.enable(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("modify.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("modify.failed"));
        }
    }

    /**
     * get AlertInstance info by id
     *
     * @param alertInstance {@link AlertInstance}
     * @return {@link Result} of {@link AlertInstance}
     * @throws Exception {@link Exception}
     */
    @PostMapping("/getOneById")
    public Result<AlertInstance> getOneById(@RequestBody AlertInstance alertInstance)
            throws Exception {
        alertInstance = alertInstanceService.getById(alertInstance.getId());
        return Result.succeed(alertInstance, I18nMsgUtils.getMsg("response.get.success"));
    }

    /**
     * get all enabled AlertInstance
     *
     * @return {@link Result} of {@link AlertInstance}
     */
    @GetMapping("/listEnabledAll")
    public Result<List<AlertInstance>> listEnabledAll() {
        return Result.succeed(
                alertInstanceService.listEnabledAll(), I18nMsgUtils.getMsg("response.get.success"));
    }

    /**
     * send test alert message
     *
     * @param alertInstance {@link AlertInstance}
     * @return {@link Result} of {@link Void}
     */
    @PostMapping("/sendTest")
    public Result<Void> sendTest(@RequestBody AlertInstance alertInstance) {
        AlertResult alertResult = alertInstanceService.testAlert(alertInstance);
        if (alertResult.getSuccess()) {
            return Result.succeed(I18nMsgUtils.getMsg("send.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("send.failed"));
        }
    }
}
