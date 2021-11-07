package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.ClusterConfigurationMapper;
import com.dlink.model.ClusterConfiguration;
import com.dlink.service.ClusterConfigurationService;
import org.springframework.stereotype.Service;

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
}
