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

package com.dlink.job;

import com.dlink.assertion.Asserts;
import com.dlink.context.SpringContextUtils;
import com.dlink.daemon.constant.FlinkTaskConstant;
import com.dlink.daemon.pool.DefaultThreadPool;
import com.dlink.daemon.task.DaemonTask;
import com.dlink.daemon.task.DaemonTaskConfig;
import com.dlink.model.JobInstance;
import com.dlink.model.JobStatus;
import com.dlink.service.TaskService;

import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;

@DependsOn("springContextUtils")
public class FlinkJobTask implements DaemonTask {

    private static final Logger log = LoggerFactory.getLogger(FlinkJobTask.class);

    private DaemonTaskConfig config;
    public static final String TYPE = "jobInstance";
    private static TaskService taskService;
    private long preDealTime;

    static {
        taskService = SpringContextUtils.getBean("taskServiceImpl", TaskService.class);
    }

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void dealTask() {
        long gap = System.currentTimeMillis() - this.preDealTime;
        if (gap < FlinkTaskConstant.TIME_SLEEP) {
            try {
                Thread.sleep(FlinkTaskConstant.TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        preDealTime = System.currentTimeMillis();
        JobInstance jobInstance = taskService.refreshJobInstance(config.getId(), false);
        if ((!JobStatus.isDone(jobInstance.getStatus())) || (Asserts.isNotNull(jobInstance.getFinishTime())
                && Duration.between(jobInstance.getFinishTime(), LocalDateTime.now()).toMinutes() < 1)) {
            DefaultThreadPool.getInstance().execute(this);
        } else {
            taskService.handleJobDone(jobInstance);
            FlinkJobTaskPool.getInstance().remove(config.getId().toString());
        }
    }
}
