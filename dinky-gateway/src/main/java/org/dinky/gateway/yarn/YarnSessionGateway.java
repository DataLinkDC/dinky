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
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.YarnResult;
import com.dlink.utils.LogUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.Arrays;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;

/**
 * YarnSessionGateway
 *
 * @author wenmo
 * @since 2022/12/25
 */
public class YarnSessionGateway extends YarnGateway {

    @Override
    public GatewayType getType() {
        return GatewayType.YARN_SESSION;
    }

    @Override
    public GatewayResult deployCluster() {
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        YarnResult result = YarnResult.build(getType());
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient), true);

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

        if (Asserts.isNotNull(config.getJarPaths())) {
            yarnClusterDescriptor
                    .addShipFiles(Arrays.stream(config.getJarPaths()).map(FileUtil::file).collect(Collectors.toList()));
        }

        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deploySessionCluster(
                    clusterSpecificationBuilder.createClusterSpecification());
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            ApplicationId applicationId = clusterClient.getClusterId();
            result.setAppId(applicationId.toString());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        } finally {
            yarnClusterDescriptor.close();
        }
        return result;
    }
}
