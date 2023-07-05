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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.DesensitizedUtil;
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

    private final Consumer<Configuration<?>> initMethod = null;

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
    private final Configuration<Boolean> useRestAPI =
            key("flink.settings.useRestAPI")
                    .booleanType()
                    .defaultValue(true)
                    .note("在运维 Flink 任务时是否使用 RestAPI");
    private final Configuration<String> sqlSeparator =
            key("flink.settings.sqlSeparator")
                    .stringType()
                    .defaultValue(";\\n")
                    .note("FlinkSQL语句分割符");
    private final Configuration<Integer> jobIdWait =
            key("flink.settings.jobIdWait")
                    .intType()
                    .defaultValue(30)
                    .note("提交 Application 或 PerJob 任务时获取 Job ID 的最大等待时间（秒）");

    private final Configuration<String> mavenSettings =
            key("maven.settings.settingsFilePath")
                    .stringType()
                    .defaultValue("")
                    .note("Maven Settings 文件路径");

    private final Configuration<String> mavenRepository =
            key("maven.settings.repository")
                    .stringType()
                    .defaultValue("https://maven.aliyun.com/nexus/content/repositories/central")
                    .note("Maven private server address");

    private final Configuration<String> mavenRepositoryUser =
            key("maven.settings.repositoryUser")
                    .stringType()
                    .defaultValue("")
                    .note("Maven private server authentication username");

    private final Configuration<String> mavenRepositoryPassword =
            key("maven.settings.repositoryPassword")
                    .stringType()
                    .defaultValue("")
                    .desensitizedHandler(DesensitizedUtil::password)
                    .note("Maven Central Repository Auth Password");

    private final Configuration<String> pythonHome =
            key("env.settings.pythonHome").stringType().defaultValue("python3").note("PYTHON HOME");

    private final Configuration<String> dinkyAddr =
            key("env.settings.dinkyAddr")
                    .stringType()
                    .defaultValue(System.getProperty("dinkyAddr"))
                    .note(
                            "the address must be the same as the address configured in the Dinky Application background url");

    private final Configuration<Boolean> dolphinschedulerEnable =
            key("dolphinscheduler.settings.enable")
                    .booleanType()
                    .defaultValue(false)
                    .note("Dolphinscheduler ON-OFF");

    private final Configuration<String> dolphinschedulerUrl =
            key("dolphinscheduler.settings.url")
                    .stringType()
                    .defaultValue("")
                    .note(
                            "The address must be the same as the address configured in the DolphinScheduler background , eg: http://127.0.0.1:12345/dolphinscheduler");
    private final Configuration<String> dolphinschedulerToken =
            key("dolphinscheduler.settings.token")
                    .stringType()
                    .defaultValue("")
                    .note(
                            "DolphinScheduler's Token , Please create a token in DolphinScheduler's Security Center -> Token Management, and modify the following configuration");
    private final Configuration<String> dolphinschedulerProjectName =
            key("dolphinscheduler.settings.projectName")
                    .stringType()
                    .defaultValue("Dinky")
                    .note("The project name specified in DolphinScheduler, case insensitive");

    private final Configuration<String> ldapUrl =
            key("ldap.settings.url").stringType().defaultValue("").note("ldap server address");

    private final Configuration<String> ldapUserDn =
            key("ldap.settings.userDn")
                    .stringType()
                    .defaultValue("")
                    .note("ldap login dn or username");

    private final Configuration<String> ldapUserPassword =
            key("ldap.settings.userPassword")
                    .stringType()
                    .defaultValue("")
                    .note("ldap login password");
    //    private final Configuration<Integer> ldapCountLimit =
    //            key("ldap.settings.countLimit")
    //                    .intType()
    //                    .defaultValue(0)
    //                    .note("");
    private final Configuration<Integer> ldapTimeLimit =
            key("ldap.settings.timeLimit")
                    .intType()
                    .defaultValue(30)
                    .note("ldap connection timeout");

    private final Configuration<String> ldapBaseDn =
            key("ldap.settings.baseDn").stringType().defaultValue("").note("ldap user base dn");

    private final Configuration<String> ldapFilter =
            key("ldap.settings.filter").stringType().defaultValue("").note("ldap user filter");

    private final Configuration<Boolean> ldapAutoload =
            key("ldap.settings.autoload")
                    .booleanType()
                    .defaultValue(true)
                    .note("Whether auto-mapping ldap users is enabled");

    private final Configuration<String> ldapDefaultTeant =
            key("ldap.settings.defaultTeant")
                    .stringType()
                    .defaultValue("DefaultTenant")
                    .note("ldap default default teant code");

    private final Configuration<String> ldapCastUsername =
            key("ldap.settings.castUsername").stringType().defaultValue("cn").note("");

    private final Configuration<String> ldapCastNickname =
            key("ldap.settings.castNickname").stringType().defaultValue("sn").note("");

    private final Configuration<Boolean> ldapEnable =
            key("ldap.settings.enable").booleanType().defaultValue(false).note("LDAP ON-OFF");

    private final Configuration<Boolean> metricsSysEnable =
            key("metrics.settings.sys.enable")
                    .booleanType()
                    .defaultValue(false)
                    .note("Is the collection system indicator enabled");

    private final Configuration<Integer> metricsSysGatherTiming =
            key("metrics.settings.sys.gatherTiming")
                    .intType()
                    .defaultValue(3000)
                    .note("System METRICS gather Timing (unit: ms)");
    private final Configuration<Integer> flinkMetricsGatherTiming =
            key("metrics.settings.flink.gatherTiming")
                    .intType()
                    .defaultValue(3000)
                    .note("FLINK METRICS gather Timing (unit: ms)");

    private final Configuration<Integer> flinkMetricsGatherTimeout =
            key("metrics.settings.flink.gatherTimeout")
                    .intType()
                    .defaultValue(1000)
                    .note("FLINK METRICS gather timeout (unit: ms)");

    private final Configuration<Boolean> resourcesEnable =
            key("resource.settings.enable").booleanType().defaultValue(true).note("是否启用");

    private final Configuration<String> resourcesUploadBasePath =
            key("resource.settings.upload.base.path")
                    .stringType()
                    .defaultValue("/dinky")
                    .note(
                            "resource store on HDFS/OSS path, resource file will store to this base path, self configuration, please make sure the directory exists on hdfs and have read write permissions. \"/dinky\" is recommended");
    private final Configuration<ResourcesModelEnum> resourcesModel =
            key("resource.settings.model")
                    .enumType(ResourcesModelEnum.class)
                    .defaultValue(ResourcesModelEnum.HDFS)
                    .note("存储模式：支持HDFS、OSS");

    private final Configuration<String> resourcesOssEndpoint =
            key("resource.settings.oss.endpoint")
                    .stringType()
                    .defaultValue("http://localhost:9000")
                    .note("对象存储服务的URL，例如：https://oss-cn-hangzhou.aliyuncs.com");

    private final Configuration<String> resourcesOssAccessKey =
            key("resource.settings.oss.accessKey")
                    .stringType()
                    .defaultValue("minioadmin")
                    .note("Access key就像用户ID，可以唯一标识你的账户");

    private final Configuration<String> resourcesOssSecretKey =
            key("resource.settings.oss.secretKey")
                    .stringType()
                    .defaultValue("minioadmin")
                    .note("Secret key是你账户的密码");

    private final Configuration<String> resourcesOssBucketName =
            key("resource.settings.oss.bucketName")
                    .stringType()
                    .defaultValue("dinky")
                    .note("默认的存储桶名称");
    private final Configuration<String> resourcesOssRegion =
            key("resource.settings.oss.region").stringType().defaultValue("").note("区域");
    private final Configuration<String> resourcesHdfsUser =
            key("resource.settings.hdfs.root.user")
                    .stringType()
                    .defaultValue("hdfs")
                    .note("HDFS操作用户名");
    private final Configuration<String> resourcesHdfsDefaultFS =
            key("resource.settings.hdfs.fs.defaultFS")
                    .stringType()
                    .defaultValue("file:///")
                    .note("HDFS defaultFS");

    /** Initialize after spring bean startup */
    public void initAfterBeanStarted() {
        if (StrUtil.isBlank(dinkyAddr.getDefaultValue())) {
            ReflectUtil.setFieldValue(dinkyAddr, "defaultValue", System.getProperty("dinkyAddr"));
        }
    }

    public void setConfiguration(String key, String value) {
        CONFIGURATION_LIST.stream()
                .filter(x -> x.getKey().equals(key))
                .forEach(
                        item -> {
                            if (value == null) {
                                item.setValue(item.getDefaultValue());
                                item.runParameterCheck();
                                item.runChangeEvent();
                                return;
                            }
                            if (!StrUtil.equals(Convert.toStr(item.getValue()), value)) {
                                item.setValue(value);
                                item.runParameterCheck();
                                item.runChangeEvent();
                            }
                        });
    }

    public void initSetConfiguration(Map<String, String> configMap) {
        CONFIGURATION_LIST.forEach(
                item -> {
                    if (!configMap.containsKey(item.getKey())) {
                        return;
                    }
                    final String value = configMap.get(item.getKey());
                    if (value == null) {
                        item.setValue(item.getDefaultValue());
                        return;
                    }
                    item.setValue(value);
                });
        CONFIGURATION_LIST.stream()
                .peek(Configuration::runParameterCheck)
                .forEach(Configuration::runChangeEvent);
    }

    public Map<String, List<Configuration<?>>> getAllConfiguration() {
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
