package com.dlink.alert;

import java.util.Map;

/**
 * AlertConfig
 *
 * @author wenmo
 * @since 2022/2/23 19:09
 **/
public class AlertConfig {
    private String name;
    private String type;
    private Map<String, String> param;

    public AlertConfig() {
    }

    public AlertConfig(String name, String type, Map<String, String> param) {
        this.name = name;
        this.type = type;
        this.param = param;
    }

    public static AlertConfig build(String name, String type, Map<String, String> param) {
        return new AlertConfig(name, type, param);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}
