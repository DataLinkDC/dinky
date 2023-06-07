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

import org.dinky.data.model.AlertGroup;
import org.dinky.data.model.AlertHistory;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertHistoryService;
import org.dinky.utils.I18nMsgUtils;

import java.util.ArrayList;
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
    public Result<Void> saveOrUpdate(@RequestBody AlertGroup alertGroup) throws Exception {
        if (alertGroupService.saveOrUpdate(alertGroup)) {
            return Result.succeed(I18nMsgUtils.getMsg("save.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("save.failed"));
        }
    }

    /**
     * list alert groups
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} with {@link AlertGroup}
     */
    @PostMapping
    public ProTableResult<AlertGroup> listAlertGroups(@RequestBody JsonNode para) {
        return alertGroupService.selectForProTable(para);
    }

    /**
     * batch Delete alert group , this method is {@link Deprecated} in the future , please use
     * {@link #deleteGroupById(Integer)} instead
     *
     * @param para {@link JsonNode}
     * @return {@link Result} with {@link Void}
     */
    @DeleteMapping
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!alertGroupService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * get alert group by id
     *
     * @param alertGroup
     * @return
     * @throws Exception
     */
    @PostMapping("/getOneById")
    public Result<AlertGroup> getOneById(@RequestBody AlertGroup alertGroup) throws Exception {
        alertGroup = alertGroupService.getById(alertGroup.getId());
        return Result.succeed(alertGroup, I18nMsgUtils.getMsg("response.get.success"));
    }

    /**
     * get all enabled alert group
     *
     * @return {@link Result} with {@link List} of {@link AlertGroup}
     */
    @GetMapping("/listEnabledAll")
    public Result<List<AlertGroup>> listEnabledAll() {
        return Result.succeed(
                alertGroupService.listEnabledAll(), I18nMsgUtils.getMsg("response.get.success"));
    }

    /**
     * enable or disable alert group
     *
     * @return {@link Result} with {@link List} of {@link AlertGroup}
     */
    @PutMapping("/enable")
    public Result<List<AlertGroup>> enable(@RequestParam("id") Integer id) {
        if (alertGroupService.enable(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("modify.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("modify.failed"));
        }
    }

    /**
     * delete alert group by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteGroupById(@RequestParam("id") Integer id) {
        if (alertGroupService.deleteGroupById(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    /**
     * list alert history
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} with {@link AlertHistory}
     */
    @PostMapping("/history")
    public ProTableResult<AlertHistory> listAlertHistory(@RequestBody JsonNode para) {
        return alertHistoryService.selectForProTable(para);
    }
}
