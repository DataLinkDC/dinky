package com.dlink.gateway;

import com.dlink.gateway.config.GatewayConfig;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractGateway
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public abstract class AbstractGateway implements Gateway {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractGateway.class);
    protected GatewayConfig config;
    protected Configuration configuration;

    public AbstractGateway() {
    }

    public AbstractGateway(GatewayConfig config) {
        this.config = config;
    }

    @Override
    public boolean canHandle(GatewayType type) {
        return type == getType();
    }

    @Override
    public void setGatewayConfig(GatewayConfig config) {
        this.config = config;
    }

    protected abstract void init();
}
