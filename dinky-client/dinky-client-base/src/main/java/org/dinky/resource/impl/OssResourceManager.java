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

package org.dinky.resource.impl;

import org.dinky.data.exception.BusException;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.ResourcesVO;
import org.dinky.oss.OssTemplate;
import org.dinky.resource.BaseResourceManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

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
    public void putFile(String path, InputStream fileStream) {
        try {
            getOssTemplate().putObject(getOssTemplate().getBucketName(), getFilePath(path), fileStream);
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
    public List<ResourcesVO> getFullDirectoryStructure(int rootId) {
        String basePath = getBasePath();

        List<S3ObjectSummary> listBucketObjects =
                getOssTemplate().listBucketObjects(getOssTemplate().getBucketName(), basePath);
        Map<Integer, ResourcesVO> resourcesMap = new HashMap<>();

        for (S3ObjectSummary obj : listBucketObjects) {
            obj.setKey(obj.getKey().replace(basePath, ""));
            if (obj.getKey().isEmpty()) {
                continue;
            }
            String[] split = obj.getKey().split("/");
            String parent = "";
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                int pid = parent.isEmpty() ? rootId : parent.hashCode();
                parent = parent + "/" + s;
                ResourcesVO.ResourcesVOBuilder builder = ResourcesVO.builder()
                        .id(parent.hashCode())
                        .pid(pid)
                        .fullName(parent)
                        .fileName(s)
                        .isDirectory(obj.getKey().endsWith("/"))
                        .size(obj.getSize());
                if (i == split.length - 1) {
                    builder.isDirectory(obj.getKey().endsWith("/"));
                } else {
                    builder.isDirectory(true);
                }
                resourcesMap.put(parent.hashCode(), builder.build());
            }
        }
        return new ArrayList<>(resourcesMap.values());
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
