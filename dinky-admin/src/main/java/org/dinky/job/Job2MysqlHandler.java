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
import org.dinky.daemon.task.DaemonFactory;
import org.dinky.daemon.task.DaemonTaskConfig;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.Cluster;
import org.dinky.data.model.History;
import org.dinky.data.model.JobHistory;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.Task;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.HistoryService;
import org.dinky.service.JarService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;
import org.dinky.utils.JSONUtil;

import java.time.LocalDateTime;

import org.springframework.context.annotation.DependsOn;

/**
 * Job2MysqlHandler
 *
 * @since 2021/6/27 0:04
 */
@DependsOn("springContextUtils")
public class Job2MysqlHandler implements JobHandler {

    private static final HistoryService historyService;
    private static final ClusterInstanceService clusterInstanceService;
    private static final ClusterConfigurationService clusterConfigurationService;
    private static final JarService jarService;
    private static final JobInstanceService jobInstanceService;
    private static final JobHistoryService jobHistoryService;
    private static final TaskService taskService;

    static {
        historyService = SpringContextUtils.getBean("historyServiceImpl", HistoryService.class);
        clusterInstanceService =
                SpringContextUtils.getBean(
                        "clusterInstanceServiceImpl", ClusterInstanceService.class);
        clusterConfigurationService =
                SpringContextUtils.getBean(
                        "clusterConfigurationServiceImpl", ClusterConfigurationService.class);
        jarService = SpringContextUtils.getBean("jarServiceImpl", JarService.class);
        jobInstanceService =
                SpringContextUtils.getBean("jobInstanceServiceImpl", JobInstanceService.class);
        jobHistoryService =
                SpringContextUtils.getBean("jobHistoryServiceImpl", JobHistoryService.class);
        taskService = SpringContextUtils.getBean("taskServiceImpl", TaskService.class);
    }

    @Override
    public boolean init() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setType(job.getType().getLongValue());
        if (job.isUseGateway()) {
            history.setClusterConfigurationId(job.getJobConfig().getClusterConfigurationId());
        } else {
            history.setClusterId(job.getJobConfig().getClusterId());
        }
        history.setJobManagerAddress(job.getJobManagerAddress());
        history.setJobName(job.getJobConfig().getJobName());
        history.setSession(job.getJobConfig().getSession());
        history.setStatus(job.getStatus().ordinal());
        history.setStatement(job.getStatement());
        history.setStartTime(job.getStartTime());
        history.setTaskId(job.getJobConfig().getTaskId());
        history.setConfigJson(JSONUtil.toJsonString(job.getJobConfig()));
        historyService.save(history);

        job.setId(history.getId());

        return true;
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public boolean running() {
        return true;
    }

    @Override
    public boolean success() {
        Job job = JobContextHolder.getJob();
        Integer taskId = job.getJobConfig().getTaskId();

        History history = new History();
        history.setId(job.getId());
        if (job.isUseGateway() && Asserts.isNullString(job.getJobId())) {
            job.setJobId("unknown");
            history.setStatus(JobStatus.FAILED.ordinal());
            history.setJobId(job.getJobId());
            history.setEndTime(job.getEndTime());
            history.setError("没有获取到任何JID，请自行排查原因");
            historyService.updateById(history);
            return false;
        }

        history.setStatus(job.getStatus().ordinal());
        history.setJobId(job.getJobId());
        history.setEndTime(job.getEndTime());
        history.setJobManagerAddress(job.isUseGateway() ? job.getJobManagerAddress() : null);

        Integer clusterId = job.getJobConfig().getClusterId();
        Cluster cluster;
        final Integer clusterConfigurationId = job.getJobConfig().getClusterConfigurationId();
        if (job.isUseGateway()) {
            cluster =
                    clusterInstanceService.registersCluster(
                            Cluster.autoRegistersCluster(
                                    job.getJobManagerAddress(),
                                    job.getJobId(),
                                    job.getJobConfig().getJobName() + "_" + LocalDateTime.now(),
                                    job.getType().getLongValue(),
                                    clusterConfigurationId,
                                    taskId));
            if (Asserts.isNotNull(cluster)) {
                clusterId = cluster.getId();
            }
        } else if (GatewayType.LOCAL.equalsValue(job.getJobConfig().getType())
                && Asserts.isNotNullString(job.getJobManagerAddress())) {
            cluster =
                    clusterInstanceService.registersCluster(
                            Cluster.autoRegistersCluster(
                                    job.getJobManagerAddress(),
                                    job.getJobId(),
                                    job.getJobConfig().getJobName() + "_" + LocalDateTime.now(),
                                    job.getType().getLongValue(),
                                    null,
                                    taskId));
            if (Asserts.isNotNull(cluster)) {
                clusterId = cluster.getId();
            }
        } else {
            cluster = clusterInstanceService.getById(clusterId);
        }

        history.setClusterId(clusterId);
        historyService.updateById(history);

        if (Asserts.isNullCollection(job.getJids())
                || (GatewayType.LOCAL.equalsValue(job.getJobConfig().getType())
                        && Asserts.isNullString(job.getJobManagerAddress()))) {
            return true;
        }

        String jid = job.getJids().get(0);
        JobInstance jobInstance = history.buildJobInstance();
        jobInstance.setHistoryId(job.getId());
        jobInstance.setClusterId(clusterId);
        jobInstance.setTaskId(taskId);
        jobInstance.setName(job.getJobConfig().getJobName());
        jobInstance.setJid(jid);
        jobInstance.setStep(job.getJobConfig().getStep());
        jobInstance.setStatus(JobStatus.INITIALIZING.getValue());
        jobInstanceService.save(jobInstance);

        job.setJobInstanceId(jobInstance.getId());
        Task task = new Task();
        task.setId(taskId);
        task.setJobInstanceId(jobInstance.getId());
        taskService.updateById(task);

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(jobInstance.getId());
        jobHistory.setClusterJson(JSONUtil.toJsonString(cluster));

        jobHistory.setJarJson(
                Asserts.isNotNull(job.getJobConfig().getJarId())
                        ? JSONUtil.toJsonString(jarService.getById(job.getJobConfig().getJarId()))
                        : null);

        jobHistory.setClusterConfigurationJson(
                Asserts.isNotNull(clusterConfigurationId)
                        ? JSONUtil.toJsonString(
                                clusterConfigurationService.getClusterConfigById(
                                        clusterConfigurationId))
                        : null);
        jobHistoryService.save(jobHistory);

        DaemonFactory.addTask(DaemonTaskConfig.build(FlinkJobTask.TYPE, jobInstance.getId()));
        return true;
    }

    @Override
    public boolean failed() {
        Job job = JobContextHolder.getJob();
        History history = new History();
        history.setId(job.getId());
        history.setJobId(job.getJobId());
        history.setStatus(job.getStatus().ordinal());
        history.setJobManagerAddress(job.getJobManagerAddress());
        history.setEndTime(job.getEndTime());
        history.setError(job.getError());
        historyService.updateById(history);
        return true;
    }

    @Override
    public boolean callback() {
        return true;
    }

    @Override
    public boolean close() {
        return true;
    }
}
