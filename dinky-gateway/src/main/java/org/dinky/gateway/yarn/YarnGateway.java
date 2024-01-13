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

package org.dinky.gateway.yarn;

import org.dinky.assertion.Asserts;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.gateway.AbstractGateway;
import org.dinky.gateway.config.ClusterConfig;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.model.CustomConfig;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.gateway.result.YarnResult;
import org.dinky.utils.FlinkJsonUtil;
import org.dinky.utils.ThreadUtil;

import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.messages.webmonitor.JobDetails;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.messages.JobsOverviewHeaders;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;

public abstract class YarnGateway extends AbstractGateway {

    public static final String HADOOP_CONFIG = "fs.hdfs.hadoopconf";
    private static final String HTML_TAG_REGEX = "<pre>(.*)</pre>";

    protected YarnConfiguration yarnConfiguration;

    protected YarnClient yarnClient;

    public YarnGateway() {}

    public YarnGateway(GatewayConfig config) {
        super(config);
    }

    public void init() {
        initConfig();
        initYarnClient();
    }

    private void initConfig() {
        final ClusterConfig clusterConfig = config.getClusterConfig();
        configuration = GlobalConfiguration.loadConfiguration(clusterConfig.getFlinkConfigPath());
        if (!configuration.contains(RestOptions.PORT)) {
            configuration.set(RestOptions.PORT, RestOptions.PORT.defaultValue());
        }
        configuration.set(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "parent-first");

        final FlinkConfig flinkConfig = config.getFlinkConfig();
        if (Asserts.isNotNull(flinkConfig.getConfiguration())) {
            addConfigParas(flinkConfig.getConfiguration());
        }

        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());

        configuration.set(
                YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(clusterConfig.getFlinkLibPath()));
        if (Asserts.isNotNullString(flinkConfig.getJobName())) {
            configuration.set(YarnConfigOptions.APPLICATION_NAME, flinkConfig.getJobName());
        }

        if (Asserts.isNotNullString(clusterConfig.getHadoopConfigPath())) {
            configuration.setString(HADOOP_CONFIG, clusterConfig.getHadoopConfigPath());
        }

        if (configuration.containsKey(SecurityOptions.KERBEROS_LOGIN_KEYTAB.key())) {
            try {
                SecurityUtils.install(new SecurityConfiguration(configuration));
                UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
                logger.info("安全认证结束，用户和认证方式:" + currentUser.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (getType().isApplicationMode()) {
            configuration.set(YarnConfigOptions.APPLICATION_TYPE, "Dinky Flink");
            resetCheckpointInApplicationMode(flinkConfig.getJobName());
        }

        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, clusterConfig.getFlinkConfigPath());
    }

    private void initYarnClient() {
        final ClusterConfig clusterConfig = config.getClusterConfig();
        yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.addResource(getYanConfigFilePath("yarn-site.xml"));
        yarnConfiguration.addResource(getYanConfigFilePath("core-site.xml"));
        yarnConfiguration.addResource(getYanConfigFilePath("hdfs-site.xml"));

        List<CustomConfig> hadoopConfigList = clusterConfig.getHadoopConfigList();
        if (CollectionUtil.isNotEmpty(hadoopConfigList)) {
            hadoopConfigList.forEach((customConfig) -> {
                Assert.notNull(customConfig.getName(), "Custom hadoop config has null key");
                Assert.notNull(customConfig.getValue(), "Custom hadoop config has null value");
                yarnConfiguration.set(customConfig.getName(), customConfig.getValue());
            });
        }

        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
    }

    private Path getYanConfigFilePath(String path) {
        return new Path(URI.create(config.getClusterConfig().getHadoopConfigPath() + "/" + path));
    }

    public SavePointResult savepointCluster(String savePoint) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        ApplicationId applicationId = getApplicationId();
        YarnClusterDescriptor clusterDescriptor = createInitYarnClusterDescriptor();
        return runClusterSavePointResult(savePoint, applicationId, clusterDescriptor);
    }

    public SavePointResult savepointJob(String savePoint) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        if (Asserts.isNull(config.getFlinkConfig().getJobId())) {
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to" + " savepont.");
        }

        ApplicationId applicationId = getApplicationId();
        YarnClusterDescriptor clusterDescriptor = createInitYarnClusterDescriptor();
        SavePointResult result = runSavePointResult(savePoint, applicationId, clusterDescriptor);

