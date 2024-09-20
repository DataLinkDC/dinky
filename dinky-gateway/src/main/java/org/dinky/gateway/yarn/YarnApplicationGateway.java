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
import org.dinky.constant.CustomerConfigureOptions;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.enums.GatewayType;
import org.dinky.executor.ClusterDescriptorAdapterImpl;
import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.YarnResult;
import org.dinky.utils.LogUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.URLUtil;

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

    /**
     * format url
     * <p>if url is rs protocol, convert to file path</p>
     *
     * @param url url
     * @return formatted url
     */
    private String formatUrl(String url) {
        if (URLUtil.url(url).getProtocol().equals("rs")) {
            return URLUtils.toFile(url).getAbsolutePath();
        } else {
            return url;
        }
    }

    @Override
    public GatewayResult submitJar(FlinkUdfPathContextHolder udfPathContextHolder) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        List<String> beforePipelineJars = configuration.get(PipelineOptions.JARS);

        AppConfig appConfig = config.getAppConfig();
        configuration.set(PipelineOptions.JARS, Collections.singletonList(formatUrl(appConfig.getUserJarPath())));

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
            ClusterDescriptorAdapterImpl clusterDescriptorAdapter =
                    new ClusterDescriptorAdapterImpl(yarnClusterDescriptor);
            if (CollUtil.isNotEmpty(beforePipelineJars)) {
                clusterDescriptorAdapter.addShipFiles(
                        beforePipelineJars.stream().map(URLUtils::toFile).collect(Collectors.toList()));
            }
            clusterDescriptorAdapter.addShipFiles(Collections.singletonList(preparSqlFile()));
            addConfigParas(
                    CustomerConfigureOptions.EXEC_SQL_FILE, configuration.get(CustomerConfigureOptions.EXEC_SQL_FILE));

            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(
                    clusterSpecificationBuilder.createClusterSpecification(), applicationConfiguration);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();

            webUrl = getWebUrl(clusterClient, result);

            ApplicationId applicationId = clusterClient.getClusterId();
            result.setId(applicationId.toString());
            result.setWebURL(webUrl);
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        } finally {
            close();
        }
        return result;
    }
}
