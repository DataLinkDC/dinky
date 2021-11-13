package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.gateway.config.ClusterConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.mapper.ClusterConfigurationMapper;
import com.dlink.model.ClusterConfiguration;
import com.dlink.service.ClusterConfigurationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClusterConfigServiceImpl
 *
 * @author wenmo
 * @since 2021/11/6 20:54
 */
@Service
public class ClusterConfigurationServiceImpl extends SuperServiceImpl<ClusterConfigurationMapper,ClusterConfiguration> implements ClusterConfigurationService {
    @Override
    public ClusterConfiguration getClusterConfigById(Integer id) {
        ClusterConfiguration clusterConfiguration = baseMapper.selectById(id);
        clusterConfiguration.parseConfig();
        return clusterConfiguration;
    }

    @Override
    public List<ClusterConfiguration> listEnabledAll() {
        return this.list(new QueryWrapper<ClusterConfiguration>().eq("enabled",1));
    }

    @Override
    public GatewayConfig buildGatewayConfig(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getClusterConfigById(id);
        GatewayConfig gatewayConfig = new GatewayConfig();
        gatewayConfig.setClusterConfig(ClusterConfig.build(clusterConfiguration.getConfig().get("flinkConfigPath"),
                clusterConfiguration.getConfig().get("flinkLibPath"),
                clusterConfiguration.getConfig().get("hadoopConfigPath")));
        return gatewayConfig;
    }
}