        if (ActionType.CANCEL == config.getFlinkConfig().getAction()
                || SavePointType.CANCEL.equals(config.getFlinkConfig().getSavePointType())) {
            try {
                autoCancelCluster(clusterDescriptor.retrieve(applicationId).getClusterClient());
            } catch (ClusterRetrieveException e) {
                logger.error(e.getMessage());
            }
        }

        return result;
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
        configuration.set(
                YarnConfigOptions.APPLICATION_ID, config.getClusterConfig().getAppId());
        ApplicationId applicationId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(applicationId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like" + " to connect.");
        }
        return applicationId;
    }

    @Override
    public JobStatus getJobStatusById(String id) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        config.getClusterConfig().setAppId(id);
        try {
            ApplicationReport applicationReport = yarnClient.getApplicationReport(getApplicationId());
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
                case ACCEPTED:
                case NEW:
                case NEW_SAVING:
                    return JobStatus.CREATED;
                default:
                    return JobStatus.UNKNOWN;
            }
        } catch (YarnException | IOException e) {
            logger.error(e.getMessage());
            return JobStatus.UNKNOWN;
        }
    }

    @Override
    public void killCluster() {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        try {
            yarnClient.killApplication(getApplicationId());
        } catch (YarnException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    protected YarnClusterDescriptor createYarnClusterDescriptorWithJar(FlinkUdfPathContextHolder udfPathContextHolder) {
        YarnClusterDescriptor yarnClusterDescriptor = createInitYarnClusterDescriptor();

        if (Asserts.isNotNull(config.getJarPaths())) {
            yarnClusterDescriptor.addShipFiles(
                    Arrays.stream(config.getJarPaths()).map(FileUtil::file).collect(Collectors.toList()));
            yarnClusterDescriptor.addShipFiles(new ArrayList<>(udfPathContextHolder.getPyUdfFile()));
        }
        Set<File> otherPluginsFiles = udfPathContextHolder.getOtherPluginsFiles();

        if (CollUtil.isNotEmpty(otherPluginsFiles)) {
            yarnClusterDescriptor.addShipFiles(CollUtil.newArrayList(otherPluginsFiles));
        }
        return yarnClusterDescriptor;
    }

    protected YarnClusterDescriptor createInitYarnClusterDescriptor() {
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
                configuration,
                yarnConfiguration,
                yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient),
                true);
        return yarnClusterDescriptor;
    }

    protected String getWebUrl(ClusterClient<ApplicationId> clusterClient, YarnResult result)
            throws YarnException, IOException, InterruptedException {
        String webUrl;
        int counts = SystemConfiguration.getInstances().getJobIdWait();
        while (yarnClient.getApplicationReport(clusterClient.getClusterId()).getYarnApplicationState()
                        == YarnApplicationState.ACCEPTED
                && counts-- > 0) {
            Thread.sleep(1000);
        }
        // 睡眠2秒，防止application快速识别抛出的错误
        ThreadUtil.sleep(2000);
        ApplicationReport applicationReport = yarnClient.getApplicationReport(clusterClient.getClusterId());
        if (applicationReport.getYarnApplicationState() != YarnApplicationState.RUNNING) {
            String logUrl = yarnClient
                    .getContainers(applicationReport.getCurrentApplicationAttemptId())
                    .get(0)
                    .getLogUrl();
            String log = ReUtil.getGroup1(HTML_TAG_REGEX, HttpUtil.get(logUrl + "/jobmanager.log?start=-10000"));
            logger.error("\n\nHistory log url is: {}\n\n ", logUrl);
            throw new RuntimeException(
                    "Yarn application state is not running, please check yarn cluster status. Log content:\n" + log);
        }
        webUrl = applicationReport.getOriginalTrackingUrl();
        final List<JobDetails> jobDetailsList = new ArrayList<>();
        while (jobDetailsList.isEmpty() && counts-- > 0) {
            Thread.sleep(1000);

            String url = yarnClient
                            .getApplicationReport(clusterClient.getClusterId())
                            .getTrackingUrl()
                    + JobsOverviewHeaders.URL.substring(1);

            String json = HttpUtil.get(url);
            MultipleJobsDetails jobsDetails = FlinkJsonUtil.toBean(json, JobsOverviewHeaders.getInstance());
            jobDetailsList.addAll(jobsDetails.getJobs());
            if (!jobDetailsList.isEmpty()) {
                break;
            }
        }

        if (!jobDetailsList.isEmpty()) {
            List<String> jobIds = new ArrayList<>();
            for (JobDetails jobDetails : jobDetailsList) {
                jobIds.add(jobDetails.getJobId().toHexString());
            }
            result.setJids(jobIds);
        }
        return webUrl;
    }
}
