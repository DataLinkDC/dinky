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

import org.dinky.data.exception.BusException;
import org.dinky.data.model.Cluster;
import org.dinky.data.model.Jar;
import org.dinky.data.model.Statement;
import org.dinky.data.model.Task;

/**
 * Assert
 *
 * @since 2021/5/30 11:13
 */
public interface Assert {

    static void check(Cluster cluster) {
        if (cluster.getId() == null) {
            throw new BusException("Flink 集群【" + cluster.getId() + "】不存在");
        }
    }

    static void check(Task task) {
        if (task == null) {
            throw new BusException("作业不存在");
        }
    }

    static void check(Statement statement) {
        if (statement == null) {
            throw new BusException("FlinkSql语句不存在");
        }
    }

    static void checkHost(String host) {
        if (host == null || "".equals(host)) {
            throw new BusException("集群地址暂不可用");
        }
    }

    static void check(Jar jar) {
        if (jar == null) {
            throw new BusException("自定义Jar不存在");
        }
    }
}
