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
import org.dinky.executor.ExecutorConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.gateway.model.FlinkClusterConfig;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.RestOptions;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * JobConfig
 *
 * @since 2021/6/27 18:45
 */
@Getter
@Setter
@ApiModel(value = "JobConfig", description = "Configuration details of a job")
public class JobConfig {

    @ApiModelProperty(value = "Flink run mode", dataType = "String", example = "batch", notes = "Flink run mode")
    private String type;

    @ApiModelProperty(value = "Task JobLifeCycle", dataType = "Integer", example = "2", notes = "Task JobLifeCycle")
    private Integer step;

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
            value = "Flag indicating whether to use session",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use session")
    private boolean useSession;

    @ApiModelProperty(
            value = "Session information",
            dataType = "String",
            example = "session-123",
            notes = "Session information")
    private String session;

    @ApiModelProperty(
            value = "Flag indicating whether to use remote execution",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use remote execution")
    private boolean useRemote;

    @ApiModelProperty(value = "Cluster ID", dataType = "Integer", example = "456", notes = "Cluster ID")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Cluster configuration ID",
            dataType = "Integer",
            example = "789",
            notes = "Cluster configuration ID")
    private Integer clusterConfigurationId;

    @ApiModelProperty(value = "JAR file ID", dataType = "Integer", example = "101", notes = "JAR file ID")
    private Integer jarId;

