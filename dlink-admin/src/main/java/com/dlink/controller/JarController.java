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

package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.function.constant.PathConstant;
import com.dlink.function.data.model.UDF;
import com.dlink.function.util.UDFUtil;
import com.dlink.model.Jar;
import com.dlink.model.Task;
import com.dlink.service.JarService;
import com.dlink.service.TaskService;

import org.apache.flink.table.catalog.FunctionLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * JarController
 *
 * @author wenmo
 * @since 2021/11/13
 **/
@Slf4j
@RestController
@RequestMapping("/api/jar")
public class JarController {

    @Autowired
    private JarService jarService;

    @Autowired
    private TaskService taskService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Jar jar) throws Exception {
        if (jarService.saveOrUpdate(jar)) {
            return Result.succeed("新增成功");
        } else {
            return Result.failed("新增失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<Jar> listJars(@RequestBody JsonNode para) {
        return jarService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!jarService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody Jar jar) throws Exception {
        jar = jarService.getById(jar.getId());
        return Result.succeed(jar, "获取成功");
    }

    /**
     * 获取可用的jar列表
     */
    @GetMapping("/listEnabledAll")
    public Result listEnabledAll() {
        List<Jar> jars = jarService.listEnabledAll();
        return Result.succeed(jars, "获取成功");
    }

    @PostMapping("/udf/generateJar")
    public Result<Map<String, List<String>>> generateJar() {
        List<Task> allUDF = taskService.getAllUDF();
        List<UDF> udfCodes = allUDF.stream().map(task -> {
            return UDF.builder().code(task.getStatement()).className(task.getSavePointPath())
                    .functionLanguage(FunctionLanguage.valueOf(task.getDialect().toUpperCase())).build();
        }).collect(Collectors.toList());
        Map<String, List<String>> resultMap = UDFUtil.buildJar(udfCodes);
        String msg = StrUtil.format("udf jar生成成功，jar文件在{}；\n本次成功 class:{}。\n失败 class:{}", PathConstant.UDF_JAR_TMP_PATH,
                resultMap.get("success"), resultMap.get("failed"));
        return Result.succeed(resultMap, msg);
    }
}
