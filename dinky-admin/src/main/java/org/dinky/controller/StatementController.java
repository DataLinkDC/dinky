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

import org.dinky.data.model.Statement;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.StatementService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * StatementController
 *
 * @since 2021/5/28 13:48
 */
@Slf4j
@RestController
@RequestMapping("/api/statement")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 新增或者更新 */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody Statement statement) throws Exception {
        if (statementService.saveOrUpdate(statement)) {
            return Result.succeed("新增成功");
        } else {
            return Result.failed("新增失败");
        }
    }

    /** 动态查询列表 */
    @PostMapping
    public ProTableResult<Statement> listStatements(@RequestBody JsonNode para) {
        return statementService.selectForProTable(para);
    }

    /** 批量删除 */
    @DeleteMapping
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            boolean isAdmin = false;
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!statementService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0 && !isAdmin) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /** 获取指定ID的信息 */
    @PostMapping("/getOneById")
    public Result<Statement> getOneById(@RequestBody Statement statement) throws Exception {
        statement = statementService.getById(statement.getId());
        return Result.succeed(statement, "获取成功");
    }

    @PostMapping("/getWatchTables")
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
