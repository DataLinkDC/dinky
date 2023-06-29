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

import org.dinky.process.exception.DinkyException;
import org.dinky.service.resource.BaseResourceManager;
import org.dinky.utils.OssTemplate;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

public class OssResourceManager implements BaseResourceManager {
    OssTemplate ossTemplate;

    @Override
    public void remove(String path) {
        ossTemplate.removeObject(ossTemplate.getBucketName(), getFile(path));
    }

    @Override
    public void rename(String path, String newPath) {
        CopyObjectRequest copyObjectRequest =
                new CopyObjectRequest(
                        ossTemplate.getBucketName(),
                        getFile(path),
                        ossTemplate.getBucketName(),
                        getFile(newPath));
        ossTemplate.getAmazonS3().copyObject(copyObjectRequest);
        DeleteObjectRequest deleteObjectRequest =
                new DeleteObjectRequest(ossTemplate.getBucketName(), getFile(path));
        ossTemplate.getAmazonS3().deleteObject(deleteObjectRequest);
    }

    @Override
    public void putFile(String path, MultipartFile file) {
        try {
            ossTemplate.putObject(
                    ossTemplate.getBucketName(), getFile(path), file.getInputStream());
        } catch (Exception e) {
            throw new DinkyException(e);
        }
    }

    @Override
    public String getFileContent(String path) {
        return ossTemplate.getObjectURL(ossTemplate.getBucketName(), getFile(path));
    }

    public void setOssTemplate(OssTemplate ossTemplate) {
        this.ossTemplate = ossTemplate;
    }
}
