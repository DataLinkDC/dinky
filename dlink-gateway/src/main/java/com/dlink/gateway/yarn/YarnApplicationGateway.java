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
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.YarnResult;
import com.dlink.model.SystemConfiguration;
import com.dlink.utils.LogUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;

/**
 * YarnApplicationGateway
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public class YarnApplicationGateway extends YarnGateway {

    public YarnApplicationGateway(GatewayConfig config) {
        super(config);
    }

    public YarnApplicationGateway() {
    }

    @Override
    public GatewayType getType() {
        return GatewayType.YARN_APPLICATION;
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        throw new GatewayException("Couldn't deploy Yarn Application Cluster with job graph.");
    }

    @Override
    public GatewayResult submitJar() {
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        YarnResult result = YarnResult.build(getType());
        AppConfig appConfig = config.getAppConfig();
        configuration.set(PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));
        String[] userJarParas = appConfig.getUserJarParas();
        if (Asserts.isNull(userJarParas)) {
            userJarParas = new String[0];
        }
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient), true);

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                new ClusterSpecification.ClusterSpecificationBuilder();
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
            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(
                    clusterSpecificationBuilder.createClusterSpecification(),
                    applicationConfiguration);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            Collection<JobStatusMessage> jobStatusMessages = clusterClient.listJobs().get();
            int counts = SystemConfiguration.getInstances().getJobIdWait();
            while (jobStatusMessages.size() == 0 && counts > 0) {
                Thread.sleep(1000);
                counts--;
                jobStatusMessages = clusterClient.listJobs().get();
                if (jobStatusMessages.size() > 0) {
                    break;
                }
            }
            if (jobStatusMessages.size() > 0) {
                List<String> jids = new ArrayList<>();
                for (JobStatusMessage jobStatusMessage : jobStatusMessages) {
                    jids.add(jobStatusMessage.getJobId().toHexString());
                }
                result.setJids(jids);
            }
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
