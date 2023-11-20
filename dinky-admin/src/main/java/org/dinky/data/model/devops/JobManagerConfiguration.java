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

package org.dinky.data.model.devops;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: dinky
 * @description: JobManager 配置信息
 * @create: 2022-06-26 10:53
 */
@Data
@ApiModel(value = "JobManagerConfiguration", description = "Job Manager Configuration Information")
public class JobManagerConfiguration {

    @ApiModelProperty(value = "Metrics", dataType = "Map<String, String>", notes = "Metrics related to the job manager")
    private Map<String, String> metrics;

    @ApiModelProperty(
            value = "Job Manager Config",
            dataType = "Map<String, String>",
            notes = "Configuration settings for the job manager")
    private Map<String, String> jobManagerConfig;

    @ApiModelProperty(value = "Job Manager Log", dataType = "String", notes = "Log information for the job manager")
    private String jobManagerLog;

    @ApiModelProperty(value = "Job Manager Stdout", dataType = "String", notes = "Standard output for the job manager")
    private String jobManagerStdout;
}
