package com.dlink.app.db;

import org.apache.flink.api.java.utils.ParameterTool;

/**
 * DBConfig
 *
 * @author qiwenkai
 * @since 2021/10/27 14:46
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

    public static DBConfig build(String driver, String url, String username, String password){
        return new DBConfig(driver,url,username,password);
    }


    public static DBConfig build(ParameterTool parameters){
        return new DBConfig(parameters.get("driver", null),
                parameters.get("url", null),
                parameters.get("username", null),
                parameters.get("password", null));
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
