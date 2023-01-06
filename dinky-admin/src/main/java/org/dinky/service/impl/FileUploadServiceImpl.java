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
import org.dinky.common.result.Result;
import org.dinky.constant.UploadFileConstant;
import org.dinky.model.CodeEnum;
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

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * FileUploadServiceImpl
 **/
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private UploadFileRecordService uploadFileRecordService;

    @Override
    public Result upload(MultipartFile file, String dir, Byte fileType) {
        byte target = getTarget(dir, fileType);
        if (Objects.equals(target, UploadFileConstant.TARGET_LOCAL)) {
            new File(dir).mkdirs();
        }

        String filePath = FilePathUtil.addFileSeparator(dir) + file.getOriginalFilename();
        switch (target) {
            case UploadFileConstant.TARGET_LOCAL: {
                try {
                    file.transferTo(new File(filePath));
                    if (uploadFileRecordService.saveOrUpdateFile(file.getOriginalFilename(), dir, filePath, fileType, UploadFileConstant.TARGET_LOCAL)) {
                        return Result.succeed("上传成功");
                    } else {
                        return Result.failed("数据库异常");
                    }
                } catch (IOException e) {
                    log.error("File " + file.getOriginalFilename() + " upload to local dir fail, exception is:\n" + ExceptionUtil.stacktraceToString(e));
                    return Result.failed("上传失败");
                }
            }
            case UploadFileConstant.TARGET_HDFS: {
                Result result = HdfsUtil.uploadFile(filePath, file);
                if (Objects.equals(result.getCode(), CodeEnum.SUCCESS.getCode())) {
                    if (uploadFileRecordService.saveOrUpdateFile(file.getOriginalFilename(), dir, filePath, fileType, UploadFileConstant.TARGET_HDFS)) {
                        return Result.succeed("上传成功");
                    } else {
                        return Result.failed("数据库异常");
                    }
                } else {
                    return result;
                }
            }
            default:
                return Result.failed("非法的上传文件目的地");
        }
    }

    @Override
    public Result upload(MultipartFile file, Byte fileType) {
        String dir = UploadFileConstant.getDirPath(fileType);
        if (StringUtils.isEmpty(dir)) {
            return Result.failed("非法的上传文件类型");
        }
        return upload(file, dir, fileType);
    }

    @Override
    public Result upload(MultipartFile[] files, String dir, Byte fileType) {
        if (files.length > 0) {
            for (MultipartFile file : files) {
                Result uploadResult = upload(file, dir, fileType);
                if (Objects.equals(uploadResult.getCode(), CodeEnum.ERROR.getCode())) {
                    return uploadResult;
                }
            }
            if (!uploadFileRecordService.saveOrUpdateDir(dir, fileType, getTarget(dir, fileType))) {
                return Result.failed("数据库异常");
            }
            return Result.succeed("全部上传成功");
        } else {
            return Result.succeed("没有检测到要上传的文件");
        }
    }

    @Override
    public Result uploadHdfs(MultipartFile file, String dir, String hadoopConfigPath) {
        String filePath = FilePathUtil.addFileSeparator(dir) + file.getOriginalFilename();
        Result result = HdfsUtil.uploadFile(filePath, file, hadoopConfigPath);
        if (Objects.equals(result.getCode(), CodeEnum.SUCCESS.getCode())) {
            if (uploadFileRecordService.saveOrUpdateFile(file.getOriginalFilename(), dir, filePath, UploadFileConstant.FLINK_LIB_ID, UploadFileConstant.TARGET_HDFS)) {
                return Result.succeed("上传成功");
            } else {
                return Result.failed("数据库异常");
            }
        } else {
            return result;
        }
    }

    @Override
    public Result uploadHdfs(MultipartFile[] files, String dir, String hadoopConfigPath) {
        if (Asserts.isNullString(dir)) {
            dir = UploadFileConstant.getDirPath(UploadFileConstant.FLINK_LIB_ID);
        }
        if (files.length > 0) {
            for (MultipartFile file : files) {
                Result uploadResult = uploadHdfs(file, dir, hadoopConfigPath);
                if (Objects.equals(uploadResult.getCode(), CodeEnum.ERROR.getCode())) {
                    return uploadResult;
                }
            }
            if (!uploadFileRecordService.saveOrUpdateDir(dir, UploadFileConstant.FLINK_LIB_ID, UploadFileConstant.TARGET_HDFS)) {
                return Result.failed("数据库异常");
            }
            return Result.succeed("全部上传成功");
        } else {
            return Result.succeed("没有检测到要上传的文件");
        }
    }

    @Override
    public Result upload(MultipartFile[] files, Byte fileType) {
        String dir = UploadFileConstant.getDirPath(fileType);
        if (StringUtils.isEmpty(dir)) {
            return Result.failed("非法的上传文件类型");
        }
        return upload(files, dir, fileType);
    }

    /**
     * Get upload file target.
     *
     * @param dir      If null, will return -1
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
