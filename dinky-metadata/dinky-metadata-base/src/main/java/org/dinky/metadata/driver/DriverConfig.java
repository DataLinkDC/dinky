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

package org.dinky.metadata.driver;

import org.dinky.assertion.Asserts;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DriverConfig
 *
 * @since 2021/7/19 23:21
 */
@Getter
@Setter
@ApiModel(value = "DriverConfig", description = "Configuration for the driver component")
public class DriverConfig {

    @ApiModelProperty(
            value = "Name of the driver",
            dataType = "String",
            example = "MyDriver",
            notes = "Name of the driver component")
    private String name;

    @ApiModelProperty(
            value = "Type of the driver",
            dataType = "String",
            example = "Flink",
            notes = "Type of the driver component")
    private String type;

    @ApiModelProperty(
            value = "IP address of the driver",
            dataType = "String",
            example = "192.168.1.100",
            notes = "IP address of the driver component")
    private String ip;

    @ApiModelProperty(
            value = "Port number for communication",
            dataType = "Integer",
            example = "8081",
            notes = "Port number for communication with the driver")
    private Integer port;

    @ApiModelProperty(
            value = "URL for the driver",
            dataType = "String",
            example = "http://192.168.1.100:8081",
            notes = "URL for accessing the driver component")
    private String url;

    @ApiModelProperty(
            value = "Username for authentication",
            dataType = "String",
            example = "user123",
            notes = "Username for authentication (if applicable)")
    private String username;

    @ApiModelProperty(
            value = "Password for authentication",
            dataType = "String",
            example = "password123",
            notes = "Password for authentication (if applicable)")
    private String password;

    public DriverConfig() {}

    public DriverConfig(String name, String type, String url, String username, String password) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DriverConfig build(Map<String, String> confMap) {
        Asserts.checkNull(confMap, "数据源配置不能为空");
        return new DriverConfig(
                confMap.get("name"),
                confMap.get("type"),
                confMap.get("url"),
                confMap.get("username"),
                confMap.get("password"));
    }
}
