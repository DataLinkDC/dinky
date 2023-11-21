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

package org.dinky.job;

import org.dinky.daemon.pool.FlinkJobThreadPool;
import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicResizeFlinkJobPoolTask implements DaemonTask {

    private DaemonTaskConfig config;
    public static final String TYPE = DynamicResizeFlinkJobPoolTask.class.toString();
    private final FlinkJobThreadPool defaultThreadPool = FlinkJobThreadPool.getInstance();

    @Override
    public boolean dealTask() {
        int taskSize = defaultThreadPool.getTaskSize();
        // Calculate the desired number of worker threads, adding one worker for every 100 tasks
        int num = taskSize / 100 + 1;
        // Dynamically adjust the number of worker threads in the thread pool
        if (defaultThreadPool.getWorkCount() < num) {
            defaultThreadPool.addWorkers(num - defaultThreadPool.getWorkCount());
        } else if (defaultThreadPool.getWorkCount() > num) {
            defaultThreadPool.removeWorker(defaultThreadPool.getWorkCount() - num);
        }
        return false;
    }

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public DaemonTaskConfig getConfig() {
        return config;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
