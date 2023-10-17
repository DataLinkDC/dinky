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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@NoArgsConstructor
public class FlinkJobNodeBackPressure implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Status", required = true, notes = "Status", dataType = "String", example = "ok")
    @JsonProperty("status")
    private String status;

    @ApiModelProperty(
            value = "BackpressureLevel",
            required = true,
            notes = "BackpressureLevel",
            dataType = "String",
            example = "ok")
    @JsonProperty("backpressureLevel")
    private String backpressureLevel;

    @ApiModelProperty(
            value = "EndTimestamp",
            required = true,
            notes = "EndTimestamp",
            dataType = "Long",
            example = "1696647436365")
    @JsonProperty("end-timestamp")
    private Long endTimestamp;

    @ApiModelProperty(value = "Subtasks", required = true, notes = "Subtasks", dataType = "List", example = "Subtasks")
    @JsonProperty("subtasks")
    private List<Subtasks> subtasks;

    @JsonCreator
    public FlinkJobNodeBackPressure(
            @JsonProperty("status") String status,
            @JsonProperty("backpressureLevel") String backpressureLevel,
            @JsonProperty("end-timestamp") Long endTimestamp,
            @JsonProperty("subtasks") List<Subtasks> subtasks) {
        this.status = status;
        this.backpressureLevel = backpressureLevel;
        this.endTimestamp = endTimestamp;
        this.subtasks = subtasks;
    }

    // double
    //    @ApiModelProperty(value = "BackpressureLevel", required = true, notes = "BackpressureLevel", dataType =
    // "String", example = "ok")
    //    @JsonProperty("backpressure-level")
    //    private String backpressureLevel;

    @Data
    @Builder
    @NoArgsConstructor
    @ApiModel(value = "FlinkJobNodeBackPressure-Subtasks", description = "Flink Job Node BackPressure Subtasks Info")
    public static class Subtasks {

        @ApiModelProperty(value = "Subtask", required = true, notes = "Subtask", dataType = "Integer", example = "0")
        @JsonProperty("subtask")
        private Integer subtask;

        @ApiModelProperty(
                value = "BackpressureLevel",
                required = true,
                notes = "BackpressureLevel",
                dataType = "String",
                example = "ok")
        @JsonProperty("backpressureLevel")
        private String backpressureLevel;

        @ApiModelProperty(value = "Ratio", required = true, notes = "Ratio", dataType = "Double", example = "0")
        @JsonProperty("ratio")
        private Double ratio;

        @ApiModelProperty(value = "IdleRatio", required = true, notes = "IdleRatio", dataType = "Double", example = "1")
        @JsonProperty("idleRatio")
        private Double idleRatio;

        @ApiModelProperty(value = "BusyRatio", required = true, notes = "BusyRatio", dataType = "Double", example = "0")
        @JsonProperty("busyRatio")
        private Double busyRatio;
        //
        // double
        //         @ApiModelProperty(value = "BackpressureLevel", required = true, notes = "BackpressureLevel", dataType
        // = "String", example = "ok")
        //        @JsonProperty("backpressure-level")
        //        private String backpressureLevel;

        @JsonCreator
        public Subtasks(
                @JsonProperty("subtask") Integer subtask,
                @JsonProperty("backpressureLevel") String backpressureLevel,
                @JsonProperty("ratio") Double ratio,
                @JsonProperty("idleRatio") Double idleRatio,
                @JsonProperty("busyRatio") Double busyRatio) {
            this.subtask = subtask;
            this.backpressureLevel = backpressureLevel;
            this.ratio = ratio;
            this.idleRatio = idleRatio;
            this.busyRatio = busyRatio;
        }
    }
}
