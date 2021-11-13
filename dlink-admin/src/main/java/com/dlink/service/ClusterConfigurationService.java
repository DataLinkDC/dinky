package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.model.ClusterConfiguration;

import java.util.List;

/**
 * ClusterConfigService
 *
 * @author wenmo
 * @since 2021/11/6 20:52
 */
public interface ClusterConfigurationService extends ISuperService<ClusterConfiguration> {

    ClusterConfiguration getClusterConfigById(Integer id);

    List<ClusterConfiguration> listEnabledAll();

    GatewayConfig buildGatewayConfig(Integer id);

}
