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

package com.dlink.gateway.kubernetes;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.config.ActionType;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.TestResult;
import com.dlink.utils.FlinkUtil;
import com.dlink.utils.LogUtil;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClientFactory;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * KubernetesGateway
 *
 * @author wenmo
 * @since 2021/12/26 14:09
 */
public abstract class KubernetesGateway extends AbstractGateway {

    protected FlinkKubeClient client;

    public KubernetesGateway() {
    }

    public KubernetesGateway(GatewayConfig config) {
        super(config);
    }

    public void init() {
        initConfig();
        initKubeClient();
    }

    private void initConfig() {
        configuration = GlobalConfiguration.loadConfiguration(config.getClusterConfig().getFlinkConfigPath());
        if (Asserts.isNotNull(config.getFlinkConfig().getConfiguration())) {
            addConfigParas(config.getFlinkConfig().getConfiguration());
        }
        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());
        if (Asserts.isNotNullString(config.getFlinkConfig().getSavePoint())) {
            configuration.setString(SavepointConfigOptions.SAVEPOINT_PATH, config.getFlinkConfig().getSavePoint());
        }
        if (Asserts.isNotNullString(config.getFlinkConfig().getJobName())) {
            configuration.set(KubernetesConfigOptions.CLUSTER_ID, config.getFlinkConfig().getJobName());
        }
        if (getType().isApplicationMode()) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            if (configuration.contains(CheckpointingOptions.CHECKPOINTS_DIRECTORY)) {
                configuration.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY,
                        configuration.getString(CheckpointingOptions.CHECKPOINTS_DIRECTORY) + "/" + uuid);
            }
            if (configuration.contains(CheckpointingOptions.SAVEPOINT_DIRECTORY)) {
                configuration.set(CheckpointingOptions.SAVEPOINT_DIRECTORY,
                        configuration.getString(CheckpointingOptions.SAVEPOINT_DIRECTORY) + "/" + uuid);
            }
        }
    }

    private void initKubeClient() {
        client = FlinkKubeClientFactory.getInstance().fromConfiguration(configuration, "client");
    }

    private void addConfigParas(Map<String, String> configMap) {
        if (Asserts.isNotNull(configMap)) {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                if (Asserts.isAllNotNullString(entry.getKey(), entry.getValue())) {
                    this.configuration.setString(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public SavePointResult savepointCluster() {
        return savepointCluster(null);
    }

    public SavePointResult savepointCluster(String savePoint) {
        if (Asserts.isNull(client)) {
            init();
        }
        SavePointResult result = SavePointResult.build(getType());
        configuration.set(KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        KubernetesClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);
        try (ClusterClient<String> clusterClient = clusterDescriptor.retrieve(
                clusterId).getClusterClient()) {
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
        return null;
    }

    public SavePointResult savepointJob() {
        return savepointJob(null);
    }

    public SavePointResult savepointJob(String savePoint) {
        if (Asserts.isNull(client)) {
            init();
        }
        if (Asserts.isNull(config.getFlinkConfig().getJobId())) {
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to savepont.");
        }
        SavePointResult result = SavePointResult.build(getType());
        configuration.set(KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        KubernetesClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);
        try (ClusterClient<String> clusterClient = clusterDescriptor.retrieve(clusterId).getClusterClient()) {
            List<JobInfo> jobInfos = new ArrayList<>();
            jobInfos.add(new JobInfo(config.getFlinkConfig().getJobId(), JobInfo.JobStatus.FAIL));
            runSavePointJob(jobInfos, clusterClient, savePoint);
            result.setJobInfos(jobInfos);
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        return result;
    }

    private void runSavePointJob(List<JobInfo> jobInfos, ClusterClient<String> clusterClient, String savePoint)
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

    public TestResult test() {
        try {
            initConfig();
        } catch (Exception e) {
            logger.error("测试 Flink 配置失败：" + e.getMessage());
            return TestResult.fail("测试 Flink 配置失败：" + e.getMessage());
        }
        try {
            initKubeClient();
            logger.info("配置连接测试成功");
            return TestResult.success();
        } catch (Exception e) {
            logger.error("测试 Kubernetes 配置失败：" + e.getMessage());
            return TestResult.fail("测试 Kubernetes 配置失败：" + e.getMessage());
        }
    }

    @Override
    public void killCluster() {
        if (Asserts.isNull(client)) {
            init();
        }
        configuration.set(KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        KubernetesClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);
        try {
            clusterDescriptor.killCluster(clusterId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
