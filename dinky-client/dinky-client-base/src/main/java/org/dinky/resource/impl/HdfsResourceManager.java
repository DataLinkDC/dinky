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

import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.ResourcesVO;
import org.dinky.resource.BaseResourceManager;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public class HdfsResourceManager implements BaseResourceManager {
    FileSystem hdfs;

    @Override
    public void remove(String path) {
        try {
            getHdfs().delete(new Path(getFilePath(path)), true);
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_DELETE_FAILED, e);
        }
    }

    @Override
    public void rename(String path, String newPath) {
        try {
            getHdfs().rename(new Path(getFilePath(path)), new Path(getFilePath(newPath)));
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_RENAME_FAILED, e);
        }
    }

    @Override
    public void putFile(String path, InputStream fileStream) {
        try {
            FSDataOutputStream stream = getHdfs().create(new Path(getFilePath(path)), true);
            stream.write(IoUtil.readBytes(fileStream));
            stream.flush();
            stream.close();
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public void putFile(String path, File file) {
        try {
            FSDataOutputStream stream = getHdfs().create(new Path(getFilePath(path)), true);
            stream.write(FileUtil.readBytes(file));
            stream.flush();
            stream.close();
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public String getFileContent(String path) {
        return IoUtil.readUtf8(readFile(path));
    }

    @Override
    public List<ResourcesVO> getFullDirectoryStructure(int rootId) {
        String basePath = new Path(getBasePath()).toUri().getPath();
        checkHdfsFile(basePath);

        List<FileStatus> filePathsList = new ArrayList<>();
        listAllHdfsFilePaths(basePath, filePathsList);

        List<ResourcesVO> resList = new ArrayList<>();

        for (FileStatus file : filePathsList) {
            Path parentPath = file.getPath().getParent();
            if (parentPath == null) {
                continue;
            }
            int parentId = 1;
            String parentSimplePath = parentPath.toUri().getPath();

            // Determine whether it is the root directory
            if (!parentSimplePath.equals(basePath)) {
                String path = parentSimplePath.replace(basePath, "");
                parentId = path.isEmpty() ? rootId : path.hashCode();
            }

            String self = StrUtil.replaceFirst(file.getPath().toUri().getPath(), basePath, "");

            ResourcesVO resources = ResourcesVO.builder()
                    .id(self.hashCode())
                    .pid(parentId)
                    .fullName(self)
                    .fileName(file.getPath().getName())
                    .isDirectory(file.isDirectory())
                    .type(0)
                    .size(file.getLen())
                    .build();

            resList.add(resources);
        }
        return resList;
    }

    @Override
    public InputStream readFile(String path) {
        try {
            return getHdfs().open(new Path(getFilePath(path)));
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_READ_FAILED, e);
        }
    }

    public FileSystem getHdfs() {
        if (hdfs == null && instances.getResourcesEnable().getValue()) {
            throw new BusException(Status.RESOURCE_HDFS_CONFIGURATION_ERROR);
        }
        return hdfs;
    }

    public synchronized void setHdfs(FileSystem hdfs) {
        this.hdfs = hdfs;
    }

    public void checkHdfsFile(String path) {
        try {
            getHdfs().exists(new Path(path));
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_DIR_OR_FILE_NOT_EXIST, e);
        }
    }

    public FileStatus[] listHdfsFilePaths(String path) {
        try {
            return getHdfs().listStatus(new Path(path));
        } catch (IOException e) {
            throw new BusException(Status.RESOURCE_FILE_PATH_VISIT_FAILED, e);
        }
    }

    public void listAllHdfsFilePaths(String path, List<FileStatus> fileStatusList) {

        FileStatus[] paths = listHdfsFilePaths(path);

        if (ArrayUtil.isEmpty(paths)) {
            return;
        }

        for (FileStatus file : paths) {
            fileStatusList.add(file);
            if (file.isDirectory()) {
                listAllHdfsFilePaths(file.getPath().toString(), fileStatusList);
            }
        }
    }
}
