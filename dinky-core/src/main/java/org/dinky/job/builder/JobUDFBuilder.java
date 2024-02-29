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

package org.dinky.job.builder;

import static org.dinky.function.util.UDFUtil.GATEWAY_TYPE_MAP;
import static org.dinky.function.util.UDFUtil.SESSION;
import static org.dinky.function.util.UDFUtil.YARN;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobManager;
import org.dinky.utils.URLUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * JobUDFBuilder
 *
 */
@Slf4j
public class JobUDFBuilder extends JobBuilder {

    public JobUDFBuilder(JobManager jobManager) {
        super(jobManager);
    }

    public static JobUDFBuilder build(JobManager jobManager) {
        return new JobUDFBuilder(jobManager);
    }

    @Override
    public void run() throws Exception {
        Asserts.checkNotNull(jobParam, "No executable statement.");
        List<UDF> udfList = jobManager.getJobParam().getUdfList();
        Integer taskId = config.getTaskId();
        if (taskId == null) {
            taskId = -RandomUtil.randomInt(0, 1000);
        }
        // 1. Obtain the path of the jar package and inject it into the remote environment
        List<File> jarFiles =
                new ArrayList<>(jobManager.getUdfPathContextHolder().getAllFileSet());

        String[] jarPaths = CollUtil.removeNull(jarFiles).stream()
                .map(File::getAbsolutePath)
                .toArray(String[]::new);

        if (GATEWAY_TYPE_MAP.get(SESSION).contains(runMode)) {
            config.setJarFiles(jarPaths);
        }

        // 2.Compile Python
        String[] pyPaths = UDFUtil.initPythonUDF(
                udfList, runMode, config.getTaskId(), executor.getTableConfig().getConfiguration());

        executor.initUDF(jarPaths);

        if (ArrayUtil.isNotEmpty(pyPaths)) {
            for (String pyPath : pyPaths) {
                if (StrUtil.isNotBlank(pyPath)) {
                    jobManager.getUdfPathContextHolder().addPyUdfPath(new File(pyPath));
                }
            }
        }

        Set<File> pyUdfFile = jobManager.getUdfPathContextHolder().getPyUdfFile();
        executor.initPyUDF(
                SystemConfiguration.getInstances().getPythonHome(),
                pyUdfFile.stream().map(File::getAbsolutePath).toArray(String[]::new));
        if (GATEWAY_TYPE_MAP.get(YARN).contains(runMode)) {
            config.getGatewayConfig().setJarPaths(ArrayUtil.append(jarPaths, pyPaths));
        }

        try {
            List<URL> jarList = CollUtil.newArrayList(URLUtils.getURLs(jarFiles));
            // 3.Write the required files for UDF
            UDFUtil.writeManifest(taskId, jarList, jobManager.getUdfPathContextHolder());
            UDFUtil.addConfigurationClsAndJars(
                    jobManager.getExecutor().getCustomTableEnvironment(),
                    jarList,
                    CollUtil.newArrayList(URLUtils.getURLs(jarFiles)));
        } catch (Exception e) {
            throw new RuntimeException("add configuration failed: ", e);
        }

        log.info(StrUtil.format("A total of {} UDF have been Init.", udfList.size() + pyUdfFile.size()));
        log.info("Initializing Flink UDF...Finish");
    }
}
