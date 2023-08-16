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
import org.dinky.data.model.Statement;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.StatementService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * StatementController
 *
 * @since 2021/5/28 13:48
 */
@Slf4j
@RestController
@Api(tags = "Statement Controller")
@RequestMapping("/api/statement")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Insert or update statement
     *
     * @param statement {@link Statement}
     * @return {@link Result}<{@link Void}>
     */
    @PutMapping
    @ApiOperation("Insert Or Update Statement")
    @Log(title = "Insert Or Update Statement", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result<Void> saveOrUpdateStatement(@RequestBody Statement statement) {
        if (statementService.saveOrUpdate(statement)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * query statement list
     *
     * @param para {@link JsonNode} params
     * @return {@link ProTableResult}<{@link Statement}>
     */
    @PostMapping
    @ApiOperation("Query Statement List")
    public ProTableResult<Statement> listStatements(@RequestBody JsonNode para) {
        return statementService.selectForProTable(para);
    }

    @PostMapping("/getWatchTables")
    @ApiOperation("Get Watch Tables")
    @SuppressWarnings("unchecked")
    public Result<List<String>> getWatchTables(@RequestBody String statement) {
        try {
            Map<String, String> data = objectMapper.readValue(statement, Map.class);
            String ss = data.get("statement");
            return Result.succeed(statementService.getWatchTables(ss));
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
}
