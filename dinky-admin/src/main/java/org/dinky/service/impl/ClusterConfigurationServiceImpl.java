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

import org.dinky.assertion.DinkyAssert;
import org.dinky.data.dto.ClusterConfigurationDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.Task;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.gateway.result.TestResult;
import org.dinky.job.JobManager;
import org.dinky.mapper.ClusterConfigurationMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * ClusterConfigServiceImpl
 *
 * @since 2021/11/6 20:54
 */
@Service
public class ClusterConfigurationServiceImpl extends SuperServiceImpl<ClusterConfigurationMapper, ClusterConfiguration>
        implements ClusterConfigurationService {

    @Value("classpath:DinkyFlinkDockerfile")
    org.springframework.core.io.Resource dockerfileResource;

    @Autowired
    @Lazy
    private TaskService taskService;

    @Override
    public ClusterConfiguration getClusterConfigById(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ClusterConfiguration> listEnabledAllClusterConfig() {
        return this.list(new QueryWrapper<ClusterConfiguration>().eq("enabled", 1));
    }

    @Override
    public FlinkClusterConfig getFlinkClusterCfg(Integer id) {
        ClusterConfiguration cfg = this.getClusterConfigById(id);
        DinkyAssert.checkNull(cfg, "The clusterConfiguration not exists!");
        return FlinkClusterConfig.create(cfg.getType(), cfg.getConfigJson());
    }

    @Override
    public TestResult testGateway(ClusterConfigurationDTO config) {
        config.getConfig().setType(GatewayType.get(config.getType()));
        return JobManager.testGateway(GatewayConfig.build(config.getConfig()));
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean modifyClusterConfigStatus(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getById(id);
        if (clusterConfiguration != null) {
            clusterConfiguration.setEnabled(!clusterConfiguration.getEnabled());
            return this.updateById(clusterConfiguration);
        }
        return false;
    }

    /**
     * @param keyword
     * @return
     */
    @Override
    public List<ClusterConfigurationDTO> selectListByKeyWord(String keyword) {
        return getBaseMapper()
                .selectList(new LambdaQueryWrapper<ClusterConfiguration>().like(ClusterConfiguration::getName, keyword))
                .stream()
                .map(ClusterConfigurationDTO::fromBean)
                .collect(Collectors.toList());
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean deleteClusterConfigurationById(Integer id) {
        if (hasRelationShip(id)) {
            throw new BusException(Status.CLUSTER_CONFIG_EXIST_RELATIONSHIP);
        }
        return removeById(id);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean hasRelationShip(Integer id) {
        return !taskService
                .list(new LambdaQueryWrapper<Task>().eq(Task::getClusterConfigurationId, id))
                .isEmpty();
    }
}
