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

import org.dinky.data.constant.DirConstant;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.result.Result;
import org.dinky.service.SystemService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/** SystemInfoController */
@RestController
@Api(tags = "System Controller")
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    /**
     * All log files for this project
     *
     * @return {@link Result} <{@link List}<{@link TreeNodeDTO}>>
     */
    @GetMapping("/listLogDir")
    @ApiOperation("List Log Files")
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_INFO_LOG_LIST)
    public Result<List<TreeNodeDTO>> listLogDir() {
        return Result.succeed(systemService.listLogDir());
    }

    /**
     * get root log file content
     *
     * @return {@link Result} <{@link String}>
     */
    @GetMapping("/getRootLog")
    @ApiOperation("Get Root Log File Content")
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_INFO_ROOT_LOG)
    public Result<String> getRootLog() {
        return Result.data(systemService.readFile(DirConstant.ROOT_LOG_PATH));
    }

    /**
     * readFile by path
     *
     * @param path {@link String}
     * @return {@link Result} <{@link String}>
     */
    @GetMapping("/readFile")
    @ApiOperation("Read File By File Path")
    @ApiImplicitParam(name = "path", value = "File Path", required = true, dataType = "String")
    public Result<String> readFile(@RequestParam String path) {
        return Result.data(systemService.readFile(path));
    }

    @GetMapping("/queryAllClassLoaderJarFiles")
    @ApiOperation("Query All ClassLoader Jar Files")
    public Result<Map<String, List<String>>> queryAllClassLoaderJarFiles() {
        return Result.succeed(systemService.queryAllClassLoaderJarFiles());
    }
}
