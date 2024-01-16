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

package org.dinky.service.impl;

import org.dinky.data.model.Configuration;
import org.dinky.data.model.SysConfig;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.mapper.SysConfigMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.SysConfigService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;

/**
 * SysConfigServiceImpl
 *
 * @since 2021/11/18
 */
@Service
public class SysConfigServiceImpl extends SuperServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public Map<String, List<Configuration<?>>> getAll() {
        return SystemConfiguration.getInstances().getAllConfiguration();
    }

    /**
     * Get one configuration by key.
     *
     * @param key
     * @return A map of string keys to lists of {@link Configuration} objects.
     */
    @Override
    public Configuration<Object> getOneConfigByKey(String key) {

        List<Configuration<?>> configurationList =
                getAll().entrySet().stream().flatMap(x -> x.getValue().stream()).collect(Collectors.toList());
        return (Configuration<Object>) configurationList.stream()
                .filter(x -> x.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No such configuration: " + key));
    }

    @Override
    public void initSysConfig() {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        systemConfiguration.initAfterBeanStarted();
        List<Configuration<?>> configurationList = systemConfiguration.getAllConfiguration().entrySet().stream()
                .flatMap(x -> x.getValue().stream())
                .collect(Collectors.toList());
        List<SysConfig> sysConfigList = list();
        List<String> nameList = sysConfigList.stream().map(SysConfig::getName).collect(Collectors.toList());
        configurationList.stream()
                .filter(x -> !nameList.contains(x.getKey()))
                .map(x -> {
                    SysConfig sysConfig = new SysConfig();
                    sysConfig.setName(x.getKey());
                    sysConfig.setValue(Convert.toStr(x.getDefaultValue()));
                    return sysConfig;
                })
                .forEach(Model::insertOrUpdate);
        Map<String, String> configMap =
                CollUtil.toMap(list(), new HashMap<>(), SysConfig::getName, SysConfig::getValue);
        systemConfiguration.initSetConfiguration(configMap);
    }

    @Override
    public void updateSysConfigByKv(String key, String value) {
        SysConfig config = getOne(new QueryWrapper<SysConfig>().eq("name", key));
        config.setValue(value);
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();

        systemConfiguration.setConfiguration(key, value);
        config.updateById();
    }
}
