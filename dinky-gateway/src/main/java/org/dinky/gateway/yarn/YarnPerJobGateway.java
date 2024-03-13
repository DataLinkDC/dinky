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
import org.dinky.data.enums.GatewayType;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.YarnResult;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.Arrays;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;

/**
 * YarnApplicationGateway
 *
 * @since 2021/10/29
 */
public class YarnPerJobGateway extends YarnGateway {

    @Override
    public GatewayType getType() {
        return GatewayType.YARN_PER_JOB;
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        if (Asserts.isNull(yarnClient)) {
            init();
        }

        if (Asserts.isNotNull(config.getJarPaths())) {
            jobGraph.addJars(Arrays.stream(config.getJarPaths())
                    .map(path -> URLUtil.getURL(FileUtil.file(path)))
                    .collect(Collectors.toList()));
        }

        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();

        YarnResult result = YarnResult.build(getType());
        try (YarnClusterDescriptor yarnClusterDescriptor = createInitYarnClusterDescriptor()) {
            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployJobCluster(
                    clusterSpecificationBuilder.createClusterSpecification(), jobGraph, true);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            ApplicationId applicationId = clusterClient.getClusterId();
            String webUrl = getWebUrl(clusterClient, result);
            result.setId(applicationId.toString());
            result.setWebURL(webUrl);

            result.success();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
