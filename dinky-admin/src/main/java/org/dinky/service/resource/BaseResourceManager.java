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

package org.dinky.service.resource;

import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.oss.OssTemplate;
import org.dinky.service.resource.impl.HdfsResourceManager;
import org.dinky.service.resource.impl.LocalResourceManager;
import org.dinky.service.resource.impl.OssResourceManager;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;

public interface BaseResourceManager {
    SystemConfiguration instances = SystemConfiguration.getInstances();

    void remove(String path);

    void rename(String path, String newPath);

    void putFile(String path, MultipartFile file);

    void putFile(String path, File file);

    String getFileContent(String path);

    InputStream readFile(String path);

    static BaseResourceManager getInstance() {
        switch (SystemConfiguration.getInstances().getResourcesModel().getValue()) {
            case HDFS:
                return Singleton.get(HdfsResourceManager.class);
            case OSS:
                return Singleton.get(OssResourceManager.class);
            case LOCAL:
                return Singleton.get(LocalResourceManager.class);
            default:
                return null;
        }
    }

    static void initResourceManager() {
        switch (instances.getResourcesModel().getValue()) {
            case LOCAL:
                Singleton.get(LocalResourceManager.class);
            case OSS:
                OssTemplate template = new OssTemplate(instances.getOssProperties());
                Singleton.get(OssResourceManager.class).setOssTemplate(template);
                break;
            case HDFS:
                final Configuration configuration = new Configuration();
                Charset charset = Charset.defaultCharset();
                String coreSite = instances.getResourcesHdfsCoreSite().getValue();
                Opt.ofBlankAble(coreSite).ifPresent(x -> configuration.addResource(IoUtil.toStream(x, charset), true));
                String hdfsSite = instances.getResourcesHdfsHdfsSite().getValue();
                Opt.ofBlankAble(hdfsSite).ifPresent(x -> configuration.addResource(IoUtil.toStream(x, charset), true));
                if (StrUtil.isEmpty(coreSite)) {
                    configuration.set(
                            "fs.defaultFS",
                            instances.getResourcesHdfsDefaultFS().getValue());
                }
                try {
                    FileSystem fileSystem = FileSystem.get(
                            FileSystem.getDefaultUri(configuration),
                            configuration,
                            instances.getResourcesHdfsUser().getValue());
                    Singleton.get(HdfsResourceManager.class).setHdfs(fileSystem);
                } catch (Exception e) {
                    throw new DinkyException(e);
                }
        }
    }

    default String getFilePath(String path) {
        return FileUtil.normalize(
                FileUtil.file(instances.getResourcesUploadBasePath().getValue(), path)
                        .toString());
    }
}
