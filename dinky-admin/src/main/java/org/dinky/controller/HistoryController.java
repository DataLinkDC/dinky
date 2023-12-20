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

import org.dinky.data.model.job.History;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.HistoryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HistoryController
 *
 * @since 2021/6/26 23:09
 */
@Slf4j
@Api(tags = "History Controller")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /**
     * query list history
     *
     * @param para
     * @return
     */
    @PostMapping("/list")
    @ApiOperation("Query History List")
    @ApiImplicitParam(name = "para", value = "Query Parameters", dataType = "JsonNode", paramType = "body")
    public ProTableResult<History> listHistory(@RequestBody JsonNode para) {
        return historyService.selectForProTable(para);
    }

    /**
     * 获取Job实例的所有信息
     */
    @GetMapping("/getLatestHistoryById")
    @ApiOperation("Get latest history info by id")
    @ApiImplicitParam(name = "id", value = "task id", dataType = "Integer", paramType = "query", required = true)
    public Result<History> getLatestHistoryById(@RequestParam Integer id) {
        return Result.succeed(historyService.getLatestHistoryById(id));
    }
}
