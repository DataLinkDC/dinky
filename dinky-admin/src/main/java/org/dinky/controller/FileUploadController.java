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

import org.dinky.data.constant.UploadFileConstant;
import org.dinky.data.result.Result;
import org.dinky.service.FileUploadService;

import org.apache.commons.lang3.StringUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** FileUploadController */
@Slf4j
@RestController
@RequestMapping("/api/fileUpload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload file<br>
     *
     * @param files Multi files
     * @param dir Dir, default is empty. If not provide, please provide the 'fileType' value
     * @param fileType Please refer {@link UploadFileConstant}, default is -1. If not provide,
     *     please provide the 'dir' value
     * @return {@link Result}
     */
    @PostMapping
    public Result<Void> upload(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam(value = "dir", defaultValue = "", required = false) String dir,
            @RequestParam(value = "fileType", defaultValue = "-1", required = false)
                    Byte fileType) {
        if (!StringUtils.isEmpty(dir) && fileType != -1) {
            return Result.failed("不要同时指定 dir 和 fileType 参数");
        } else if (StringUtils.isEmpty(dir) && fileType == -1) {
            return Result.failed("dir 和 fileType 参数必选其一");
        }

        if (StringUtils.isEmpty(dir)) {
            fileUploadService.upload(files, fileType);
        } else {
            fileUploadService.upload(files, dir, fileType);
        }
        return Result.succeed();
    }

    /**
     * Upload hdfs file<br>
     *
     * @param files Multi files
     * @param dir Dir, default is empty. If not provide, please provide the 'fileType' value
     * @param hadoopConfigPath Please refer {@link UploadFileConstant}, default is -1. If not
     *     provide, please provide the 'dir' value
     * @return {@link Result}
     */
    @PostMapping(value = "hdfs")
    public Result<Void> uploadHdfs(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam(value = "dir", defaultValue = "", required = false) String dir,
            @RequestParam(value = "hadoopConfigPath", required = false) String hadoopConfigPath) {
        fileUploadService.uploadHdfs(files, dir, hadoopConfigPath);
        return Result.succeed();
    }
}
