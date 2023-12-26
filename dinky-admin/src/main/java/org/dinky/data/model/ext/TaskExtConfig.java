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

package org.dinky.data.model.ext;

import org.dinky.assertion.Asserts;
import org.dinky.data.ext.ConfigItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "TaskExtConfig", description = "Extended Configuration for Task")
public class TaskExtConfig implements Serializable {

    @ApiModelProperty(
            value = "UDF Config",
            dataType = "TaskUdfConfig",
            notes = "UDF (User-Defined Function) configuration for the task")
    private TaskUdfConfig udfConfig;

    @ApiModelProperty(
            value = "Custom Config",
            dataType = "List<ConfigItem>",
            notes = "Custom configuration items for the task")
    private List<ConfigItem> customConfig = new ArrayList<>();

    // 获取自定义配置的某个key的值
    public String getCustomConfigValue(String key) {
        return customConfig.stream()
                .filter(item -> item.getKey().equals(key))
                .findFirst()
                .orElseGet(() -> new ConfigItem(key, ""))
                .getValue();
    }

    // 获取自定义配置的所有key
    @JsonIgnore
    public List<String> getCustomConfigKeys() {
        return customConfig.stream().map(ConfigItem::getKey).collect(Collectors.toList());
    }

    // 获取自定义配置的所有key-value
    @JsonIgnore
    public Map<String, String> getCustomConfigMaps() {
        return Asserts.isNotNullCollection(customConfig)
                ? customConfig.stream()
                        .filter(item -> item.getKey() != null && item.getValue() != null)
                        .collect(Collectors.toMap(ConfigItem::getKey, ConfigItem::getValue))
                : new HashMap<>();
    }

    // 是否包含某个key
    public boolean containsKey(String key) {
        return customConfig.stream().anyMatch(item -> item.getKey().equals(key));
    }
}
