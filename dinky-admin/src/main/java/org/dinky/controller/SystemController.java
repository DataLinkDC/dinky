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
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.result.Result;
import org.dinky.service.SystemService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/** SystemController */
@RestController
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
    public Result<List<TreeNodeDTO>> listLogDir() {
        return Result.succeed(systemService.listLogDir());
    }

    /**
     * get root log file content
     *
     * @return {@link Result} <{@link String}>
     */
    @GetMapping("/getRootLog")
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
    public Result<String> readFile(@RequestParam String path) {
        return Result.data(systemService.readFile(path));
    }
}
