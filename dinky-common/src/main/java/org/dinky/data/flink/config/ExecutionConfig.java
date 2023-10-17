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

package org.dinky.data.flink.config;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "ExecutionConfig", description = "Execution Config")
@Builder
@Data
@NoArgsConstructor
public class ExecutionConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "Execution Mode",
            required = true,
            notes = "Execution Mode",
            dataType = "String",
            example = "PIPELINED")
    @JsonProperty("execution-mode")
    private String executionMode;

    @ApiModelProperty(
            value = "Restart Strategy",
            required = true,
            notes = "Restart Strategy",
            dataType = "String",
            example = "Cluster level default restart strategy")
    @JsonProperty("restart-strategy")
    private String restartStrategy;

    @ApiModelProperty(
            value = "Job Parallelism",
            required = true,
            notes = "Job Parallelism",
            dataType = "Integer",
            example = "1")
    @JsonProperty("job-parallelism")
    private Integer jobParallelism;

    @ApiModelProperty(
            value = "Object Reuse Mode",
            required = true,
            notes = "Object Reuse Mode",
            dataType = "Boolean",
            example = "false")
    @JsonProperty("object-reuse-mode")
    private Boolean objectReuseMode;

    @ApiModelProperty(value = "User Config", required = true, notes = "User Config", dataType = "ObjectNode")
    @JsonProperty("user-config")
    private Object userConfig;

    @JsonCreator
    public ExecutionConfig(
            @JsonProperty("execution-mode") String executionMode,
            @JsonProperty("restart-strategy") String restartStrategy,
            @JsonProperty("job-parallelism") Integer jobParallelism,
            @JsonProperty("object-reuse-mode") Boolean objectReuseMode,
            @JsonProperty("user-config") Object userConfig) {
        this.executionMode = executionMode;
        this.restartStrategy = restartStrategy;
        this.jobParallelism = jobParallelism;
        this.objectReuseMode = objectReuseMode;
        this.userConfig = userConfig;
    }
}
