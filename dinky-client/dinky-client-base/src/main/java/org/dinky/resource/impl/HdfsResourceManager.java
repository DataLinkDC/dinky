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

import org.apache.hadoop.fs.FileStatus;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.ResourcesVO;
import org.dinky.resource.BaseResourceManager;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.springframework.util.ObjectUtils;

public class HdfsResourceManager implements BaseResourceManager {
    FileSystem hdfs;

    @Override
    public void remove(String path) {
        try {
            getHdfs().delete(new Path(getFilePath(path)), true);
        } catch (IOException e) {
            throw BusException.valueOf("file.delete.failed", e);
        }
    }

    @Override
    public void rename(String path, String newPath) {
        try {
            getHdfs().rename(new Path(getFilePath(path)), new Path(getFilePath(newPath)));
        } catch (IOException e) {
            throw BusException.valueOf("file.rename.failed", e);
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
            throw BusException.valueOf("file.upload.failed", e);
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
            throw BusException.valueOf("file.upload.failed", e);
        }
    }

    @Override
    public String getFileContent(String path) {
        return IoUtil.readUtf8(readFile(path));
    }

    @Override
    public List<ResourcesVO> getFullDirectoryStructure(int rootId) {
        String basePath = getBasePath();
        if(!basePath.toUpperCase().contains("hdfs://") && !getHdfs().getUri().getHost().isEmpty()) {
            basePath = getHdfs().getUri().toString() + basePath;
        }
        checkHdfsFile(basePath);

        List<FileStatus> filePathsList = new ArrayList<>();
        listAllHdfsFilePaths(basePath, filePathsList);

        List<ResourcesVO> resList = new ArrayList<>();

        for (FileStatus file : filePathsList) {

            Path parentPath = file.getPath().getParent();
            /*if (parentPath.toString().equals(basePath)) {
                // 跳过根目录 | skip root directory
                continue;
            }*/

            String parent = "";
            if (parentPath != null) {
                parent = parentPath.toString().replace(basePath, "");
            }
            int pid = parent.isEmpty() ? rootId : parent.hashCode();
            String self = file.getPath().toString().replace(basePath, "");

            ResourcesVO resources = ResourcesVO.builder()
                    .id(self.hashCode())
                    .pid(pid)
                    .fullName(self)
                    .fileName(file.getPath().getName())
                    .isDirectory(file.isDirectory())
                    .type(0)
                    .size(file.getBlockSize())
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

    public void checkHdfsFile(String path){
        try {
            getHdfs().exists(new Path(path));
        } catch (IOException e) {
            throw BusException.valueOf("hdfs.dir.or.file.not.exist", e);
        }
    }

    public FileStatus[] listHdfsFilePaths(String path) {
        try {
            return getHdfs().listStatus(new Path(path));
        } catch (IOException e) {
            throw BusException.valueOf("file.path.visit.failed", e);
        }
    }

    public void listAllHdfsFilePaths(String path, List<FileStatus> fileStatusList) {

        FileStatus[] paths = listHdfsFilePaths(path);

        if (ObjectUtils.isEmpty(paths) || paths.length == 0) {
            return;
        }

        for (FileStatus file : paths) {
            fileStatusList.add(file);
            if(file.isDirectory()) {
                listAllHdfsFilePaths(file.getPath().toString(), fileStatusList);
            }
        }

    }

}
