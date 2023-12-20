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

package org.dinky.app.resource.impl;

import org.dinky.app.resource.BaseResourceManager;
import org.dinky.data.exception.BusException;
import org.dinky.oss.OssTemplate;

import java.io.InputStream;

public class OssResourceManager implements BaseResourceManager {
    OssTemplate ossTemplate;

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
