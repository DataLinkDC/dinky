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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkConfig
 *
 * @since 2021/11/3 21:56
 */
@Getter
@Setter
public class FlinkConfig {

    private String jobName;
    private String jobId;
    private String flinkVersion;
    private ActionType action;
    private SavePointType savePointType;
    private String savePoint;
    private Map<String, String> configuration = new HashMap<>();
    private List<Map<String, String>> flinkConfigList = new ArrayList<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String DEFAULT_SAVEPOINT_PREFIX = "hdfs:///flink/savepoints/";

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
                    .forEach(
                            node ->
                                    configMap.put(
                                            node.get("key").asText(), node.get("value").asText()));
        }
        return new FlinkConfig(
                jobName,
                jobId,
                ActionType.get(actionStr),
                SavePointType.get(savePointTypeStr),
                savePoint,
                configMap);
    }

    public static FlinkConfig build(
            String jobId, String actionStr, String savePointTypeStr, String savePoint) {
        return new FlinkConfig(
                null,
                jobId,
                ActionType.get(actionStr),
                SavePointType.get(savePointTypeStr),
                savePoint,
                null);
    }
}
