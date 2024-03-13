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

import org.dinky.assertion.Asserts;
import org.dinky.assertion.DinkyAssert;
import org.dinky.cluster.FlinkCluster;
import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.data.dto.ClusterInstanceDTO;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.Task;
import org.dinky.gateway.config.GatewayConfig;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.mapper.ClusterInstanceMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.ClusterConfigurationService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.TaskService;
import org.dinky.utils.IpUtil;
import org.dinky.utils.URLUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.thread.ThreadUtil;
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

    @Autowired
    @Lazy
    private TaskService taskService;

    @Override
    public FlinkClusterInfo checkHeartBeat(String hosts, String host) {
        return FlinkCluster.testFlinkJobManagerIP(hosts, host);
    }

    @Override
    public String getJobManagerAddress(ClusterInstance clusterInstance) {
        // TODO 这里判空逻辑有问题，clusterInstance有可能为null
        DinkyAssert.check(clusterInstance);
        FlinkClusterInfo info =
                FlinkCluster.testFlinkJobManagerIP(clusterInstance.getHosts(), clusterInstance.getJobManagerHost());
        String host = null;
        if (info.isEffective()) {
            host = info.getJobManagerAddress();
        }
        DinkyAssert.checkHost(host);
        if (!host.equals(clusterInstance.getJobManagerHost())) {
            clusterInstance.setJobManagerHost(host);
            updateById(clusterInstance);
        }
        return host;
    }

    @Override
    public String buildEnvironmentAddress(JobConfig config) {
        Integer id = config.getClusterId();
        Boolean useRemote = config.isUseRemote();
        if (useRemote && id != null && id != 0) {
            return buildRemoteEnvironmentAddress(id);
        } else {
            Map<String, String> flinkConfig = config.getConfigJson();
            int port;
            if (Asserts.isNotNull(flinkConfig) && flinkConfig.containsKey("rest.port")) {
                port = Integer.valueOf(flinkConfig.get("rest.port"));
            } else {
                port = URLUtils.getRandomPort();
                while (!IpUtil.isPortAvailable(port)) {
                    port = URLUtils.getRandomPort();
                }
            }
            return buildLocalEnvironmentAddress(port);
        }
    }

    private String buildRemoteEnvironmentAddress(Integer id) {
        return getJobManagerAddress(getById(id));
    }

    private String buildLocalEnvironmentAddress(int port) {
        return "0.0.0.0:" + port;
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
    public ClusterInstance registersCluster(ClusterInstanceDTO clusterInstanceDTO) {
        ClusterInstance clusterInstance = clusterInstanceDTO.toBean();

        return this.registersCluster(clusterInstance);
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
        if (hasRelationShip(id)) {
            throw new BusException(Status.CLUSTER_INSTANCE_EXIST_RELATIONSHIP);
        }
        ClusterInstance clusterInstance = getById(id);
        // if cluster instance is not null and cluster instance is health, can not delete, must kill cluster instance
        // first
        if (Asserts.isNotNull(clusterInstance) && checkHealth(clusterInstance)) {
            throw new BusException(Status.CLUSTER_INSTANCE_HEALTH_NOT_DELETE);
        }
        return removeById(id);
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
        if (hasRelationShip(id)) {
            throw new BusException(Status.CLUSTER_INSTANCE_EXIST_RELATIONSHIP);
        } else if (Asserts.isNull(clusterInstance)) {
            throw new BusException(Status.CLUSTER_NOT_EXIST);
        } else if (!checkHealth(clusterInstance)) {
            throw new BusException(Status.CLUSTER_INSTANCE_NOT_HEALTH);
        } else if (clusterInstance.getType().equals(GatewayType.LOCAL.getLongValue())) {
            // todo: kill local cluster instance by id is not support
            throw new BusException(Status.CLUSTER_INSTANCE_LOCAL_NOT_SUPPORT_KILL);
        } else {
            Integer clusterConfigurationId = clusterInstance.getClusterConfigurationId();
            FlinkClusterConfig flinkClusterConfig =
                    clusterConfigurationService.getFlinkClusterCfg(clusterConfigurationId);
            GatewayConfig gatewayConfig = GatewayConfig.build(flinkClusterConfig);
            JobManager.killCluster(gatewayConfig, clusterInstance.getName());
        }
    }

    @Override
    public ClusterInstance deploySessionCluster(Integer id) {
        ClusterConfiguration clusterCfg = clusterConfigurationService.getClusterConfigById(id);
        if (Asserts.isNull(clusterCfg)) {
            throw new GatewayException("The cluster configuration does not exist.");
        }
        GatewayConfig gatewayConfig =
                GatewayConfig.build(FlinkClusterConfig.create(clusterCfg.getType(), clusterCfg.getConfigJson()));
        gatewayConfig.setType(gatewayConfig.getType().getSessionType());
        GatewayResult gatewayResult = JobManager.deploySessionCluster(gatewayConfig);
        if (gatewayResult.isSuccess()) {
            Asserts.checkNullString(gatewayResult.getWebURL(), "Unable to obtain Web URL.");
            return registersCluster(ClusterInstanceDTO.builder()
                    .hosts(gatewayResult.getWebURL().replace("http://", ""))
                    .name(gatewayResult.getId())
                    .alias(clusterCfg.getName() + "_" + LocalDateTime.now())
                    .type(gatewayConfig.getType().getLongValue())
                    .clusterConfigurationId(id)
                    .autoRegisters(true)
                    .enabled(true)
                    .build());
        }
        throw new DinkyException("Deploy session cluster error: " + gatewayResult.getError());
    }

    /**
     * @param searchKeyWord
     * @return
     */
    @Override
    public List<ClusterInstance> selectListByKeyWord(String searchKeyWord, boolean isAutoCreate) {
        return getBaseMapper()
                .selectList(new LambdaQueryWrapper<ClusterInstance>()
                        .and(true, i -> i.eq(ClusterInstance::getAutoRegisters, isAutoCreate))
                        .and(true, i -> i.like(ClusterInstance::getName, searchKeyWord)
                                .or()
                                .like(ClusterInstance::getAlias, searchKeyWord)
                                .or()
                                .like(ClusterInstance::getNote, searchKeyWord)));
    }

    /**
     * check cluster instance has relationship
     *
     * @param id {@link Integer} alert template id
     * @return {@link Boolean} true: has relationship, false: no relationship
     */
    @Override
    public boolean hasRelationShip(Integer id) {
        return !taskService
                .list(new LambdaQueryWrapper<Task>().eq(Task::getClusterId, id))
                .isEmpty();
    }

    @Override
    public Long heartbeat() {
        List<ClusterInstance> clusterInstances = this.list();
        ExecutorService executor = ThreadUtil.newExecutor(Math.min(clusterInstances.size(), 10));
        List<CompletableFuture<Integer>> futures = clusterInstances.stream()
                .map(c -> CompletableFuture.supplyAsync(
                        () -> this.registersCluster(c).getStatus(), executor))
                .collect(Collectors.toList());
        return futures.stream().map(CompletableFuture::join).filter(x -> x == 1).count();
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
