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

package org.dinky.daemon.entity;

import org.dinky.daemon.task.DaemonTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TaskWorker.class);

    private volatile boolean running = true;

    private TaskQueue<DaemonTask> queue;

    public TaskWorker(TaskQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        // log.info("TaskWorker run");
        while (running) {
            DaemonTask daemonTask = queue.dequeue();
            if (daemonTask != null) {
                try {
                    daemonTask.dealTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
        // log.info(Thread.currentThread().getName() + "TaskWorker shutdown");
        running = false;
    }
}
