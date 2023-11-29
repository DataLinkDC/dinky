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
import java.util.Map;

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
 *     "jid": "62254c597e60e3b978e1663f29b333cd",
 *     "name": "测测吧",
 *     "isStoppable": false,
 *     "state": "RUNNING",
 *     "start-time": 1696641743871,
 *     "end-time": -1,
 *     "duration": 3290276,
 *     "maxParallelism": -1,
 *     "now": 1696645034147,
 *     "timestamps": {
 *         "CANCELED": 0,
 *         "FAILING": 0,
 *         "RECONCILING": 0,
 *         "RUNNING": 1696641743875,
 *         "CREATED": 1696641743873,
 *         "RESTARTING": 0,
 *         "SUSPENDED": 0,
 *         "FAILED": 0,
 *         "FINISHED": 0,
 *         "INITIALIZING": 1696641743871,
 *         "CANCELLING": 0
 *     },
 *     "vertices": [
 *         {
 *             "id": "cbc357ccb763df2852fee8c4fc7d55f2",
 *             "name": "Source: source_table3[1] -> Calc[2] -> WatermarkAssigner[3]",
 *             "maxParallelism": 128,
 *             "parallelism": 1,
 *             "status": "RUNNING",
 *             "start-time": 1696641743946,
 *             "end-time": -1,
 *             "duration": 3290201,
 *             "tasks": {
 *                 "INITIALIZING": 0,
 *                 "CANCELED": 0,
 *                 "FAILED": 0,
 *                 "FINISHED": 0,
 *                 "RECONCILING": 0,
 *                 "CREATED": 0,
 *                 "SCHEDULED": 0,
 *                 "DEPLOYING": 0,
 *                 "RUNNING": 1,
 *                 "CANCELING": 0
 *             },
 *             "metrics": {
 *                 "read-bytes": 0,
 *                 "read-bytes-complete": true,
 *                 "write-bytes": 163840,
 *                 "write-bytes-complete": true,
 *                 "read-records": 0,
 *                 "read-records-complete": true,
 *                 "write-records": 3285,
 *                 "write-records-complete": true,
 *                 "accumulated-backpressured-time": 0,
 *                 "accumulated-idle-time": 0,
 *                 "accumulated-busy-time": "NaN"
 *             }
 *         },
 *         {
 *             "id": "c27dcf7b54ef6bfd6cff02ca8870b681",
 *             "name": "OverAggregate[5] -> Calc[6] -> Sink: sink_table5[7]",
 *             "maxParallelism": 128,
 *             "parallelism": 1,
 *             "status": "RUNNING",
 *             "start-time": 1696641743947,
 *             "end-time": -1,
 *             "duration": 3290200,
 *             "tasks": {
 *                 "INITIALIZING": 0,
 *                 "CANCELED": 0,
 *                 "FAILED": 0,
 *                 "FINISHED": 0,
 *                 "RECONCILING": 0,
 *                 "CREATED": 0,
 *                 "SCHEDULED": 0,
 *                 "DEPLOYING": 0,
 *                 "RUNNING": 1,
 *                 "CANCELING": 0
 *             },
 *             "metrics": {
 *                 "read-bytes": 177394,
 *                 "read-bytes-complete": true,
 *                 "write-bytes": 0,
 *                 "write-bytes-complete": true,
 *                 "read-records": 3285,
 *                 "read-records-complete": true,
 *                 "write-records": 0,
 *                 "write-records-complete": true,
 *                 "accumulated-backpressured-time": 0,
 *                 "accumulated-idle-time": 3368661,
 *                 "accumulated-busy-time": 0
 *             }
 *         }
 *     ],
 *     "status-counts": {
 *         "INITIALIZING": 0,
 *         "CANCELED": 0,
 *         "FAILED": 0,
 *         "FINISHED": 0,
 *         "RECONCILING": 0,
 *         "CREATED": 0,
 *         "SCHEDULED": 0,
 *         "DEPLOYING": 0,
 *         "RUNNING": 2,
 *         "CANCELING": 0
 *     },
 *     "plan": {
 *         "jid": "62254c597e60e3b978e1663f29b333cd",
 *         "name": "测测吧",
 *         "type": "STREAMING",
 *         "nodes": [
 *             {
 *                 "id": "c27dcf7b54ef6bfd6cff02ca8870b681",
 *                 "parallelism": 1,
 *                 "operator": "",
 *                 "operator_strategy": "",
 *                 "description": "[5]:OverAggregate(partitionBy=[product], orderBy=[order_time ASC], window=[ RANG BETWEEN 60000 PRECEDING AND CURRENT ROW], select=[product, amount, order_time, COUNT(amount) AS w0$o0, $SUM0(amount) AS w0$o1])<br/>+- [6]:Calc(select=[product, amount, order_time, CASE((w0$o0 &gt; 0), w0$o1, null:BIGINT) AS one_minute_sum])<br/>   +- [7]:Sink(table=[default_catalog.default_database.sink_table5], fields=[product, amount, order_time, one_minute_sum])<br/>",
 *                 "inputs": [
 *                     {
 *                         "num": 0,
 *                         "id": "cbc357ccb763df2852fee8c4fc7d55f2",
 *                         "ship_strategy": "HASH",
 *                         "exchange": "pipelined_bounded"
 *                     }
 *                 ],
 *                 "optimizer_properties": {}
 *             },
 *             {
 *                 "id": "cbc357ccb763df2852fee8c4fc7d55f2",
 *                 "parallelism": 1,
 *                 "operator": "",
 *                 "operator_strategy": "",
 *                 "description": "[1]:TableSourceScan(table=[[default_catalog, default_database, source_table3]], fields=[order_id, product, amount])<br/>+- [2]:Calc(select=[product, amount, CAST(CURRENT_TIMESTAMP() AS TIMESTAMP(3)) AS order_time])<br/>   +- [3]:WatermarkAssigner(rowtime=[order_time], watermark=[(order_time - 2000:INTERVAL SECOND)])<br/>",
 *                 "optimizer_properties": {}
 *             }
 *         ]
 *     }
 * }
 */
