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

import org.dinky.common.result.Result;
import org.dinky.model.Configuration;
import org.dinky.service.SysConfigService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.lang.Dict;
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
        return Result.succeed(sysConfigService.getAll(), "获取成功");
    }
}
