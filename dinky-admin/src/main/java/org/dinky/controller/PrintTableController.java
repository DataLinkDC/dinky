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

import org.dinky.data.result.Result;
import org.dinky.data.vo.PrintTableVo;
import org.dinky.service.PrintTableService;
import org.dinky.utils.JsonUtils;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@RestController
@Api(tags = "Print Table Controller")
@AllArgsConstructor
@RequestMapping("/api/printTable")
@SaCheckLogin
public class PrintTableController {

    private final PrintTableService printTableService;

    @PostMapping("/getPrintTables")
    @ApiOperation("Get Print Tables")
    @SuppressWarnings("unchecked")
    @ApiImplicitParam(name = "statement", value = "Statement", dataType = "String", paramType = "body", required = true)
    public Result<List<PrintTableVo>> getPrintTables(@RequestBody String statement) {
        try {
            Map<String, String> data = JsonUtils.toMap(statement);
            String ss = data.get("statement");
            return Result.succeed(printTableService.getPrintTables(ss));
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        }
    }
}
