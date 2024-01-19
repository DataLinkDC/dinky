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

import java.lang.ref.WeakReference;

/**
 * ExecutorFactory
 *
 * @author qiwenkai
 * @since 2023/9/21 9:45
 **/
public final class ExecutorFactory {

    private ExecutorFactory() {}

    public static Executor getDefaultExecutor() {
        return new LocalStreamExecutor(ExecutorConfig.DEFAULT, new WeakReference<>(DinkyClassLoader.build()).get());
    }

    public static Executor buildExecutor(ExecutorConfig executorConfig, DinkyClassLoader classLoader) {
        if (executorConfig.isRemote()) {
            return buildRemoteExecutor(executorConfig, classLoader);
        } else {
            return buildLocalExecutor(executorConfig, classLoader);
        }
    }

    public static Executor buildLocalExecutor(ExecutorConfig executorConfig, DinkyClassLoader classLoader) {
        if (executorConfig.isUseBatchModel()) {
            return new LocalBatchExecutor(executorConfig, classLoader);
        } else {
            return new LocalStreamExecutor(executorConfig, classLoader);
        }
    }

    public static Executor buildAppStreamExecutor(ExecutorConfig executorConfig, DinkyClassLoader classLoader) {
        if (executorConfig.isUseBatchModel()) {
            return new AppBatchExecutor(executorConfig, classLoader);
        } else {
            return new AppStreamExecutor(executorConfig, classLoader);
        }
    }

    public static Executor buildRemoteExecutor(ExecutorConfig executorConfig, DinkyClassLoader classLoader) {
        if (executorConfig.isUseBatchModel()) {
            return new RemoteBatchExecutor(executorConfig, classLoader);
        } else {
            return new RemoteStreamExecutor(executorConfig, classLoader);
        }
    }
}
