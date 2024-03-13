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
import org.dinky.classloader.DinkyClassLoader;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.exception.DinkyException;
import org.dinky.job.JobConfig;

import org.apache.flink.configuration.PipelineOptions;

import java.io.File;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;

public class DinkyClassLoaderUtil {

    public static void initClassLoader(JobConfig config, DinkyClassLoader dinkyClassLoader) {

        FlinkUdfPathContextHolder udfPathContextHolder = dinkyClassLoader.getUdfPathContextHolder();
        if (CollUtil.isNotEmpty(config.getConfigJson())) {
            String pipelineJars = config.getConfigJson().get(PipelineOptions.JARS.key());
            String classpaths = config.getConfigJson().get(PipelineOptions.CLASSPATHS.key());
            // add custom jar path
            if (Asserts.isNotNullString(pipelineJars)) {
                String[] paths = pipelineJars.split(",");
                for (String path : paths) {
                    File file = FileUtil.file(path);
                    if (!file.exists()) {
                        throw new DinkyException("file: " + path + " not exists!");
                    }
                    udfPathContextHolder.addUdfPath(file);
                }
            }
            // add custom classpath
            if (Asserts.isNotNullString(classpaths)) {
                String[] paths = pipelineJars.split(",");
                for (String path : paths) {
                    File file = FileUtil.file(path);
                    if (!file.exists()) {
                        throw new DinkyException("file: " + path + " not exists!");
                    }
                    udfPathContextHolder.addOtherPlugins(file);
                }
            }
        }

        dinkyClassLoader.addURLs(udfPathContextHolder.getAllFileSet());
    }
}
