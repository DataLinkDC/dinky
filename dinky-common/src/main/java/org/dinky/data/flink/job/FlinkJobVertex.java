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

import com.alibaba.fastjson2.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "FlinkJobVertex", description = "Flink Job Vertex Info")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlinkJobVertex implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Vertex ID", notes = "Vertex ID")
    @JSONField(name = "id")
    private String id;

    @ApiModelProperty(value = "Vertex Name", notes = "Vertex Name")
    @JSONField(name = "name")
    private String name;

    @ApiModelProperty(value = "Vertex Max Parallelism", notes = "Vertex Max Parallelism")
    @JSONField(name = "maxParallelism")
    private Integer maxParallelism;

    @ApiModelProperty(value = "Vertex Parallelism", notes = "Vertex Parallelism")
    @JSONField(name = "parallelism")
    private Integer parallelism;

    @ApiModelProperty(value = "Vertex Status", notes = "Vertex Status")
    @JSONField(name = "status")
    private String status;

    @ApiModelProperty(value = "Vertex Start Time", notes = "Vertex Start Time")
    @JSONField(name = "start-time")
    private Long startTime;

    @ApiModelProperty(value = "Vertex End Time", notes = "Vertex End Time")
    @JSONField(name = "end-time")
    private Long endTime;

    @ApiModelProperty(value = "Vertex Duration", notes = "Vertex Duration")
    @JSONField(name = "duration")
    private Long duration;

    @ApiModelProperty(value = "Vertex Tasks", notes = "Vertex Tasks")
    @JSONField(name = "tasks")
    private Map<String, Long> tasks;

    @ApiModelProperty(value = "Vertex Metrics", notes = "Vertex Metrics")
    @JSONField(name = "metrics")
    private Map<String, Object> metrics;
}
