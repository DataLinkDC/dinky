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
import org.dinky.data.model.Cluster;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.exception.GatewayException;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.RequiredArgsConstructor;

/**
 * ClusterInstanceServiceImpl
 *
 * @since 2021/5/28 14:02
 */
@Service
@RequiredArgsConstructor
public class ClusterInstanceServiceImpl extends SuperServiceImpl<ClusterInstanceMapper, Cluster>
        implements ClusterInstanceService {

    private final ClusterConfigurationService clusterConfigurationService;

    @Override
    public FlinkClusterInfo checkHeartBeat(String hosts, String host) {
        return FlinkCluster.testFlinkJobManagerIP(hosts, host);
    }

    @Override
    public String getJobManagerAddress(Cluster cluster) {
        Assert.check(cluster);
        FlinkClusterInfo info =
                FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
        String host = null;
        if (info.isEffective()) {
            host = info.getJobManagerAddress();
        }
        Assert.checkHost(host);
        if (!host.equals(cluster.getJobManagerHost())) {
            cluster.setJobManagerHost(host);
            updateById(cluster);
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
    public List<Cluster> listEnabledAll() {
        return this.list(new QueryWrapper<Cluster>().eq("enabled", 1));
    }

    @Override
    public List<Cluster> listSessionEnable() {
        return baseMapper.listSessionEnable();
    }

    @Override
    public List<Cluster> listAutoEnable() {
        return list(new QueryWrapper<Cluster>().eq("enabled", 1).eq("auto_registers", 1));
    }

    @Override
    public Cluster registersCluster(Cluster cluster) {
        checkHealth(cluster);
        saveOrUpdate(cluster);
        return cluster;
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
    public Boolean enableClusterInstance(Integer id) {
        Cluster clusterInfo = getById(id);
        clusterInfo.setEnabled(!clusterInfo.getEnabled());
        checkHealth(clusterInfo);
        return updateById(clusterInfo);
    }

    @Override
    public Integer recycleCluster() {
        List<Cluster> clusters = listAutoEnable();
        int count = 0;
        for (Cluster item : clusters) {
            if ((!checkHealth(item)) && removeById(item)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void killCluster(Integer id) {
        Cluster cluster = getById(id);
        if (Asserts.isNull(cluster)) {
            throw new GatewayException("The cluster does not exist.");
        } else if (!checkHealth(cluster)) {
            throw new GatewayException("The cluster has been killed.");
        }
        Integer clusterConfigurationId = cluster.getClusterConfigurationId();
        ClusterConfiguration clusterConfiguration =
                clusterConfigurationService.getClusterConfigById(clusterConfigurationId);
        if (Asserts.isNull(clusterConfiguration)) {
            throw new GatewayException("The cluster configuration does not exist.");
        }
        GatewayConfig gatewayConfig = GatewayConfig.build(clusterConfiguration.getConfig());
        gatewayConfig.setType(GatewayType.get(cluster.getType()));
        JobManager.killCluster(gatewayConfig, cluster.getName());
    }

    @Override
    public Cluster deploySessionCluster(Integer id) {
        ClusterConfiguration clusterConfiguration =
                clusterConfigurationService.getClusterConfigById(id);
        if (Asserts.isNull(clusterConfiguration)) {
            throw new GatewayException("The cluster configuration does not exist.");
        }
        GatewayConfig gatewayConfig = GatewayConfig.build(clusterConfiguration.getConfig());
        gatewayConfig.setType(GatewayType.getSessionType(clusterConfiguration.getType()));
        GatewayResult gatewayResult = JobManager.deploySessionCluster(gatewayConfig);
        return registersCluster(
                Cluster.autoRegistersCluster(
                        gatewayResult.getWebURL().replace("http://", ""),
                        gatewayResult.getId(),
                        clusterConfiguration.getName() + "_" + LocalDateTime.now(),
                        clusterConfiguration.getName() + LocalDateTime.now(),
                        id,
                        null));
    }

    private boolean checkHealth(Cluster cluster) {
        FlinkClusterInfo info = checkHeartBeat(cluster.getHosts(), cluster.getJobManagerHost());
        if (!info.isEffective()) {
            cluster.setJobManagerHost("");
            cluster.setStatus(0);
            return false;
        } else {
            cluster.setJobManagerHost(info.getJobManagerAddress());
            cluster.setStatus(1);
            cluster.setVersion(info.getVersion());
            return true;
        }
    }
}
