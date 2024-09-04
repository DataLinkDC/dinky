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

import org.apache.flink.core.fs.BlockLocation;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.FileSystemKind;
import org.apache.flink.core.fs.Path;

import java.io.IOException;
import java.net.URI;

public class HttpFileSystem extends FileSystem {
    public static final HttpFileSystem INSTANCE = new HttpFileSystem();

    private HttpFileSystem() {}

    @Override
    public Path getWorkingDirectory() {
        return null;
    }

    @Override
    public Path getHomeDirectory() {
        return null;
    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public FileStatus getFileStatus(Path f) throws IOException {
        return null;
    }

    @Override
    public BlockLocation[] getFileBlockLocations(FileStatus file, long start, long len) throws IOException {
        return new BlockLocation[0];
    }

    @Override
    public FSDataInputStream open(Path f, int bufferSize) throws IOException {
        return null;
    }

    @Override
    public FSDataInputStream open(Path f) throws IOException {
        return null;
    }

    @Override
    public FileStatus[] listStatus(Path f) throws IOException {
        return new FileStatus[0];
    }

    @Override
    public boolean delete(Path f, boolean recursive) throws IOException {
        return false;
    }

    @Override
    public boolean mkdirs(Path f) throws IOException {
        return false;
    }

    @Override
    public FSDataOutputStream create(Path f, WriteMode overwriteMode) throws IOException {
        return new HttpFsDataOutputStream(f);
    }

    @Override
    public boolean rename(Path src, Path dst) throws IOException {
        return false;
    }

    @Override
    public boolean isDistributedFS() {
        return false;
    }

    @Override
    public FileSystemKind getKind() {
        return null;
    }
}
