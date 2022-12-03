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

package com.dlink.job;

import com.dlink.assertion.Asserts;
import com.dlink.constant.NetConstant;
import com.dlink.executor.ExecutorSetting;
import com.dlink.function.data.model.UDF;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.config.ClusterConfig;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.session.SessionConfig;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * JobConfig
 *
 * @author wenmo
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
    private List<UDF> udfList;
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
    }

    public void setAddress(String address) {
        if (GatewayType.LOCAL.equalsValue(type) && Asserts.isNotNull(config)
                && config.containsKey(RestOptions.PORT.key())) {
            this.address = address + NetConstant.COLON + config.get(RestOptions.PORT.key());
        } else {
            this.address = address;
        }
    }

    public JobConfig(String type, boolean useSession, boolean useRemote, boolean useSqlFragment,
            boolean useStatementSet, Integer parallelism, Map<String, String> config) {
        this.type = type;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.parallelism = parallelism;
        this.config = config;
    }

    public JobConfig(String type, boolean useResult, boolean useChangeLog, boolean useAutoCancel, boolean useSession,
            String session, Integer clusterId,
            Integer clusterConfigurationId, Integer jarId, Integer taskId, String jobName,
            boolean useSqlFragment,
            boolean useStatementSet, boolean useBatchModel, Integer maxRowNum, Integer checkpoint,
            Integer parallelism,
            Integer savePointStrategyValue, String savePointPath, Map<String, String> variables,
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

    public JobConfig(String type, boolean useResult, boolean useChangeLog, boolean useAutoCancel, boolean useSession,
            String session, boolean useRemote, String address,
            String jobName, boolean useSqlFragment,
            boolean useStatementSet, Integer maxRowNum, Integer checkpoint, Integer parallelism,
            Integer savePointStrategyValue, String savePointPath, Map<String, String> config,
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

    public JobConfig(String type, boolean useResult, boolean useSession, String session, boolean useRemote,
            Integer clusterId, Integer maxRowNum) {
        this.type = type;
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.maxRowNum = maxRowNum;
    }

    public JobConfig(String type, Integer step, boolean useResult, boolean useSession, boolean useRemote,
            Integer clusterId,
            Integer clusterConfigurationId, Integer jarId, Integer taskId, String jobName,
            boolean useSqlFragment,
            boolean useStatementSet, boolean useBatchModel, Integer checkpoint, Integer parallelism,
            Integer savePointStrategyValue,
            String savePointPath, Map<String, String> config) {
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
        return new ExecutorSetting(checkpoint, parallelism, useSqlFragment, useStatementSet, useBatchModel,
                savePointPath, jobName, config);
    }

    public void setSessionConfig(SessionConfig sessionConfig) {
        if (sessionConfig != null) {
            address = sessionConfig.getAddress();
            clusterId = sessionConfig.getClusterId();
            useRemote = sessionConfig.isUseRemote();
        }
    }

    public void buildGatewayConfig(Map<String, Object> config) {
        gatewayConfig = new GatewayConfig();
        if (config.containsKey("hadoopConfigPath")) {
            gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString(),
                    config.get("flinkLibPath").toString(),
                    config.get("hadoopConfigPath").toString()));
        } else {
            gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString()));
        }
        AppConfig appConfig = new AppConfig();
        if (config.containsKey("userJarPath") && Asserts.isNotNullString((String) config.get("userJarPath"))) {
            appConfig.setUserJarPath(config.get("userJarPath").toString());
            if (config.containsKey("userJarMainAppClass")
                    && Asserts.isNotNullString((String) config.get("userJarMainAppClass"))) {
                appConfig.setUserJarMainAppClass(config.get("userJarMainAppClass").toString());
            }
            if (config.containsKey("userJarParas") && Asserts.isNotNullString((String) config.get("userJarParas"))) {
                // There may be multiple spaces between the parameter and value during user input,
                // which will directly lead to a parameter passing error and needs to be eliminated
                String[] temp = config.get("userJarParas").toString().split(" ");
                List<String> paraSplit = new ArrayList<>();
                for (String s : temp) {
                    if (!TextUtils.isEmpty(s.trim())) {
                        paraSplit.add(s);
                    }
                }
                appConfig.setUserJarParas(paraSplit.toArray(new String[0]));
            }
            gatewayConfig.setAppConfig(appConfig);
        }
        if (config.containsKey("flinkConfig")
                && Asserts.isNotNullMap((Map<String, String>) config.get("flinkConfig"))) {
            gatewayConfig.setFlinkConfig(FlinkConfig.build((Map<String, String>) config.get("flinkConfig")));
            gatewayConfig.getFlinkConfig().getConfiguration().put(CoreOptions.DEFAULT_PARALLELISM.key(),
                    String.valueOf(parallelism));
        }
        if (config.containsKey("kubernetesConfig")) {
            Map<String, String> kubernetesConfig = (Map<String, String>) config.get("kubernetesConfig");
            gatewayConfig.getFlinkConfig().getConfiguration().putAll(kubernetesConfig);
        }
        // at present only k8s task have this
        if (config.containsKey("taskCustomConfig")) {
            Map<String, Map<String, String>> taskCustomConfig = (Map<String, Map<String, String>>) config
                    .get("taskCustomConfig");
            if (taskCustomConfig.containsKey("kubernetesConfig")) {
                gatewayConfig.getFlinkConfig().getConfiguration().putAll(taskCustomConfig.get("kubernetesConfig"));
            }
            if (taskCustomConfig.containsKey("flinkConfig")) {
                gatewayConfig.getFlinkConfig().getConfiguration().putAll(taskCustomConfig.get("flinkConfig"));
            }
        }
    }

    public void addGatewayConfig(List<Map<String, String>> configList) {
        if (Asserts.isNull(gatewayConfig)) {
            gatewayConfig = new GatewayConfig();
        }
        for (Map<String, String> item : configList) {
            if (Asserts.isNotNull(item)) {
                gatewayConfig.getFlinkConfig().getConfiguration().put(item.get("key"), item.get("value"));
            }
        }
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
