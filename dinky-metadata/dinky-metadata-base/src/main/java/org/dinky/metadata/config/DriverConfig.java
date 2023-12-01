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

package org.dinky.metadata.config;

import org.dinky.assertion.Asserts;
import org.dinky.utils.JsonUtils;

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
public class DriverConfig<T> {

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
            value = "Type of the driver",
            dataType = "String",
            example = "Flink",
            notes = "Type of the driver component")
    private T connectConfig;

    public DriverConfig() {}

    public DriverConfig(String name, String type, T connectConfig) {
        this.name = name;
        this.type = type;
        this.connectConfig = connectConfig;
    }

    public static <T extends IConnectConfig> DriverConfig<T> build(Map<String, String> confMap, Class<T> clazz) {
        Asserts.checkNull(confMap, "数据源配置不能为空");
        return new DriverConfig<T>(confMap.get("name"), confMap.get("type"), JsonUtils.convertValue(confMap, clazz));
    }
}
