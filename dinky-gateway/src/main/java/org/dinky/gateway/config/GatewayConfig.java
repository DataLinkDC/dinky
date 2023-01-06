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

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayType;

import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static GatewayConfig build(Map<String, Object> config) {
        GatewayConfig gatewayConfig = new GatewayConfig();
        if (config.containsKey("hadoopConfigPath")) {
            gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString(),
                    config.get("flinkLibPath").toString(),
                    config.get("hadoopConfigPath").toString()));
        } else {
            gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString()));
        }
        AppConfig appConfig = new AppConfig();
        if (config.containsKey("userJarPath") && Asserts.isNotNullString((String) config.get("userJarPath"))) {
            appConfig.setUserJarPath(config.get("userJarPath").toString());
            if (config.containsKey("userJarMainAppClass")
                    && Asserts.isNotNullString((String) config.get("userJarMainAppClass"))) {
                appConfig.setUserJarMainAppClass(config.get("userJarMainAppClass").toString());
            }
            if (config.containsKey("userJarParas") && Asserts.isNotNullString((String) config.get("userJarParas"))) {
                // There may be multiple spaces between the parameter and value during user input,
                // which will directly lead to a parameter passing error and needs to be eliminated
                String[] temp = config.get("userJarParas").toString().split(" ");
                List<String> paraSplit = new ArrayList<>();
                for (String s : temp) {
                    if (!TextUtils.isEmpty(s.trim())) {
                        paraSplit.add(s);
                    }
                }
                appConfig.setUserJarParas(paraSplit.toArray(new String[0]));
            }
            gatewayConfig.setAppConfig(appConfig);
        }
        if (config.containsKey("flinkConfig")
                && Asserts.isNotNullMap((Map<String, String>) config.get("flinkConfig"))) {
            gatewayConfig.setFlinkConfig(FlinkConfig.build((Map<String, String>) config.get("flinkConfig")));
        }
        if (config.containsKey("kubernetesConfig")) {
            Map<String, String> kubernetesConfig = (Map<String, String>) config.get("kubernetesConfig");
            gatewayConfig.getFlinkConfig().getConfiguration().putAll(kubernetesConfig);
        }
        // at present only k8s task have this
        if (config.containsKey("taskCustomConfig")) {
            Map<String, Map<String, String>> taskCustomConfig = (Map<String, Map<String, String>>) config
                    .get("taskCustomConfig");
            if (taskCustomConfig.containsKey("kubernetesConfig")) {
                gatewayConfig.getFlinkConfig().getConfiguration().putAll(taskCustomConfig.get("kubernetesConfig"));
            }
            if (taskCustomConfig.containsKey("flinkConfig")) {
                gatewayConfig.getFlinkConfig().getConfiguration().putAll(taskCustomConfig.get("flinkConfig"));
            }
        }
        if (config.containsKey("taskId")) {
            gatewayConfig.setTaskId(Integer.valueOf(config.get("taskId").toString()));
        }
        // config.setType(GatewayType.get(para.get("type").asText()));
        return gatewayConfig;
    }

}
