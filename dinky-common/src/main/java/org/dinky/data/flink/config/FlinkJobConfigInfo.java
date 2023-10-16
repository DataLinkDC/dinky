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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *   "jid": "62254c597e60e3b978e1663f29b333cd",
 *   "name": "测测吧",
 *   "execution-config": {
 *     "execution-mode": "PIPELINED",
 *     "restart-strategy": "Cluster level default restart strategy",
 *     "job-parallelism": 1,
 *     "object-reuse-mode": false,
 *     "user-config": {}
 *   }
 * }
 */
@ApiModel(value = "FlinkJobConfigInfo", description = "Flink Job Config Info")
@Data
@NoArgsConstructor
public class FlinkJobConfigInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "Job ID",
            required = true,
            notes = "Job ID",
            dataType = "String",
            example = "62254c597e60e3b978e1663f29b333cd")
    @JsonProperty("jid")
    private String jid;

    @ApiModelProperty(value = "Job Name", required = true, notes = "Job Name", dataType = "String", example = "test")
    @JsonProperty("name")
    private String name;

    @ApiModelProperty(value = "Execution Config", required = true, notes = "Execution Config", dataType = "ObjectNode")
    @JsonProperty("execution-config")
    private ExecutionConfig executionConfig;

    @JsonCreator
    public FlinkJobConfigInfo(
            @JsonProperty("jid") String jid,
            @JsonProperty("name") String name,
            @JsonProperty("execution-config") ExecutionConfig executionConfig) {
        this.jid = jid;
        this.name = name;
        this.executionConfig = executionConfig;
    }
}
