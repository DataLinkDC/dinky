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
import org.dinky.service.resource.BaseResourceManager;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.IoUtil;

public class HdfsResourceManager implements BaseResourceManager {
    FileSystem hdfs;

    @Override
    public void remove(String path) {
        try {
            getHdfs().delete(new Path(getFile(path)), true);
        } catch (IOException e) {
            throw BusException.valueOf("file.delete.failed", e);
        }
    }

    @Override
    public void rename(String path, String newPath) {
        try {
            getHdfs().rename(new Path(getFile(path)), new Path(getFile(newPath)));
        } catch (IOException e) {
            throw BusException.valueOf("file.rename.failed", e);
        }
    }

    @Override
    public void putFile(String path, MultipartFile file) {
        try {
            FSDataOutputStream stream = getHdfs().create(new Path(getFile(path)), true);
            stream.write(file.getBytes());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            throw BusException.valueOf("file.upload.failed", e);
        }
    }

    @Override
    public String getFileContent(String path) {
        try {
            return IoUtil.readUtf8(getHdfs().open(new Path(getFile(path))));
        } catch (IOException e) {
            throw BusException.valueOf("file.read.failed", e);
        }
    }

    public FileSystem getHdfs() {
        if (hdfs == null && instances.getResourcesEnable().getValue()) {
            throw BusException.valueOf("Resource configuration error, HDFS is not enabled");
        }
        return hdfs;
    }

    public synchronized void setHdfs(FileSystem hdfs) {
        this.hdfs = hdfs;
    }
}
