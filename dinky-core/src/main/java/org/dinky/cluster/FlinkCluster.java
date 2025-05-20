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

package org.dinky.cluster;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.io.IORuntimeException;

/**
 * FlinkCluster
 *
 * @since 2021/5/25 15:08
 */
public class FlinkCluster {

    private static Logger logger = LoggerFactory.getLogger(FlinkCluster.class);

    public static FlinkClusterInfo testFlinkJobManagerIP(FlinkClusterInstanceConfig config) {
        if (config.getClusterType().isDeployYarnCluster()) {
            JsonNode configNode = config.getConfig();
            if (configNode != null && configNode.has("applicationId") && configNode.has("resourceManager")) {
                String applicationId = configNode.get("applicationId").asText();
                String resourceManagerStr = configNode.get("resourceManager").asText();
                if (Asserts.isNotNullString(resourceManagerStr)) {
                    String[] resourceManagers = resourceManagerStr.split(",");
                    for (String resourceManager : resourceManagers) {
                        if (Asserts.isNotNullString(resourceManager.trim())) {
                            String yarnUrl = "http://" + resourceManager.trim() + "/proxy/" + applicationId + "/";
                            FlinkClusterInfo info = executeSocketTest(yarnUrl);
                            if (info.isEffective()) {
                                return info;
                            }
                        }
                    }
                }
            }
        }

        if (Asserts.isNotNullString(config.getHost())) {
            FlinkClusterInfo info = executeSocketTest(config.getHost());
            if (info.isEffective()) {
                return info;
            }
        }
        String[] servers = config.getHosts().split(",");
        for (String server : servers) {
            FlinkClusterInfo info = executeSocketTest(server);
            if (info.isEffective()) {
                return info;
            }
        }
        return FlinkClusterInfo.INEFFECTIVE;
    }

    private static FlinkClusterInfo executeSocketTest(String host) {
        try {
            String res = FlinkAPI.build(host).getVersion();
            if (Asserts.isNotNullString(res)) {
                return FlinkClusterInfo.build(host, res);
            }
        } catch (IORuntimeException e) {
            logger.info("Flink jobManager 地址排除 -- " + host);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return FlinkClusterInfo.INEFFECTIVE;
    }
}
