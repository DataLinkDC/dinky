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

package org.dinky.utils;

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.UploadFileConstant;
import org.dinky.data.exception.BusException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/** Hdfs Handle */
@Slf4j
public class HdfsUtil {

    /**
     * Init internal hdfs client
     *
     * @param hadoopConfigPath HDFS config path
     */
    private static FileSystem init(String hadoopConfigPath) {
        FileSystem hdfs;
        if (Asserts.isNullString(hadoopConfigPath)) {
            hadoopConfigPath = FilePathUtil.removeFileSeparator(UploadFileConstant.HADOOP_CONF_DIR);
        }
        String coreSiteFilePath = hadoopConfigPath + "/core-site.xml";
        String hdfsSiteFilePath = hadoopConfigPath + "/hdfs-site.xml";
        if (!new File(coreSiteFilePath).exists() || !new File(hdfsSiteFilePath).exists()) {
            throw BusException.valueOf("hdfs.file.lose");
        }
        try {
            final Configuration configuration = new Configuration();
            configuration.addResource(new Path(coreSiteFilePath));
            configuration.addResource(new Path(hdfsSiteFilePath));
            hdfs = FileSystem.get(configuration);
        } catch (IOException e) {
            String msg = ExceptionUtil.stacktraceToString(e);
            log.error(msg);
            throw BusException.valueOf("hdfs.init.failed", e);
        }
        log.info("hdfs client initialized successfully");
        return hdfs;
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path HDFS path
     * @param bytes File byte content
     * @param hadoopConfigPath hdfs config path
     */
    public static void uploadFile(String path, byte[] bytes, String hadoopConfigPath) {
        try (FileSystem hdfs = init(hadoopConfigPath);
                FSDataOutputStream stream = hdfs.create(new Path(path), true)) {
            stream.write(bytes);
            stream.flush();
        } catch (IOException e) {
            throw BusException.valueOf("file.upload.failed", e);
        }
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path HDFS path
     * @param file MultipartFile instance
     */
    public static void uploadFile(String path, MultipartFile file) {
        try {
            uploadFile(path, file.getBytes(), null);
        } catch (IOException e) {
            throw BusException.valueOf("file.upload.failed", e);
        }
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path HDFS path
     * @param file MultipartFile instance
     * @param hadoopConfigPath hdfs config path
     */
    public static void uploadFile(String path, MultipartFile file, String hadoopConfigPath) {
        try {
            uploadFile(path, file.getBytes(), hadoopConfigPath);
        } catch (IOException e) {
            throw BusException.valueOf("file.upload.failed", e);
        }
    }
}
