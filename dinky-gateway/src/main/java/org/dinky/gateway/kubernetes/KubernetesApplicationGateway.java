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
import org.dinky.data.constant.NetConstant;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.KubernetesResult;
import org.dinky.utils.LogUtil;
import org.dinky.utils.ThreadUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

/**
 * KubernetesApplicationGateway
 *
 * @since 2021/12/26 14:59
 */
public class KubernetesApplicationGateway extends KubernetesGateway {

    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_APPLICATION;
    }

    @Override
    public GatewayResult submitJar() {
        if (Asserts.isNull(client)) {
            init();
        }

        combineFlinkConfig();
        AppConfig appConfig = config.getAppConfig();
        String[] userJarParas =
                Asserts.isNotNull(appConfig.getUserJarParas())
                        ? appConfig.getUserJarParas()
                        : new String[0];

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());

        KubernetesResult result = KubernetesResult.build(getType());

        try (KubernetesClusterDescriptor kubernetesClusterDescriptor =
                new KubernetesClusterDescriptor(configuration, client)) {
            ClusterClientProvider<String> clusterClientProvider =
                    kubernetesClusterDescriptor.deployApplicationCluster(
                            clusterSpecificationBuilder.createClusterSpecification(),
                            applicationConfiguration);
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            Collection<JobStatusMessage> jobStatusMessages = clusterClient.listJobs().get();

            int counts = SystemConfiguration.getInstances().getJobIdWait();
            while (jobStatusMessages.size() == 0 && counts > 0) {
                Thread.sleep(1000);
                counts--;
                try {
                    jobStatusMessages = clusterClient.listJobs().get();
                } catch (ExecutionException e) {
                    if (StrUtil.contains(e.getMessage(), "Number of retries has been exhausted.")) {
                        // refresh the job manager ip address
                        clusterClient.close();
                        clusterClient = clusterClientProvider.getClusterClient();
                    } else {
                        LogUtil.getError(e);
                        throw e;
                    }
                }

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

            String jobId = "";
            // application mode only have one job, so we can get any one to be jobId
            for (JobStatusMessage jobStatusMessage : jobStatusMessages) {
                jobId = jobStatusMessage.getJobId().toHexString();
            }
            // if JobStatusMessage not have job id,  it`s maybe wrong with submit,throw exception
            if (TextUtils.isEmpty(jobId)) {
                int cost = SystemConfiguration.getInstances().getJobIdWait() - counts;
                String clusterId = clusterClient.getClusterId();
                throw new Exception("无法获得jobId请联系管理排查问题,等待时长：" + cost + ",job name:" + clusterId);
            }
            result.setId(jobId);
            result.setWebURL(clusterClient.getWebInterfaceURL());
            waitForTaskManagerToBeReady(result.getWebURL(), jobId);
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        }
        return result;
    }

    /**
     * 等待tm完成，若不等待，则后续步骤可能会查询不到状态，报 NullPointerException
     *
     * @param apiPath
     * @param jobId
     */
    static void waitForTaskManagerToBeReady(String apiPath, String jobId) {
        int jobIdWait = SystemConfiguration.getInstances().getJobIdWait();
        String fullPath = String.format("http://%s/jobs/%s", apiPath, jobId);
        for (int i = 1; i <= jobIdWait; i++) {
            try {
                // 不抛异常，就为成功
                String result = HttpUtil.get(fullPath, NetConstant.SERVER_TIME_OUT_ACTIVE);
                logger.info("get job status success,jobPath:{},result: {}", fullPath, result);
                break;
            } catch (Exception e) {
                logger.info("Unable to connect to Flink JobManager: {},wait count:{}", fullPath, i);
                ThreadUtil.sleep(1000);
            }
        }
    }
}
