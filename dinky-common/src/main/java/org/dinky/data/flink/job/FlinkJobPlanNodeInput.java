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

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  <p>
 *  {
 *  *                         "num": 0,
 *  *                         "id": "cbc357ccb763df2852fee8c4fc7d55f2",
 *  *                         "ship_strategy": "HASH",
 *  *                         "exchange": "pipelined_bounded"
 *  *                     }
 */
@ApiModel(value = "FlinkJobPlanNodeInput", description = "Flink Job Plan Node Input Info")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlinkJobPlanNodeInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "num", notes = "num", dataType = "Integer", example = "1")
    @JsonProperty(value = "num")
    private Integer num;

    @ApiModelProperty(value = "id", notes = "id", dataType = "String", example = "1")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "ship_strategy", notes = "ship_strategy", dataType = "String", example = "1")
    @JsonProperty(value = "ship_strategy")
    private String shipStrategy;

    @ApiModelProperty(value = "exchange", notes = "exchange", dataType = "String", example = "1")
    @JsonProperty(value = "exchange")
    private String exchange;
}
