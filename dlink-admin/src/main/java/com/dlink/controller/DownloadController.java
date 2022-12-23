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

import com.dlink.assertion.Asserts;
import com.dlink.exception.BusException;
import com.dlink.function.constant.PathConstant;
import com.dlink.function.util.ZipWriter;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
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
    public void downloadJavaUDF(@PathVariable Integer taskId, HttpServletResponse resp) {
        if (Asserts.isNull(taskId)) {
            throw new BusException("task id can not null!");
        }
        String udfPackagePath = PathConstant.getUdfPackagePath(taskId);
        File depManifestFile = FileUtil.file(udfPackagePath + PathConstant.DEP_MANIFEST);
        if (!depManifestFile.exists()) {
            return;
        }
        JSONObject jsonObject = new JSONObject(FileUtil.readUtf8String(depManifestFile));
        JSONArray jars = jsonObject.getJSONArray("jars");
        List<String> filePath = jars.stream().map(Convert::toStr).collect(Collectors.toList());
        String[] jarNameList = filePath.stream().map(FileUtil::getName).map(x -> "jar/" + x).toArray(String[]::new);

        File zipFile = FileUtil.file(udfPackagePath + PathConstant.DEP_ZIP);
        InputStream[] inputStreams = filePath.stream().map(FileUtil::getInputStream).toArray(InputStream[]::new);
        try (ZipWriter zip = new ZipWriter(zipFile, Charset.defaultCharset())) {
            zip.add(jarNameList, inputStreams);
            zip.add(depManifestFile.getName(), FileUtil.getInputStream(depManifestFile));
        }
        ServletUtil.write(resp, FileUtil.getInputStream(zipFile));
        FileUtil.del(zipFile);
    }

    @GetMapping("downloadPythonUDF/{taskId}")
    public void downloadPythonUDF(@PathVariable Integer taskId, HttpServletResponse resp) {
        ServletUtil.write(resp, FileUtil.file(PathConstant.getUdfPackagePath(taskId) + PathConstant.UDF_PYTHON_NAME));
    }
}
