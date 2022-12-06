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
import com.dlink.config.Docker;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.function.constant.PathConstant;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.ClusterConfig;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.result.TestResult;
import com.dlink.job.JobManager;
import com.dlink.mapper.ClusterConfigurationMapper;
import com.dlink.model.ClusterConfiguration;
import com.dlink.service.ClusterConfigurationService;
import com.dlink.utils.DockerClientUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.io.FileUtil;

/**
 * ClusterConfigServiceImpl
 *
 * @author wenmo
 * @since 2021/11/6 20:54
 */
@Service
public class ClusterConfigurationServiceImpl extends SuperServiceImpl<ClusterConfigurationMapper, ClusterConfiguration> implements ClusterConfigurationService {

    @Value("classpath:DinkyFlinkDockerfile")
    org.springframework.core.io.Resource dockerfileResource;

    @Override
    public ClusterConfiguration getClusterConfigById(Integer id) {
        ClusterConfiguration clusterConfiguration = baseMapper.selectById(id);
        clusterConfiguration.parseConfig();
        return clusterConfiguration;
    }

    @Override
    public List<ClusterConfiguration> listEnabledAll() {
        return this.list(new QueryWrapper<ClusterConfiguration>().eq("enabled", 1));
    }

    @Override
    public Map getGatewayConfig(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getClusterConfigById(id);
        return clusterConfiguration.getConfig();
    }

    @Override
    public TestResult testGateway(ClusterConfiguration clusterConfiguration) {
        clusterConfiguration.parseConfig();
        Map<String, Object> config = clusterConfiguration.getConfig();
        GatewayConfig gatewayConfig = new GatewayConfig();
        if (config.containsKey("hadoopConfigPath")) {
            gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString(),
                config.get("flinkLibPath").toString(),
                config.get("hadoopConfigPath").toString()));
        } else {
            gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString()));
        }
        if (config.containsKey("flinkConfig")) {
            gatewayConfig.setFlinkConfig(FlinkConfig.build((Map<String, String>) config.get("flinkConfig")));
        }
        if (Asserts.isEqualsIgnoreCase(clusterConfiguration.getType(), "Yarn")) {
            gatewayConfig.setType(GatewayType.YARN_APPLICATION);
        } else if (Asserts.isEqualsIgnoreCase(clusterConfiguration.getType(), "Kubernetes")) {
            gatewayConfig.setType(GatewayType.KUBERNETES_APPLICATION);
            Map kubernetesConfig = (Map) config.get("kubernetesConfig");
            if (kubernetesConfig.containsKey("kubernetes.namespace")) {
                gatewayConfig.getFlinkConfig().getConfiguration().put("kubernetes.namespace", kubernetesConfig.get("kubernetes.namespace").toString());
            }
            if (kubernetesConfig.containsKey("kubernetes.cluster-id")) {
                gatewayConfig.getFlinkConfig().getConfiguration().put("kubernetes.cluster-id", kubernetesConfig.get("kubernetes.cluster-id").toString());
            } else {
                //初始化FlinkKubeClient需要CLUSTER_ID,先用UUID代替，后面使用job名称来作为CLUSTER_ID
                gatewayConfig.getFlinkConfig().getConfiguration().put("kubernetes.cluster-id", UUID.randomUUID().toString());
            }
            if (kubernetesConfig.containsKey("kubernetes.container.image")) {
                gatewayConfig.getFlinkConfig().getConfiguration().put("kubernetes.container.image", kubernetesConfig.get("kubernetes.container.image").toString());
            }
            String fileDir = FileUtil.isDirectory(PathConstant.WORK_DIR + "/dlink-doc") ? PathConstant.WORK_DIR + "/dlink-doc" : PathConstant.WORK_DIR;
            File dockerFile = null;
            try {
                dockerFile = FileUtil.writeUtf8String(FileUtil.readUtf8String(dockerfileResource.getFile()), fileDir + "/DinkyFlinkDockerfile");
                Docker docker = Docker.build((Map) getClusterConfigById(clusterConfiguration.getId()).getConfig().get("dockerConfig"));
                if (docker != null && StringUtils.isNotBlank(docker.getInstance())) {
                    new DockerClientUtils(docker,dockerFile).initImage();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return JobManager.testGateway(gatewayConfig);
    }
}
