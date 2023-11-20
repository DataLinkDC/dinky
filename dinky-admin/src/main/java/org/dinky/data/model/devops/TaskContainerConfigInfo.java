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
 * @description:
 * @create: 2022-06-27 11:41
 */
@Data
@ApiModel(value = "TaskContainerConfigInfo", description = "Task Container Configuration Information")
public class TaskContainerConfigInfo {

    @ApiModelProperty(
            value = "Metrics",
            dataType = "Map<String, String>",
            notes = "Metrics related to the task container")
    private Map<String, String> metrics;

    @ApiModelProperty(value = "Task Manager Log", dataType = "String", notes = "Log file path for the task manager")
    private String taskManagerLog;

    @ApiModelProperty(
            value = "Task Manager Stdout",
            dataType = "String",
            notes = "Standard output file path for the task manager")
    private String taskManagerStdout;

    @ApiModelProperty(
            value = "Task Manager Thread Dump",
            dataType = "String",
            notes = "Thread dump information for the task manager")
    private String taskManagerThreadDump;
}
