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


package com.dlink.daemon.task;

import java.util.Optional;
import java.util.ServiceLoader;

import com.dlink.assertion.Asserts;
import com.dlink.daemon.exception.DaemonTaskException;

public interface DaemonTask {

    static Optional<DaemonTask> get(DaemonTaskConfig config) {
        Asserts.checkNotNull(config, "线程任务配置不能为空");
        ServiceLoader<DaemonTask> daemonTasks = ServiceLoader.load(DaemonTask.class);
        for (DaemonTask daemonTask : daemonTasks) {
            if (daemonTask.canHandle(config.getType())) {
                return Optional.of(daemonTask.setConfig(config));
            }
        }
        return Optional.empty();
    }

    static DaemonTask build(DaemonTaskConfig config) {
        Optional<DaemonTask> optionalDaemonTask = DaemonTask.get(config);
        if (!optionalDaemonTask.isPresent()) {
            throw new DaemonTaskException("不支持线程任务类型【" + config.getType() + "】");
        }
        DaemonTask daemonTask = optionalDaemonTask.get();
        return daemonTask;
    }

    DaemonTask setConfig(DaemonTaskConfig config);

    default boolean canHandle(String type) {
        return Asserts.isEqualsIgnoreCase(getType(), type);
    }

    String getType();

    void dealTask();
}
