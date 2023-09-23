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

package org.dinky.data.dto;

import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.job.JobConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * APIExecuteJarDTO
 *
 * @since 2021/12/12 19:46
 */
@Getter
@Setter
@ApiModel(value = "APIExecuteJarDTO", description = "API Execute JAR Data Transfer Object")
public class APIExecuteJarDTO {

    @ApiModelProperty(
            value = "Type",
            dataType = "String",
            example = "Flink",
            notes = "The type of the JAR execution (e.g., 'Flink')")
    private String type;

    @ApiModelProperty(
            value = "Job Name",
            dataType = "String",
            example = "MyJob",
            notes = "The name of the job to execute")
    private String jobName;

    @ApiModelProperty(
            value = "Savepoint Path",
            dataType = "String",
            example = "/path/to/savepoint",
            notes = "The path to the savepoint to restore from (if applicable)")
    private String savePointPath;

    @ApiModelProperty(
            value = "Gateway Configuration",
            dataType = "GatewayConfig",
            notes = "The configuration for the gateway")
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        JobConfig config = new JobConfig();
        config.setType(type);
        config.setJobName(jobName);
        config.setSavePointStrategy(SavePointStrategy.CUSTOM);
        config.setSavePointPath(savePointPath);
        config.setGatewayConfig(gatewayConfig);
        return config;
    }
}
