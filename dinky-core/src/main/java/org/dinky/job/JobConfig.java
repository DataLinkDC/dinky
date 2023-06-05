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
import org.dinky.executor.ExecutorSetting;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.enums.SavePointStrategy;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.RestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * JobConfig
 *
 * @since 2021/6/27 18:45
 */
@Getter
@Setter
public class JobConfig {

    // flink run mode
    private String type;
    // task JobLifeCycle
    private Integer step;
    private boolean useResult;
    private boolean useChangeLog;
    private boolean useAutoCancel;
    private boolean useSession;
    private String session;
    private boolean useRemote;
    private Integer clusterId;
    private Integer clusterConfigurationId;
    private Integer jarId;
    private boolean isJarTask = false;
    private String address;
    private Integer taskId;
    private String[] jarFiles;
    private String[] pyFiles;
    private String jobName;
    private boolean useSqlFragment;
    private boolean useStatementSet;
    private boolean useBatchModel;
    private Integer maxRowNum;
    private Integer checkpoint;
    private Integer parallelism;
    private SavePointStrategy savePointStrategy;
    private String savePointPath;
    private GatewayConfig gatewayConfig;
    private Map<String, String> variables;
    private Map<String, String> config;

    public JobConfig() {
        this.config = new HashMap<String, String>();
    }

    public void setAddress(String address) {
        if (GatewayType.LOCAL.equalsValue(type)
                && Asserts.isNotNull(config)
                && config.containsKey(RestOptions.PORT.key())) {
            int colonIndex = address.indexOf(':');
            if (colonIndex == -1) {
                this.address = address + NetConstant.COLON + config.get(RestOptions.PORT.key());
            } else {
                this.address =
                        address.replaceAll("(?<=:)\\d{0,6}$", config.get(RestOptions.PORT.key()));
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
            Map<String, String> config) {
        this.type = type;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.parallelism = parallelism;
        this.config = config;
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
            Map<String, String> config) {
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
        this.config = config;
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
            Map<String, String> config,
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
        this.config = config;
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
            Map<String, String> config) {
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
        this.config = config;
    }

    public ExecutorSetting getExecutorSetting() {
        return new ExecutorSetting(
                checkpoint,
                parallelism,
                useSqlFragment,
                useStatementSet,
                useBatchModel,
                savePointPath,
                jobName,
                config);
    }

    public void buildGatewayConfig(Map<String, Object> config) {
        gatewayConfig = GatewayConfig.build(config);
        if (config.containsKey("flinkConfig")
                && Asserts.isNotNullMap((Map<String, String>) config.get("flinkConfig"))) {
            gatewayConfig
                    .getFlinkConfig()
                    .getConfiguration()
                    .put(CoreOptions.DEFAULT_PARALLELISM.key(), String.valueOf(parallelism));
        }
    }

    public void addGatewayConfig(List<Map<String, String>> configList) {
        if (Asserts.isNull(gatewayConfig)) {
            gatewayConfig = new GatewayConfig();
        }
        for (Map<String, String> item : configList) {
            if (Asserts.isNotNull(item)) {
                gatewayConfig
                        .getFlinkConfig()
                        .getConfiguration()
                        .put(item.get("key"), item.get("value"));
            }
        }
    }

    public void addGatewayConfig(Map<String, Object> config) {
        if (Asserts.isNull(gatewayConfig)) {
            gatewayConfig = new GatewayConfig();
        }
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            gatewayConfig
                    .getFlinkConfig()
                    .getConfiguration()
                    .put(entry.getKey(), (String) entry.getValue());
        }
    }

    public boolean isUseRemote() {
        return !GatewayType.LOCAL.equalsValue(type);
    }

    public void buildLocal() {
        type = GatewayType.LOCAL.getLongValue();
    }
}
