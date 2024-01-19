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

import org.dinky.data.model.Savepoints;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.SavepointsService;

import java.util.List;

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
 * SavePointsController
 *
 * @since 2021/11/21
 */
@Slf4j
@RestController
@Api(tags = "SavePoints Controller")
@RequestMapping("/api/savepoints")
@RequiredArgsConstructor
public class SavePointsController {

    private final SavepointsService savepointsService;

    /**
     * query all savepoint list
     *
     * @param para {@link JsonNode} params
     * @return {@link ProTableResult}<{@link Savepoints}>
     */
    @PostMapping
    @ApiOperation("Query SavePoint List")
    @ApiImplicitParam(
            name = "para",
            value = "Query Params",
            dataType = "JsonNode",
            paramType = "body",
            required = true,
            dataTypeClass = JsonNode.class)
    public ProTableResult<Savepoints> listSavePoints(@RequestBody JsonNode para) {
        return savepointsService.selectForProTable(para);
    }
    /**
     * query savepoint list by task id
     *
     * @param taskId {@link Integer}
     * @return {@link Result}<{@link List}<{@link Savepoints}>>
     */
    @GetMapping("/listSavepointsByTaskId")
    @ApiOperation("Query SavePoint List By TaskId")
    @ApiImplicitParam(name = "taskId", value = "Task ID", dataType = "Integer", paramType = "query", required = true)
    public Result<List<Savepoints>> listSavePointsByTaskId(@RequestParam Integer taskId) {
        return Result.succeed(savepointsService.listSavepointsByTaskId(taskId));
    }
}
