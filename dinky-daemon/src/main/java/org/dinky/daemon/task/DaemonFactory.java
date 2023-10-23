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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DaemonFactory {

    /**
     *
     * <p><strong>Start the daemon thread.</strong></p>
     * <p>This method accepts a List&lt;{@link org.dinky.daemon.task.DaemonTaskConfig}&gt; parameter to configure daemon tasks.
     * Inside the method, it creates a thread and performs the following operations:</p>
     *
     * <ol>
     *   <li>Iterate through each configuration item in <em>configList</em> and construct the corresponding <em>DaemonTask</em>.</li>
     *   <li>Submit each <em>DaemonTask</em> to the thread pool for execution.</li>
     *   <li>Enter an infinite loop where the following actions are performed:
     *     <ul>
     *       <li>Calculate the waiting time based on the task count, ensuring that the polling interval between tasks
     *         stays within the specified minimum and maximum intervals.</li>
     *       <li>Calculate the desired number of working threads, <em>num</em>, increasing one working thread for
     *         every 100 tasks.</li>
     *       <li>Dynamically adjust the number of working threads in the thread pool based on a comparison between
     *         the actual number of working threads and the desired quantity.</li>
     *     </ul>
     *   </li>
     * </ol>
     */
    public static void start(List<DaemonTaskConfig> configList) {
        Thread thread = new Thread(() -> {
            DefaultThreadPool defaultThreadPool = DefaultThreadPool.getInstance();
            for (DaemonTaskConfig config : configList) {
                // Build a daemon task based on the config and Submit the task to the thread pool for execution
                DaemonTask daemonTask = DaemonTask.build(config);
                defaultThreadPool.execute(daemonTask);
            }

            while (true) {
                int taskSize = defaultThreadPool.getTaskSize();
                try {
                    // where (taskSize + 1) is to avoid dividing by 0 when taskSize is 0.
                    Thread.sleep(Math.max(
                            FlinkTaskConstant.MAX_POLLING_GAP / (taskSize + 1), FlinkTaskConstant.MIN_POLLING_GAP));
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                // Calculate the desired number of worker threads, adding one worker for every 100 tasks
                int num = taskSize / 100 + 1;

                // Dynamically adjust the number of worker threads in the thread pool
                if (defaultThreadPool.getWorkCount() < num) {
                    defaultThreadPool.addWorkers(num - defaultThreadPool.getWorkCount());
                } else if (defaultThreadPool.getWorkCount() > num) {
                    defaultThreadPool.removeWorker(defaultThreadPool.getWorkCount() - num);
                }
            }
        });
        thread.start();
    }

    /**
     * @param config
     * add task
     * */
    public static void refeshOraddTask(DaemonTaskConfig config) {
        DefaultThreadPool.getInstance().execute(DaemonTask.build(config));
    }
}
