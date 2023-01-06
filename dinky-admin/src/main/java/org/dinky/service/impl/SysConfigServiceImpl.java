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

package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.SysConfigMapper;
import com.dlink.model.SysConfig;
import com.dlink.model.SystemConfiguration;
import com.dlink.service.SysConfigService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SysConfigServiceImpl
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Service
public class SysConfigServiceImpl extends SuperServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();
        List<SysConfig> sysConfigs = list();
        for (SysConfig item : sysConfigs) {
            map.put(item.getName(), item.getValue());
        }
        SystemConfiguration.getInstances().addConfiguration(map);
        return map;
    }

    @Override
    public void initSysConfig() {
        SystemConfiguration.getInstances().setConfiguration(mapper.valueToTree(getAll()));
    }

    @Override
    public void updateSysConfigByJson(JsonNode node) {
        if (node != null && node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String name = entry.getKey();
                String value = entry.getValue().asText();
                SysConfig config = getOne(new QueryWrapper<SysConfig>().eq("name", name));
                SysConfig newConfig = new SysConfig();
                newConfig.setValue(value);
                if (Asserts.isNull(config)) {
                    newConfig.setName(name);
                    save(newConfig);
                } else {
                    newConfig.setId(config.getId());
                    updateById(newConfig);
                }
            }
        }
        SystemConfiguration.getInstances().setConfiguration(node);
    }
}
