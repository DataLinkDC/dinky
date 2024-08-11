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

package org.dinky.gateway.model;

import org.dinky.data.enums.GatewayType;
import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.config.ClusterConfig;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.K8sConfig;

import java.util.Optional;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** @since */
@Getter
@Setter
@ApiModel(value = "FlinkClusterConfig", description = "Configuration for a Flink cluster")
public class FlinkClusterConfig {

    @ApiModelProperty(
            value = "Gateway Type",
            dataType = "GatewayType",
            example = "REST",
            notes = "The type of gateway for the Flink cluster")
    private GatewayType type;

    @ApiModelProperty(
            value = "Cluster Configuration",
            dataType = "ClusterConfig",
            notes = "Configuration settings for the Flink cluster")
    private ClusterConfig clusterConfig;

    @ApiModelProperty(
            value = "Flink Configuration",
            dataType = "FlinkConfig",
            notes = "Configuration settings specific to Flink")
    private FlinkConfig flinkConfig;

    @ApiModelProperty(
            value = "Application Configuration",
            dataType = "AppConfig",
            notes = "Configuration settings for the application")
    private AppConfig appConfig = new AppConfig();

    @ApiModelProperty(
            value = "Kubernetes Configuration",
            dataType = "K8sConfig",
            notes = "Configuration settings for Kubernetes (if applicable)")
    private K8sConfig kubernetesConfig;

    public static FlinkClusterConfig create(String type, FlinkClusterConfig flinkClusterConfig) {
        Optional.ofNullable(flinkClusterConfig).ifPresent(config -> config.setType(GatewayType.get(type)));
        return flinkClusterConfig;
    }
}