    @ApiModelProperty(
            value = "Flag indicating whether it's a JAR task",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether it's a JAR task")
    private boolean isJarTask;

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
    private boolean useSqlFragment;

    @ApiModelProperty(
            value = "Flag indicating whether to use statement set",
            dataType = "boolean",
            example = "false",
            notes = "Flag indicating whether to use statement set")
    private boolean useStatementSet;

    @ApiModelProperty(
            value = "Flag indicating whether to use batch model",
            dataType = "boolean",
            example = "true",
            notes = "Flag indicating whether to use batch model")
    private boolean useBatchModel;

    @ApiModelProperty(
            value = "Maximum number of rows",
            dataType = "Integer",
            example = "1000",
            notes = "Maximum number of rows")
    private Integer maxRowNum;

    @ApiModelProperty(
            value = "Checkpoint interval",
            dataType = "Integer",
            example = "5000",
            notes = "Checkpoint interval")
    private Integer checkpoint;

    @ApiModelProperty(value = "Parallelism level", dataType = "Integer", example = "4", notes = "Parallelism level")
    private Integer parallelism;

    @ApiModelProperty(value = "Save point strategy", dataType = "SavePointStrategy", notes = "Save point strategy")
    private SavePointStrategy savePointStrategy;

    @ApiModelProperty(
            value = "Path for save points",
            dataType = "String",
            example = "/savepoints",
            notes = "Path for save points")
    private String savePointPath;

    @ApiModelProperty(value = "Gateway configuration", dataType = "GatewayConfig", notes = "Gateway configuration")
    private GatewayConfig gatewayConfig;

    @ApiModelProperty(
            value = "Map of variables",
            dataType = "Map<String, String>",
            example = "{\"var1\": \"value1\", \"var2\": \"value2\"}",
            notes = "Map of variables")
    private Map<String, String> variables;

    @ApiModelProperty(
            value = "JSON configuration",
            dataType = "Map<String, String>",
            example = "{\"config1\": \"value1\", \"config2\": \"value2\"}",
            notes = "JSON configuration")
    private Map<String, String> configJson;

    public JobConfig() {
        this.configJson = new HashMap<String, String>();
    }

    public void setAddress(String address) {
        if (GatewayType.LOCAL.equalsValue(type)
                && Asserts.isNotNull(configJson)
                && configJson.containsKey(RestOptions.PORT.key())) {
            int colonIndex = address.indexOf(':');
            if (colonIndex == -1) {
                this.address = address + NetConstant.COLON + configJson.get(RestOptions.PORT.key());
            } else {
                this.address = address.replaceAll("(?<=:)\\d{0,6}$", configJson.get(RestOptions.PORT.key()));
            }
        } else {
            this.address = address;
        }
    }

    public JobConfig(
            String type,
            boolean useSession,
            boolean useRemote,
            boolean useSqlFragment,
            boolean useStatementSet,
            Integer parallelism,
            Map<String, String> configJson) {
        this.type = type;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.parallelism = parallelism;
        this.configJson = configJson;
    }

    public JobConfig(
            String type,
            boolean useResult,
            boolean useChangeLog,
            boolean useAutoCancel,
            boolean useSession,
            String session,
            Integer clusterId,
            Integer clusterConfigurationId,
            Integer jarId,
            Integer taskId,
            String jobName,
            boolean useSqlFragment,
            boolean useStatementSet,
            boolean useBatchModel,
            Integer maxRowNum,
            Integer checkpoint,
            Integer parallelism,
            Integer savePointStrategyValue,
            String savePointPath,
            Map<String, String> variables,
            Map<String, String> configJson) {
        this.type = type;
        this.useResult = useResult;
        this.useChangeLog = useChangeLog;
        this.useAutoCancel = useAutoCancel;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = true;
        this.clusterId = clusterId;
        this.clusterConfigurationId = clusterConfigurationId;
        this.jarId = jarId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.useBatchModel = useBatchModel;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointStrategy = SavePointStrategy.get(savePointStrategyValue);
        this.savePointPath = savePointPath;
        this.variables = variables;
        this.configJson = configJson;
    }

    public JobConfig(
            String type,
            boolean useResult,
            boolean useChangeLog,
            boolean useAutoCancel,
            boolean useSession,
            String session,
            boolean useRemote,
            String address,
            String jobName,
            boolean useSqlFragment,
            boolean useStatementSet,
            Integer maxRowNum,
            Integer checkpoint,
            Integer parallelism,
            Integer savePointStrategyValue,
            String savePointPath,
            Map<String, String> configJson,
            GatewayConfig gatewayConfig) {
        this.type = type;
        this.useResult = useResult;
        this.useChangeLog = useChangeLog;
        this.useAutoCancel = useAutoCancel;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointStrategy = SavePointStrategy.get(savePointStrategyValue);
        this.savePointPath = savePointPath;
        this.configJson = configJson;
        this.gatewayConfig = gatewayConfig;
        setAddress(address);
    }

    public JobConfig(
            String type,
            boolean useResult,
            boolean useSession,
            String session,
            boolean useRemote,
            Integer clusterId,
            Integer maxRowNum) {
        this.type = type;
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.maxRowNum = maxRowNum;
    }

    public JobConfig(
            String type,
            Integer step,
            boolean useResult,
            boolean useSession,
            boolean useRemote,
            Integer clusterId,
            Integer clusterConfigurationId,
            Integer jarId,
            Integer taskId,
            String jobName,
            boolean useSqlFragment,
            boolean useStatementSet,
            boolean useBatchModel,
            Integer checkpoint,
            Integer parallelism,
            Integer savePointStrategyValue,
            String savePointPath,
            Map<String, String> configJson,
            boolean isJarTask) {
        this.type = type;
        this.step = step;
        this.useResult = useResult;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.clusterConfigurationId = clusterConfigurationId;
        this.jarId = jarId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.useBatchModel = useBatchModel;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointStrategy = SavePointStrategy.get(savePointStrategyValue);
        this.savePointPath = savePointPath;
        this.configJson = configJson;
        this.isJarTask = isJarTask;
    }

    public ExecutorConfig getExecutorSetting() {
        return ExecutorConfig.build(
                address,
                checkpoint,
                parallelism,
                useSqlFragment,
                useStatementSet,
                useBatchModel,
                savePointPath,
                jobName,
                configJson,
                variables);
    }

    public void buildGatewayConfig(FlinkClusterConfig config) {
        gatewayConfig = GatewayConfig.build(config);
        gatewayConfig.setTaskId(getTaskId());
        gatewayConfig.getFlinkConfig().setJobName(getJobName());
        gatewayConfig
                .getFlinkConfig()
                .getConfiguration()
                .put(CoreOptions.DEFAULT_PARALLELISM.key(), String.valueOf(parallelism));
        setUseRemote(false); // todo: remove
    }

    public void addGatewayConfig(Map<String, Object> config) {
        if (Asserts.isNull(gatewayConfig)) {
            gatewayConfig = new GatewayConfig();
        }
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            gatewayConfig.getFlinkConfig().getConfiguration().put(entry.getKey(), (String) entry.getValue());
        }
    }

    public boolean isUseRemote() {
        return !GatewayType.LOCAL.equalsValue(type);
    }

    public void buildLocal() {
        type = GatewayType.LOCAL.getLongValue();
    }
}
