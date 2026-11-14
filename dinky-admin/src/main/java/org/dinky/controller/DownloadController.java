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
import org.dinky.data.model.Configuration;
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

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
    public void downloadFromRs(
            String path, HttpServletResponse resp, @RequestHeader(value = "token", required = false) String token) {
        if (!isResourceRequestAllowed(token)) {
            throw new BusException("token is not correct");
        }
        try {
            File file = resolveInsideResourceDirectory(path);
            ResourcesModelEnum model =
                    SystemConfiguration.getInstances().getResourcesModel().getValue();
            if (model == null || model == ResourcesModelEnum.LOCAL) {
                if (!file.isFile()) {
                    return;
                }
                ServletUtil.write(resp, FileUtil.getInputStream(file));
                return;
            }
            String relative = resourceRootDir()
                    .toPath()
                    .relativize(file.toPath())
                    .toString()
                    .replace('\\', '/');
            ServletUtil.write(resp, BaseResourceManager.getInstance().readFile(relative));
        } catch (IOException e) {
            log.error("download file failed", e);
            throw new BusException("download file failed");
        }
    }

    @PostMapping("uploadFromRsByLocal")
    @ApiOperation("Upload From Resource By Local")
    public Result<Void> uploadFromRs(
            String path,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "token", required = false) String token) {
        if (!isResourceRequestAllowed(token)) {
            return Result.failed("token is not correct");
        }
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        if (!systemConfiguration.getResourcesEnable().getValue()
                || !systemConfiguration.getResourcesModel().getValue().equals(ResourcesModelEnum.LOCAL)) {
            return Result.failed("resources model is not local or resources is not enable");
        }

        try {
            File dest = resolveInsideResourceDirectory(path);
            File parent = dest.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.transferTo(dest);
            return Result.succeed();
        } catch (BusException e) {
            return Result.failed(e.getMessage());
        } catch (IOException e) {
            log.error("upload file failed", e);
            throw new BusException("upload file failed");
        }
    }

    /**
     * Allow a login session or a configured application token.
     */
    private boolean isResourceRequestAllowed(String token) {
        if (hasLoginSession()) {
            return true;
        }
        return isConfiguredApplicationToken(token);
    }

    private boolean hasLoginSession() {
        try {
            return StpUtil.isLogin();
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isConfiguredApplicationToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        Configuration<String> dinkyToken = SystemConfiguration.getInstances().getDinkyToken();
        String configured = dinkyToken.getValue();
        if (StrUtil.isBlank(configured)) {
            return false;
        }
        String factoryValue = dinkyToken.getDefaultValue();
        if (StrUtil.equals(configured, factoryValue)) {
            return false;
        }
        return StrUtil.equals(configured, token);
    }

    /**
     * Resolve a local resource path and keep the result inside the configured resource directory.
     */
    private File resolveInsideResourceDirectory(String path) throws IOException {
        if (StrUtil.isBlank(path)) {
            throw new BusException("path is invalid");
        }
        File root = resourceRootDir();
        File asGiven = new File(path).getCanonicalFile();
        if (isInsideResourceDirectory(root, asGiven)) {
            return asGiven;
        }
        String relative = path;
        while (relative.startsWith("/") || relative.startsWith("\\")) {
            relative = relative.substring(1);
        }
        File underRoot = new File(root, relative).getCanonicalFile();
        if (isInsideResourceDirectory(root, underRoot)) {
            return underRoot;
        }
        throw new BusException("path is invalid");
    }

    private File resourceRootDir() throws IOException {
        Configuration<String> basePath = SystemConfiguration.getInstances().getResourcesUploadBasePath();
        String value = basePath.getValue();
        if (StrUtil.isBlank(value)) {
            value = basePath.getDefaultValue();
        }
        if (StrUtil.isBlank(value)) {
            throw new BusException("path is invalid");
        }
        return new File(value).getCanonicalFile();
    }

    private static boolean isInsideResourceDirectory(File root, File file) {
        String rootPath = root.getPath();
        String filePath = file.getPath();
        return filePath.equals(rootPath) || filePath.startsWith(rootPath + File.separator);
    }
}
