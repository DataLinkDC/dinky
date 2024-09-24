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
import org.dinky.constant.CustomerConfigureOptions;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.constant.DirConstant;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.CustomConfig;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.executor.ClusterDescriptorAdapterImpl;
import org.dinky.gateway.AbstractGateway;
import org.dinky.gateway.config.ClusterConfig;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.gateway.result.YarnResult;
import org.dinky.utils.FlinkJsonUtil;
import org.dinky.utils.ThreadUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
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
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.zookeeper.ZooKeeper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

public abstract class YarnGateway extends AbstractGateway {
    private static final String HTML_TAG_REGEX = "<pre>(.*)</pre>";
    private final String TMP_SQL_EXEC_DIR =
            String.format("%s/sql-exec/%s", DirConstant.getTempRootDir(), UUID.randomUUID());

    protected YarnConfiguration yarnConfiguration;

    protected YarnClient yarnClient;

    public YarnGateway() {}

    public YarnGateway(GatewayConfig config) {
        super(config);
    }

    @Override
    public void init() {
        initConfig();
        initYarnClient();
    }

    private void initConfig() {
        final ClusterConfig clusterConfig = config.getClusterConfig();
        configuration = GlobalConfiguration.loadConfiguration(
                clusterConfig.getFlinkConfigPath().trim());
        configuration.set(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "parent-first");

        final FlinkConfig flinkConfig = config.getFlinkConfig();
        if (Asserts.isNotNull(flinkConfig.getConfiguration())) {
            addConfigParas(flinkConfig.getConfiguration());
        }
        // Adding custom Flink configurations
        if (Asserts.isNotNull(flinkConfig.getFlinkConfigList())) {
            addConfigParas(flinkConfig.getFlinkConfigList());
        }

        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());

        configuration.set(
                YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(clusterConfig.getFlinkLibPath()));
        if (Asserts.isNotNullString(flinkConfig.getJobName())) {
            configuration.set(YarnConfigOptions.APPLICATION_NAME, flinkConfig.getJobName());
        }

        if (Asserts.isNotNullString(clusterConfig.getHadoopConfigPath())) {
            configuration.setString(
                    ConfigConstants.PATH_HADOOP_CONFIG,
                    FileUtil.file(clusterConfig.getHadoopConfigPath()).getAbsolutePath());
        }

