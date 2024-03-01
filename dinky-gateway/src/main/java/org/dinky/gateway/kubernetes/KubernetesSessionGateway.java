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
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.enums.GatewayType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.KubernetesResult;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;

import cn.hutool.core.text.StrFormatter;

/**
 * KubernetesSessionGateway
 *
 * @since 2022/12/25 23:25
 */
public class KubernetesSessionGateway extends KubernetesGateway {

    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_SESSION;
    }

    @Override
    public GatewayResult deployCluster(FlinkUdfPathContextHolder udfPathContextHolder) {
        if (Asserts.isNull(client)) {
            String clusterId = StrFormatter.format("dinky-flink-session-{}", System.currentTimeMillis());
            addConfigParas(KubernetesConfigOptions.CLUSTER_ID, clusterId);
            init();
        }

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();

        KubernetesResult result = KubernetesResult.build(getType());
        try (KubernetesClusterDescriptor kubernetesClusterDescriptor =
                new KubernetesClusterDescriptor(configuration, client)) {
            ClusterClientProvider<String> clusterClientProvider = kubernetesClusterDescriptor.deploySessionCluster(
                    clusterSpecificationBuilder.createClusterSpecification());
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            result.setId(clusterClient.getClusterId());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
