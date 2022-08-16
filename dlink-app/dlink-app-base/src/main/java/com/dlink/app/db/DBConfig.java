/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
        return "DBConfig{"
                + "driver='" + driver + '\''
                + ", url='" + url + '\''
                + ", username='" + username + '\''
                + ", password='" + password + '\''
                + '}';
    }
}
