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
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SysConfigController
 *
 * @since 2021/11/18
 */
@Slf4j
@RestController
@RequestMapping("/api/sysConfig")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /** 批量删除 */
    @PostMapping("/modifyConfig")
    public Result<Void> modifyConfig(@RequestBody Dict params) {
        sysConfigService.updateSysConfigByKv(params.getStr("key"), params.getStr("value"));
        return Result.succeed();
    }

    /** 获取所有配置 */
    @GetMapping("/getAll")
    public Result<Map<String, List<Configuration<?>>>> getAll() {
        Map<String, List<Configuration<?>>> all = sysConfigService.getAll();
        Map<String, List<Configuration<?>>> map =
                MapUtil.map(
                        all,
                        (k, v) -> v.stream().map(Configuration::show).collect(Collectors.toList()));
        return Result.succeed(map);
    }
}
