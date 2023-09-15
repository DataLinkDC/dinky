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
import org.dinky.job.JobConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * APISavePointDTO
 *
 * @since 2021/12/12 19:09
 */
@Getter
@Setter
@ApiModel(value = "APISavePointDTO", description = "API Save Point Data Transfer Object")
public class APISavePointDTO {

    @ApiModelProperty(value = "Job ID", dataType = "String", example = "job123", notes = "The ID of the job")
    private String jobId;

    @ApiModelProperty(
            value = "Save Point Type",
            dataType = "String",
            example = "full",
            notes = "The type of save point (e.g., last)")
    private String savePointType;

    @ApiModelProperty(
            value = "Save Point",
            dataType = "String",
            example = "path/to/savepoint",
            notes = "The path to the save point")
    private String savePoint;

    @ApiModelProperty(
            value = "Address",
            dataType = "String",
            example = "localhost:8081",
            notes = "The address of the job manager")
    private String address;

    @ApiModelProperty(
            value = "Gateway Configuration",
            dataType = "GatewayConfig",
            notes = "Gateway configuration details")
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        JobConfig config = new JobConfig();
        config.setAddress(address);
        config.setGatewayConfig(gatewayConfig);
        return config;
    }
}
