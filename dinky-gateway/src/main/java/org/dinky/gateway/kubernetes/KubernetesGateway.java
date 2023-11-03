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
import org.dinky.data.enums.Status;
import org.dinky.gateway.AbstractGateway;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.K8sConfig;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.utils.TextUtil;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.Fabric8FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClientFactory;
import org.apache.http.util.TextUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ReflectUtil;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * KubernetesGateway
 *
 */
public abstract class KubernetesGateway extends AbstractGateway {

    protected FlinkKubeClient client;
    protected KubernetesClient kubernetesClient;

    public KubernetesGateway() {}

    public void init() {
        initConfig();
        initKubeClient();
    }

    private void initConfig() {
        String flinkConfigPath = config.getClusterConfig().getFlinkConfigPath();
        FlinkConfig flinkConfig = config.getFlinkConfig();
        K8sConfig k8sConfig = config.getKubernetesConfig();

        try {
            addConfigParas(
                    GlobalConfiguration.loadConfiguration(flinkConfigPath).toMap());
        } catch (Exception e) {
            logger.warn("load locale config yaml failed：{},Skip config it", e.getMessage());
        }

        addConfigParas(flinkConfig.getConfiguration());
        addConfigParas(k8sConfig.getConfiguration());
        addConfigParas(DeploymentOptions.TARGET, getType().getLongValue());
        addConfigParas(KubernetesConfigOptions.CLUSTER_ID, flinkConfig.getJobName());
        addConfigParas(
                PipelineOptions.JARS,
                Collections.singletonList(config.getAppConfig().getUserJarPath()));

        preparPodTemplate(k8sConfig.getPodTemplate(), KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE);
        preparPodTemplate(k8sConfig.getJmPodTemplate(), KubernetesConfigOptions.JOB_MANAGER_POD_TEMPLATE);
        preparPodTemplate(k8sConfig.getTmPodTemplate(), KubernetesConfigOptions.TASK_MANAGER_POD_TEMPLATE);

        if (getType().isApplicationMode()) {
            resetCheckpointInApplicationMode(flinkConfig.getJobName());
        }
    }

    private void preparPodTemplate(String podTemplate, ConfigOption<String> option) {
        if (TextUtil.isEmpty(podTemplate)) {
            return;
        }
        String filePath = String.format(
                "%s/tmp/Kubernets/%s.yaml",
                System.getProperty("user.dir"), config.getFlinkConfig().getJobName());
        if (FileUtil.exist(filePath)) {
            Assert.isTrue(FileUtil.del(filePath));
        }
        FileUtil.writeUtf8String(podTemplate, filePath);
        addConfigParas(option, filePath);
    }

    private void initKubeClient() {
        client = FlinkKubeClientFactory.getInstance().fromConfiguration(configuration, "client");
        String kubeFile = configuration.getString(KubernetesConfigOptions.KUBE_CONFIG_FILE);
        if (TextUtils.isEmpty(kubeFile)) {
            kubernetesClient = new DefaultKubernetesClient();
        } else {
            String kubeStr = FileUtil.readString(kubeFile, StandardCharsets.UTF_8);
            kubernetesClient = DefaultKubernetesClient.fromConfig(kubeStr);
        }
    }

    public SavePointResult savepointCluster(String savePoint) {
        if (Asserts.isNull(client)) {
            init();
        }

        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        addConfigParas(
                KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like" + " to connect.");
        }

        KubernetesClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);

        return runClusterSavePointResult(savePoint, clusterId, clusterDescriptor);
    }

    public SavePointResult savepointJob(String savePoint) {
        if (Asserts.isNull(client)) {
            init();
        }
        if (Asserts.isNull(config.getFlinkConfig().getJobId())) {
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to" + " savepont.");
        }

        addConfigParas(
                KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like" + " to connect.");
        }
        KubernetesClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);

        return runSavePointResult(savePoint, clusterId, clusterDescriptor);
    }

    public TestResult test() {
        try {
            initConfig();
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
            return TestResult.success();
        } catch (Exception e) {
            logger.error(Status.GAETWAY_KUBERNETS_TEST_FAILED.getMessage(), e);
            return TestResult.fail(
                    StrFormatter.format("{}:{}", Status.GAETWAY_KUBERNETS_TEST_FAILED.getMessage(), e.getMessage()));
        }
    }

    @Override
    public void killCluster() {
        if (Asserts.isNull(client)) {
            init();
        }
        addConfigParas(
                KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like" + " to connect.");
        }
        KubernetesClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);

        try {
            clusterDescriptor.killCluster(clusterId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
