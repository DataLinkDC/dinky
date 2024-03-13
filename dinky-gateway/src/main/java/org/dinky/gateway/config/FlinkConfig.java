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

package org.dinky.gateway.config;

import org.dinky.assertion.Asserts;
import org.dinky.gateway.enums.ActionType;
import org.dinky.gateway.enums.SavePointType;
import org.dinky.gateway.model.CustomConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * FlinkConfig
 *
 * @since 2021/11/3 21:56
 */
@Getter
@Setter
@ApiModel(value = "FlinkConfig", description = "Configuration for a Flink job")
public class FlinkConfig {

    @ApiModelProperty(
            value = "Name of the Flink job",
            dataType = "String",
            example = "MyFlinkJob",
            notes = "Name of the Flink job")
    private String jobName;

    @ApiModelProperty(
            value = "ID of the Flink job",
            dataType = "String",
            example = "job-12345",
            notes = "ID of the Flink job")
    private String jobId;

    @ApiModelProperty(
            value = "Flink version",
            dataType = "String",
            example = "1.13.2",
            notes = "Version of Flink used for the job")
    private String flinkVersion;

    @ApiModelProperty(
            value = "Action to perform (e.g., START, STOP)",
            dataType = "String",
            example = "START",
            notes = "Action to perform for the Flink job")
    private ActionType action;

    @ApiModelProperty(
            value = "Type of savepoint (e.g., TRIGGER, CANCEL)",
            dataType = "String",
            example = "TRIGGER",
            notes = "Type of savepoint to create")
    private SavePointType savePointType;

    @ApiModelProperty(
            value = "Path to savepoint",
            dataType = "String",
            example = "/path/to/savepoint",
            notes = "Path to the savepoint")
    private String savePoint;

    @ApiModelProperty(
            value = "Additional configuration properties",
            dataType = "List",
            example = "[{\"name\":\"key1\",\"value\":\"value1\"}, {\"name\":\"key2\",\"value\":\"value2\"}]",
            notes = "Additional configuration properties for the job on web page")
    private List<CustomConfig> flinkConfigList = new ArrayList<>();

    @ApiModelProperty(
            value = "Additional configuration properties",
            dataType = "Map",
            example = "{\"key1\":\"value1\",\"key2\":\"value2\"}",
            notes = "Fixed configuration properties for the job on web page")
    private Map<String, String> configuration = new HashMap<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    public FlinkConfig() {}

    public FlinkConfig(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public FlinkConfig(
            String jobName,
            String jobId,
            ActionType action,
            SavePointType savePointType,
            String savePoint,
            Map<String, String> configuration) {
        this.jobName = jobName;
        this.jobId = jobId;
        this.action = action;
        this.savePointType = savePointType;
        this.savePoint = savePoint;
        this.configuration = configuration;
    }

    public static FlinkConfig build(Map<String, String> paras) {
        return new FlinkConfig(paras);
    }

    public static FlinkConfig build(
            String jobName,
            String jobId,
            String actionStr,
            String savePointTypeStr,
            String savePoint,
            String configParasStr) {
        Map<String, String> configMap = new HashMap<>();
        JsonNode paras = null;
        if (Asserts.isNotNullString(configParasStr)) {
            try {
                paras = mapper.readTree(configParasStr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Objects.requireNonNull(paras)
                    .forEach(node -> configMap.put(
                            node.get("key").asText(), node.get("value").asText()));
        }
        return new FlinkConfig(
                jobName, jobId, ActionType.get(actionStr), SavePointType.get(savePointTypeStr), savePoint, configMap);
    }

    public static FlinkConfig build(String jobId, String actionStr, String savePointTypeStr, String savePoint) {
        return new FlinkConfig(
                null, jobId, ActionType.get(actionStr), SavePointType.get(savePointTypeStr), savePoint, null);
    }
}
