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

import org.dinky.context.EngineContextHolder;
import org.dinky.data.constant.CommonConstant;
import org.dinky.data.constant.DirConstant;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.TaskOwnerAlertStrategyEnum;
import org.dinky.data.enums.TaskOwnerLockStrategyEnum;
import org.dinky.data.properties.OssProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
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

    public static Configuration.OptionBuilder key(Status status) {
        return new Configuration.OptionBuilder(status.getKey());
    }

    private static final List<Configuration<?>> CONFIGURATION_LIST = Arrays.stream(
                    ReflectUtil.getFields(SystemConfiguration.class, f -> f.getType() == Configuration.class))
            .map(f -> (Configuration<?>) ReflectUtil.getFieldValue(systemConfiguration, f))
            .collect(Collectors.toList());

    private final Configuration<Boolean> useRestAPI = key(Status.SYS_FLINK_SETTINGS_USERESTAPI)
            .booleanType()
            .defaultValue(true)
            .note(Status.SYS_FLINK_SETTINGS_USERESTAPI_NOTE);

    private final Configuration<Integer> jobIdWait = key(Status.SYS_FLINK_SETTINGS_JOBIDWAIT)
            .intType()
            .defaultValue(30)
            .note(Status.SYS_FLINK_SETTINGS_JOBIDWAIT_NOTE);

    private final Configuration<Boolean> useFlinkHistoryServer = key(Status.SYS_FLINK_SETTINGS_USE_FLINK_HISTORY_SERVER)
            .booleanType()
            .defaultValue(true)
            .note(Status.SYS_FLINK_SETTINGS_USE_FLINK_HISTORY_SERVER_NOTE);
    private final Configuration<Integer> flinkHistoryServerPort =
            key(Status.SYS_FLINK_SETTINGS_FLINK_HISTORY_SERVER_PORT)
                    .intType()
                    .defaultValue(8082)
                    .note(Status.SYS_FLINK_SETTINGS_FLINK_HISTORY_SERVER_PORT_NOTE);
    private final Configuration<Integer> flinkHistoryServerArchiveRefreshInterval =
            key(Status.SYS_FLINK_SETTINGS_FLINK_HISTORY_SERVER_ARCHIVE_REFRESH_INTERVAL)
                    .intType()
                    .defaultValue(5000)
                    .note(Status.SYS_FLINK_SETTINGS_FLINK_HISTORY_SERVER_ARCHIVE_REFRESH_INTERVAL_NOTE);

    private final Configuration<String> mavenSettings = key(Status.SYS_MAVEN_SETTINGS_SETTINGSFILEPATH)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_MAVEN_SETTINGS_SETTINGSFILEPATH_NOTE);

    private final Configuration<String> mavenRepository = key(Status.SYS_MAVEN_SETTINGS_REPOSITORY)
            .stringType()
            .defaultValue("https://maven.aliyun.com/nexus/content/repositories/central")
            .note(Status.SYS_MAVEN_SETTINGS_REPOSITORY_NOTE);

    private final Configuration<String> mavenRepositoryUser = key(Status.SYS_MAVEN_SETTINGS_REPOSITORYUSER)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_MAVEN_SETTINGS_REPOSITORYUSER_NOTE);

    private final Configuration<String> mavenRepositoryPassword = key(Status.SYS_MAVEN_SETTINGS_REPOSITORYPASSWORD)
            .stringType()
            .defaultValue("")
            .desensitizedHandler(DesensitizedUtil::password)
            .note(Status.SYS_MAVEN_SETTINGS_REPOSITORYPASSWORD_NOTE);

    private final Configuration<String> pythonHome = key(Status.SYS_ENV_SETTINGS_PYTHONHOME)
            .stringType()
            .defaultValue("python3")
            .note(Status.SYS_ENV_SETTINGS_PYTHONHOME_NOTE);
    private final Configuration<String> dinkyAddr = key(Status.SYS_ENV_SETTINGS_DINKYADDR)
            .stringType()
            .defaultValue(System.getProperty("dinkyAddr"))
            .note(Status.SYS_ENV_SETTINGS_DINKYADDR_NOTE);
    private final Configuration<String> dinkyToken = key(Status.SYS_ENV_SETTINGS_DINKYTOKEN)
            .stringType()
            .defaultValue("efda1551-7958-4e0f-80a8-dfd107df3e38")
            .note(Status.SYS_ENV_SETTINGS_DINKYTOKEN_NOTE);

    private final Configuration<Integer> jobReSendDiffSecond = key(Status.SYS_ENV_SETTINGS_JOB_RESEND_DIFF_SECOND)
            .intType()
            .defaultValue(60)
            .note(Status.SYS_ENV_SETTINGS_JOB_RESEND_DIFF_SECOND_NOTE);

    private final Configuration<Integer> diffMinuteMaxSendCount =
            key(Status.SYS_ENV_SETTINGS_DIFF_MINUTE_MAX_SEND_COUNT)
                    .intType()
                    .defaultValue(2)
                    .note(Status.SYS_ENV_SETTINGS_DIFF_MINUTE_MAX_SEND_COUNT_NOTE);

    private final Configuration<Integer> jobMaxRetainCount = key(Status.SYS_ENV_SETTINGS_MAX_RETAIN_COUNT)
            .intType()
            .defaultValue(10)
            .note(Status.SYS_ENV_SETTINGS_MAX_RETAIN_COUNT_NOTE);

    private final Configuration<Integer> jobMaxRetainDays = key(Status.SYS_ENV_SETTINGS_MAX_RETAIN_DAYS)
            .intType()
            .defaultValue(30)
            .note(Status.SYS_ENV_SETTINGS_MAX_RETAIN_DAYS_NOTE);

    // the default value is the same as the default value of the expressionVariable
    private final Configuration<String> expressionVariable = key(Status.SYS_ENV_SETTINGS_EXPRESSION_VARIABLE)
            .stringType()
            .defaultValue(CommonConstant.DEFAULT_EXPRESSION_VARIABLES)
            .note(Status.SYS_ENV_SETTINGS_EXPRESSION_VARIABLE_NOTE);

    private final Configuration<TaskOwnerLockStrategyEnum> taskOwnerLockStrategy =
            key(Status.SYS_ENV_SETTINGS_TASK_OWNER_LOCK_STRATEGY)
                    .enumType(TaskOwnerLockStrategyEnum.class)
                    .defaultValue(TaskOwnerLockStrategyEnum.ALL)
                    .note(Status.SYS_ENV_SETTINGS_TASK_OWNER_LOCK_STRATEGY_NOTE);

    private final Configuration<TaskOwnerAlertStrategyEnum> taskOwnerAlertStrategy =
            key(Status.SYS_ENV_SETTINGS_TASK_OWNER_ALERT_STRATEGY)
                    .enumType(TaskOwnerAlertStrategyEnum.class)
                    .defaultValue(TaskOwnerAlertStrategyEnum.NONE)
                    .note(Status.SYS_ENV_SETTINGS_TASK_OWNER_ALERT_STRATEGY_NOTE);

    private final Configuration<Boolean> dolphinschedulerEnable = key(Status.SYS_DOLPHINSCHEDULER_SETTINGS_ENABLE)
            .booleanType()
            .defaultValue(false)
            .note(Status.SYS_DOLPHINSCHEDULER_SETTINGS_ENABLE_NOTE);

    private final Configuration<String> dolphinschedulerUrl = key(Status.SYS_DOLPHINSCHEDULER_SETTINGS_URL)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_DOLPHINSCHEDULER_SETTINGS_URL_NOTE);
    private final Configuration<String> dolphinschedulerToken = key(Status.SYS_DOLPHINSCHEDULER_SETTINGS_TOKEN)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_DOLPHINSCHEDULER_SETTINGS_TOKEN_NOTE);
    private final Configuration<String> dolphinschedulerProjectName =
            key(Status.SYS_DOLPHINSCHEDULER_SETTINGS_PROJECTNAME)
                    .stringType()
                    .defaultValue("Dinky")
                    .note(Status.SYS_DOLPHINSCHEDULER_SETTINGS_PROJECTNAME_NOTE);

    private final Configuration<String> ldapUrl =
            key(Status.SYS_LDAP_SETTINGS_URL).stringType().defaultValue("").note(Status.SYS_LDAP_SETTINGS_URL_NOTE);

    private final Configuration<String> ldapUserDn = key(Status.SYS_LDAP_SETTINGS_USERDN)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_LDAP_SETTINGS_USERDN_NOTE);

    private final Configuration<String> ldapUserPassword = key(Status.SYS_LDAP_SETTINGS_USERPASSWORD)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_LDAP_SETTINGS_USERPASSWORD_NOTE);

    private final Configuration<Integer> ldapTimeLimit = key(Status.SYS_LDAP_SETTINGS_TIMELIMIT)
            .intType()
            .defaultValue(30)
            .note(Status.SYS_LDAP_SETTINGS_TIMELIMIT_NOTE);

    private final Configuration<String> ldapBaseDn = key(Status.SYS_LDAP_SETTINGS_BASEDN)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_LDAP_SETTINGS_BASEDN_NOTE);

    private final Configuration<String> ldapCastUsername = key(Status.SYS_LDAP_SETTINGS_CASTUSERNAME)
            .stringType()
            .defaultValue("cn")
            .note(Status.SYS_LDAP_SETTINGS_CASTUSERNAME_NOTE);

    private final Configuration<String> ldapCastNickname = key(Status.SYS_LDAP_SETTINGS_CASTNICKNAME)
            .stringType()
            .defaultValue("sn")
            .note(Status.SYS_LDAP_SETTINGS_CASTNICKNAME_NOTE);

    private final Configuration<String> ldapFilter = key(Status.SYS_LDAP_SETTINGS_FILTER)
            .stringType()
            .defaultValue("(&(objectClass=inetOrgPerson))")
            .note(Status.SYS_LDAP_SETTINGS_FILTER_NOTE);

    private final Configuration<Boolean> ldapAutoload = key(Status.SYS_LDAP_SETTINGS_AUTOLOAD)
            .booleanType()
            .defaultValue(true)
            .note(Status.SYS_LDAP_SETTINGS_AUTOLOAD_NOTE);

    private final Configuration<String> ldapDefaultTeant = key(Status.SYS_LDAP_SETTINGS_DEFAULTTEANT)
            .stringType()
            .defaultValue("DefaultTenant")
            .note(Status.SYS_LDAP_SETTINGS_DEFAULTTEANT_NOTE);

    private final Configuration<Boolean> ldapEnable = key(Status.SYS_LDAP_SETTINGS_ENABLE)
            .booleanType()
            .defaultValue(false)
            .note(Status.SYS_LDAP_SETTINGS_ENABLE_NOTE);

    private final Configuration<Boolean> metricsSysEnable = key(Status.SYS_METRICS_SETTINGS_SYS_ENABLE)
            .booleanType()
            .defaultValue(false)
            .note(Status.SYS_METRICS_SETTINGS_SYS_ENABLE_NOTE);

    private final Configuration<Integer> metricsSysGatherTiming = key(Status.SYS_METRICS_SETTINGS_SYS_GATHERTIMING)
            .intType()
            .defaultValue(3000)
            .note(Status.SYS_METRICS_SETTINGS_SYS_GATHERTIMING_NOTE);
    private final Configuration<Integer> flinkMetricsGatherTiming = key(Status.SYS_METRICS_SETTINGS_FLINK_GATHERTIMING)
            .intType()
            .defaultValue(3000)
            .note(Status.SYS_METRICS_SETTINGS_FLINK_GATHERTIMING_NOTE);

    private final Configuration<Integer> flinkMetricsGatherTimeout =
            key(Status.SYS_METRICS_SETTINGS_FLINK_GATHERTIMEOUT)
                    .intType()
                    .defaultValue(1000)
                    .note(Status.SYS_METRICS_SETTINGS_FLINK_GATHERTIMEOUT_NOTE);

    private final Configuration<Boolean> resourcesEnable = key(Status.SYS_RESOURCE_SETTINGS_ENABLE)
            .booleanType()
            .defaultValue(true)
            .note(Status.SYS_RESOURCE_SETTINGS_ENABLE_NOTE);

    private final Configuration<Boolean> physicalDeletion = key(Status.SYS_RESOURCE_SETTINGS_PHYSICAL_DELETION)
            .booleanType()
            .defaultValue(false)
            .note(Status.SYS_RESOURCE_SETTINGS_PHYSICAL_DELETION_NOTE);

    private final Configuration<ResourcesModelEnum> resourcesModel = key(Status.SYS_RESOURCE_SETTINGS_MODEL)
            .enumType(ResourcesModelEnum.class)
            .defaultValue(ResourcesModelEnum.LOCAL)
            .note(Status.SYS_RESOURCE_SETTINGS_MODEL_NOTE);

    private final Configuration<String> resourcesUploadBasePath = key(Status.SYS_RESOURCE_SETTINGS_UPLOAD_BASE_PATH)
            .stringType()
            .defaultValue("/dinky")
            .note(Status.SYS_RESOURCE_SETTINGS_UPLOAD_BASE_PATH_NOTE);

    private final Configuration<String> resourcesOssEndpoint = key(Status.SYS_RESOURCE_SETTINGS_OSS_ENDPOINT)
            .stringType()
            .defaultValue("http://localhost:9000")
            .note(Status.SYS_RESOURCE_SETTINGS_OSS_ENDPOINT_NOTE);

    private final Configuration<String> resourcesOssAccessKey = key(Status.SYS_RESOURCE_SETTINGS_OSS_ACCESSKEY)
            .stringType()
            .defaultValue("minioadmin")
            .note(Status.SYS_RESOURCE_SETTINGS_OSS_ACCESSKEY_NOTE);

    private final Configuration<String> resourcesOssSecretKey = key(Status.SYS_RESOURCE_SETTINGS_OSS_SECRETKEY)
            .stringType()
            .defaultValue("minioadmin")
            .note(Status.SYS_RESOURCE_SETTINGS_OSS_SECRETKEY_NOTE);

    private final Configuration<String> resourcesOssBucketName = key(Status.SYS_RESOURCE_SETTINGS_OSS_BUCKETNAME)
            .stringType()
            .defaultValue("dinky")
            .note(Status.SYS_RESOURCE_SETTINGS_OSS_BUCKETNAME_NOTE);
    private final Configuration<String> resourcesOssRegion = key(Status.SYS_RESOURCE_SETTINGS_OSS_REGION)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_RESOURCE_SETTINGS_OSS_REGION_NOTE);
    private final Configuration<String> resourcesHdfsUser = key(Status.SYS_RESOURCE_SETTINGS_HDFS_ROOT_USER)
            .stringType()
            .defaultValue("hdfs")
            .note(Status.SYS_RESOURCE_SETTINGS_HDFS_ROOT_USER_NOTE);
    private final Configuration<String> resourcesHdfsDefaultFS = key(Status.SYS_RESOURCE_SETTINGS_HDFS_FS_DEFAULTFS)
            .stringType()
            .defaultValue("file:///")
            .note(Status.SYS_RESOURCE_SETTINGS_HDFS_FS_DEFAULTFS_NOTE);
    private final Configuration<String> resourcesHdfsCoreSite = key(Status.SYS_RESOURCE_SETTINGS_HDFS_CORE_SITE)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_RESOURCE_SETTINGS_HDFS_CORE_SITE_NOTE);
    private final Configuration<String> resourcesHdfsHdfsSite = key(Status.SYS_RESOURCE_SETTINGS_HDFS_HDFS_SITE)
            .stringType()
            .defaultValue("")
            .note(Status.SYS_RESOURCE_SETTINGS_HDFS_HDFS_SITE_NOTE);
    private final Configuration<Boolean> resourcesPathStyleAccess = key(Status.SYS_RESOURCE_SETTINGS_PATH_STYLE_ACCESS)
            .booleanType()
            .defaultValue(true)
            .note(Status.SYS_RESOURCE_SETTINGS_PATH_STYLE_ACCESS_NOTE);

    /**
     * Initialize after spring bean startup
     */
    public void initAfterBeanStarted() {
        if (StrUtil.isBlank(dinkyAddr.getDefaultValue())) {
            ReflectUtil.setFieldValue(dinkyAddr, "defaultValue", System.getProperty("dinkyAddr"));
        }
    }

    public void setConfiguration(String key, String value) {
        CONFIGURATION_LIST.stream().filter(x -> x.getKey().equals(key)).forEach(item -> {
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
        CONFIGURATION_LIST.forEach(item -> {
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
        CONFIGURATION_LIST.stream().peek(Configuration::runParameterCheck).forEach(Configuration::runChangeEvent);
    }

    public void initExpressionVariableList(Map<String, String> configMap) {
        CONFIGURATION_LIST.forEach(item -> {
            if (item.getKey().equals(expressionVariable.getKey())) {
                EngineContextHolder.loadExpressionVariableClass(configMap.get(item.getKey()));
            }
        });
    }

    public Map<String, List<Configuration<?>>> getAllConfiguration() {
        Map<String, List<Configuration<?>>> data = new TreeMap<>();
        for (Configuration<?> item : CONFIGURATION_LIST) {
            final String key = item.getKey();
            String k = StrUtil.split(key, ".").get(1);
            Opt.ofBlankAble(k).ifPresent(name -> {
                item.setName(Status.findMessageByKey(item.getKey()));
                item.setNote(Status.findMessageByKey(item.getNoteKey()));
                data.computeIfAbsent(k, x -> new ArrayList<>());
                data.get(k).add(item);
            });
        }
        return data;
    }

    public boolean isUseRestAPI() {
        return useRestAPI.getValue();
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

    public OssProperties getOssProperties() {
        return OssProperties.builder()
                .enable(true)
                .endpoint(resourcesOssEndpoint.getValue())
                .accessKey(resourcesOssAccessKey.getValue())
                .secretKey(resourcesOssSecretKey.getValue())
                .bucketName(resourcesOssBucketName.getValue())
                .region(resourcesOssRegion.getValue())
                .pathStyleAccess(resourcesPathStyleAccess.getValue())
                .build();
    }

    public TaskOwnerLockStrategyEnum getTaskOwnerLockStrategy() {
        return taskOwnerLockStrategy.getValue();
    }

    public static final String FLINK_JOB_ARCHIVE = "rs:/tmp/flink-job-archive";

    public Map<String, String> getFlinkHistoryServerConfiguration() {
        Map<String, String> config = new HashMap<>();
        if (useFlinkHistoryServer.getValue()) {
            config.put(
                    "historyserver.web.port", flinkHistoryServerPort.getValue().toString());
            config.put(
                    "historyserver.archive.fs.refresh-interval",
                    flinkHistoryServerArchiveRefreshInterval.getValue().toString());
            config.put(
                    "historyserver.web.tmpdir",
                    FileUtil.file(DirConstant.getTempRootDir(), "flink-job-archive")
                            .getAbsolutePath());
            config.put("historyserver.archive.fs.dir", FLINK_JOB_ARCHIVE);
        }
        return config;
    }
}
