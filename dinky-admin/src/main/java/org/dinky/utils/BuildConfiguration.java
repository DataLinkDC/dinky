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

package org.dinky.utils;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.data.model.devops.TaskContainerConfigInfo;
import org.dinky.data.model.devops.TaskManagerConfiguration;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

// TODO 后面优化掉
public class BuildConfiguration {

    public static void buildTaskManagerConfiguration(
            Set<TaskManagerConfiguration> taskManagerConfigurationList,
            FlinkAPI flinkAPI,
            JsonNode taskManagerContainers) {

        if (Asserts.isNotNull(taskManagerContainers)) {
            JsonNode taskmanagers = taskManagerContainers.get("taskmanagers");
            for (JsonNode taskManagers : taskmanagers) {
                TaskManagerConfiguration taskManagerConfiguration = new TaskManagerConfiguration();

                // 解析 taskManager 的配置信息
                // 获取container id
                String containerId = taskManagers.get("id").asText();
                // 获取container path
                String containerPath = taskManagers.get("path").asText();
                // 获取container dataPort
                Integer dataPort = taskManagers.get("dataPort").asInt();
                // 获取container jmxPort
                Integer jmxPort = taskManagers.get("jmxPort").asInt();
                // 获取container
                Long timeSinceLastHeartbeat =
                        taskManagers.get("timeSinceLastHeartbeat").asLong();
                // timeSinceLastHeartbeat
                // 获取container slotsNumber
                Integer slotsNumber = taskManagers.get("slotsNumber").asInt();
                // 获取container freeSlots
                Integer freeSlots = taskManagers.get("freeSlots").asInt();
                // 获取container
                String totalResource = JsonUtils.toJsonString(taskManagers.get("totalResource"));
                // totalResource
                // 获取container
                String freeResource = JsonUtils.toJsonString(taskManagers.get("freeResource"));
                // freeResource
                // 获取container hardware
                String hardware = JsonUtils.toJsonString(taskManagers.get("hardware"));
                // 获取container
                String memoryConfiguration = JsonUtils.toJsonString(taskManagers.get("memoryConfiguration"));
                // memoryConfiguration
                Asserts.checkNull(containerId, "获取不到 containerId , containerId不能为空");

                /* TaskManagerConfiguration 赋值 */
                taskManagerConfiguration.setContainerId(containerId);
                taskManagerConfiguration.setContainerPath(containerPath);
                taskManagerConfiguration.setDataPort(dataPort);
                taskManagerConfiguration.setJmxPort(jmxPort);
                taskManagerConfiguration.setTimeSinceLastHeartbeat(timeSinceLastHeartbeat);
                taskManagerConfiguration.setSlotsNumber(slotsNumber);
                taskManagerConfiguration.setFreeSlots(freeSlots);
                taskManagerConfiguration.setTotalResource(totalResource);
                taskManagerConfiguration.setFreeResource(freeResource);
                taskManagerConfiguration.setHardware(hardware);
                taskManagerConfiguration.setMemoryConfiguration(memoryConfiguration);

                /* TaskContainerConfigInfo 赋值 */
                TaskContainerConfigInfo taskContainerConfigInfo = new TaskContainerConfigInfo();

                taskManagerConfiguration.setTaskContainerConfigInfo(taskContainerConfigInfo);

                // 将taskManagerConfiguration添加到set集合中
                taskManagerConfigurationList.add(taskManagerConfiguration);
            }
        }
    }
}
