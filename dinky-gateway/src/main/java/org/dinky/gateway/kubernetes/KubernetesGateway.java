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

package org.dinky.gateway.kubernetes;

import org.dinky.assertion.Asserts;
import org.dinky.gateway.AbstractGateway;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.config.K8sConfig;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.utils.TextUtil;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.Fabric8FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClientFactory;

import java.lang.reflect.Method;
import java.util.Collections;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;

/**
 * KubernetesGateway
 *
 * @since 2021/12/26 14:09
 */
public abstract class KubernetesGateway extends AbstractGateway {

    protected FlinkKubeClient client;

    public KubernetesGateway() {}

    public KubernetesGateway(GatewayConfig config) {
        super(config);
    }

    public void init() {
        initConfig();
        initKubeClient();
    }

    private void initConfig() {
        String flinkConfigPath = config.getClusterConfig().getFlinkConfigPath();
        if (!TextUtil.isEmpty(flinkConfigPath)) {
            configuration = GlobalConfiguration.loadConfiguration(flinkConfigPath);
        }

        FlinkConfig flinkConfig = config.getFlinkConfig();
        flinkConfig.getConfiguration().putAll(config.getKubernetesConfig().getConfiguration());
        addConfigParas(flinkConfig.getConfiguration());
        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());
        configuration.set(KubernetesConfigOptions.CLUSTER_ID, flinkConfig.getJobName());

        K8sConfig k8sConfig = config.getKubernetesConfig();
        preparPodTemplate(
                k8sConfig.getPodTemplate(), KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE);
        preparPodTemplate(
                k8sConfig.getJmPodTemplate(), KubernetesConfigOptions.JOB_MANAGER_POD_TEMPLATE);
        preparPodTemplate(
                k8sConfig.getTmPodTemplate(), KubernetesConfigOptions.TASK_MANAGER_POD_TEMPLATE);

        if (getType().isApplicationMode()) {
            resetCheckpointInApplicationMode();
        }
    }

    public void preparPodTemplate(String podTemplate, ConfigOption<String> option) {
        if (TextUtil.isEmpty(podTemplate)) {
            return;
        }
        String filePath =
                String.format(
                        "%s/tmp/Kubernets/%s.yaml",
                        System.getProperty("user.dir"), config.getFlinkConfig().getJobName());
        if (FileUtil.exist(filePath)) {
            Assert.isTrue(FileUtil.del(filePath));
        }
        FileUtil.writeUtf8String(podTemplate, filePath);
        configuration.set(option, filePath);
    }

    private void initKubeClient() {
        client = FlinkKubeClientFactory.getInstance().fromConfiguration(configuration, "client");
    }

    public SavePointResult savepointCluster(String savePoint) {
        if (Asserts.isNull(client)) {
            init();
        }

        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        configuration.set(KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }

        KubernetesClusterDescriptor clusterDescriptor =
                clusterClientFactory.createClusterDescriptor(configuration);

        return runClusterSavePointResult(savePoint, clusterId, clusterDescriptor);
    }

    public SavePointResult savepointJob(String savePoint) {
        if (Asserts.isNull(client)) {
            init();
        }
        if (Asserts.isNull(config.getFlinkConfig().getJobId())) {
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to savepont.");
        }

        configuration.set(KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();

        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }

        KubernetesClusterDescriptor clusterDescriptor =
                clusterClientFactory.createClusterDescriptor(configuration);

        return runSavePointResult(savePoint, clusterId, clusterDescriptor);
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
            if (client instanceof Fabric8FlinkKubeClient) {
                Object internalClient = ReflectUtil.getFieldValue(client, "internalClient");
                Method method = ReflectUtil.getMethod(internalClient.getClass(), "getVersion");
                Object versionInfo = method.invoke(internalClient);
                logger.info(
                        "k8s cluster link successful ; k8s version: {} ; platform: {}",
                        ReflectUtil.getFieldValue(versionInfo, "gitVersion"),
                        ReflectUtil.getFieldValue(versionInfo, "platform"));
            }
            logger.info("配置连接测试成功");
            return TestResult.success();
        } catch (Exception e) {
            logger.error("测试 Kubernetes 配置失败：", e);
            return TestResult.fail("测试 Kubernetes 配置失败：" + ExceptionUtil.getRootCauseMessage(e));
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
        KubernetesClusterDescriptor clusterDescriptor =
                clusterClientFactory.createClusterDescriptor(configuration);

        try {
            clusterDescriptor.killCluster(clusterId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void combineFlinkConfig() {
        String flinkConfigPath = config.getClusterConfig().getFlinkConfigPath();
        Configuration loadConfiguration = GlobalConfiguration.loadConfiguration(flinkConfigPath);
        if (loadConfiguration != null) {
            loadConfiguration.addAll(configuration);
            configuration = loadConfiguration;
        }
        configuration.set(DeploymentOptionsInternal.CONF_DIR, flinkConfigPath);
        configuration.set(
                PipelineOptions.JARS,
                Collections.singletonList(config.getAppConfig().getUserJarPath()));
    }
}
