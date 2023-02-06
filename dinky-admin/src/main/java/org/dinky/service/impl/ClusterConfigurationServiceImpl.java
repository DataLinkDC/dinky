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

import org.dinky.config.Docker;
import org.dinky.db.service.impl.SuperServiceImpl;
import org.dinky.function.constant.PathConstant;
import org.dinky.gateway.GatewayType;
import org.dinky.gateway.config.ClusterConfig;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.result.TestResult;
import org.dinky.job.JobManager;
import org.dinky.mapper.ClusterConfigurationMapper;
import org.dinky.model.ClusterConfiguration;
import org.dinky.model.FlinkClusterConfiguration;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.utils.DockerClientUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;

/**
 * ClusterConfigServiceImpl
 *
 * @author wenmo
 * @since 2021/11/6 20:54
 */
@Service
public class ClusterConfigurationServiceImpl
        extends SuperServiceImpl<ClusterConfigurationMapper, ClusterConfiguration>
        implements ClusterConfigurationService {

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
    public Map<String, Object> getGatewayConfig(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getClusterConfigById(id);
        return clusterConfiguration.getConfig();
    }

    @Override
    public TestResult testGateway(ClusterConfiguration clusterConfiguration) {
        FlinkClusterConfiguration config = clusterConfiguration.parse();
        GatewayConfig gatewayConfig = new GatewayConfig();

        Opt.ofBlankAble(config.getHadoopConfigPath())
                .ifPresentOrElse(
                        hadoopConfigPath ->
                                gatewayConfig.setClusterConfig(
                                        ClusterConfig.build(
                                                config.getFlinkConfigPath(),
                                                config.getFlinkLibPath(),
                                                hadoopConfigPath)),
                        () ->
                                gatewayConfig.setClusterConfig(
                                        ClusterConfig.build(config.getFlinkConfigPath())));

        FlinkConfig flinkConfig =
                CollUtil.isEmpty(config.getFlinkConfig())
                        ? FlinkConfig.build(new HashMap<>(8))
                        : FlinkConfig.build(config.getFlinkConfig());
        Map<String, String> flinkConfigMap = flinkConfig.getConfiguration();
        gatewayConfig.setFlinkConfig(flinkConfig);

        if (config.getType() == FlinkClusterConfiguration.Type.Yarn) {
            gatewayConfig.setType(GatewayType.YARN_APPLICATION);
        } else if (config.getType() == FlinkClusterConfiguration.Type.Kubernetes) {
            gatewayConfig.setType(GatewayType.KUBERNETES_APPLICATION);

            Dict kubernetesConfig = Dict.of(config.getKubernetesConfig());

            Opt.ofBlankAble(kubernetesConfig.getStr("kubernetes.namespace"))
                    .ifPresent(v -> flinkConfigMap.put("kubernetes.namespace", v));

            Opt.ofBlankAble(kubernetesConfig.getStr("kubernetes.cluster-id"))
                    .ifPresentOrElse(
                            v -> flinkConfigMap.put("kubernetes.cluster-id", v),
                            () ->
                                    flinkConfigMap.put(
                                            "kubernetes.cluster-id", UUID.randomUUID().toString()));

            Opt.ofBlankAble(kubernetesConfig.getStr("kubernetes.container.image"))
                    .ifPresent(v -> flinkConfigMap.put("kubernetes.container.image", v));

            String fileDir =
                    FileUtil.isDirectory(PathConstant.WORK_DIR + "/dinky-doc")
                            ? PathConstant.WORK_DIR + "/dinky-doc"
                            : PathConstant.WORK_DIR;
            File dockerFile;
            try {
                dockerFile =
                        FileUtil.writeUtf8String(
                                FileUtil.readUtf8String(dockerfileResource.getFile()),
                                fileDir + "/DinkyFlinkDockerfile");
                Docker docker =
                        Docker.build((Map) clusterConfiguration.getConfig().get("dockerConfig"));
                if (docker != null
                        && StringUtils.isNotBlank(docker.getInstance())
                        && clusterConfiguration.getId() != null) {
                    new DockerClientUtils(docker, dockerFile).initImage();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return JobManager.testGateway(gatewayConfig);
    }
}
