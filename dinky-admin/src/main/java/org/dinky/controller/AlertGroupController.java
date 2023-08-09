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

import org.dinky.data.annotation.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.AlertGroup;
import org.dinky.data.model.AlertHistory;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertHistoryService;

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

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** AlertGroupController */
@Slf4j
@RestController
@RequestMapping("/api/alertGroup")
@RequiredArgsConstructor
public class AlertGroupController {

    private final AlertGroupService alertGroupService;
    private final AlertHistoryService alertHistoryService;

    /**
     * save or update alert Group
     *
     * @param alertGroup {@link AlertGroup}
     * @return {@link Result} with {@link Void}
     * @throws Exception {@link Exception}
     */
    @PutMapping
    @Log(title = "Insert OR Update AlertGroup ", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert OR Update AlertGroup")
    public Result<Void> saveOrUpdateAlertGroup(@RequestBody AlertGroup alertGroup)
            throws Exception {
        if (alertGroupService.saveOrUpdate(alertGroup)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * list alert groups
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} with {@link AlertGroup}
     */
    @PostMapping
    @ApiOperation("Query AlertGroup List")
    public ProTableResult<AlertGroup> listAlertGroups(@RequestBody JsonNode para) {
        return alertGroupService.selectForProTable(para);
    }

    /**
     * get all enabled alert group
     *
     * @return {@link Result} with {@link List} of {@link AlertGroup}
     */
    @GetMapping("/listEnabledAll")
    @ApiOperation("Query AlertGroup List Of Enable")
    public Result<List<AlertGroup>> listEnabledAllAlertGroups() {
        return Result.succeed(alertGroupService.listEnabledAllAlertGroups());
    }

    /**
     * enable or disable alert group
     *
     * @return {@link Result} with {@link List} of {@link AlertGroup}
     */
    @PutMapping("/enable")
    @ApiOperation("Update AlertGroup Status")
    @Log(title = "Update AlertGroup Status", businessType = BusinessType.UPDATE)
    public Result<List<AlertGroup>> modifyAlertGroupStatus(@RequestParam("id") Integer id) {
        if (alertGroupService.modifyAlertGroupStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * delete alert group by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete AlertGroup By Id")
    @Log(title = "Delete AlertGroup By Id", businessType = BusinessType.DELETE)
    public Result<Void> deleteGroupById(@RequestParam("id") Integer id) {
        if (alertGroupService.deleteGroupById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * list alert history
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} with {@link AlertHistory}
     */
    @PostMapping("/history")
    @ApiOperation("Query AlertHistory List")
    public ProTableResult<AlertHistory> listAlertHistory(@RequestBody JsonNode para) {
        return alertHistoryService.selectForProTable(para);
    }
}
