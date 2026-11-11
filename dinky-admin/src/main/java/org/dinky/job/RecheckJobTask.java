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

import org.dinky.api.FlinkAPI;
import org.dinky.context.SpringContextUtils;
import org.dinky.daemon.pool.FlinkJobThreadPool;
import org.dinky.daemon.task.DaemonTask;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.job.JobInstance;
import org.dinky.service.JobInstanceService;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.DependsOn;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@DependsOn("springContextUtils")
@Slf4j
@Data
public class RecheckJobTask implements DaemonTask {

    public static final String TYPE = RecheckJobTask.class.toString();

    private DaemonTaskConfig config;

    private static JobInstanceService jobInstanceService;

    static {
        jobInstanceService = SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
    }

    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public DaemonTaskConfig getConfig() {
        return config;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean dealTask() {
        // Since flink-operator supports task redeployment and automatic recovery of failed jobs,
        // we need to recheck the abnormal jobs here. If the job status has recovered to normal,
        // put it back into the task monitoring queue.
        log.info("Starting recheck of job instances...");

        List<JobInstance> jobInstances = jobInstanceService.listJobInstancesToRecheck();
        log.info("Found {} job instances to recheck", jobInstances.size());

        FlinkJobThreadPool flinkJobThreadPool = FlinkJobThreadPool.getInstance();
        for (JobInstance jobInstance : jobInstances) {
            log.info(
                    "Rechecking job instance: id={}, name={}, taskId={}",
                    jobInstance.getId(),
                    jobInstance.getName(),
                    jobInstance.getTaskId());
            JobInfoDetail jobInfoDetail = jobInstanceService.getJobInfoDetail(jobInstance.getId());
            Optional<JobStatus> newStatus = this.recheckJobInstanceStatus(jobInfoDetail);
            log.info("Job '{}' status after recheck: {}", jobInstance.getName(), newStatus.orElse(null));
            if (newStatus.isPresent() && newStatus.get() == JobStatus.RUNNING) {
                log.info("Job '{}' is RUNNING again, re-adding to monitoring queue", jobInstance.getName());
                DaemonTaskConfig config =
                        DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInstance.getId(), jobInstance.getTaskId());
                DaemonTask daemonTask = DaemonTask.build(config);
                flinkJobThreadPool.execute(daemonTask);
            }
        }
        log.info("Job recheck completed.");
        return true;
    }

    private Optional<JobStatus> recheckJobInstanceStatus(JobInfoDetail jobInfoDetail) {
        try {
            String jmHost = jobInfoDetail.getClusterInstance().getJobManagerHost();
            log.info("Querying job status from JobManager host: {}", jmHost);
            List<JsonNode> jobs = FlinkAPI.build(jmHost).listJobs();
            if (jobs == null || jobs.isEmpty()) {
                log.warn("No jobs found on JobManager host: {}", jmHost);
                return Optional.empty();
            }
            JsonNode firstJob = jobs.get(0);
            log.debug("Job response: {}", firstJob.toString());
            String newStatus = firstJob.get("state").asText();
            log.info("Fetched job state: {}", newStatus);
            return Optional.of(JobStatus.valueOf(newStatus));
        } catch (Exception e) {
            log.warn(
                    "Failed to fetch job status, task: {}",
                    jobInfoDetail.getInstance().getName());
            return Optional.empty();
        }
    }
}
