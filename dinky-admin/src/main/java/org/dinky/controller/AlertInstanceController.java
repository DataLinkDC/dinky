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

import io.swagger.annotations.ApiOperation;
import org.dinky.alert.AlertPool;
import org.dinky.alert.AlertResult;
import org.dinky.annotation.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.AlertInstance;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.AlertInstanceService;

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
    @Log(title = "INSERT OR UPDATE AlertInstance", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("INSERT OR UPDATE AlertInstance")
    public Result<Void> saveOrUpdate(@RequestBody AlertInstance alertInstance) throws Exception {
        if (alertInstanceService.saveOrUpdate(alertInstance)) {
            AlertPool.remove(alertInstance.getName());
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * listAlertInstances
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link AlertInstance}
     */
    @PostMapping
    @Log(title = "Query AlertInstance List", businessType = BusinessType.QUERY)
    @ApiOperation("Query AlertInstance List")
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
    @Log(title = "Delete AlertInstance By Id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete AlertInstance By Id")
    public Result<AlertInstance> deleteInstanceById(@RequestParam("id") Integer id) {
        if (alertInstanceService.deleteAlertInstance(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * delete AlertInstance by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/enable")
    @Log(title = "Update AlertInstance Status", businessType = BusinessType.UPDATE)
    @ApiOperation("Update AlertInstance Status")
    public Result<AlertInstance> enable(@RequestParam("id") Integer id) {
        if (alertInstanceService.enable(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * get all enabled AlertInstance
     *
     * @return {@link Result} of {@link AlertInstance}
     */
    @GetMapping("/listEnabledAll")
    @Log(title = "Query AlertInstance List Of Enable", businessType = BusinessType.QUERY)
    @ApiOperation("Query AlertInstance List Of Enable")
    public Result<List<AlertInstance>> listEnabledAll() {
        return Result.succeed(alertInstanceService.listEnabledAll());
    }

    /**
     * send test alert message
     *
     * @param alertInstance {@link AlertInstance}
     * @return {@link Result} of {@link Void}
     */
    @PostMapping("/sendTest")
    @Log(title = "Test Send To AlertInstance", businessType = BusinessType.TEST)
    @ApiOperation("Test Send To AlertInstance")
    public Result<Void> sendTest(@RequestBody AlertInstance alertInstance) {
        AlertResult alertResult = alertInstanceService.testAlert(alertInstance);
        if (alertResult.getSuccess()) {
            return Result.succeed(Status.SEND_TEST_SUCCESS);
        } else {
            return Result.failed(Status.SEND_TEST_FAILED);
        }
    }
}
