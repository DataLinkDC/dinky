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

import org.dinky.daemon.pool.FlinkJobThreadPool;
import org.dinky.daemon.task.DaemonTask;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class TaskWorker implements Runnable {

    private volatile boolean running = true;

    private final TaskQueue<DaemonTask> queue;

    public TaskWorker(TaskQueue<DaemonTask> queue) {
        this.queue = queue;
    }

    /**
     * Perform tasks.
     * <p>
     * This method is used to perform tasks. Continuously fetch tasks from the queue
     * while the task is running (call the queue.dequeue() method). </p>
     * <p>If the task is fetched, try to process the task (call the daemonTask.dealTask() method).</p>
     * <p>If the processing task does not complete (returns False),
     * the task is put back into the queue again (call the queue.enqueue(daemonTask) method).
     * </p>
     */
    @Override
    public void run() {
        log.debug("TaskWorker run:" + Thread.currentThread().getName());
        while (running) {
            DaemonTask daemonTask = queue.getNext();
            if (daemonTask != null) {
                try {
                    boolean done = daemonTask.dealTask();
                    if (done) {
                        FlinkJobThreadPool.getInstance().removeByTaskConfig(daemonTask.getConfig());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void shutdown() {
        log.debug(Thread.currentThread().getName() + "TaskWorker shutdown");
        running = false;
    }
}
