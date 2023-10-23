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

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.io.FileUtil;

/**
 * LocalStreamExecutor
 *
 * @since 2021/5/25 13:48
 */
public class LocalStreamExecutor extends Executor {

    public LocalStreamExecutor(ExecutorConfig executorConfig) {
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
        if (executorConfig.isValidConfig()) {
            Configuration configuration = Configuration.fromMap(executorConfig.getConfig());
            if (configuration.contains(RestOptions.PORT)) {
                this.environment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
            } else {
                this.environment = StreamExecutionEnvironment.createLocalEnvironment(configuration);
            }
        } else {
            this.environment = StreamExecutionEnvironment.createLocalEnvironment();
        }
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.create(environment);
    }
}
