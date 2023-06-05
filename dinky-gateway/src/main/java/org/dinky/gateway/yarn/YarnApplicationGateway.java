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
import org.dinky.data.model.SystemConfiguration;
import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.YarnResult;
import org.dinky.utils.LogUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * YarnApplicationGateway
 *
 * @since 2021/10/29
 */
public class YarnApplicationGateway extends YarnGateway {

    @Override
    public GatewayType getType() {
        return GatewayType.YARN_APPLICATION;
    }

    @Override
    public GatewayResult submitJar() {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        AppConfig appConfig = config.getAppConfig();
        configuration.set(
                PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));

        configuration.setString(
                "python.files",
                FlinkUdfPathContextHolder.getPyUdfFile().stream()
                        .map(File::getName)
                        .collect(Collectors.joining(",")));

        String[] userJarParas =
                Asserts.isNotNull(appConfig.getUserJarParas())
                        ? appConfig.getUserJarParas()
                        : new String[0];

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());

        YarnResult result = YarnResult.build(getType());
        try (YarnClusterDescriptor yarnClusterDescriptor = createYarnClusterDescriptorWithJar()) {
            ClusterClientProvider<ApplicationId> clusterClientProvider =
                    yarnClusterDescriptor.deployApplicationCluster(
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
                List<String> jobIds = new ArrayList<>();
                for (JobStatusMessage jobStatusMessage : jobStatusMessages) {
                    jobIds.add(jobStatusMessage.getJobId().toHexString());
                }
                result.setJids(jobIds);
            }

            ApplicationId applicationId = clusterClient.getClusterId();
            result.setId(applicationId.toString());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        return result;
    }
}
