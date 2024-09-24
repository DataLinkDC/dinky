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

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.DirConstant;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.FlinkUdfManifest;
import org.dinky.data.model.ResourcesModelEnum;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.Result;
import org.dinky.function.constant.PathConstant;
import org.dinky.function.util.ZipWriter;
import org.dinky.resource.BaseResourceManager;
import org.dinky.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.servlet.ServletUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @since 0.7.0
 */
@Slf4j
@RestController
@Api(tags = "UDF & App Jar Controller")
@RequestMapping("/download")
public class DownloadController {
    @GetMapping("downloadDepJar/{taskId}")
    @ApiOperation("Download UDF Jar")
    public void downloadJavaUDF(@PathVariable Integer taskId, HttpServletResponse resp) {
        if (Asserts.isNull(taskId)) {
            throw new BusException("task id can not null!");
        }
        String udfPackagePath = PathConstant.getUdfPackagePath(taskId);
        File depManifestFile = FileUtil.file(udfPackagePath + PathConstant.DEP_MANIFEST);
        if (!depManifestFile.exists()) {
            return;
        }
        FlinkUdfManifest flinkUdfManifest =
                JsonUtils.toBean(FileUtil.readUtf8String(depManifestFile), FlinkUdfManifest.class);
        List<String> filePath =
                flinkUdfManifest.getJars().stream().map(Convert::toStr).collect(Collectors.toList());
        List<String> pyFilePath =
                flinkUdfManifest.getPythonFiles().stream().map(Convert::toStr).collect(Collectors.toList());
        String[] jarNameList =
                filePath.stream().map(FileUtil::getName).map(x -> "jar/" + x).toArray(String[]::new);
        String[] pyFileNameList =
                pyFilePath.stream().map(FileUtil::getName).map(x -> "py/" + x).toArray(String[]::new);

        File zipFile = FileUtil.file(udfPackagePath + PathConstant.DEP_ZIP);
        InputStream[] inputStreams =
                filePath.stream().map(FileUtil::getInputStream).toArray(InputStream[]::new);
        InputStream[] pyInputStreams =
                pyFilePath.stream().map(FileUtil::getInputStream).toArray(InputStream[]::new);
        try (ZipWriter zip = new ZipWriter(zipFile, Charset.defaultCharset())) {
            if (ArrayUtil.isNotEmpty(jarNameList)) {
                zip.add(jarNameList, inputStreams);
            }
            if (ArrayUtil.isNotEmpty(pyFileNameList)) {
                zip.add(pyFileNameList, pyInputStreams);
            }
            zip.add(depManifestFile.getName(), FileUtil.getInputStream(depManifestFile));
        }
        ServletUtil.write(resp, FileUtil.getInputStream(zipFile));
        FileUtil.del(zipFile);
    }

    /**
     * Provide Docker to download dinky-app.jar via HTTP request
     *
     * @param version version of dinky-app.jar
     * @param resp    resp
     */
    @GetMapping("downloadAppJar/{version}")
    @ApiOperation("Download App Jar")
    public void downloadAppJar(@PathVariable String version, HttpServletResponse resp) {
        List<File> files = FileUtil.loopFiles(DirConstant.getRootPath() + "/jar", pathname -> pathname.getName()
                .contains("dinky-app-" + version));
        if (CollUtil.isNotEmpty(files)) {
            ServletUtil.write(resp, files.get(0));
        }
    }

    @GetMapping("downloadFromRs")
    @ApiOperation("Download From Resource")
    @SaIgnore
    public void downloadFromRs(String path, HttpServletResponse resp) {
        InputStream inputStream = BaseResourceManager.getInstance().readFile(path);
        ServletUtil.write(resp, inputStream);
    }

    @PostMapping("uploadFromRsByLocal")
    @ApiOperation("Upload From Resource By Local")
    @SaIgnore
    public Result<Void> uploadFromRs(
            String path, @RequestParam("file") MultipartFile file, @RequestHeader("token") String token) {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        if (!systemConfiguration.getDinkyToken().getValue().equals(token)) {
            return Result.failed("token is not correct");
        }
        if (!systemConfiguration.getResourcesEnable().getValue()
                || !systemConfiguration.getResourcesModel().getValue().equals(ResourcesModelEnum.LOCAL)) {
            return Result.failed("resources model is not local or resources is not enable");
        }

        try {
            File dest = new File(path);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            return Result.succeed();
        } catch (IOException e) {
            log.error("upload file failed", e);
            throw new BusException("upload file failed");
        }
    }
}
