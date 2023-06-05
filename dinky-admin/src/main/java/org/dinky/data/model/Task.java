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

package org.dinky.data.model;

import org.dinky.assertion.Asserts;
import org.dinky.job.JobConfig;
import org.dinky.mybatis.model.SuperEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务
 *
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_task")
@Slf4j
public class Task extends SuperEntity<Task> {

    private static final long serialVersionUID = 5988972129893667154L;
    private String dialect;

    private Integer tenantId;

    private String type;

    private Integer checkPoint;

    private Integer savePointStrategy;

    private String savePointPath;

    private Integer parallelism;

    private Boolean fragment;

    private Boolean statementSet;

    private Boolean batchModel;

    private Integer clusterId;

    private Integer clusterConfigurationId;

    private Integer databaseId;

    private Integer jarId;

    private Integer envId;

    private Integer alertGroupId;

    private String configJson;

    private String note;

    private Integer step;

    private Integer jobInstanceId;

    private Integer versionId;

    @TableField(exist = false)
    private String statement;

    @TableField(exist = false)
    private String clusterName;

    @TableField(exist = false)
    private List<Savepoints> savePoints;

    @TableField(exist = false)
    private List<Map<String, String>> config = new ArrayList<>();

    @TableField(exist = false)
    private String path;

    @TableField(exist = false)
    private String jarName;

    @TableField(exist = false)
    private String clusterConfigurationName;

    @TableField(exist = false)
    private String databaseName;

    @TableField(exist = false)
    private String envName;

    @TableField(exist = false)
    private String alertGroupName;

    public static final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> parseConfig() {
        if (Asserts.isNullString(configJson)) {
            return config;
        }

        try {
            config = objectMapper.readValue(configJson, ArrayList.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return config;
    }

    public JobConfig buildSubmitConfig() {
        boolean useRemote = clusterId != null && clusterId != 0;
        Map<String, String> map =
                config.stream()
                        .filter(Asserts::isNotNull)
                        .collect(
                                Collectors.toMap(
                                        item -> item.get("key"),
                                        item -> item.get("value"),
                                        (a, b) -> b));

        int jid = Asserts.isNull(jarId) ? 0 : jarId;
        boolean fg = Asserts.isNotNull(fragment) && fragment;
        boolean sts = Asserts.isNotNull(statementSet) && statementSet;
        return new JobConfig(
                type,
                step,
                false,
                false,
                useRemote,
                clusterId,
                clusterConfigurationId,
                jid,
                getId(),
                getName(),
                fg,
                sts,
                batchModel,
                checkPoint,
                parallelism,
                savePointStrategy,
                savePointPath,
                map);
    }

    public JsonNode parseJsonNode(ObjectMapper mapper) {
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("name", this.getName());
        jsonNode.put("dialect", this.dialect);
        jsonNode.put("type", this.type);
        jsonNode.put("statement", this.statement);
        jsonNode.put("checkPoint", this.checkPoint);
        jsonNode.put("savePointStrategy", this.savePointStrategy);
        jsonNode.put("savePointPath", this.savePointPath);
        jsonNode.put("parallelism", this.parallelism);
        jsonNode.put("fragment", this.fragment);
        jsonNode.put("statementSet", this.statementSet);
        jsonNode.put("batchModel", this.batchModel);
        jsonNode.put("clusterName", this.clusterName);
        jsonNode.put("configJson", this.configJson);
        jsonNode.put("note", this.note);
        jsonNode.put("step", this.step);
        jsonNode.put("enabled", this.getEnabled());
        return jsonNode;
    }
}
