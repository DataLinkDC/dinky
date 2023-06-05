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

package org.dinky.data.model;

import lombok.Data;

/**
 * @program: dinky
 * @description:
 * @create: 2022-06-27 11:18
 */
@Data
public class TaskManagerConfiguration {

    private String containerId;
    private String containerPath;
    private Integer dataPort;
    private Integer jmxPort;
    private Long timeSinceLastHeartbeat;
    private Integer slotsNumber;
    private Integer freeSlots;

    private String totalResource;
    private String freeResource;
    private String hardware;
    private String memoryConfiguration;

    private TaskContainerConfigInfo taskContainerConfigInfo;
}
