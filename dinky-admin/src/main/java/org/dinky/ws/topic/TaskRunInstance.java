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

package org.dinky.ws.topic;

import org.dinky.daemon.pool.FlinkJobThreadPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskRunInstance extends BaseTopic {
    public static final TaskRunInstance INSTANCE = new TaskRunInstance();
    private Set<Integer> runningJobIds = new ConcurrentSkipListSet<>();

    private TaskRunInstance() {}

    @Override
    public Map<String, Object> autoDataSend(Set<String> allParams) {
        Set<Integer> currentMonitorTaskIds = FlinkJobThreadPool.getInstance().getCurrentMonitorTaskIds();
        if (!runningJobIds.equals(currentMonitorTaskIds)) {
            log.info("New Status:" + currentMonitorTaskIds.toString());
            runningJobIds.clear();
            runningJobIds.addAll(currentMonitorTaskIds);
            Map<String, Object> result = new HashMap<>();
            result.put("RunningTaskId", currentMonitorTaskIds);
            return result;
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> firstDataSend(Set<String> allParams) {
        Map<String, Object> result = new HashMap<>();
        result.put("RunningTaskId", FlinkJobThreadPool.getInstance().getCurrentMonitorTaskIds());
        return result;
    }
}
