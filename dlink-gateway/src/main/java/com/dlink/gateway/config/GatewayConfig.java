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

package com.dlink.gateway.config;

import com.dlink.gateway.GatewayType;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * SubmitConfig
 *
 * @author wenmo
 * @since 2021/10/29
 **/
@Getter
@Setter
public class GatewayConfig {

    private Integer taskId;
    private String[] jarPaths;
    private GatewayType type;
    private ClusterConfig clusterConfig;
    private FlinkConfig flinkConfig;
    private AppConfig appConfig;

    private static final ObjectMapper mapper = new ObjectMapper();

    public GatewayConfig() {
        clusterConfig = new ClusterConfig();
        flinkConfig = new FlinkConfig();
        appConfig = new AppConfig();
    }

    public static GatewayConfig build(JsonNode para) {
        GatewayConfig config = new GatewayConfig();
        if (para.has("taskId")) {
            config.setTaskId(para.get("taskId").asInt());
        }
        config.setType(GatewayType.get(para.get("type").asText()));
        if (para.has("flinkConfigPath")) {
            config.getClusterConfig().setFlinkConfigPath(para.get("flinkConfigPath").asText());
        }
        if (para.has("flinkLibPath")) {
            config.getClusterConfig().setFlinkLibPath(para.get("flinkLibPath").asText());
        }
        if (para.has("yarnConfigPath")) {
            config.getClusterConfig().setYarnConfigPath(para.get("yarnConfigPath").asText());
        }
        if (para.has("jobName")) {
            config.getFlinkConfig().setJobName(para.get("jobName").asText());
        }
        if (para.has("userJarPath")) {
            config.getAppConfig().setUserJarPath(para.get("userJarPath").asText());
        }
        if (para.has("userJarParas")) {
            config.getAppConfig().setUserJarParas(para.get("userJarParas").asText().split("\\s+"));
        }
        if (para.has("userJarMainAppClass")) {
            config.getAppConfig().setUserJarMainAppClass(para.get("userJarMainAppClass").asText());
        }
        if (para.has("savePoint")) {
            config.getFlinkConfig().setSavePoint(para.get("savePoint").asText());
        }
        if (para.has("configParas")) {
            try {
                Map<String, String> configMap = new HashMap<>();
                JsonNode paras = mapper.readTree(para.get("configParas").asText());
                paras.forEach((JsonNode node) -> {
                    configMap.put(node.get("key").asText(), node.get("value").asText());
                });
                config.getFlinkConfig().setConfiguration(configMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

}
