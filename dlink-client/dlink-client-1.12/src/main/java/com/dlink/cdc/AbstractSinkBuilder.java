package com.dlink.cdc;

import com.dlink.model.FlinkCDCConfig;

/**
 * AbstractCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:28
 **/
public abstract class AbstractSinkBuilder {

    protected FlinkCDCConfig config;

    public AbstractSinkBuilder() {
    }

    public AbstractSinkBuilder(FlinkCDCConfig config) {
        this.config = config;
    }

    public FlinkCDCConfig getConfig() {
        return config;
    }

    public void setConfig(FlinkCDCConfig config) {
        this.config = config;
    }
}
