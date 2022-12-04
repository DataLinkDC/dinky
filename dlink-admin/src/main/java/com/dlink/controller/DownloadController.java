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

import com.dlink.function.constant.PathConstant;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadController {
    @GetMapping("downloadDepJar/{taskId}")
    public void downloadJavaUDF(@PathVariable Integer taskId, HttpServletResponse resp) throws IOException {
        ServletUtil.write(resp, FileUtil.file(PathConstant.getUdfPackagePath(taskId) + PathConstant.UDF_JAR_NAME));
    }

    @GetMapping("downloadPythonUDF/{taskId}")
    public void downloadPythonUDF(@PathVariable Integer taskId, HttpServletResponse resp) throws IOException {
        ServletUtil.write(resp, FileUtil.file(PathConstant.getUdfPackagePath(taskId) + PathConstant.UDF_PYTHON_NAME));
    }
}
