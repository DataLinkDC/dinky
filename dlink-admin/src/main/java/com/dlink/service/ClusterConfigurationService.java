package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.ClusterConfiguration;

/**
 * ClusterConfigService
 *
 * @author wenmo
 * @since 2021/11/6 20:52
 */
public interface ClusterConfigurationService extends ISuperService<ClusterConfiguration> {

    ClusterConfiguration getClusterConfigById(Integer id);
}
