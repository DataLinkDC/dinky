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
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "FlinkJobVertex", description = "Flink Job Vertex Info")
@Builder
@Data
@NoArgsConstructor
public class FlinkJobVertex implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Vertex ID", notes = "Vertex ID")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "Vertex Name", notes = "Vertex Name")
    @JsonProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "Vertex Max Parallelism", notes = "Vertex Max Parallelism")
    @JsonProperty(value = "maxParallelism")
    private Integer maxParallelism;

    @ApiModelProperty(value = "Vertex Parallelism", notes = "Vertex Parallelism")
    @JsonProperty(value = "parallelism")
    private Integer parallelism;

    @ApiModelProperty(value = "Vertex Status", notes = "Vertex Status")
    @JsonProperty(value = "status")
    private String status;

    @ApiModelProperty(value = "Vertex Start Time", notes = "Vertex Start Time")
    @JsonProperty(value = "start-time")
    private Long startTime;

    @ApiModelProperty(value = "Vertex End Time", notes = "Vertex End Time")
    @JsonProperty(value = "end-time")
    private Long endTime;

    @ApiModelProperty(value = "Vertex Duration", notes = "Vertex Duration")
    @JsonProperty(value = "duration")
    private Long duration;

    @ApiModelProperty(value = "Vertex Tasks", notes = "Vertex Tasks")
    @JsonProperty(value = "tasks")
    private Map<String, Long> tasks;

    @ApiModelProperty(value = "Vertex Metrics", notes = "Vertex Metrics")
    @JsonProperty(value = "metrics")
    private Map<String, Object> metrics;

    @JsonCreator
    public FlinkJobVertex(
            @JsonProperty(value = "id") String id,
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "maxParallelism") Integer maxParallelism,
            @JsonProperty(value = "parallelism") Integer parallelism,
            @JsonProperty(value = "status") String status,
            @JsonProperty(value = "start-time") Long startTime,
            @JsonProperty(value = "end-time") Long endTime,
            @JsonProperty(value = "duration") Long duration,
            @JsonProperty(value = "tasks") Map<String, Long> tasks,
            @JsonProperty(value = "metrics") Map<String, Object> metrics) {
        this.id = id;
        this.name = name;
        this.maxParallelism = maxParallelism;
        this.parallelism = parallelism;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.tasks = tasks;
        this.metrics = metrics;
    }
}
