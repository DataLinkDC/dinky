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

package org.dinky.assertion;

import org.dinky.data.dto.TaskDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.Task;

/**
 * Assert
 *
 * @since 2021/5/30 11:13
 */
public interface DinkyAssert {

    static void check(ClusterInstance clusterInstance) {
        if (clusterInstance.getId() == null) {
            throw new BusException("Flink 集群【" + clusterInstance.getId() + "】不存在");
        }
    }

    static void check(TaskDTO task) {
        if (task == null) {
            throw new BusException(Status.TASK_NOT_EXIST);
        }
    }

    static void check(Task task) {
        if (task == null) {
            throw new BusException(Status.TASK_NOT_EXIST);
        }
    }

    static void checkNull(Object o, Status status) {
        if (o == null) {
            throw new BusException(status);
        }
    }

    static void checkNull(Object o, String msg) {
        if (o == null) {
            throw new BusException(msg);
        }
    }

    static void checkHost(String host) {
        if (host == null || "".equals(host)) {
            throw new BusException("集群地址暂不可用");
        }
    }
}
