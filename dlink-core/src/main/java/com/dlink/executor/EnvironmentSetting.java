package com.dlink.executor;

/**
 * EnvironmentSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:45
 **/
public class EnvironmentSetting {
    private String host;
    private int port;

    public EnvironmentSetting(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
