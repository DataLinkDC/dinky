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

import org.dinky.daemon.task.DaemonTask;

/**
 * @operate
 * @return
 */
public interface ThreadPool {

    // 执行任务
    void execute(DaemonTask daemonTask);

    // 关闭连接池
    void shutdown();

    // 增加工作数
    void addWorkers(int num);

    // 减少工作数
    void removeWorker(int num);

    int getTaskSize();
}
