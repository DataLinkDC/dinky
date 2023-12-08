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

import org.dinky.classloader.DinkyClassLoader;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * AppStreamExecutor
 *
 * @since 2021/11/18
 */
public class AppStreamExecutor extends Executor {

    public AppStreamExecutor(ExecutorConfig executorConfig, DinkyClassLoader classLoader) {
        this.executorConfig = executorConfig;
        if (executorConfig.isValidConfig()) {
            Configuration configuration = Configuration.fromMap(executorConfig.getConfig());
            this.environment = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        } else {
            this.environment = StreamExecutionEnvironment.getExecutionEnvironment();
        }
        init(classLoader);
    }

    @Override
    CustomTableEnvironment createCustomTableEnvironment(ClassLoader classLoader) {
        return CustomTableEnvironmentImpl.create(environment, classLoader);
    }
}
