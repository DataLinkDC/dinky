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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskExtConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private TaskUdfConfig udfConfig;
    private List<ConfigItem> customConfig;

    // 获取自定义配置的某个key的值
    public String getCustomConfigValue(String key) {
        if (customConfig == null) {
            return null;
        }
        if (!containsKey(key)) {
            return null;
        }
        for (ConfigItem item : customConfig) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    // 获取自定义配置的所有key
    public List<String> getCustomConfigKeys() {
        if (customConfig == null) {
            return null;
        }
        return customConfig.stream().map(ConfigItem::getKey).collect(Collectors.toList());
    }

    // 获取自定义配置的所有key-value
    public List<Map<String, Object>> getCustomConfigMaps() {
        if (customConfig == null) {
            return null;
        }
        return customConfig.stream()
                .map(item -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put(item.getKey(), item.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // 是否包含某个key
    public boolean containsKey(String key) {
        if (customConfig == null) {
            return false;
        }
        for (ConfigItem item : customConfig) {
            if (item.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }
}
