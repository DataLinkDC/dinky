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

import org.dinky.data.enums.GatewayType;
import org.dinky.job.JobConfig;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * APIExplainSqlDTO
 *
 * @since 2021/12/12 13:01
 */
@Getter
@Setter
@ApiModel(value = "APIExplainSqlDTO", description = "API Explain SQL Data Transfer Object")
public class APIExplainSqlDTO extends AbstractStatementDTO {

    @ApiModelProperty(
            value = "Use Statement Set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use a statement set")
    private boolean useStatementSet = false;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", notes = "The parallelism for execution")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Configuration",
            dataType = "Map<String, String>",
            notes = "Additional configuration settings")
    private Map<String, String> configuration;

    public JobConfig getJobConfig() {
        return JobConfig.builder()
                .type(GatewayType.LOCAL.getLongValue())
                .useRemote(false)
                .fragment(isFragment())
                .statementSet(useStatementSet)
                .parallelism(parallelism)
                .configJson(configuration)
                .build();
    }
}
