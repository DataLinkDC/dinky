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
import org.dinky.gateway.kubernetes.utils.K8sClientHelper;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.Fabric8FlinkKubeClient;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.python.PythonOptions;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.UUID;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ReflectUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * KubernetesGateway
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class KubernetesGateway extends AbstractGateway {

    protected String flinkConfigPath;
    protected FlinkConfig flinkConfig;
    protected K8sConfig k8sConfig;

    private K8sClientHelper k8sClientHelper;

    public KubernetesGateway() {}

    public void init() {
        initConfig();
    }

    protected void initConfig() {
        flinkConfigPath = config.getClusterConfig().getFlinkConfigPath();
        flinkConfig = config.getFlinkConfig();
        k8sConfig = config.getKubernetesConfig();

        configuration.set(CoreOptions.CLASSLOADER_RESOLVE_ORDER, "parent-first");
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

        if (getType().isApplicationMode()) {
            // remove python file
            configuration.removeConfig(PythonOptions.PYTHON_FILES);
            resetCheckpointInApplicationMode(flinkConfig.getJobName());
        }

        k8sClientHelper = new K8sClientHelper(configuration, k8sConfig);
    }

    public SavePointResult savepointCluster(String savePoint) {
        if (Asserts.isNull(k8sClientHelper.getClient())) {
            initConfig();
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
        if (Asserts.isNull(k8sClientHelper.getClient())) {
            initConfig();
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
            // Test mode no jobName, use uuid .
            addConfigParas(KubernetesConfigOptions.CLUSTER_ID, UUID.randomUUID().toString());
            initConfig();
            FlinkKubeClient client = k8sClientHelper.getClient();
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
        } finally {
            close();
        }
    }

    @Override
    public void killCluster() {
        if (Asserts.isNull(k8sClientHelper.getClient())) {
            initConfig();
        }
        addConfigParas(
                KubernetesConfigOptions.CLUSTER_ID, config.getClusterConfig().getAppId());
        KubernetesClusterClientFactory clusterClientFactory = new KubernetesClusterClientFactory();
        String clusterId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(clusterId)) {
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like" + " to connect.");
        }

        try (KubernetesClusterDescriptor clusterDescriptor =
                clusterClientFactory.createClusterDescriptor(configuration)) {
            clusterDescriptor.killCluster(clusterId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean close() {
        if (k8sClientHelper != null) {
            return k8sClientHelper.close();
        }
        return true;
    }
}
