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

import org.dinky.assertion.Asserts;
import org.dinky.context.SpringContextUtils;
import org.dinky.daemon.constant.FlinkTaskConstant;
import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.job.handler.JobAlertHandler;
import org.dinky.job.handler.JobMetricsHandler;
import org.dinky.job.handler.JobRefreshHandler;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.DependsOn;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@DependsOn("springContextUtils")
@Slf4j
@Data
public class FlinkJobTask implements DaemonTask {

    private DaemonTaskConfig config;

    public static final String TYPE = FlinkJobTask.class.toString();

    private static final JobInstanceService jobInstanceService;

    private static final MonitorService monitorService;

    private long preDealTime;

    private long refreshCount = 0;

    private Map<String, Map<String, String>> verticesAndMetricsMap = new ConcurrentHashMap<>();

    static {
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        monitorService = SpringContextUtils.getBean("monitorServiceImpl", MonitorService.class);
    }

    private JobInfoDetail jobInfoDetail;

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        this.config = config;
        this.jobInfoDetail = jobInstanceService.getJobInfoDetail(config.getId());
        // Get a list of metrics and deduplicate them based on vertices and metrics
        monitorService
                .getMetricsLayoutByTaskId(jobInfoDetail.getInstance().getTaskId())
                .forEach(m -> {
                    verticesAndMetricsMap.putIfAbsent(m.getVertices(), new ConcurrentHashMap<>());
                    verticesAndMetricsMap.get(m.getVertices()).put(m.getMetrics(), "");
                });
        return this;
    }

    @Override
    public DaemonTaskConfig getConfig() {
        return config;
    }

    /**
     * Processing tasks.
     * <p>
     * Handle job refresh, alarm, monitoring and other actions
     * Returns true if the job has completed or exceeded the time to obtain data,
     * indicating that the processing is complete and moved out of the thread pool
     * Otherwise, false is returned, indicating that the processing is not completed and continues to remain in the thread pool
     * </p>
     *
     * @return Returns true if the job has completed, otherwise returns false
     */
    @Override
    public boolean dealTask() {
        volatilityBalance();

        boolean isDone = JobRefreshHandler.refreshJob(jobInfoDetail, isNeedSave());
        if (Asserts.isAllNotNull(jobInfoDetail.getClusterInstance())) {
            JobAlertHandler.getInstance().check(jobInfoDetail);
            JobMetricsHandler.refeshAndWriteFlinkMetrics(jobInfoDetail, verticesAndMetricsMap);
        }
        return isDone;
    }

    /**
     * Volatility balance.
     * <p>
     * This method is used to perform volatility equilibrium operations. Between each call,
     * by calculating the time interval between the current time and the last processing time,
     * If the interval is less than the set sleep time (TIME_SLEEP),
     * The thread sleeps for a period of time. Then update the last processing time to the current time.
     * </p>
     */
    public void volatilityBalance() {
        long gap = System.currentTimeMillis() - this.preDealTime;
        if (gap < FlinkTaskConstant.TIME_SLEEP) {
            try {
                Thread.sleep(FlinkTaskConstant.TIME_SLEEP);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        preDealTime = System.currentTimeMillis();
    }

    /**
     * Determine if you need to save.
     * <p>
     * This method is used to determine whether saving is required.
     * According to the value of the refresh count,
     * if the refreshCount is divisible by 60, it returns true, indicating that it needs to be saved.
     * At the same time, if you need to save, reset refreshCount to 0 to restart the count.
     * Finally, increment refreshCount by 1.
     * </p>
     *
     * @return Returns true if you need to save, otherwise returns false
     */
    public boolean isNeedSave() {
        boolean isNeed = refreshCount % 60 == 0;
        if (isNeed) {
            refreshCount = 0;
        }
        refreshCount++;
        return isNeed;
    }

    /**
     * Determine whether objects are equal.
     * <p>
     * This method is used to determine whether the current object is equal to the given object.
     * If two object references are the same, return true directly.
     * Returns false if the given object is null or the class of the given object is not the same as the class of the current object.
     * Otherwise, convert the given object to the FlinkJobTask type and compare the config IDs of the two objects equal.
     * In FlinkJobTask, the ID of the config is unique, so only need to compare whether the ID of the config is equal or not.
     * If the objects are judged to be equal, the same task will join the thread pool multiple times.
     * </p>
     *
     * @param obj object to compare
     * @return Returns true if two objects are equal, otherwise returns false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FlinkJobTask other = (FlinkJobTask) obj;
        return Objects.equals(config.getId(), other.config.getId());
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
