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

package org.dinky.service.impl;

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.UploadFileConstant;
import org.dinky.data.exception.BusException;
import org.dinky.service.FileUploadService;
import org.dinky.service.UploadFileRecordService;
import org.dinky.utils.FilePathUtil;
import org.dinky.utils.HdfsUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/** FileUploadServiceImpl */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource private UploadFileRecordService uploadFileRecordService;

    @Override
    public void upload(MultipartFile file, String dir, Byte fileType) {
        byte target = getTarget(dir, fileType);
        if (Objects.equals(target, UploadFileConstant.TARGET_LOCAL)) {
            FileUtil.mkdir(dir);
        }

        String filePath = FilePathUtil.addFileSeparator(dir) + file.getOriginalFilename();
        switch (target) {
            case UploadFileConstant.TARGET_LOCAL:
                {
                    try {
                        file.transferTo(new File(filePath));
                        if (uploadFileRecordService.saveOrUpdateFile(
                                file.getOriginalFilename(),
                                dir,
                                filePath,
                                fileType,
                                UploadFileConstant.TARGET_LOCAL)) {
                            return;
                        } else {
                            throw BusException.valueOfMsg("数据库异常");
                        }
                    } catch (IOException e) {
                        throw BusException.valueOf("file.upload.failed", e);
                    }
                }
            case UploadFileConstant.TARGET_HDFS:
                {
                    HdfsUtil.uploadFile(filePath, file);
                    if (uploadFileRecordService.saveOrUpdateFile(
                            file.getOriginalFilename(),
                            dir,
                            filePath,
                            fileType,
                            UploadFileConstant.TARGET_HDFS)) {
                        return;
                    } else {
                        throw BusException.valueOfMsg("数据库异常");
                    }
                }
            default:
                throw BusException.valueOfMsg("非法的上传文件目的地");
        }
    }

    @Override
    public void upload(MultipartFile file, Byte fileType) {
        String dir = UploadFileConstant.getDirPath(fileType);
        if (StringUtils.isEmpty(dir)) {
            throw BusException.valueOfMsg("非法的上传文件类型");
        }
        upload(file, dir, fileType);
    }

    @Override
    public void upload(MultipartFile[] files, String dir, Byte fileType) {
        if (StringUtils.isEmpty(dir)) {
            throw BusException.valueOfMsg("非法的上传文件类型");
        }
        if (files.length > 0) {
            for (MultipartFile file : files) {
                upload(file, dir, fileType);
            }
            if (!uploadFileRecordService.saveOrUpdateDir(dir, fileType, getTarget(dir, fileType))) {
                throw BusException.valueOfMsg("数据库异常");
            }
        } else {
            throw BusException.valueOfMsg("没有检测到要上传的文件");
        }
    }

    @Override
    public void uploadHdfs(MultipartFile file, String dir, String hadoopConfigPath) {
        if (Asserts.isNullString(dir)) {
            dir = UploadFileConstant.getDirPath(UploadFileConstant.FLINK_LIB_ID);
        }
        String filePath = FilePathUtil.addFileSeparator(dir) + file.getOriginalFilename();
        HdfsUtil.uploadFile(filePath, file, hadoopConfigPath);

        if (!uploadFileRecordService.saveOrUpdateFile(
                file.getOriginalFilename(),
                dir,
                filePath,
                UploadFileConstant.FLINK_LIB_ID,
                UploadFileConstant.TARGET_HDFS)) {
            throw BusException.valueOfMsg("数据库异常");
        }
    }

    @Override
    public void uploadHdfs(MultipartFile[] files, String dir, String hadoopConfigPath) {
        if (Asserts.isNotNull(files)) {
            try {
                for (MultipartFile file : files) {
                    uploadHdfs(file, dir, hadoopConfigPath);
                }
                if (!uploadFileRecordService.saveOrUpdateDir(
                        dir, UploadFileConstant.FLINK_LIB_ID, UploadFileConstant.TARGET_HDFS)) {
                    throw BusException.valueOfMsg("数据库异常");
                }
            } catch (Exception e) {
                throw BusException.valueOf("unknown.error", e);
            }
        }
    }

    @Override
    public void upload(MultipartFile[] files, Byte fileType) {
        String dir = UploadFileConstant.getDirPath(fileType);
        upload(files, dir, fileType);
    }

    /**
     * Get upload file target.
     *
     * @param dir If null, will return -1
     * @param fileType Internal upload file type, refer {@link UploadFileConstant}
     * @return Upload file target, refer {@link UploadFileConstant}
     */
    private byte getTarget(String dir, byte fileType) {
        byte target = UploadFileConstant.getTarget(fileType);
        if (target == -1) {
            target = FilePathUtil.getDirTarget(dir);
        }
        return target;
    }
}
