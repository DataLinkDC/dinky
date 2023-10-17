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

package org.dinky.data.flink.job;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "FlinkJobPlan", description = "Flink Job Plan Info")
@Builder
@Data
@NoArgsConstructor
public class FlinkJobPlan implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "jid", notes = "jid", dataType = "String", example = "1")
    @JsonProperty(value = "jid")
    private String jid;

    @ApiModelProperty(value = "name", notes = "name", dataType = "String", example = "1")
    @JsonProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "type", notes = "type", dataType = "String", example = "1")
    @JsonProperty(value = "type")
    private String type;

    @ApiModelProperty(value = "nodes", notes = "nodes", dataType = "List")
    @JsonProperty(value = "nodes")
    private List<FlinkJobPlanNode> nodes;

    @JsonCreator
    public FlinkJobPlan(
            @JsonProperty(value = "jid") String jid,
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "type") String type,
            @JsonProperty(value = "nodes") List<FlinkJobPlanNode> nodes) {
        this.jid = jid;
        this.name = name;
        this.type = type;
        this.nodes = nodes;
    }
}
