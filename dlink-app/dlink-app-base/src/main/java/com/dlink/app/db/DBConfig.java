package com.dlink.app.db;

import com.dlink.constant.FlinkParamConstant;

import java.util.Map;

/**
 * DBConfig
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class DBConfig {

    private String driver;
    private String url;
    private String username;
    private String password;

    public DBConfig(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DBConfig build(String driver, String url, String username, String password) {
        return new DBConfig(driver, url, username, password);
    }


    public static DBConfig build(Map<String, String> params) {
        return new DBConfig(params.get(FlinkParamConstant.DRIVER),
                params.get(FlinkParamConstant.URL),
                params.get(FlinkParamConstant.USERNAME),
                params.get(FlinkParamConstant.PASSWORD));
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DBConfig{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
