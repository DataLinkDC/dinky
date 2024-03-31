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

package org.dinky.gateway.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AppConfig
 *
 * @since 2021/11/3 21:55
 */
@Data
@ApiModel(value = "AppConfig", description = "Configuration for the Flink application")
public class AppConfig {

    @ApiModelProperty(
            value = "Path to user JAR file",
            dataType = "String",
            example = "/path/to/user/app.jar",
            notes = "Path to the user's application JAR file")
    private String userJarPath;

    @ApiModelProperty(
            value = "User JAR file parameters",
            dataType = "String[]",
            example = "[]",
            notes = "Parameters to be passed to the user's application JAR file")
    private String[] userJarParas;

    @ApiModelProperty(
            value = "Main application class in the JAR file",
            dataType = "String",
            example = "com.example.MyAppMainClass",
            notes = "Fully qualified class name of the main application class in the JAR file")
    private String userJarMainAppClass;
}
