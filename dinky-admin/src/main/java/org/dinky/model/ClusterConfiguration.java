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

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClusterConfig
 *
 * @author wenmo
 * @since 2021/11/6
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_cluster_configuration")
public class ClusterConfiguration extends SuperEntity {

    private static final long serialVersionUID = 5830130188542066241L;

    private Integer tenantId;

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String type;

    private String configJson;

    private Boolean isAvailable;

    private String note;

    @TableField(exist = false)
    private Map<String, Object> config = new HashMap<>();

    public Map<String, Object> parseConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (Asserts.isNotNullString(configJson)) {
                config = objectMapper.readValue(configJson, HashMap.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return config;
    }
}
