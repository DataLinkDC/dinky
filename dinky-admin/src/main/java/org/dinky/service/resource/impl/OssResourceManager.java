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

package org.dinky.service.resource.impl;

import org.dinky.data.exception.BusException;
import org.dinky.data.exception.DinkyException;
import org.dinky.oss.OssTemplate;
import org.dinky.service.resource.BaseResourceManager;

import java.io.File;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;

public class OssResourceManager implements BaseResourceManager {
    OssTemplate ossTemplate;

    @Override
    public void remove(String path) {
        getOssTemplate().removeObject(getOssTemplate().getBucketName(), getFilePath(path));
    }

    @Override
    public void rename(String path, String newPath) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                getOssTemplate().getBucketName(),
                getFilePath(path),
                getOssTemplate().getBucketName(),
                getFilePath(newPath));
        getOssTemplate().getAmazonS3().copyObject(copyObjectRequest);
        DeleteObjectRequest deleteObjectRequest =
                new DeleteObjectRequest(getOssTemplate().getBucketName(), getFilePath(path));
        getOssTemplate().getAmazonS3().deleteObject(deleteObjectRequest);
    }

    @Override
    public void putFile(String path, MultipartFile file) {
        try {
            getOssTemplate().putObject(getOssTemplate().getBucketName(), getFilePath(path), file.getInputStream());
        } catch (Exception e) {
            throw new DinkyException(e);
        }
    }

    @Override
    public void putFile(String path, File file) {
        try {
            getOssTemplate()
                    .putObject(getOssTemplate().getBucketName(), getFilePath(path), FileUtil.getInputStream(file));
        } catch (Exception e) {
            throw new DinkyException(e);
        }
    }

    @Override
    public String getFileContent(String path) {
        return IoUtil.readUtf8(readFile(path));
    }

    @Override
    public InputStream readFile(String path) {
        return getOssTemplate()
                .getObject(getOssTemplate().getBucketName(), getFilePath(path))
                .getObjectContent();
    }

    public OssTemplate getOssTemplate() {
        if (ossTemplate == null && instances.getResourcesEnable().getValue()) {
            throw BusException.valueOf("Resource configuration error, OSS is not enabled");
        }
        return ossTemplate;
    }

    public void setOssTemplate(OssTemplate ossTemplate) {
        this.ossTemplate = ossTemplate;
    }
}
