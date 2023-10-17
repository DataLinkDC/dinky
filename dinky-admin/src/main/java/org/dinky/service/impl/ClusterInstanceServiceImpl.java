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

import org.dinky.assertion.Assert;
import org.dinky.assertion.Asserts;
import org.dinky.cluster.FlinkCluster;
import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.constant.FlinkConstant;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.ClusterInstance;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.job.JobManager;
import org.dinky.mapper.ClusterInstanceMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * ClusterInstanceServiceImpl
 *
 * @since 2021/5/28 14:02
 */
@Service
@RequiredArgsConstructor
public class ClusterInstanceServiceImpl extends SuperServiceImpl<ClusterInstanceMapper, ClusterInstance>
        implements ClusterInstanceService {

    private final ClusterConfigurationService clusterConfigurationService;

    @Override
    public FlinkClusterInfo checkHeartBeat(String hosts, String host) {
        return FlinkCluster.testFlinkJobManagerIP(hosts, host);
    }

    @Override
    public String getJobManagerAddress(ClusterInstance clusterInstance) {
        Assert.check(clusterInstance);
        FlinkClusterInfo info =
                FlinkCluster.testFlinkJobManagerIP(clusterInstance.getHosts(), clusterInstance.getJobManagerHost());
        String host = null;
        if (info.isEffective()) {
            host = info.getJobManagerAddress();
        }
        Assert.checkHost(host);
        if (!host.equals(clusterInstance.getJobManagerHost())) {
            clusterInstance.setJobManagerHost(host);
            updateById(clusterInstance);
        }
        return host;
    }

    @Override
    public String buildEnvironmentAddress(boolean useRemote, Integer id) {
        if (useRemote && id != 0) {
            return buildRemoteEnvironmentAddress(id);
        } else {
            return buildLocalEnvironmentAddress();
        }
    }

    @Override
    public String buildRemoteEnvironmentAddress(Integer id) {
        return getJobManagerAddress(getById(id));
    }

    @Override
    public String buildLocalEnvironmentAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            if (inetAddress != null) {
                return inetAddress.getHostAddress();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return FlinkConstant.LOCAL_HOST;
    }

    @Override
    public List<ClusterInstance> listEnabledAllClusterInstance() {
        return this.list(new QueryWrapper<ClusterInstance>().eq("enabled", 1));
    }

    @Override
    public List<ClusterInstance> listSessionEnable() {
        return baseMapper.listSessionEnable();
    }

    @Override
    public List<ClusterInstance> listAutoEnable() {
        return list(new QueryWrapper<ClusterInstance>().eq("enabled", 1).eq("auto_registers", 1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClusterInstance registersCluster(ClusterInstance clusterInstance) {
        checkHealth(clusterInstance);
        if (StrUtil.isEmpty(clusterInstance.getAlias())) {
            clusterInstance.setAlias(clusterInstance.getName());
        }
        saveOrUpdate(clusterInstance);
        return clusterInstance;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean deleteClusterInstanceById(Integer id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean modifyClusterInstanceStatus(Integer id) {
        ClusterInstance clusterInstanceInfo = getById(id);
        clusterInstanceInfo.setEnabled(!clusterInstanceInfo.getEnabled());
        checkHealth(clusterInstanceInfo);
        return updateById(clusterInstanceInfo);
    }

    @Override
    public Integer recycleCluster() {
        List<ClusterInstance> clusterInstances = listAutoEnable();
        int count = 0;
        for (ClusterInstance item : clusterInstances) {
            if ((!checkHealth(item)) && removeById(item)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void killCluster(Integer id) {
        ClusterInstance clusterInstance = getById(id);
        if (Asserts.isNull(clusterInstance)) {
            throw new GatewayException("The clusterInstance does not exist.");
        } else if (!checkHealth(clusterInstance)) {
            throw new GatewayException("The clusterInstance has been killed.");
        }
        Integer clusterConfigurationId = clusterInstance.getClusterConfigurationId();
        FlinkClusterConfig flinkClusterConfig = clusterConfigurationService.getFlinkClusterCfg(clusterConfigurationId);
        GatewayConfig gatewayConfig = GatewayConfig.build(flinkClusterConfig);
        JobManager.killCluster(gatewayConfig, clusterInstance.getName());
    }

    @Override
    public ClusterInstance deploySessionCluster(Integer id) {
        ClusterConfiguration clusterCfg = clusterConfigurationService.getClusterConfigById(id);
        if (Asserts.isNull(clusterCfg)) {
            throw new GatewayException("The cluster configuration does not exist.");
        }
        GatewayConfig gatewayConfig =
                GatewayConfig.build(FlinkClusterConfig.create(clusterCfg.getType(), clusterCfg.getConfigJson()));
        GatewayResult gatewayResult = JobManager.deploySessionCluster(gatewayConfig);
        return registersCluster(ClusterInstance.autoRegistersCluster(
                gatewayResult.getWebURL().replace("http://", ""),
                gatewayResult.getId(),
                clusterCfg.getName() + "_" + LocalDateTime.now(),
                clusterCfg.getName() + LocalDateTime.now(),
                id,
                null));
    }

    private boolean checkHealth(ClusterInstance clusterInstance) {
        FlinkClusterInfo info = checkHeartBeat(clusterInstance.getHosts(), clusterInstance.getJobManagerHost());
        if (!info.isEffective()) {
            clusterInstance.setJobManagerHost("");
            clusterInstance.setStatus(0);
            return false;
        } else {
            clusterInstance.setJobManagerHost(info.getJobManagerAddress());
            clusterInstance.setStatus(1);
            clusterInstance.setVersion(info.getVersion());
            return true;
        }
    }
}
