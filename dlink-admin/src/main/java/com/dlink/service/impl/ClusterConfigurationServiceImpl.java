package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.ClusterConfig;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.result.TestResult;
import com.dlink.job.JobManager;
import com.dlink.mapper.ClusterConfigurationMapper;
import com.dlink.model.ClusterConfiguration;
import com.dlink.service.ClusterConfigurationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ClusterConfigServiceImpl
 *
 * @author wenmo
 * @since 2021/11/6 20:54
 */
@Service
public class ClusterConfigurationServiceImpl extends SuperServiceImpl<ClusterConfigurationMapper, ClusterConfiguration> implements ClusterConfigurationService {
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
            }else{
                //初始化FlinkKubeClient需要CLUSTER_ID,先用UUID代替，后面使用job名称来作为CLUSTER_ID
                gatewayConfig.getFlinkConfig().getConfiguration().put("kubernetes.cluster-id", UUID.randomUUID().toString());
            }
            if (kubernetesConfig.containsKey("kubernetes.container.image")) {
                gatewayConfig.getFlinkConfig().getConfiguration().put("kubernetes.container.image", kubernetesConfig.get("kubernetes.container.image").toString());
            }
        }
        return JobManager.testGateway(gatewayConfig);
    }
}
