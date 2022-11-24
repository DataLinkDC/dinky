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

package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.config.ActionType;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.TestResult;
import com.dlink.model.JobStatus;
import com.dlink.utils.FlinkUtil;
import com.dlink.utils.LogUtil;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.security.SecurityConfiguration;
import org.apache.flink.runtime.security.SecurityUtils;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * YarnSubmiter
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public abstract class YarnGateway extends AbstractGateway {

    public static final String HADOOP_CONFIG = "fs.hdfs.hadoopconf";

    protected YarnConfiguration yarnConfiguration;
    protected YarnClient yarnClient;

    public YarnGateway() {
    }

    public YarnGateway(GatewayConfig config) {
        super(config);
    }

    public void init() {
        initConfig();
        initYarnClient();
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
        configuration.set(YarnConfigOptions.PROVIDED_LIB_DIRS,
                Collections.singletonList(config.getClusterConfig().getFlinkLibPath()));
        if (Asserts.isNotNullString(config.getFlinkConfig().getJobName())) {
            configuration.set(YarnConfigOptions.APPLICATION_NAME, config.getFlinkConfig().getJobName());
        }

        if (Asserts.isNotNullString(config.getClusterConfig().getYarnConfigPath())) {
            configuration.setString(HADOOP_CONFIG, config.getClusterConfig().getYarnConfigPath());
        }

        if (configuration.containsKey(SecurityOptions.KERBEROS_LOGIN_KEYTAB.key())) {
            try {
                SecurityUtils.install(new SecurityConfiguration(configuration));
                UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
                logger.info("安全认证结束，用户和认证方式:" + currentUser.toString());
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        if (getType().isApplicationMode()) {
            configuration.set(YarnConfigOptions.APPLICATION_TYPE,"Dinky Flink");
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
        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, config.getClusterConfig().getFlinkConfigPath());
    }

    private void initYarnClient() {
        yarnConfiguration = new YarnConfiguration();
        yarnConfiguration
                .addResource(new Path(URI.create(config.getClusterConfig().getYarnConfigPath() + "/yarn-site.xml")));
        yarnConfiguration
                .addResource(new Path(URI.create(config.getClusterConfig().getYarnConfigPath() + "/core-site.xml")));
        yarnConfiguration
                .addResource(new Path(URI.create(config.getClusterConfig().getYarnConfigPath() + "/hdfs-site.xml")));
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
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
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        /*
         * if(Asserts.isNotNullString(config.getClusterConfig().getYarnConfigPath())) { configuration =
         * GlobalConfiguration.loadConfiguration(config.getClusterConfig().getYarnConfigPath()); }else { configuration =
         * new Configuration(); }
         */
        SavePointResult result = SavePointResult.build(getType());
        ApplicationId applicationId = getApplicationId();
        /*
         * YarnClusterDescriptor clusterDescriptor = clusterClientFactory .createClusterDescriptor( configuration);
         */
        YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient), true);
        try (
                ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(
                        applicationId).getClusterClient()) {
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
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }

    public SavePointResult savepointJob() {
        return savepointJob(null);
    }

    public SavePointResult savepointJob(String savePoint) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        if (Asserts.isNull(config.getFlinkConfig().getJobId())) {
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to savepont.");
        }
        /*
         * if(Asserts.isNotNullString(config.getClusterConfig().getYarnConfigPath())) { configuration =
         * GlobalConfiguration.loadConfiguration(config.getClusterConfig().getYarnConfigPath()); }else { configuration =
         * new Configuration(); }
         */
        SavePointResult result = SavePointResult.build(getType());
        ApplicationId applicationId = getApplicationId();
        /*
         * YarnClusterDescriptor clusterDescriptor = clusterClientFactory .createClusterDescriptor( configuration);
         */
        YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient), true);
        try (
                ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(
                        applicationId).getClusterClient()) {
            List<JobInfo> jobInfos = new ArrayList<>();
            jobInfos.add(new JobInfo(config.getFlinkConfig().getJobId(), JobInfo.JobStatus.FAIL));
            runSavePointJob(jobInfos, clusterClient, savePoint);
            result.setJobInfos(jobInfos);
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        if (ActionType.CANCEL == config.getFlinkConfig().getAction()
                || SavePointType.CANCEL.equals(config.getFlinkConfig().getSavePointType())) {
            try {
                autoCancelCluster(clusterDescriptor.retrieve(applicationId).getClusterClient());
            } catch (ClusterRetrieveException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void runSavePointJob(List<JobInfo> jobInfos, ClusterClient<ApplicationId> clusterClient,
                                 String savePoint) throws Exception {
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

    private void autoCancelCluster(ClusterClient<ApplicationId> clusterClient) {
        Executors.newCachedThreadPool().submit(() -> {
            try {
                Thread.sleep(3000);
                clusterClient.shutDownCluster();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                clusterClient.close();
            }
        });
    }

    public TestResult test() {
        try {
            initConfig();
        } catch (Exception e) {
            logger.error("测试 Flink 配置失败：" + e.getMessage());
            return TestResult.fail("测试 Flink 配置失败：" + e.getMessage());
        }
        try {
            initYarnClient();
            if (yarnClient.isInState(Service.STATE.STARTED)) {
                logger.info("配置连接测试成功");
                return TestResult.success();
            } else {
                logger.error("该配置无对应 Yarn 集群存在");
                return TestResult.fail("该配置无对应 Yarn 集群存在");
            }
        } catch (Exception e) {
            logger.error("测试 Yarn 配置失败：" + e.getMessage());
            return TestResult.fail("测试 Yarn 配置失败：" + e.getMessage());
        }
    }

    private ApplicationId getApplicationId() {
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        configuration.set(YarnConfigOptions.APPLICATION_ID, config.getClusterConfig().getAppId());
        ApplicationId applicationId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(applicationId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        return applicationId;
    }

    @Override
    public JobStatus getJobStatusById(String id) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        config.getClusterConfig().setAppId(id);
        ApplicationReport applicationReport = null;
        try {
            applicationReport = yarnClient.getApplicationReport(getApplicationId());
            YarnApplicationState yarnApplicationState = applicationReport.getYarnApplicationState();
            FinalApplicationStatus finalApplicationStatus = applicationReport.getFinalApplicationStatus();
            switch (yarnApplicationState) {
                case FINISHED:
                    switch (finalApplicationStatus) {
                        case KILLED:
                            return JobStatus.CANCELED;
                        case FAILED:
                            return JobStatus.FAILED;
                        default:
                            return JobStatus.FINISHED;
                    }
                case RUNNING:
                    return JobStatus.RUNNING;
                case FAILED:
                    return JobStatus.FAILED;
                case KILLED:
                    return JobStatus.CANCELED;
                case SUBMITTED:
                    return JobStatus.CREATED;
                default:
                    return JobStatus.INITIALIZING;
            }
        } catch (YarnException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JobStatus.UNKNOWN;
    }
}
