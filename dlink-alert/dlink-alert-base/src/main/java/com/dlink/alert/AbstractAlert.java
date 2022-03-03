package com.dlink.alert;

/**
 * AbstractAlert
 *
 * @author wenmo
 * @since 2022/2/23 19:22
 **/
public abstract class AbstractAlert implements Alert {
    private AlertConfig config;

    public AlertConfig getConfig() {
        return config;
    }

    public Alert setConfig(AlertConfig config) {
        this.config = config;
        return this;
    }
}
