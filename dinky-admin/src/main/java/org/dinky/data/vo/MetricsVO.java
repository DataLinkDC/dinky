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

package org.dinky.data.vo;

import org.dinky.data.annotations.paimon.PartitionKey;
import org.dinky.data.annotations.paimon.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import cn.hutool.core.map.MapUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "MetricsVO", description = "Metrics Value Object")
public class MetricsVO implements Serializable {
    private static final Map<String, String> OPTIONS = MapUtil.builder("file.format", "parquet")
            .put("snapshot.time-retained", "10 s")
            .put("partition.expiration-time", "7d")
            .put("partition.expiration-check-interval", "1d")
            .put("partition.timestamp-formatter", "yyyy-MM-dd")
            .put("partition.timestamp-pattern", "$date")
            .build();

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @PrimaryKey
    @ApiModelProperty(
            value = "Timestamp of heartbeart",
            dataType = "LocalDateTime",
            notes = "Timestamp of the heartbeat data.",
            example = "2023-09-15 14:30:00")
    private LocalDateTime heartTime;

    @ApiModelProperty(value = "Model name", dataType = "String", notes = "Name of the model.")
    @PartitionKey
    private String model;

    @ApiModelProperty(value = "Content of metrics", dataType = "Object", notes = "Content of the metrics data.")
    private Object content;

    @PartitionKey
    private String date;
}
