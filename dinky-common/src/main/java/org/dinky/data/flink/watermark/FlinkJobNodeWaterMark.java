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

package org.dinky.data.flink.watermark;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * [
 *   {
 *     "id": "0.currentInputWatermark",
 *     "value": "1696676233964"
 *   }
 * ]
 */
@ApiModel(value = "FlinkJobNodeWaterMark", description = "Flink Job Node WaterMark Info")
@Builder
@Data
@NoArgsConstructor
public class FlinkJobNodeWaterMark implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Id", required = true, notes = "Id", dataType = "String", example = "1")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "Value", required = true, notes = "Value", dataType = "String", example = "1")
    @JsonProperty(value = "value")
    private String value;

    @JsonCreator
    public FlinkJobNodeWaterMark(@JsonProperty(value = "id") String id, @JsonProperty(value = "value") String value) {
        this.id = id;
        this.value = value;
    }
}