        if (configuration.containsKey(SecurityOptions.KERBEROS_LOGIN_KEYTAB.key())) {
            try {
                SecurityUtils.install(new SecurityConfiguration(configuration));
                UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
                logger.info(
                        "Security authentication completed, user and authentication method:{}", currentUser.toString());
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

        synchronized (YarnGateway.class) {
            String hadoopUserName;
            try {
                hadoopUserName = UserGroupInformation.getLoginUser().getUserName();
            } catch (Exception e) {
                hadoopUserName = "hdfs";
            }

            // Set the username for the yarn submission
            String yarnUser = configuration.get(CustomerConfigureOptions.YARN_APPLICATION_USER);
            if (StrUtil.isNotBlank(yarnUser)) {
                UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser(yarnUser));
            }
            try {
                yarnClient.start();
            } finally {
                if (StrUtil.isNotBlank(yarnUser)) {
                    UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser(hadoopUserName));
                }
            }
        }
    }

    private Path getYanConfigFilePath(String path) {
        return new Path(URI.create(config.getClusterConfig().getHadoopConfigPath() + "/" + path));
    }

    @Override
    public SavePointResult savepointCluster(String savePoint) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        ApplicationId applicationId = getApplicationId();
        YarnClusterDescriptor clusterDescriptor = createInitYarnClusterDescriptor();
        return runClusterSavePointResult(savePoint, applicationId, clusterDescriptor);
    }

    @Override
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
                logger.error(e.getMessage());
            } finally {
                clusterClient.close();
            }
        });
    }

    @Override
    public TestResult test() {
        try {
            initConfig();
        } catch (Exception e) {
            logger.error("Failed to test Flink configuration：" + e.getMessage());
            return TestResult.fail("Failed to test Flink configuration：" + e.getMessage());
        }

        try {
            initYarnClient();
            if (yarnClient.isInState(Service.STATE.STARTED)) {
                logger.info("Configuration connection test successful");
                return TestResult.success();
            } else {
                logger.error("This configuration does not have a corresponding Yarn cluster present");
                return TestResult.fail("This configuration does not have a corresponding Yarn cluster present");
            }
        } catch (Exception e) {
            logger.error("Test Yarn configuration failed: {}", e.getMessage());
            return TestResult.fail("Test Yarn configuration failed:" + e.getMessage());
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
        ClusterDescriptorAdapterImpl clusterDescriptorAdapter = new ClusterDescriptorAdapterImpl(yarnClusterDescriptor);
        if (Asserts.isNotNull(config.getJarPaths())) {
            clusterDescriptorAdapter.addShipFiles(
                    Arrays.stream(config.getJarPaths()).map(FileUtil::file).collect(Collectors.toList()));
            clusterDescriptorAdapter.addShipFiles(new ArrayList<>(udfPathContextHolder.getPyUdfFile()));
        }
        Set<File> otherPluginsFiles = udfPathContextHolder.getAllFileSet();

        if (CollUtil.isNotEmpty(otherPluginsFiles)) {
            clusterDescriptorAdapter.addShipFiles(new ArrayList<>(otherPluginsFiles));
        }
        return yarnClusterDescriptor;
    }

    protected YarnClusterDescriptor createInitYarnClusterDescriptor() {
        return new YarnClusterDescriptor(
                configuration,
                yarnConfiguration,
                yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient),
                true);
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
        webUrl = clusterClient.getWebInterfaceURL();
        final List<JobDetails> jobDetailsList = new ArrayList<>();
        while (jobDetailsList.isEmpty() && counts-- > 0) {
            ApplicationReport applicationReport = yarnClient.getApplicationReport(clusterClient.getClusterId());
            if (applicationReport.getYarnApplicationState() != YarnApplicationState.RUNNING) {
                String log = getYarnContainerLog(applicationReport);
                throw new RuntimeException(String.format(
                        "Yarn application state is not running, please check yarn cluster status. Web URL is: %s , Log content: %s",
                        webUrl, log));
            }
            // 睡眠1秒，防止flink因为依赖或其他问题导致任务秒挂
            Thread.sleep(1000);
            String url = yarnClient
                            .getApplicationReport(clusterClient.getClusterId())
                            .getTrackingUrl()
                    + JobsOverviewHeaders.URL.substring(1);

            String json = HttpUtil.get(url);
            try {
                MultipleJobsDetails jobsDetails = FlinkJsonUtil.toBean(json, JobsOverviewHeaders.getInstance());
                jobDetailsList.addAll(jobsDetails.getJobs());
            } catch (Exception e) {
                Thread.sleep(1000);
                String log = getYarnContainerLog(applicationReport);
                logger.error(
                        "Yarn application state is not running, please check yarn cluster status. Log content: {}",
                        log);
            }
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

    protected String getYarnContainerLog(ApplicationReport applicationReport) throws YarnException, IOException {
        // Wait for up to 2.5 s. If the history log is not found yet, a prompt message will be returned.
        int counts = 5;
        while (yarnClient
                        .getContainers(applicationReport.getCurrentApplicationAttemptId())
                        .isEmpty()
                && counts-- > 0) {
            ThreadUtil.sleep(500);
        }
        List<ContainerReport> containers = yarnClient.getContainers(applicationReport.getCurrentApplicationAttemptId());
        if (CollUtil.isNotEmpty(containers)) {
            String logUrl = containers.get(0).getLogUrl();
            String content = HttpUtil.get(logUrl + "/jobmanager.log?start=-10000");
            return ReUtil.getGroup1(HTML_TAG_REGEX, content);
        }
        return "No history log found yet. so can't get log url, please check yarn cluster status or check if the flink job is running in yarn cluster or please go to yarn interface to view the log.";
    }

    protected File preparSqlFile() {
        File tempSqlFile = new File(
                String.format("%s/%s", TMP_SQL_EXEC_DIR, configuration.get(CustomerConfigureOptions.EXEC_SQL_FILE)));
        logger.info("Temp sql file path : {}", tempSqlFile.getAbsolutePath());
        String sql = config == null ? "" : config.getSql();
        FileUtil.writeString(Optional.ofNullable(sql).orElse(""), tempSqlFile.getAbsolutePath(), "UTF-8");
        return tempSqlFile;
    }

    public boolean close() {
        return FileUtil.del(TMP_SQL_EXEC_DIR);
    }

    @Override
    public String getLatestJobManageHost(String appId, String oldJobManagerHost) {
        initConfig();

        HighAvailabilityMode highAvailabilityMode = HighAvailabilityMode.fromConfig(configuration);

        if (HighAvailabilityMode.ZOOKEEPER == highAvailabilityMode) {
            configuration.set(HighAvailabilityOptions.HA_CLUSTER_ID, appId);
            String zkQuorum = configuration.getValue(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);

            if (zkQuorum == null || StringUtils.isBlank(zkQuorum)) {
                throw new RuntimeException("No valid ZooKeeper quorum has been specified. "
                        + "You can specify the quorum via the configuration key '"
                        + HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM.key()
                        + "'.");
            }
            int sessionTimeout = Convert.toInt(configuration.get(HighAvailabilityOptions.ZOOKEEPER_SESSION_TIMEOUT));
            String root = configuration.getValue(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT);
            String namespace = configuration.getValue(HighAvailabilityOptions.HA_CLUSTER_ID);

            ZooKeeper zooKeeper = null;
            try {
                zooKeeper = new ZooKeeper(zkQuorum, sessionTimeout, watchedEvent -> {});
                String path = generateZookeeperPath(root, namespace, "leader", "rest_server", "connection_info");
                byte[] data = zooKeeper.getData(path, false, null);
                if (data != null && data.length > 0) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    ObjectInputStream ois = new ObjectInputStream(bais);

                    final String leaderAddress = ois.readUTF();
                    if (Asserts.isNotNullString(leaderAddress)) {
                        String hosts = leaderAddress.substring(7);
                        if (!oldJobManagerHost.equals(hosts)) {
                            return hosts;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                if (Asserts.isNotNull(zooKeeper)) {
                    try {
                        zooKeeper.close();
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                }
            }
        } else {
            logger.info("High availability non-ZooKeeper mode, current mode is：{}", highAvailabilityMode);
        }
        return null;
    }

    /**
     * Creates a ZooKeeper path of the form "/a/b/.../z".
     */
    private static String generateZookeeperPath(String... paths) {

        return Arrays.stream(paths)
                .map(YarnGateway::trimSlashes)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("/", "/", ""));
    }

    private static String trimSlashes(String input) {
        int left = 0;
        int right = input.length() - 1;

        while (left <= right && input.charAt(left) == '/') {
            left++;
        }

        while (right >= left && input.charAt(right) == '/') {
            right--;
        }

        if (left <= right) {
            return input.substring(left, right + 1);
        } else {
            return "";
        }
    }
}
