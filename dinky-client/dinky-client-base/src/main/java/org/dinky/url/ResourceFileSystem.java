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

import org.dinky.resource.BaseResourceManager;

import org.apache.flink.api.common.io.InputStreamFSInputWrapper;
import org.apache.flink.core.fs.BlockLocation;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.FileSystemKind;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.local.LocalFileStatus;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceFileSystem extends FileSystem {

    public static final URI URI_SCHEMA = URI.create("rs:/");
    private static ResourceFileSystem INSTANCE;

    public static synchronized ResourceFileSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourceFileSystem();
        }
        return INSTANCE;
    }

    public BaseResourceManager getBaseResourceManager() {
        return BaseResourceManager.getInstance();
    }

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
        return URI_SCHEMA;
    }

    @Override
    public FileStatus getFileStatus(Path f) throws IOException {
        return new LocalFileStatus(getFile(f), this);
    }

    protected File getFile(Path f) {
        return new File(getBaseResourceManager().getFilePath(f.getPath()));
    }

    @Override
    public BlockLocation[] getFileBlockLocations(FileStatus file, long start, long len) throws IOException {
        return new BlockLocation[0];
    }

    @Override
    public FSDataInputStream open(Path f, int bufferSize) throws IOException {
        return open(f);
    }

    @Override
    public FSDataInputStream open(Path f) throws IOException {
        return new InputStreamFSInputWrapper(getBaseResourceManager().readFile(f.getPath()));
    }

    @Override
    public FileStatus[] listStatus(Path f) throws IOException {
        Path path = new Path(getBaseResourceManager().getFilePath(f.getPath()));
        return getBaseResourceManager().getFileSystem().listStatus(path);
    }

    @Override
    public boolean delete(Path f, boolean recursive) throws IOException {
        try {
            getBaseResourceManager().remove(f.getPath());
            return true;
        } catch (Exception e) {
            log.error("delete file failed, path: {}", f.getPath(), e);
        }
        return false;
    }

    @Override
    public boolean mkdirs(Path f) throws IOException {
        return false;
    }

    @Override
    public FSDataOutputStream create(Path f, WriteMode overwriteMode) throws IOException {
        Path path = new Path(getBaseResourceManager().getFilePath(f.getPath()));
        return getBaseResourceManager().getFileSystem().create(path, overwriteMode);
    }

    @Override
    public boolean rename(Path src, Path dst) throws IOException {
        try {
            getBaseResourceManager().rename(src.getPath(), dst.getPath());
            return true;
        } catch (Exception e) {
            log.error("rename file failed, src: {}, dst: {}", src.getPath(), dst.getPath(), e);
        }
        return false;
    }

    @Override
    public boolean isDistributedFS() {
        return true;
    }

    @Override
    public FileSystemKind getKind() {
        return FileSystemKind.OBJECT_STORE;
    }

    public static ResourceFileSystem getSharedInstance() {
        return getInstance();
    }
}
