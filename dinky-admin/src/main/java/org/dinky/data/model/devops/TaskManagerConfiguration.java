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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: dinky
 * @description:
 * @create: 2022-06-27 11:18
 */
@Data
@ApiModel(value = "TaskManagerConfiguration", description = "Task Manager Configuration")
public class TaskManagerConfiguration {

    @ApiModelProperty(value = "Container ID", dataType = "String", notes = "ID of the task manager container")
    private String containerId;

    @ApiModelProperty(value = "Container Path", dataType = "String", notes = "Path of the task manager container")
    private String containerPath;

    @ApiModelProperty(
            value = "Data Port",
            dataType = "Integer",
            example = "12345",
            notes = "Data port for communication")
    private Integer dataPort;

    @ApiModelProperty(
            value = "JMX Port",
            dataType = "Integer",
            example = "6789",
            notes = "JMX (Java Management Extensions) port")
    private Integer jmxPort;

    @ApiModelProperty(
            value = "Time Since Last Heartbeat",
            dataType = "Long",
            example = "60000",
            notes = "Time elapsed since the last heartbeat")
    private Long timeSinceLastHeartbeat;

    @ApiModelProperty(value = "Slots Number", dataType = "Integer", example = "4", notes = "Number of slots available")
    private Integer slotsNumber;

    @ApiModelProperty(value = "Free Slots", dataType = "Integer", example = "2", notes = "Number of free slots")
    private Integer freeSlots;

    @ApiModelProperty(value = "Total Resource", dataType = "String", notes = "Total resource information")
    private String totalResource;

    @ApiModelProperty(value = "Free Resource", dataType = "String", notes = "Free resource information")
    private String freeResource;

    @ApiModelProperty(value = "Hardware", dataType = "String", notes = "Hardware information")
    private String hardware;

    @ApiModelProperty(value = "Memory Configuration", dataType = "String", notes = "Memory configuration details")
    private String memoryConfiguration;

    @ApiModelProperty(
            value = "Task Container Config Info",
            dataType = "TaskContainerConfigInfo",
            notes = "Task container configuration information")
    private TaskContainerConfigInfo taskContainerConfigInfo;
}
