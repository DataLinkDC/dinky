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
import org.dinky.gateway.model.FlinkClusterConfig;

import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
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

    private Integer taskId;
    private String[] jarPaths;
    private GatewayType type;
    private ClusterConfig clusterConfig;
    private FlinkConfig flinkConfig;
    private AppConfig appConfig;
    private K8sConfig kubernetesConfig;

    public GatewayConfig() {
        clusterConfig = new ClusterConfig();
        flinkConfig = new FlinkConfig();
        appConfig = new AppConfig();
    }

    public static GatewayConfig build(FlinkClusterConfig config) {
        Assert.notNull(config);
        GatewayConfig gatewayConfig = new GatewayConfig();
        BeanUtil.copyProperties(config, gatewayConfig);
        for (Map<String, String> item : gatewayConfig.getFlinkConfig().getFlinkConfigList()) {
            if (Asserts.isNotNull(item)) {
                Assert.notNull(item.get("name"), "Custer config has null item");
                Assert.notNull(item.get("value"), "Custer config has null item");
                gatewayConfig
                        .getFlinkConfig()
                        .getConfiguration()
                        .put(item.get("name"), item.get("value"));
            }
        }
        return gatewayConfig;
    }
}
