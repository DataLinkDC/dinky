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

import org.dinky.context.EngineContextHolder;
import org.dinky.data.model.Configuration;
import org.dinky.data.model.SysConfig;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.Result;
import org.dinky.mapper.SysConfigMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.SysConfigService;
import org.dinky.service.UserService;
import org.dinky.utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SysConfigServiceImpl
 *
 * @since 2021/11/18
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysConfigServiceImpl extends SuperServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final UserService userService;

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

    /**
     * Initialize expression variables.
     */
    @Override
    public void initExpressionVariables() {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        // to initialize expression variable class and load it into the engine context
        EngineContextHolder.loadExpressionVariableClass(
                systemConfiguration.getExpressionVariable().getValue());
    }

    @Override
    public void updateSysConfigByKv(String key, String value) {
        SysConfig config = getOne(new LambdaQueryWrapper<>(SysConfig.class).eq(SysConfig::getName, key));
        config.setValue(value);
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();

        systemConfiguration.setConfiguration(key, value);
        config.updateById();
        // if the expression variable is modified, reinitialize the expression variable
        if (key.equals(systemConfiguration.getExpressionVariable().getKey())) {
            log.info(
                    "The expression variable is modified, reinitialize the expression variable to the engine context.");
            initExpressionVariables();
        }
    }

    @Override
    public Result<Map<String, Object>> getNeededCfg() {
        Map<String, Object> result = new HashMap<>();

        SystemConfiguration instances = SystemConfiguration.getInstances();

        Configuration<Boolean> isFirstSystemIn = instances.getIsFirstSystemIn();
        Configuration<Boolean> ldapEnable = instances.getLdapEnable();

        result.put(isFirstSystemIn.getKey(), isFirstSystemIn.getValue());
        result.put(ldapEnable.getKey(), ldapEnable.getValue());

        if (isFirstSystemIn.getValue()) {
            result.put(
                    instances.getDinkyAddr().getKey(), instances.getDinkyAddr().getValue());
            result.put(
                    instances.getTaskOwnerLockStrategy().getKey(),
                    instances.getTaskOwnerLockStrategy().getValue());
            result.put(
                    instances.getJobIdWait().getKey(), instances.getJobIdWait().getValue());
            result.put(
                    instances.getUseFlinkHistoryServer().getKey(),
                    instances.getUseFlinkHistoryServer().getValue());
            result.put(
                    instances.getFlinkHistoryServerPort().getKey(),
                    instances.getFlinkHistoryServerPort().getValue());
        }
        return Result.succeed(result);
    }

    @Override
    public Result<Void> setInitConfig(Map<String, Object> params) {
        SystemConfiguration instances = SystemConfiguration.getInstances();
        Configuration<Boolean> isFirstSystemIn = instances.getIsFirstSystemIn();

        if (!isFirstSystemIn.getValue()) {
            return Result.failed("not first init");
        }

        if (params.containsKey("password")) {
            String password = params.remove("password").toString();
            if (!TextUtil.isEmpty(password)) {
                User admin = userService.getUserByUsername("admin");
                admin.setPassword(SaSecureUtil.md5(password));
                userService.modifyUser(admin);
            }
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            updateSysConfigByKv(entry.getKey(), entry.getValue().toString());
        }
        updateSysConfigByKv(instances.getIsFirstSystemIn().getKey(), String.valueOf(false));
        return Result.succeed();
    }
}
