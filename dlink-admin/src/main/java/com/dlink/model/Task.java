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

package com.dlink.model;

import com.dlink.assertion.Asserts;
import com.dlink.db.model.SuperEntity;
import com.dlink.job.JobConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务
 *
 * @author wenmo
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_task")
public class Task extends SuperEntity {

    private static final long serialVersionUID = 5988972129893667154L;


    @TableField(fill = FieldFill.INSERT)
    private String alias;

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
    private List<Savepoints> savepoints;

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

    public List<Map<String, String>> parseConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (Asserts.isNotNullString(configJson)) {
                config = objectMapper.readValue(configJson, ArrayList.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return config;
    }

    public JobConfig buildSubmitConfig() {
        boolean useRemote = true;
        if (clusterId == null || clusterId == 0) {
            useRemote = false;
        }
        Map<String, String> map = new HashMap<>();
        for (Map<String, String> item : config) {
            if (Asserts.isNotNull(item)) {
                map.put(item.get("key"), item.get("value"));
            }
        }
        int jid = Asserts.isNull(jarId) ? 0 : jarId;
        boolean fg = Asserts.isNull(fragment) ? false : fragment;
        boolean sts = Asserts.isNull(statementSet) ? false : statementSet;
        return new JobConfig(type, step, false, false, useRemote, clusterId, clusterConfigurationId,jid, getId(),
                alias, fg, sts, batchModel, checkPoint, parallelism, savePointStrategy, savePointPath, map);
    }

    public JsonNode parseJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        return parseJsonNode(mapper);
    }

    public JsonNode parseJsonNode(ObjectMapper mapper) {
        JsonNode jsonNode = mapper.createObjectNode();
        ((ObjectNode)jsonNode).put("name",this.getName());
        ((ObjectNode)jsonNode).put("alias",this.alias);
        ((ObjectNode)jsonNode).put("dialect",this.dialect);
        ((ObjectNode)jsonNode).put("type",this.type);
        ((ObjectNode)jsonNode).put("statement",this.statement);
        ((ObjectNode)jsonNode).put("checkPoint",this.checkPoint);
        ((ObjectNode)jsonNode).put("savePointStrategy",this.savePointStrategy);
        ((ObjectNode)jsonNode).put("savePointPath",this.savePointPath);
        ((ObjectNode)jsonNode).put("parallelism",this.parallelism);
        ((ObjectNode)jsonNode).put("fragment",this.fragment);
        ((ObjectNode)jsonNode).put("statementSet",this.statementSet);
        ((ObjectNode)jsonNode).put("batchModel",this.batchModel);
        ((ObjectNode)jsonNode).put("clusterName",this.clusterName);
        ((ObjectNode)jsonNode).put("configJson",this.configJson);
        ((ObjectNode)jsonNode).put("note",this.note);
        ((ObjectNode)jsonNode).put("step",this.step);
        ((ObjectNode)jsonNode).put("enabled",this.getEnabled());
        return jsonNode;
    }

}
