package com.dlink.gateway.yarn;

import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.GatewayConfig;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;

/**
 * YarnSubmiter
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public abstract class YarnGateway extends AbstractGateway {

    protected DefaultClusterClientServiceLoader clientServiceLoader;

    public YarnGateway() {
    }

    public YarnGateway(GatewayConfig config) {
        super(config);
    }

    public void init(){}
}
