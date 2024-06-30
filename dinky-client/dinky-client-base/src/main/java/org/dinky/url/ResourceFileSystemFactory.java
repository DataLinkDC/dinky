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

package org.dinky.url;

import org.dinky.data.model.SystemConfiguration;

import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.FileSystemFactory;

import java.io.IOException;
import java.net.URI;

import com.google.auto.service.AutoService;

import lombok.extern.slf4j.Slf4j;

@AutoService(FileSystemFactory.class)
@Slf4j
public class ResourceFileSystemFactory implements FileSystemFactory {
    @Override
    public String getScheme() {
        return ResourceFileSystem.URI_SCHEMA.getScheme();
    }

    @Override
    public FileSystem create(URI fsUri) throws IOException {
        Boolean enable = SystemConfiguration.getInstances().getResourcesEnable().getValue();
        if (enable == null || !enable) {
            log.warn("rs protocol startup failed, not initialized");
            return null;
        }
        return ResourceFileSystem.getSharedInstance();
    }
}
