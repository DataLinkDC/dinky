package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Assert;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.config.ClusterConfig;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.result.TestResult;
import com.dlink.job.JobManager;
import com.dlink.mapper.ClusterConfigurationMapper;
import com.dlink.model.ClusterConfiguration;
import com.dlink.model.Jar;
import com.dlink.model.SystemConfiguration;
import com.dlink.service.ClusterConfigurationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public Map getGatewayConfig(Integer id) {
        ClusterConfiguration clusterConfiguration = this.getClusterConfigById(id);
        return clusterConfiguration.getConfig();
    }

    @Override
    public TestResult testGateway(ClusterConfiguration clusterConfiguration) {
        clusterConfiguration.parseConfig();
        Map<String, Object> config = clusterConfiguration.getConfig();
        GatewayConfig gatewayConfig = new GatewayConfig();
        gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString(),
                config.get("flinkLibPath").toString(),
                config.get("hadoopConfigPath").toString()));
        if(config.containsKey("flinkConfig")){
            gatewayConfig.setFlinkConfig(FlinkConfig.build((Map<String, String>)config.get("flinkConfig")));
        }
        if(Asserts.isEqualsIgnoreCase(clusterConfiguration.getType(),"Yarn")){
            gatewayConfig.setType(GatewayType.YARN_PER_JOB);
        }
        return JobManager.testGateway(gatewayConfig);
    }
}