@ApiModel(value = "FlinkJobDetailInfo", description = "Flink Job Detail Info")
@Builder
@Data
@NoArgsConstructor
public class FlinkJobDetailInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "jid", notes = "jid", dataType = "String", example = "1")
    @JsonProperty(value = "jid")
    private String jid;

    @ApiModelProperty(value = "name", notes = "name", dataType = "String", example = "1")
    @JsonProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "state", notes = "state", dataType = "String", example = "1")
    @JsonProperty(value = "state")
    private String state;

    @ApiModelProperty(value = "start-time", notes = "start-time", dataType = "long", example = "1")
    @JsonProperty(value = "start-time")
    private Long startTime;

    @ApiModelProperty(value = "end-time", notes = "end-time", dataType = "long", example = "1")
    @JsonProperty(value = "end-time")
    private Long endTime;

    @ApiModelProperty(value = "duration", notes = "duration", dataType = "long", example = "1")
    @JsonProperty(value = "duration")
    private Long duration;

    @ApiModelProperty(value = "maxParallelism", notes = "maxParallelism", dataType = "long", example = "1")
    @JsonProperty(value = "maxParallelism")
    private Long maxParallelism;

    @ApiModelProperty(value = "now", notes = "now", dataType = "long", example = "1")
    @JsonProperty(value = "now")
    private Long now;

    @ApiModelProperty(value = "timestamps", notes = "timestamps", dataType = "long", example = "1")
    @JsonProperty(value = "timestamps")
    private Map<String, Long> timestamps;

    @ApiModelProperty(value = "vertices", notes = "vertices", dataType = "List", example = "1")
    @JsonProperty(value = "vertices")
    private List<FlinkJobVertex> vertices;

    @ApiModelProperty(value = "status-counts", notes = "status-counts", dataType = "Map", example = "1")
    @JsonProperty(value = "status-counts")
    private Map<String, Integer> statusCounts;

    @ApiModelProperty(value = "plan", notes = "plan", dataType = "FlinkJobPlan", example = "1")
    @JsonProperty(value = "plan")
    private FlinkJobPlan plan;

    @JsonCreator
    public FlinkJobDetailInfo(
            @JsonProperty(value = "jid") String jid,
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "state") String state,
            @JsonProperty(value = "start-time") Long startTime,
            @JsonProperty(value = "end-time") Long endTime,
            @JsonProperty(value = "duration") Long duration,
            @JsonProperty(value = "maxParallelism") Long maxParallelism,
            @JsonProperty(value = "now") Long now,
            @JsonProperty(value = "timestamps") Map<String, Long> timestamps,
            @JsonProperty(value = "vertices") List<FlinkJobVertex> vertices,
            @JsonProperty(value = "status-counts") Map<String, Integer> statusCounts,
            @JsonProperty(value = "plan") FlinkJobPlan plan) {
        this.jid = jid;
        this.name = name;
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.maxParallelism = maxParallelism;
        this.now = now;
        this.timestamps = timestamps;
        this.vertices = vertices;
        this.statusCounts = statusCounts;
        this.plan = plan;
    }
}
