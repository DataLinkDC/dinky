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

import org.dinky.data.model.ClusterConfiguration;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.gateway.result.TestResult;
import org.dinky.job.JobManager;
import org.dinky.mapper.ClusterConfigurationMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.ClusterConfigurationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.lang.Assert;

/**
 * ClusterConfigServiceImpl
 *
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
        return baseMapper.selectById(id);
    }

    @Override
    public List<ClusterConfiguration> listEnabledAll() {
        return this.list(new QueryWrapper<ClusterConfiguration>().eq("enabled", 1));
    }

    @Override
    public FlinkClusterConfig getFlinkClusterCfg(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getClusterConfigById(id);
        Assert.notNull(clusterConfiguration, "The clusterConfiguration not exists!");
        return clusterConfiguration.getFlinkClusterCfg();
    }

    @Override
    public TestResult testGateway(ClusterConfiguration clusterConfiguration) {
        FlinkClusterConfig config = clusterConfiguration.getFlinkClusterCfg();
        GatewayConfig gatewayConfig = GatewayConfig.build(config);
        return JobManager.testGateway(gatewayConfig);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean enable(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getById(id);
        if (clusterConfiguration != null) {
            clusterConfiguration.setEnabled(!clusterConfiguration.getEnabled());
            return this.updateById(clusterConfiguration);
        }
        return false;
    }
}
