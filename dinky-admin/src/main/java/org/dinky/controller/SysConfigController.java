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

import org.dinky.data.annotations.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Configuration;
import org.dinky.data.result.Result;
import org.dinky.service.SysConfigService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SysConfigController
 *
 * @since 2021/11/18
 */
@Slf4j
@RestController
@Api(tags = "System Config Controller")
@SaCheckLogin
@RequestMapping("/api/sysConfig")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /**
     * modify system config
     *
     * @param params {@link Dict}
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/modifyConfig")
    @ApiOperation("Modify System Config")
    @Log(title = "Modify System Config", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(name = "params", value = "System Config", dataType = "Dict")
    public Result<Void> modifyConfig(@RequestBody Dict params) {
        sysConfigService.updateSysConfigByKv(params.getStr("key"), params.getStr("value"));
        return Result.succeed(Status.MODIFY_SUCCESS);
    }

    /**
     * query all system config
     *
     * @return {@link Result}<{@link Map}>
     */
    @GetMapping("/getAll")
    @ApiOperation("Query All System Config List")
    @SaIgnore
    public Result<Map<String, List<Configuration<?>>>> getAll() {
        Map<String, List<Configuration<?>>> all = sysConfigService.getAll();
        Map<String, List<Configuration<?>>> map =
                MapUtil.map(all, (k, v) -> v.stream().map(Configuration::show).collect(Collectors.toList()));
        return Result.succeed(map);
    }

    @GetMapping("/getConfigByType")
    @ApiOperation("Query One Type System Config List By Key")
    @ApiImplicitParam(
            name = "type",
            value = "System Config Type",
            dataType = "String",
            required = true,
            example = "sys")
    public Result<List<Configuration<?>>> getOneTypeByKey(@RequestParam("type") String type) {
        Map<String, List<Configuration<?>>> all = sysConfigService.getAll();
        // Filter out configurations starting with type and return a list
        List<Configuration<?>> configList = all.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(type))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return Result.succeed(configList);
    }

    @GetMapping("/getNeededCfg")
    @ApiOperation("Get Needed Config")
    @SaIgnore
    public Result<Map<String, Object>> getNeededCfg() {
        return sysConfigService.getNeededCfg();
    }

    @PostMapping("/setInitConfig")
    @ApiOperation("Get Needed Config")
    @SaIgnore
    public Result<Void> setInitConfig(@RequestBody Map<String, Object> params) {
        return sysConfigService.setInitConfig(params);
    }
}
