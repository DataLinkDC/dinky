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

import org.dinky.assertion.Asserts;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.job.JobConfig;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * APIExecuteSqlDTO
 *
 * @since 2021/12/11 21:50
 */
@Getter
@Setter
@ApiModel(value = "APIExecuteSqlDTO", description = "API Execute SQL Data Transfer Object")
public class APIExecuteSqlDTO extends AbstractStatementDTO {

    @ApiModelProperty(
            value = "Execution Type",
            dataType = "String",
            example = "BATCH",
            notes = "The type of SQL execution (e.g., 'BATCH')")
    private String type;

    @ApiModelProperty(
            value = "Use Result",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use the result")
    private boolean useResult = false;

    @ApiModelProperty(
            value = "Use Change Log",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use change logs")
    private boolean useChangeLog = false;

    @ApiModelProperty(
            value = "Use Auto Cancel",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use auto cancel")
    private boolean useAutoCancel = false;

    @ApiModelProperty(
            value = "Use Statement Set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use a statement set")
    private boolean useStatementSet = false;

    @ApiModelProperty(
            value = "Address",
            dataType = "String",
            example = "localhost:8080",
            notes = "The address for execution")
    private String address;

    @ApiModelProperty(value = "Job Name", dataType = "String", example = "MyJob", notes = "The name of the job")
    private String jobName;

    @ApiModelProperty(
            value = "Max Row Number",
            dataType = "Integer",
            example = "100",
            notes = "The maximum number of rows")
    private Integer maxRowNum = 100;

    @ApiModelProperty(value = "Checkpoint", dataType = "Integer", example = "0", notes = "The checkpoint configuration")
    private Integer checkPoint = 0;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", notes = "The parallelism for execution")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Savepoint Path",
            dataType = "String",
            example = "/path/to/savepoint",
            notes = "The path to the savepoint to restore from (if applicable)")
    private String savePointPath;

    @ApiModelProperty(
            value = "Configuration",
            dataType = "Map<String, String>",
            notes = "Additional configuration settings")
    private Map<String, String> configuration;

    @ApiModelProperty(
            value = "Gateway Configuration",
            dataType = "GatewayConfig",
            notes = "The configuration for the gateway")
    private GatewayConfig gatewayConfig;

    public JobConfig getJobConfig() {
        int savePointStrategy = Asserts.isNotNullString(savePointPath) ? 3 : 0;
        return JobConfig.builder()
                .type(type)
                .useResult(useResult)
                .useChangeLog(useChangeLog)
                .useAutoCancel(useAutoCancel)
                .useRemote(true)
                .address(address)
                .jobName(jobName)
                .fragment(isFragment())
                .statementSet(useStatementSet)
                .maxRowNum(maxRowNum)
                .checkpoint(checkPoint)
                .parallelism(parallelism)
                .savePointStrategy(SavePointStrategy.get(savePointStrategy))
                .savePointPath(savePointPath)
                .configJson(configuration)
                .gatewayConfig(gatewayConfig)
                .build();
    }
}
