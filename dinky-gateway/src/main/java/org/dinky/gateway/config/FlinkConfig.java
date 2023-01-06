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

package com.dlink.gateway.config;

import com.dlink.assertion.Asserts;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:56
 */
@Getter
@Setter
public class FlinkConfig {
    private String jobName;
    private String jobId;
    private ActionType action;
    private SavePointType savePointType;
    private String savePoint;
    //    private List<ConfigPara> configParas;
    private Map<String, String> configuration = new HashMap<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String DEFAULT_SAVEPOINT_PREFIX = "hdfs:///flink/savepoints/";

    public FlinkConfig() {
    }

    public FlinkConfig(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public FlinkConfig(String jobName, String jobId, ActionType action, SavePointType savePointType, String savePoint, Map<String, String> configuration) {
        this.jobName = jobName;
        this.jobId = jobId;
        this.action = action;
        this.savePointType = savePointType;
        this.savePoint = savePoint;
        this.configuration = configuration;
    }

    public static FlinkConfig build(Map<String, String> paras) {
        /*List<ConfigPara> configParasList = new ArrayList<>();
        for (Map.Entry<String, String> entry : paras.entrySet()) {
            configParasList.add(new ConfigPara(entry.getKey(),entry.getValue()));
        }*/
        return new FlinkConfig(paras);
    }

    public static FlinkConfig build(String jobName, String jobId, String actionStr, String savePointTypeStr, String savePoint, String configParasStr) {
        //List<ConfigPara> configParasList = new ArrayList<>();
        Map<String, String> configMap = new HashMap<>();
        JsonNode paras = null;
        if (Asserts.isNotNullString(configParasStr)) {
            try {
                paras = mapper.readTree(configParasStr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            paras.forEach((JsonNode node) -> {
                configMap.put(node.get("key").asText(), node.get("value").asText());
            });
        }
        return new FlinkConfig(jobName, jobId, ActionType.get(actionStr), SavePointType.get(savePointTypeStr), savePoint, configMap);
    }

    public static FlinkConfig build(String jobId, String actionStr, String savePointTypeStr, String savePoint) {
        return new FlinkConfig(null, jobId, ActionType.get(actionStr), SavePointType.get(savePointTypeStr), savePoint, null);
    }
}

