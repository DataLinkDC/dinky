package com.dlink.cdc;

import com.dlink.model.FlinkCDCConfig;

/**
 * AbstractCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:28
 **/
public abstract class AbstractCDCBuilder {

    protected FlinkCDCConfig config;

    public AbstractCDCBuilder() {
    }

    public AbstractCDCBuilder(FlinkCDCConfig config) {
        this.config = config;
    }

    public FlinkCDCConfig getConfig() {
        return config;
    }

    public void setConfig(FlinkCDCConfig config) {
        this.config = config;
    }
}
