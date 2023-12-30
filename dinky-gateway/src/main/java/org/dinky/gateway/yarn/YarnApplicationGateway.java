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
import org.dinky.utils.FlinkJsonUtil;
import org.dinky.utils.LogUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.messages.webmonitor.JobDetails;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.messages.JobsOverviewHeaders;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;

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
    public GatewayResult submitJar(FlinkUdfPathContextHolder udfPathContextHolder) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        AppConfig appConfig = config.getAppConfig();
        configuration.set(PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));
        configuration.setString(
                "python.files",
                udfPathContextHolder.getPyUdfFile().stream().map(File::getName).collect(Collectors.joining(",")));

        String[] userJarParas =
                Asserts.isNotNull(appConfig.getUserJarParas()) ? appConfig.getUserJarParas() : new String[0];

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());

        YarnResult result = YarnResult.build(getType());
        String webUrl;
        try (YarnClusterDescriptor yarnClusterDescriptor = createYarnClusterDescriptorWithJar(udfPathContextHolder)) {
            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(
                    clusterSpecificationBuilder.createClusterSpecification(), applicationConfiguration);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();

            int counts = SystemConfiguration.getInstances().getJobIdWait();
            while (yarnClient.getApplicationReport(clusterClient.getClusterId()).getYarnApplicationState()
                            == YarnApplicationState.ACCEPTED
                    && counts-- > 0) {
                Thread.sleep(1000);
            }
            ApplicationReport applicationReport = yarnClient.getApplicationReport(clusterClient.getClusterId());
            if (applicationReport.getYarnApplicationState() != YarnApplicationState.RUNNING) {
                throw new RuntimeException("Yarn application state is not running, please check yarn cluster status.");
            }
            webUrl = applicationReport.getOriginalTrackingUrl();
            final List<JobDetails> jobDetailsList = new ArrayList<>();
            while (jobDetailsList.isEmpty() && counts-- > 0) {
                Thread.sleep(1000);

                String url=yarnClient
                        .getApplicationReport(clusterClient.getClusterId())
                        .getTrackingUrl()+JobsOverviewHeaders.URL.substring(1);

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

            ApplicationId applicationId = clusterClient.getClusterId();
            result.setId(applicationId.toString());
            result.setWebURL(webUrl);
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        return result;
    }
}
