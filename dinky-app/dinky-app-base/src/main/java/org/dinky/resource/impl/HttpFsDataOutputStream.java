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

import org.dinky.data.model.SystemConfiguration;

import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.local.LocalDataOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

public class HttpFsDataOutputStream extends FSDataOutputStream {
    private final String uuid = UUID.randomUUID().toString();

    private final Path path;
    final File file = FileUtil.file(FileUtil.getTmpDir(), "/dinky-tmp/" + uuid + ".tmp");
    final LocalDataOutputStream localDataOutputStream;

    public HttpFsDataOutputStream(Path f) throws IOException {
        this.path = f;
        FileUtil.mkParentDirs(file);
        localDataOutputStream = new LocalDataOutputStream(file);
    }

    @Override
    public void write(int b) throws IOException {
        localDataOutputStream.write(b);
    }

    @Override
    public long getPos() throws IOException {
        return localDataOutputStream.getPos();
    }

    @Override
    public void flush() throws IOException {
        localDataOutputStream.flush();
        sendFile();
    }

    private void sendFile() {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        try (HttpResponse httpResponse = HttpUtil.createPost(
                        systemConfiguration.getDinkyAddr().getValue() + "/download/uploadFromRsByLocal")
                .header("token", systemConfiguration.getDinkyToken().getValue())
                .form("file", file)
                .form("path", path.toString())
                .execute()) {
            httpResponse.body();
        }
    }

    @Override
    public void sync() throws IOException {
        localDataOutputStream.sync();
    }

    @Override
    public void close() throws IOException {
        localDataOutputStream.close();
        sendFile();
    }
}
