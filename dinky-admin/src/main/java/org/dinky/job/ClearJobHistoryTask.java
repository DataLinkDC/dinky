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

package org.dinky.job;

import org.dinky.context.SpringContextUtils;
import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.model.Configuration;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.job.handler.ClearJobHistoryHandler;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.impl.ClusterInstanceServiceImpl;

import org.springframework.context.annotation.DependsOn;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@DependsOn("springContextUtils")
@Slf4j
@Data
public class ClearJobHistoryTask implements DaemonTask {

    public static final String TYPE = ClearJobHistoryTask.class.toString();

    private static final JobInstanceService jobInstanceService;
    private static final JobHistoryService jobHistoryService;
    private static final HistoryService historyService;
    private static final ClearJobHistoryHandler clearJobHistoryHandler;
    private static final ClusterInstanceService clusterService;

    private static Configuration<Integer> maxRetainDays;
    private static Configuration<Integer> maxRetainCount;

    static {
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        jobHistoryService = SpringContextUtils.getBean("jobHistoryServiceImpl", JobHistoryService.class);
        historyService = SpringContextUtils.getBean("historyServiceImpl", HistoryService.class);
        clusterService = SpringContextUtils.getBean("clusterInstanceServiceImpl", ClusterInstanceServiceImpl.class);
        clearJobHistoryHandler = ClearJobHistoryHandler.builder()
                .historyService(historyService)
                .jobInstanceService(jobInstanceService)
                .jobHistoryService(jobHistoryService)
                .clusterService(clusterService)
                .build();
        maxRetainDays = SystemConfiguration.getInstances().getJobMaxRetainDays();
        maxRetainCount = SystemConfiguration.getInstances().getJobMaxRetainCount();
    }

    @Override
    public boolean dealTask() {
        if (maxRetainCount.getValue() > 0) {
            clearJobHistoryHandler.clearDinkyHistory(maxRetainDays.getValue(), maxRetainCount.getValue());
            clearJobHistoryHandler.clearJobHistory(maxRetainDays.getValue(), maxRetainCount.getValue());
        }
        return false;
    }

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        return this;
    }

    @Override
    public DaemonTaskConfig getConfig() {
        return null;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
