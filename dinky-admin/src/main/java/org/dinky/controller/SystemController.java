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

import com.dlink.common.result.Result;
import com.dlink.constant.DirConstant;
import com.dlink.service.SystemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemController
 *
 * @author wenmo
 * @since 2022/10/15 19:20
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/listLogDir")
    public Result listLogDir() {
        return Result.data(systemService.listLogDir());
    }

    @GetMapping("/getRootLog")
    public Result getRootLog() {
        return Result.data(systemService.readFile(DirConstant.ROOT_LOG_PATH));
    }

    @GetMapping("/listDirByPath")
    public Result listDirByPath(@RequestParam String path) {
        return Result.data(systemService.listDirByPath(path));
    }

    @GetMapping("/readFile")
    public Result readFile(@RequestParam String path) {
        return Result.data(systemService.readFile(path));
    }
}
