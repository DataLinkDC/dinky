package com.dlink.metadata.driver;


import com.dlink.assertion.Asserts;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * DriverConfig
 *
 * @author wenmo
 * @since 2021/7/19 23:21
 */
@Getter
@Setter
public class DriverConfig {

    private String name;
    private String type;
    private String driverClassName;
    private String ip;
    private Integer port;
    private String url;
    private String username;
    private String password;

    public DriverConfig() {
    }

    public DriverConfig(String name, String type, String url, String username, String password) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DriverConfig build(Map<String, String> confMap) {
        Asserts.checkNull(confMap, "数据源配置不能为空");
        return new DriverConfig(confMap.get("name"), confMap.get("type"), confMap.get("url"), confMap.get("username")
            , confMap.get("password"));
    }
}
