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

import org.dinky.data.enums.GatewayType;
import org.dinky.gateway.model.CustomConfig;
import org.dinky.gateway.model.FlinkClusterConfig;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * SubmitConfig
 *
 * @since 2021/10/29
 */
@Getter
@Setter
@ApiModel(value = "GatewayConfig", description = "Configuration for executing Flink jobs via a gateway")
public class GatewayConfig {

    @ApiModelProperty(
            value = "ID of the task associated with the job",
            dataType = "Integer",
            example = "123",
            notes = "ID of the task")
    private Integer taskId;

    @ApiModelProperty(
            value = "Paths to the JAR files",
            dataType = "String[]",
            example = "[\"/path/to/jar1.jar\",\"/path/to/jar2.jar\"]",
            notes = "Array of JAR file paths")
    private String[] jarPaths;

    @ApiModelProperty(
            value = "Type of gateway (e.g., YARN, Kubernetes)",
            dataType = "GatewayType",
            example = "YARN",
            notes = "Type of the gateway")
    private GatewayType type;

    @ApiModelProperty(
            value = "Cluster configuration for executing the job",
            dataType = "ClusterConfig",
            notes = "Cluster configuration")
    private ClusterConfig clusterConfig;

    @ApiModelProperty(value = "Flink job configuration", dataType = "FlinkConfig", notes = "Flink job configuration")
    private FlinkConfig flinkConfig;

    @ApiModelProperty(value = "Application configuration", dataType = "AppConfig", notes = "Application configuration")
    private AppConfig appConfig;

    @ApiModelProperty(value = "Kubernetes configuration", dataType = "K8sConfig", notes = "Kubernetes configuration")
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
        for (CustomConfig customConfig : config.getFlinkConfig().getFlinkConfigList()) {
            Assert.notNull(customConfig.getName(), "Custom flink config has null key");
            Assert.notNull(customConfig.getValue(), "Custom flink config has null value");
            gatewayConfig.getFlinkConfig().getConfiguration().put(customConfig.getName(), customConfig.getValue());
        }
        return gatewayConfig;
    }
}
