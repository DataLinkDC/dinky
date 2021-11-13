package com.dlink.gateway.config;

/**
 * ConfigPara
 *
 * @author wenmo
 * @since 2021/11/2
 **/
public class ConfigPara {
    private String key;
    private String value;

    public ConfigPara() {
    }

    public ConfigPara(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
