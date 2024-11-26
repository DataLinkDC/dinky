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

package org.dinky.job;

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.NetConstant;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.model.CustomConfig;
import org.dinky.executor.ExecutorConfig;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.gateway.model.FlinkClusterConfig;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.RestOptions;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.lang.Assert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * JobConfig
 *
 * @since 2021/6/27 18:45
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel(value = "JobConfig", description = "Configuration details of a job")
public class JobConfig {

    @ApiModelProperty(
            value = "Flink run mode",
            dataType = "String",
            example = "local standalone",
            notes = "Flink run mode")
    private String type;

    @ApiModelProperty(value = "Check Point", dataType = "Integer", example = "1", notes = "Check point for the task")
    private Integer checkpoint;

    @ApiModelProperty(value = "Save point strategy", dataType = "SavePointStrategy", notes = "Save point strategy")
    private SavePointStrategy savePointStrategy;

    @ApiModelProperty(value = "Save Point Path", dataType = "String", notes = "Save point path for the task")
    private String savePointPath;

    @ApiModelProperty(value = "Parallelism level", dataType = "Integer", example = "4", notes = "Parallelism level")
    private Integer parallelism;

    @ApiModelProperty(value = "Cluster ID", dataType = "Integer", example = "456", notes = "Cluster ID")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Cluster configuration ID",
            dataType = "Integer",
            example = "789",
            notes = "Cluster configuration ID")
    private Integer clusterConfigurationId;

    @ApiModelProperty(value = "Task JobLifeCycle", dataType = "Integer", example = "2", notes = "Task JobLifeCycle")
    private Integer step;

    @ApiModelProperty(
            value = "JSON configuration",
            dataType = "Map<String, String>",
            example = "{\"config1\": \"value1\", \"config2\": \"value2\"}",
            notes = "JSON configuration")
    private Map<String, String> configJson;

    @ApiModelProperty(
            value = "UDF configuration",
            dataType = "Map<String, String>",
            example = "{\"udf1\": \"value1\", \"udf2\": \"value2\"}",
            notes = "UDF (User-Defined Function) configuration")
    private Map<String, String> udfRefer;

    @ApiModelProperty(
            value = "Flag indicating whether to use the result",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use the result")
    private boolean useResult;

    @ApiModelProperty(
            value = "Flag indicating whether to use change log",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use change log")
    private boolean useChangeLog;

    @ApiModelProperty(
            value = "Flag indicating whether to use auto-cancel",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use auto-cancel")
    private boolean useAutoCancel;

    @ApiModelProperty(
            value = "Flag indicating whether to use remote execution",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use remote execution")
    private boolean useRemote;

    @ApiModelProperty(
            value = "Job manager address",
            dataType = "String",
            example = "localhost:8081",
            notes = "Job manager address")
    private String address;

    @ApiModelProperty(value = "Task ID", dataType = "Integer", example = "123", notes = "Task ID")
    private Integer taskId;

    @ApiModelProperty(
            value = "List of JAR files",
            dataType = "String[]",
            example = "[\"file1.jar\", \"file2.jar\"]",
            notes = "List of JAR files")
    private String[] jarFiles;

    @ApiModelProperty(
            value = "List of Python files",
            dataType = "String[]",
            example = "[\"script1.py\", \"script2.py\"]",
            notes = "List of Python files")
    private String[] pyFiles;

    @ApiModelProperty(value = "Name of the job", dataType = "String", example = "MyJob", notes = "Name of the job")
    private String jobName;

    @ApiModelProperty(
            value = "Flag indicating whether to use SQL fragment",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use SQL fragment")
    private boolean fragment;

    @ApiModelProperty(
            value = "Flag indicating whether to use statement set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use statement set")
    private boolean statementSet;

    @ApiModelProperty(
            value = "Flag indicating whether to use batch model",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use batch model")
    private boolean batchModel;

    @ApiModelProperty(
            value = "Maximum number of rows",
            dataType = "Integer",
            example = "1000",
            notes = "Maximum number of rows")
    private Integer maxRowNum;

    @ApiModelProperty(
            value = "Flag indicating whether to mock sink function",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to mock sink function")
    private boolean mockSinkFunction;

    @ApiModelProperty(
            value = "Flag indicating whether to be submission mode",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to be submission mode")
    private boolean isSubmissionMode;

    @ApiModelProperty(value = "Gateway configuration", dataType = "GatewayConfig", notes = "Gateway configuration")
    private GatewayConfig gatewayConfig;

    @ApiModelProperty(
            value = "Map of variables",
            dataType = "Map<String, String>",
            example = "{\"var1\": \"value1\", \"var2\": \"value2\"}",
            notes = "Map of variables")
    private Map<String, String> variables;

    public JobConfig() {
        this.configJson = new HashMap<>();
    }

    public void setAddress(String address) {
        if (GatewayType.LOCAL.equalsValue(type)
                && Asserts.isNotNull(configJson)
                && configJson.containsKey(RestOptions.PORT.key())) {
            int colonIndex = address.indexOf(':');
            if (colonIndex == -1) {
                this.address = address + NetConstant.COLON + configJson.get(RestOptions.PORT.key());
            } else {
                String port =
                        configJson.getOrDefault(RestOptions.BIND_PORT.key(), configJson.get(RestOptions.PORT.key()));
                this.address = address.replaceAll("(?<=:)\\d{0,6}$", port);
            }
        } else {
            this.address = address;
        }
    }

    public ExecutorConfig getExecutorSetting() {
        Map<String, String> config = new HashMap<>(32);
        if (GatewayType.isDeployCluster(type) && gatewayConfig != null && gatewayConfig.getFlinkConfig() != null) {
            config.putAll(gatewayConfig.getFlinkConfig().getConfiguration());
        } else if (Asserts.isNotNull(configJson)) {
            config.putAll(configJson);
        }
        return ExecutorConfig.build(
                type,
                address,
                checkpoint,
                parallelism,
                fragment,
                statementSet,
                batchModel,
                savePointPath,
                jobName,
                config,
                variables);
    }

    public void buildGatewayConfig(FlinkClusterConfig config) {
        FlinkConfig flinkConfig = config.getFlinkConfig();
        // Prioritize loading custom Flink configuration content in the cluster configuration
        for (CustomConfig customConfig : flinkConfig.getFlinkConfigList()) {
            Assert.notNull(customConfig.getName(), "Custom flink config has null key");
            Assert.notNull(customConfig.getValue(), "Custom flink config has null value");
            flinkConfig.getConfiguration().put(customConfig.getName(), customConfig.getValue());
        }
        // Load job configuration content afterwards
        flinkConfig.getConfiguration().putAll(getConfigJson());
        flinkConfig.getConfiguration().put(CoreOptions.DEFAULT_PARALLELISM.key(), String.valueOf(parallelism));
        flinkConfig.setJobName(getJobName());

        gatewayConfig = GatewayConfig.build(config);
        gatewayConfig.setTaskId(getTaskId());
        gatewayConfig.setType(GatewayType.get(getType()));
    }

    public void addGatewayConfig(Map<String, String> config) {
        if (Asserts.isNull(gatewayConfig)) {
            gatewayConfig = new GatewayConfig();
        }
        for (Map.Entry<String, String> entry : config.entrySet()) {
            gatewayConfig.getFlinkConfig().getConfiguration().put(entry.getKey(), entry.getValue());
        }
    }

    public void addGatewayConfig(Configuration config) {
        if (Asserts.isNull(gatewayConfig)) {
            gatewayConfig = new GatewayConfig();
        }
        gatewayConfig.getFlinkConfig().getConfiguration().putAll(config.toMap());
    }

    public boolean isUseRemote() {
        return useRemote || !GatewayType.LOCAL.equalsValue(type);
    }

    public void buildLocal() {
        type = GatewayType.LOCAL.getLongValue();
    }

    public static JobConfig buildPlanConfig() {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setType(GatewayType.LOCAL.getLongValue());
        return jobConfig;
    }
}
