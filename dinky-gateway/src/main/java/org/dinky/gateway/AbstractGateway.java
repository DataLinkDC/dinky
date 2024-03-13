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

package org.dinky.gateway;

import org.dinky.assertion.Asserts;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.JobStatus;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.exception.NotSupportGetStatusException;
import org.dinky.gateway.model.JobInfo;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.utils.FlinkUtil;
import org.dinky.utils.LogUtil;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.text.StrFormatter;

/**
 * AbstractGateway
 *
 * @since 2021/10/29
 */
public abstract class AbstractGateway implements Gateway {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractGateway.class);
    protected GatewayConfig config;
    protected Configuration configuration = new Configuration();

    public AbstractGateway() {}

    public AbstractGateway(GatewayConfig config) {
        this.config = config;
    }

    @Override
    public boolean canHandle(GatewayType type) {
        return type == getType();
    }

    @Override
    public void setGatewayConfig(GatewayConfig config) {
        this.config = config;
    }

    protected abstract void init();

    protected void addConfigParas(Map<String, String> configMap) {
        if (Asserts.isNotNull(configMap)) {
            configMap.entrySet().stream()
                    .filter(entry -> Asserts.isAllNotNullString(entry.getKey(), entry.getValue()))
                    .forEach(entry -> this.configuration.setString(entry.getKey(), entry.getValue()));
        }
    }

    protected <T> void addConfigParas(ConfigOption<T> key, T value) {
        if (Asserts.isNotNull(key) && Asserts.isNotNull(value)) {
            this.configuration.set(key, value);
        } else {
            logger.warn("Gateway config key or value is null, key: {}, value: {}", key, value);
        }
    }

    public SavePointResult savepointCluster() {
        return savepointCluster(null);
    }

    public SavePointResult savepointJob() {
        return savepointJob(null);
    }

    protected <T> void runSavePointJob(List<JobInfo> jobInfos, ClusterClient<T> clusterClient, String savePoint)
            throws Exception {
        for (JobInfo jobInfo : jobInfos) {
            if (ActionType.CANCEL == config.getFlinkConfig().getAction()) {
                clusterClient.cancel(JobID.fromHexString(jobInfo.getJobId()));
                jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
                continue;
            }
            switch (config.getFlinkConfig().getSavePointType()) {
                case TRIGGER:
                    jobInfo.setSavePoint(FlinkUtil.triggerSavepoint(clusterClient, jobInfo.getJobId(), savePoint));
                    break;
                case STOP:
                    jobInfo.setSavePoint(FlinkUtil.stopWithSavepoint(clusterClient, jobInfo.getJobId(), savePoint));
                    jobInfo.setStatus(JobInfo.JobStatus.STOP);
                    break;
                case CANCEL:
                    jobInfo.setSavePoint(FlinkUtil.cancelWithSavepoint(clusterClient, jobInfo.getJobId(), savePoint));
                    jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
                    break;
                default:
            }
        }
    }

    protected <T> SavePointResult runSavePointResult(
            String savePoint, T applicationId, ClusterDescriptor<T> clusterDescriptor) {
        SavePointResult result = SavePointResult.build(getType());
        try (ClusterClient<T> clusterClient =
                clusterDescriptor.retrieve(applicationId).getClusterClient()) {
            List<JobInfo> jobInfos = Collections.singletonList(
                    new JobInfo(config.getFlinkConfig().getJobId(), JobInfo.JobStatus.FAIL));
            runSavePointJob(jobInfos, clusterClient, savePoint);
            result.setJobInfos(jobInfos);
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        return result;
    }

    protected <T> SavePointResult runClusterSavePointResult(
            String savePoint, T applicationId, ClusterDescriptor<T> clusterDescriptor) {
        SavePointResult result = SavePointResult.build(getType());
        try (ClusterClient<T> clusterClient =
                clusterDescriptor.retrieve(applicationId).getClusterClient()) {
            List<JobInfo> jobInfos = new ArrayList<>();
            CompletableFuture<Collection<JobStatusMessage>> listJobsFuture = clusterClient.listJobs();
            for (JobStatusMessage jobStatusMessage : listJobsFuture.get()) {
                JobInfo jobInfo = new JobInfo(jobStatusMessage.getJobId().toHexString());
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
                jobInfos.add(jobInfo);
            }
            runSavePointJob(jobInfos, clusterClient, savePoint);
            result.setJobInfos(jobInfos);
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        return result;
    }

    @Override
    public JobStatus getJobStatusById(String id) {
        throw new NotSupportGetStatusException(StrFormatter.format("{} is not support get status.", getType()));
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        throw new GatewayException("Couldn't deploy Flink Cluster with job graph.");
    }

    @Override
    public GatewayResult submitJar(FlinkUdfPathContextHolder udfPathContextHolder) {
        throw new GatewayException("Couldn't deploy Flink Cluster with User Application Jar.");
    }

    protected void resetCheckpointInApplicationMode(String jobName) {
        String uuid = UUID.randomUUID().toString();
        String checkpointsDirectory = configuration.getString(CheckpointingOptions.CHECKPOINTS_DIRECTORY);
        String savepointDirectory = configuration.getString(CheckpointingOptions.SAVEPOINT_DIRECTORY);

        Optional.ofNullable(checkpointsDirectory)
                .ifPresent(dir -> configuration.set(
                        CheckpointingOptions.CHECKPOINTS_DIRECTORY,
                        StrFormatter.format("{}/{}/{}", dir, jobName, uuid)));

        Optional.ofNullable(savepointDirectory)
                .ifPresent(dir -> configuration.set(
                        CheckpointingOptions.SAVEPOINT_DIRECTORY, StrFormatter.format("{}/{}/{}", dir, jobName, uuid)));
    }

    @Override
    public void killCluster() {
        logger.error("Could not kill the Flink cluster");
    }

    @Override
    public GatewayResult deployCluster(FlinkUdfPathContextHolder udfPathContextHolder) {
        logger.error("Could not deploy the Flink cluster");
        return null;
    }

    protected ClusterSpecification.ClusterSpecificationBuilder createClusterSpecificationBuilder() {
        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                new ClusterSpecification.ClusterSpecificationBuilder();
        if (configuration.contains(JobManagerOptions.TOTAL_PROCESS_MEMORY)) {
            clusterSpecificationBuilder.setMasterMemoryMB(
                    configuration.get(JobManagerOptions.TOTAL_PROCESS_MEMORY).getMebiBytes());
        }
        if (configuration.contains(TaskManagerOptions.TOTAL_PROCESS_MEMORY)) {
            clusterSpecificationBuilder.setTaskManagerMemoryMB(
                    configuration.get(TaskManagerOptions.TOTAL_PROCESS_MEMORY).getMebiBytes());
        }
        if (configuration.contains(TaskManagerOptions.NUM_TASK_SLOTS)) {
            clusterSpecificationBuilder
                    .setSlotsPerTaskManager(configuration.get(TaskManagerOptions.NUM_TASK_SLOTS))
                    .createClusterSpecification();
        }
        return clusterSpecificationBuilder;
    }

    @Override
    public boolean onJobFinishCallback(String status) {
        return true;
    }
}
