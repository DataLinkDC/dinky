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
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.KubernetesResult;
import com.dlink.utils.LogUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;

import java.util.Collections;

/**
 * KubernetesSessionGateway
 *
 * @author wenmo
 * @since 2022/12/25 23:25
 */
public class KubernetesSessionGateway extends KubernetesGateway {

    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_SESSION;
    }

    @Override
    public GatewayResult deployCluster() {
        if (Asserts.isNull(client)) {
            init();
        }
        KubernetesResult result = KubernetesResult.build(getType());
        AppConfig appConfig = config.getAppConfig();
        String flinkConfigPath = config.getClusterConfig().getFlinkConfigPath();
        Configuration loadConfiguration = GlobalConfiguration.loadConfiguration(flinkConfigPath);
        if (loadConfiguration != null) {
            loadConfiguration.addAll(configuration);
            configuration = loadConfiguration;
        }
        configuration.set(DeploymentOptionsInternal.CONF_DIR, flinkConfigPath);

        configuration.set(PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));

        KubernetesClusterDescriptor kubernetesClusterDescriptor = new KubernetesClusterDescriptor(configuration,
                client);

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder = new ClusterSpecification.ClusterSpecificationBuilder();
        if (configuration.contains(JobManagerOptions.TOTAL_PROCESS_MEMORY)) {
            clusterSpecificationBuilder
                    .setMasterMemoryMB(configuration.get(JobManagerOptions.TOTAL_PROCESS_MEMORY).getMebiBytes());
        }
        if (configuration.contains(TaskManagerOptions.TOTAL_PROCESS_MEMORY)) {
            clusterSpecificationBuilder
                    .setTaskManagerMemoryMB(configuration.get(TaskManagerOptions.TOTAL_PROCESS_MEMORY).getMebiBytes());
        }
        if (configuration.contains(TaskManagerOptions.NUM_TASK_SLOTS)) {
            clusterSpecificationBuilder.setSlotsPerTaskManager(configuration.get(TaskManagerOptions.NUM_TASK_SLOTS))
                    .createClusterSpecification();
        }

        try {
            ClusterClientProvider<String> clusterClientProvider = kubernetesClusterDescriptor.deploySessionCluster(
                    clusterSpecificationBuilder.createClusterSpecification());
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            result.setClusterId(clusterClient.getClusterId());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        } finally {
            kubernetesClusterDescriptor.close();
        }
        return result;
    }
}
