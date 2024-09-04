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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.local.LocalFileStatus;
import org.apache.flink.core.fs.local.LocalFileSystem;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.ResourcesVO;
import org.dinky.resource.BaseResourceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class LocalResourceManager implements BaseResourceManager {
    @Override
    public void remove(String path) {
        try {
            boolean isSuccess = FileUtil.del(getFilePath(path));
            if (!isSuccess) {
                throw new BusException("remove file failed,reason unknown");
            }
        } catch (IORuntimeException e) {
            throw new BusException(Status.RESOURCE_FILE_DELETE_FAILED, e);
        }
    }

    @Override
    public void rename(String path, String newPath) {
        try {
            String newName = FileUtil.getName(newPath);
            FileUtil.rename(new File(getFilePath(path)), newName, true);
        } catch (Exception e) {
            throw new BusException(Status.RESOURCE_FILE_RENAME_FAILED, e);
        }
    }

    @Override
    public void putFile(String path, InputStream fileStream) {
        try {
            FileUtil.writeFromStream(fileStream, getFilePath(path));
        } catch (Exception e) {
            throw new BusException(Status.RESOURCE_FILE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public void putFile(String path, File file) {
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        FileUtil.writeFromStream(inputStream, getFilePath(path));
    }

    @Override
    public String getFileContent(String path) {
        return IoUtil.readUtf8(readFile(path));
    }

    @Override
    public List<ResourcesVO> getFullDirectoryStructure(int rootId) {
        String basePath = FileUtil.file(getBasePath()).getPath();

        try (Stream<Path> paths = Files.walk(Paths.get(basePath))) {
            return paths.map(path -> {
                        if (path.compareTo(Paths.get(basePath)) == 0) {
                            // 跳过根目录 | skip root directory
                            return null;
                        }
                        Path parentPath = path.getParent();
                        String self = StrUtil.replaceFirst(path.toString(), basePath, "");
                        int parentId = 1;

                        // Determine whether it is the root directory
                        if (!parentPath.toUri().getPath().equals(basePath)) {
                            String parent = parentPath.toString().replace(basePath, "");
                            parentId = parent.isEmpty() ? rootId : parent.hashCode();
                        }
                        File file = new File(path.toString());
                        return ResourcesVO.builder()
                                .id(self.hashCode())
                                .pid(parentId)
                                .fullName(self)
                                .fileName(file.getName())
                                .isDirectory(file.isDirectory())
                                .type(0)
                                .size(file.length())
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_PATH_VISIT_FAILED, e);
        }
    }

    @Override
    public InputStream readFile(String path) {
        return FileUtil.getInputStream(getFilePath(path));
    }

    @Override
    public FileSystem getFileSystem() {
        return LocalFileSystem.getSharedInstance();
    }
}
