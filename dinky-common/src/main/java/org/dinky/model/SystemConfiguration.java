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

package org.dinky.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * SystemConfiguration
 *
 * @since 2021/11/18
 */
@Getter
public class SystemConfiguration {

    private static final SystemConfiguration systemConfiguration = new SystemConfiguration();

    public static SystemConfiguration getInstances() {
        return systemConfiguration;
    }

    private Consumer<Configuration<?>> initMethod = null;

    public static Configuration.OptionBuilder key(String key) {
        return new Configuration.OptionBuilder(key);
    }

    private static final List<Configuration<?>> CONFIGURATION_LIST =
            Arrays.stream(
                            ReflectUtil.getFields(
                                    SystemConfiguration.class,
                                    f -> f.getType() == Configuration.class))
                    .map(f -> (Configuration<?>) ReflectUtil.getFieldValue(systemConfiguration, f))
                    .collect(Collectors.toList());

    private Configuration<Boolean> useRestAPI =
            key("flink.settings.useRestAPI")
                    .booleanType()
                    .defaultValue(true)
                    .note("在运维 Flink 任务时是否使用 RestAPI");
    private Configuration<String> sqlSeparator =
            key("flink.settings.sqlSeparator")
                    .stringType()
                    .defaultValue(";\n")
                    .note("FlinkSQL语句分割符");
    private Configuration<Integer> jobIdWait =
            key("flink.settings.jobIdWait")
                    .intType()
                    .defaultValue(30)
                    .note("提交 Application 或 PerJob 任务时获取 Job ID 的最大等待时间（秒）");

    private Configuration<String> mavenSettings =
            key("maven.settings.settingsFilePath")
                    .stringType()
                    .defaultValue("")
                    .note("Maven Settings 文件路径");

    private Configuration<String> mavenRepository =
            key("maven.settings.repository")
                    .stringType()
                    .defaultValue("https://maven.aliyun.com/nexus/content/repositories/central")
                    .note("Maven private server address");

    private Configuration<String> mavenRepositoryUser =
            key("maven.settings.repositoryUser")
                    .stringType()
                    .defaultValue("")
                    .note("Maven private server authentication username");

    private Configuration<String> mavenRepositoryPassword =
            key("maven.settings.repositoryUser")
                    .stringType()
                    .defaultValue("")
                    .note("Maven Central Repository Auth Password");

    private Configuration<String> pythonHome =
            key("env.settings.pythonHome").stringType().defaultValue("python3").note("PYTHON HOME");

    private Configuration<String> dinkyAddr =
            key("env.settings.dinkyAddr")
                    .stringType()
                    .defaultValue(System.getProperty("dinkyAddr"))
                    .note(
                            "the address must be the same as the address configured in the Dinky Application background url");

    private Configuration<Boolean> dolphinschedulerEnable =
            key("dolphinscheduler.settings.enable")
                    .booleanType()
                    .defaultValue(false)
                    .note("Dolphinscheduler ON-OFF");

    private Configuration<String> dolphinschedulerUrl =
            key("dolphinscheduler.settings.url")
                    .stringType()
                    .defaultValue("")
                    .note(
                            "The address must be the same as the address configured in the DolphinScheduler background , eg: http://127.0.0.1:12345/dolphinscheduler");
    private Configuration<String> dolphinschedulerToken =
            key("dolphinscheduler.settings.token")
                    .stringType()
                    .defaultValue("")
                    .note(
                            "DolphinScheduler's Token , Please create a token in DolphinScheduler's Security Center -> Token Management, and modify the following configuration");
    private Configuration<String> dolphinschedulerProjectName =
            key("dolphinscheduler.settings.projectName")
                    .stringType()
                    .defaultValue("Dinky")
                    .note("The project name specified in DolphinScheduler, case insensitive");

    public void setConfiguration(JsonNode jsonNode) {
        CONFIGURATION_LIST.forEach(
                item -> {
                    final JsonNode value = jsonNode.get(item.getKey());
                    if (value == null || value.isNull()) {
                        item.setValue(item.getDefaultValue());
                        return;
                    }
                    item.setValue(value);
                });
        // initRun
        CONFIGURATION_LIST.forEach(
                c -> Opt.ofNullable(this.initMethod).ifPresent(x -> x.accept(c)));
    }

    public Map<String, List<Configuration<?>>> addConfiguration() {
        Map<String, List<Configuration<?>>> data = new TreeMap<>();
        for (Configuration<?> item : CONFIGURATION_LIST) {
            final String name = item.getKey();
            String k = StrUtil.split(name, ".").get(0);
            Opt.ofBlankAble(k)
                    .ifPresent(
                            key -> {
                                data.computeIfAbsent(k, x -> new ArrayList<>());
                                data.get(k).add(item);
                            });
        }
        return data;
    }

    public void setInitMethod(Consumer<Configuration<?>> initMethod) {
        this.initMethod = initMethod;
    }

    public boolean isUseRestAPI() {
        return useRestAPI.getValue();
    }

    public String getSqlSeparator() {
        return sqlSeparator.getValue();
    }

    public int getJobIdWait() {
        return jobIdWait.getValue();
    }

    public String getMavenSettings() {

        return mavenSettings.getValue();
    }

    public String getMavenRepository() {
        return mavenRepository.getValue();
    }

    public String getMavenRepositoryUser() {
        return mavenRepositoryUser.getValue();
    }

    public String getMavenRepositoryPassword() {
        return mavenRepositoryPassword.getValue();
    }

    public String getPythonHome() {
        return pythonHome.getValue();
    }
}
