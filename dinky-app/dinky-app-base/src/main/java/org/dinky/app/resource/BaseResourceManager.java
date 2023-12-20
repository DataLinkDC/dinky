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

package org.dinky.app.resource;

import org.dinky.app.resource.impl.HdfsResourceManager;
import org.dinky.app.resource.impl.OssResourceManager;
import org.dinky.data.model.SystemConfiguration;

import java.io.InputStream;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;

public interface BaseResourceManager {
    SystemConfiguration instances = SystemConfiguration.getInstances();

    InputStream readFile(String path);

    static BaseResourceManager getInstance() {
        switch (SystemConfiguration.getInstances().getResourcesModel().getValue()) {
            case HDFS:
                return Singleton.get(HdfsResourceManager.class);
            case OSS:
                return Singleton.get(OssResourceManager.class);
            default:
                return null;
        }
    }

    default String getFilePath(String path) {
        return FileUtil.normalize(
                FileUtil.file(instances.getResourcesUploadBasePath().getValue(), path)
                        .toString());
    }
}
