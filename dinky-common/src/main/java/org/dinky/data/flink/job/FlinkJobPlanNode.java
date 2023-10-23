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

import org.dinky.data.flink.backpressure.FlinkJobNodeBackPressure;
import org.dinky.data.flink.watermark.FlinkJobNodeWaterMark;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * {
 * *                 "id": "c27dcf7b54ef6bfd6cff02ca8870b681",
 * *                 "parallelism": 1,
 * *                 "operator": "",
 * *                 "operator_strategy": "",
 * *                 "description": "[5]:OverAggregate(partitionBy=[product], orderBy=[order_time ASC], window=[ RANG BETWEEN 60000 PRECEDING AND CURRENT ROW], select=[product, amount, order_time, COUNT(amount) AS w0$o0, $SUM0(amount) AS w0$o1])<br/>+- [6]:Calc(select=[product, amount, order_time, CASE((w0$o0 &gt; 0), w0$o1, null:BIGINT) AS one_minute_sum])<br/>   +- [7]:Sink(table=[default_catalog.default_database.sink_table5], fields=[product, amount, order_time, one_minute_sum])<br/>",
 * *                 "inputs": [
 * *                     {
 * *                         "num": 0,
 * *                         "id": "cbc357ccb763df2852fee8c4fc7d55f2",
 * *                         "ship_strategy": "HASH",
 * *                         "exchange": "pipelined_bounded"
 * *                     }
 * *                 ],
 * *                 "optimizer_properties": {}
 * *             }
 */
@ApiModel(value = "FlinkJobPlanNode", description = "Flink Job Plan Node Info")
@Builder
@Data
@NoArgsConstructor
public class FlinkJobPlanNode implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", notes = "id", dataType = "String", example = "1")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "parallelism", notes = "parallelism", dataType = "Integer", example = "1")
    @JsonProperty(value = "parallelism")
    private Integer parallelism;

    @ApiModelProperty(value = "operator", notes = "operator", dataType = "String", example = "1")
    @JsonProperty(value = "operator")
    private String operator;

    @ApiModelProperty(value = "operator_strategy", notes = "operator_strategy", dataType = "String", example = "1")
    @JsonProperty(value = "operator_strategy")
    private String operatorStrategy;

    @ApiModelProperty(value = "description", notes = "description", dataType = "String", example = "1")
    @JsonProperty(value = "description")
    private String description;

    @ApiModelProperty(value = "inputs", notes = "inputs", dataType = "List")
    @JsonProperty(value = "inputs")
    private List<FlinkJobPlanNodeInput> inputs;

    @ApiModelProperty(value = "optimizer_properties", notes = "optimizer_properties", dataType = "Object")
    @JsonProperty(value = "optimizer_properties")
    private Object optimizerProperties;

    /**
     * extend field
     */
    @ApiModelProperty(value = "backpressure", notes = "backpressure", dataType = "String", example = "1")
    @JsonProperty(value = "backpressure")
    private FlinkJobNodeBackPressure backpressure;

    @ApiModelProperty(value = "watermark", notes = "watermark", dataType = "String", example = "1")
    @JsonProperty(value = "watermark")
    private List<FlinkJobNodeWaterMark> watermark;

    @JsonCreator
    public FlinkJobPlanNode(
            @JsonProperty(value = "id") String id,
            @JsonProperty(value = "parallelism") Integer parallelism,
            @JsonProperty(value = "operator") String operator,
            @JsonProperty(value = "operator_strategy") String operatorStrategy,
            @JsonProperty(value = "description") String description,
            @JsonProperty(value = "inputs") List<FlinkJobPlanNodeInput> inputs,
            @JsonProperty(value = "optimizer_properties") Object optimizerProperties,
            @JsonProperty(value = "backpressure") FlinkJobNodeBackPressure backpressure,
            @JsonProperty(value = "watermark") List<FlinkJobNodeWaterMark> watermark) {
        this.id = id;
        this.parallelism = parallelism;
        this.operator = operator;
        this.operatorStrategy = operatorStrategy;
        this.description = description;
        this.inputs = inputs;
        this.optimizerProperties = optimizerProperties;
        this.backpressure = backpressure;
        this.watermark = watermark;
    }
}
