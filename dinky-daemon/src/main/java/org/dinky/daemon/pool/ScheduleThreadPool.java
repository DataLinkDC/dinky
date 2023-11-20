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

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class ScheduleThreadPool {
    private static final HashMap<String, ScheduledFuture<?>> SCHEDULE_MAP = new HashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public void addSchedule(DaemonTask task, Trigger trigger) {
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(task::dealTask, trigger);
        getScheduleMap().put(task.getType(), schedule);
    }

    public void removeSchedule(DaemonTask task) {
        ScheduledFuture<?> scheduledFuture = getScheduleMap().get(task.getType());
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    public static HashMap<String, ScheduledFuture<?>> getScheduleMap() {
        return SCHEDULE_MAP;
    }
}
