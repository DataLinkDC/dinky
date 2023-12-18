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

package org.dinky.executor;

import org.dinky.assertion.Asserts;
import org.dinky.classloader.DinkyClassLoader;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Opt;

/**
 * LocalStreamExecutor
 *
 * @since 2021/5/25 13:48
 */
public class LocalStreamExecutor extends Executor {

    public LocalStreamExecutor(ExecutorConfig executorConfig, DinkyClassLoader classLoader) {
        this.executorConfig = executorConfig;
        if (executorConfig.isValidJarFiles()) {
            executorConfig
                    .getConfig()
                    .put(
                            PipelineOptions.JARS.key(),
                            Stream.of(executorConfig.getJarFiles())
                                    .map(FileUtil::getAbsolutePath)
                                    .collect(Collectors.joining(",")));
        }
        if (!executorConfig.isPlan()) {
            Configuration configuration = Configuration.fromMap(
                    Opt.ofNullable(executorConfig.getConfig()).orElse(new HashMap<>()));
            if (!configuration.contains(RestOptions.PORT)) {
                if (Asserts.isNotNull(executorConfig.getPort())) {
                    configuration.set(RestOptions.PORT, executorConfig.getPort());
                }
            }
            this.environment = StreamExecutionEnvironment.createLocalEnvironment(configuration);
        } else {
            this.environment = StreamExecutionEnvironment.createLocalEnvironment();
        }
        init(classLoader);
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment(ClassLoader classLoader) {
        return CustomTableEnvironmentImpl.create(environment, classLoader);
    }
}
