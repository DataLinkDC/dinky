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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.FlinkUserCodeClassLoaders;
import org.apache.flink.util.MutableURLClassLoader;

import java.io.File;
import java.net.URL;

/**
 * RemoteStreamExecutor
 *
 * @since 2021/5/25 14:05
 */
public class RemoteStreamExecutor extends Executor {

    public RemoteStreamExecutor(ExecutorConfig executorConfig) {
        this.executorConfig = executorConfig;
        if (executorConfig.isValidConfig()) {
            Configuration configuration = Configuration.fromMap(executorConfig.getConfig());
            this.environment = StreamExecutionEnvironment.createRemoteEnvironment(
                    executorConfig.getHost(), executorConfig.getPort(), configuration, executorConfig.getJarFiles());
        } else {
            this.environment = StreamExecutionEnvironment.createRemoteEnvironment(
                    executorConfig.getHost(), executorConfig.getPort(), executorConfig.getJarFiles());
        }
        init();
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment() {
        return CustomTableEnvironmentImpl.create(environment);
    }
}
