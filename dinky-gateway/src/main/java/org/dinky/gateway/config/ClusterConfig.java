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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * ClusterConfig
 *
 * @since 2021/11/3 21:52
 */
@Getter
@Setter
@ApiModel(value = "ClusterConfig", description = "Configuration for a Flink cluster")
public class ClusterConfig {

    @ApiModelProperty(
            value = "Path to Flink configuration file",
            dataType = "String",
            example = "/opt/flink/conf/flink-conf.yaml",
            notes = "Path to the Flink configuration file")
    private String flinkConfigPath;

    @ApiModelProperty(
            value = "Path to Flink library directory",
            dataType = "String",
            example = "/opt/flink/lib",
            notes = "Path to the Flink library directory")
    private String flinkLibPath;

    @ApiModelProperty(
            value = "Path to YARN configuration file",
            dataType = "String",
            example = "/etc/hadoop/conf/yarn-site.xml",
            notes = "Path to the YARN configuration file")
    private String yarnConfigPath;

    @ApiModelProperty(
            value = "YARN application ID",
            dataType = "String",
            example = "application_12345_67890",
            notes = "ID of the YARN application associated with the Flink cluster")
    private String appId;

    public ClusterConfig() {}

    public ClusterConfig(String flinkConfigPath) {
        this.flinkConfigPath = flinkConfigPath;
    }

    public ClusterConfig(String flinkConfigPath, String flinkLibPath, String yarnConfigPath) {
        this.flinkConfigPath = flinkConfigPath;
        this.flinkLibPath = flinkLibPath;
        this.yarnConfigPath = yarnConfigPath;
    }

    public static ClusterConfig build(String flinkConfigPath) {
        return build(flinkConfigPath, null, null);
    }

    public static ClusterConfig build(String flinkConfigPath, String flinkLibPath, String yarnConfigPath) {
        return new ClusterConfig(flinkConfigPath, flinkLibPath, yarnConfigPath);
    }

    @Override
    public String toString() {
        return String.format(
                "ClusterConfig{flinkConfigPath='%s', flinkLibPath='%s', yarnConfigPath='%s', " + "appId='%s'}",
                flinkConfigPath, flinkLibPath, yarnConfigPath, appId);
    }
}
