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

package org.dinky.daemon.pool;

import org.dinky.daemon.entity.TaskQueue;
import org.dinky.daemon.entity.TaskWorker;
import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @operate
 * @return
 */
public class FlinkJobThreadPool implements ThreadPool {

    private static final int MAX_WORKER_NUM = 20;
    private static final int DEFAULT_WORKER_NUM = 1;
    private static final int MIN_WORKER_NUM = 1;

    private final List<TaskWorker> workers = Collections.synchronizedList(new ArrayList<>());

    private final Object lock = new Object();

    private final AtomicInteger workerNum = new AtomicInteger(0);

    private final TaskQueue<DaemonTask> queue = new TaskQueue<>();

    private FlinkJobThreadPool() {
        addWorkers(DEFAULT_WORKER_NUM);
    }

    private static final class DefaultThreadPoolHolder {
        private static final FlinkJobThreadPool defaultThreadPool = new FlinkJobThreadPool();
    }

    public static FlinkJobThreadPool getInstance() {
        return DefaultThreadPoolHolder.defaultThreadPool;
    }

    @Override
    public void execute(DaemonTask daemonTask) {
        if (daemonTask != null) {
            queue.addTask(daemonTask);
            resizeWorkers(queue.getTaskSize() / 10);
        }
    }

    public DaemonTask removeByTaskConfig(DaemonTaskConfig daemonTask) {
        DaemonTask removed = queue.removeByTaskConfig(daemonTask);
        resizeWorkers(queue.getTaskSize() / 10);
        return removed;
    }

    private void resizeWorkers(int afterNum) {
        synchronized (lock) {
            int workerNum = this.workerNum.get();

            afterNum = Math.min(afterNum, MAX_WORKER_NUM);
            afterNum = Math.max(afterNum, MIN_WORKER_NUM);

            if (afterNum > workerNum) {
                addWorkers(afterNum - workerNum);
            } else if (afterNum < workerNum) {
                removeWorker(workerNum - afterNum);
            }
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (lock) {
            if (num + this.workerNum.get() > MAX_WORKER_NUM) {
                num = MAX_WORKER_NUM - this.workerNum.get();
                if (num <= 0) {
                    return;
                }
            }
            for (int i = 0; i < num; i++) {
                TaskWorker worker = new TaskWorker(queue);
                workers.add(worker);
                Thread thread = new Thread(worker, "ThreadPool-Worker-" + workerNum.incrementAndGet());
                thread.start();
            }
        }
    }

    @Override
    public void removeWorker(int num) {

        synchronized (lock) {
            if (num >= this.workerNum.get()) {
                num = this.workerNum.get() - MIN_WORKER_NUM;
                if (num <= 0) {
                    return;
                }
            }
            int count = num - 1;
            while (count >= 0) {
                TaskWorker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count--;
                }
            }
            // 减少线程
            workerNum.getAndAdd(-num);
        }
    }

    @Override
    public void shutdown() {
        synchronized (lock) {
            for (TaskWorker worker : workers) {
                worker.shutdown();
            }
            workers.clear();
        }
    }

    @Override
    public int getTaskSize() {
        return queue.getTaskSize();
    }

    public DaemonTask getByTaskConfig(DaemonTaskConfig daemonTask) {
        return queue.getByTaskConfig(daemonTask);
    }

    public int getWorkCount() {
        synchronized (lock) {
            return this.workerNum.get();
        }
    }
}
