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

package org.dinky.gateway.config;

import org.dinky.assertion.Asserts;
import org.dinky.gateway.enums.GatewayType;

import org.apache.http.util.TextUtils;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * SubmitConfig
 *
 * @since 2021/10/29
 */
@Getter
@Setter
public class GatewayConfig {

    public static final String FLINK_CONFIG_PATH = "flinkConfigPath";
    public static final String FLINK_LIB_PATH = "flinkLibPath";
    public static final String FLINK_VERSION = "flinkVersion";
    public static final String HADOOP_CONFIG_PATH = "hadoopConfigPath";
    public static final String USER_JAR_PATH = "userJarPath";
    public static final String USER_JAR_MAIN_APP_CLASS = "userJarMainAppClass";
    public static final String USER_JAR_PARAS = "userJarParas";
    public static final String FLINK_CONFIG = "flinkConfig";
    public static final String KUBERNETES_CONFIG = "kubernetesConfig";
    public static final String TASK_CUSTOM_CONFIG = "taskCustomConfig";

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
        gatewayConfig.setClusterConfig(getClusterConfig(config));
        gatewayConfig.setAppConfig(getAppConfig(config));
        gatewayConfig.setFlinkConfig(getFlinkConfig(config));

        if (config.containsKey("taskId")) {
            gatewayConfig.setTaskId(Integer.valueOf(config.get("taskId").toString()));
        }
        return gatewayConfig;
    }

    private static FlinkConfig getFlinkConfig(Map<String, Object> config) {
        FlinkConfig fc = new FlinkConfig();
        if (config.containsKey(FLINK_CONFIG)
                && Asserts.isNotNullMap((Map<String, String>) config.get(FLINK_CONFIG))) {
            fc = FlinkConfig.build((Map<String, String>) config.get(FLINK_CONFIG));
        }

        final Map<String, String> configuration = fc.getConfiguration();
        final Map<String, String> k8sConfig = fc.getFlinkKubetnetsConfig();

        if (config.containsKey(KUBERNETES_CONFIG)) {
            Map<String, String> kubernetesConfig =
                    (Map<String, String>) config.get(KUBERNETES_CONFIG);
            k8sConfig.putAll(kubernetesConfig);
        }

        if (config.containsKey(FLINK_VERSION)) {
            k8sConfig.put(FLINK_VERSION, String.valueOf(config.get(FLINK_VERSION)));
        }

        // at present only k8s task have this
        if (config.containsKey(TASK_CUSTOM_CONFIG)) {
            Map<String, Map<String, String>> taskCustomConfig =
                    (Map<String, Map<String, String>>) config.get(TASK_CUSTOM_CONFIG);
            if (taskCustomConfig.containsKey(KUBERNETES_CONFIG)) {
                k8sConfig.putAll(taskCustomConfig.get(KUBERNETES_CONFIG));
            }
            if (taskCustomConfig.containsKey(FLINK_CONFIG)) {
                configuration.putAll(taskCustomConfig.get(FLINK_CONFIG));
            }
        }
        return fc;
    }

    private static AppConfig getAppConfig(Map<String, Object> config) {
        AppConfig appConfig = new AppConfig();
        if (config.containsKey(USER_JAR_PATH)
                && Asserts.isNotNullString((String) config.get(USER_JAR_PATH))) {
            appConfig.setUserJarPath(config.get(USER_JAR_PATH).toString());
            if (config.containsKey(USER_JAR_MAIN_APP_CLASS)
                    && Asserts.isNotNullString((String) config.get(USER_JAR_MAIN_APP_CLASS))) {
                appConfig.setUserJarMainAppClass(config.get(USER_JAR_MAIN_APP_CLASS).toString());
            }

            if (config.containsKey(USER_JAR_PARAS)
                    && Asserts.isNotNullString((String) config.get(USER_JAR_PARAS))) {
                // There may be multiple spaces between the parameter and value during user input,
                // which will directly lead to a parameter passing error and needs to be eliminated
                String[] temp = config.get(USER_JAR_PARAS).toString().split(" ");
                appConfig.setUserJarParas(
                        Arrays.stream(temp)
                                .filter(s -> !TextUtils.isEmpty(s.trim()))
                                .toArray(String[]::new));
            }
        }
        return appConfig;
    }

    private static ClusterConfig getClusterConfig(Map<String, Object> config) {
        if (config.containsKey(HADOOP_CONFIG_PATH)) {
            return ClusterConfig.build(
                    config.get(FLINK_CONFIG_PATH).toString(),
                    config.get(FLINK_LIB_PATH).toString(),
                    config.get(HADOOP_CONFIG_PATH).toString());
        } else {
            return ClusterConfig.build(String.valueOf(config.get(FLINK_CONFIG_PATH)));
        }
    }
}
