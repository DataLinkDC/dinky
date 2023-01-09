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

package org.dinky.daemon.task;

import org.dinky.daemon.constant.FlinkTaskConstant;
import org.dinky.daemon.pool.DefaultThreadPool;

import java.util.List;

public class DaemonFactory {

    public static void start(List<DaemonTaskConfig> configList) {
        Thread thread = new Thread(() -> {
            DefaultThreadPool defaultThreadPool = DefaultThreadPool.getInstance();
            for (DaemonTaskConfig config : configList) {
                DaemonTask daemonTask = DaemonTask.build(config);
                defaultThreadPool.execute(daemonTask);
            }
            while (true) {
                int taskSize = defaultThreadPool.getTaskSize();
                try {
                    Thread.sleep(Math.max(FlinkTaskConstant.MAX_POLLING_GAP / (taskSize + 1),
                            FlinkTaskConstant.MIN_POLLING_GAP));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int num = taskSize / 100 + 1;
                if (defaultThreadPool.getWorkCount() < num) {
                    defaultThreadPool.addWorkers(num - defaultThreadPool.getWorkCount());
                } else if (defaultThreadPool.getWorkCount() > num) {
                    defaultThreadPool.removeWorker(defaultThreadPool.getWorkCount() - num);
                }
            }
        });
        thread.start();
    }

    public static void addTask(DaemonTaskConfig config) {
        DefaultThreadPool.getInstance().execute(DaemonTask.build(config));
    }
}
