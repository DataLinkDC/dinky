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

import org.dinky.data.model.Task;
import org.dinky.data.result.Result;
import org.dinky.function.constant.PathConstant;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.service.TaskService;

import org.apache.flink.table.catalog.FunctionLanguage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JarController
 *
 * @since 2021/11/13
 */
@Slf4j
@RestController
@Api(tags = "Jar Controller")
@RequestMapping("/api/jar")
@RequiredArgsConstructor
@SaCheckLogin
public class JarController {

    private final TaskService taskService;

    @PostMapping("/udf/generateJar")
    @ApiOperation("Generate jar")
    public Result<Map<String, List<String>>> generateJar() {
        List<Task> allUDF = taskService.getAllUDF();
        List<UDF> udfCodes = allUDF.stream()
                .map(task -> UDF.builder()
                        .code(task.getStatement())
                        .className(task.getSavePointPath())
                        .functionLanguage(
                                FunctionLanguage.valueOf(task.getDialect().toUpperCase()))
                        .build())
                .collect(Collectors.toList());
        Map<String, List<String>> resultMap = UDFUtil.buildJar(udfCodes);
        String msg = StrUtil.format(
                "udf jar生成成功，jar文件在{}；\n本次成功 class:{}。\n失败 class:{}",
                PathConstant.UDF_JAR_TMP_PATH,
                resultMap.get("success"),
                resultMap.get("failed"));
        return Result.succeed(resultMap, msg);
    }
}
