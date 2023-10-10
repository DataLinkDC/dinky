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

package org.dinky.data.flink.backpressure;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     {
 *   "status": "ok",
 *   "backpressureLevel": "ok",
 *   "end-timestamp": 1696647436365,
 *   "subtasks": [
 *     {
 *       "subtask": 0,
 *       "backpressureLevel": "ok",
 *       "ratio": 0,
 *       "idleRatio": 1,
 *       "busyRatio": 0,
 *       "backpressure-level": "ok"
 *     }
 *   ],
 *   "backpressure-level": "ok"
 * }
 */
@ApiModel(value = "FlinkJobNodeBackPressure", description = "Flink Job Node BackPressure Info")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlinkJobNodeBackPressure implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Status", required = true, notes = "Status", dataType = "String", example = "ok")
    @JSONField(name = "status")
    private String status;

    @ApiModelProperty(
            value = "BackpressureLevel",
            required = true,
            notes = "BackpressureLevel",
            dataType = "String",
            example = "ok")
    @JSONField(name = "backpressureLevel")
    private String backpressureLevel;

    @ApiModelProperty(
            value = "EndTimestamp",
            required = true,
            notes = "EndTimestamp",
            dataType = "Long",
            example = "1696647436365")
    @JSONField(name = "end-timestamp")
    private Long endTimestamp;

    @ApiModelProperty(value = "Subtasks", required = true, notes = "Subtasks", dataType = "List", example = "Subtasks")
    @JSONField(name = "subtasks")
    private List<Subtasks> subtasks;

    // double
    //    @ApiModelProperty(value = "BackpressureLevel", required = true, notes = "BackpressureLevel", dataType =
    // "String", example = "ok")
    //    @JsonProperty("backpressure-level")
    //    private String backpressureLevel;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "FlinkJobNodeBackPressure-Subtasks", description = "Flink Job Node BackPressure Subtasks Info")
    public static class Subtasks {

        @ApiModelProperty(value = "Subtask", required = true, notes = "Subtask", dataType = "Integer", example = "0")
        @JSONField(name = "subtask")
        private Integer subtask;

        @ApiModelProperty(
                value = "BackpressureLevel",
                required = true,
                notes = "BackpressureLevel",
                dataType = "String",
                example = "ok")
        @JSONField(name = "backpressureLevel")
        private String backpressureLevel;

        @ApiModelProperty(value = "Ratio", required = true, notes = "Ratio", dataType = "Double", example = "0")
        @JSONField(name = "ratio")
        private Double ratio;

        @ApiModelProperty(value = "IdleRatio", required = true, notes = "IdleRatio", dataType = "Double", example = "1")
        @JSONField(name = "idleRatio")
        private Double idleRatio;

        @ApiModelProperty(value = "BusyRatio", required = true, notes = "BusyRatio", dataType = "Double", example = "0")
        @JSONField(name = "busyRatio")
        private Double busyRatio;
        //
        // double
        //         @ApiModelProperty(value = "BackpressureLevel", required = true, notes = "BackpressureLevel", dataType
        // = "String", example = "ok")
        //        @JsonProperty("backpressure-level")
        //        private String backpressureLevel;
    }
}
