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

package org.dinky.job;

import org.dinky.api.FlinkAPI;
import org.dinky.assertion.Asserts;
import org.dinky.data.model.JobManagerConfiguration;
import org.dinky.data.model.TaskContainerConfigInfo;
import org.dinky.data.model.TaskManagerConfiguration;
import org.dinky.utils.JSONUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class BuildConfiguration {

    public static void buildJobManagerConfiguration(
            JobManagerConfiguration jobManagerConfiguration, FlinkAPI flinkAPI) {

        // 获取jobManager metrics
        Map<String, String> jobManagerMetricsMap = new HashMap<>(8);
        List<LinkedHashMap> jobManagerMetricsItemsList =
                JSONUtil.toList(
                        JSONUtil.toJsonString(flinkAPI.getJobManagerMetrics()),
                        LinkedHashMap.class);
        jobManagerMetricsItemsList.forEach(
                mapItems -> {
                    String configKey = (String) mapItems.get("id");
                    String configValue = (String) mapItems.get("value");
                    if (Asserts.isNotNullString(configKey)
                            && Asserts.isNotNullString(configValue)) {
                        jobManagerMetricsMap.put(configKey, configValue);
                    }
                });
        // 获取jobManager配置信息
        Map<String, String> jobManagerConfigMap = new HashMap<>(8);
        List<LinkedHashMap> jobManagerConfigMapItemsList =
                JSONUtil.toList(
                        JSONUtil.toJsonString(flinkAPI.getJobManagerConfig()), LinkedHashMap.class);
        jobManagerConfigMapItemsList.forEach(
                mapItems -> {
                    String configKey = (String) mapItems.get("key");
                    String configValue = (String) mapItems.get("value");
                    if (Asserts.isNotNullString(configKey)
                            && Asserts.isNotNullString(configValue)) {
                        jobManagerConfigMap.put(configKey, configValue);
                    }
                });
        // 获取jobManager日志
        String jobMangerLog = flinkAPI.getJobManagerLog();
        // 获取jobManager标准输出日志
        String jobManagerStdOut = flinkAPI.getJobManagerStdOut();

        jobManagerConfiguration.setMetrics(jobManagerMetricsMap);
        jobManagerConfiguration.setJobManagerConfig(jobManagerConfigMap);
        jobManagerConfiguration.setJobManagerLog(jobMangerLog);
        jobManagerConfiguration.setJobManagerStdout(jobManagerStdOut);
    }

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
                Long timeSinceLastHeartbeat = taskManagers.get("timeSinceLastHeartbeat").asLong();
                // timeSinceLastHeartbeat
                // 获取container slotsNumber
                Integer slotsNumber = taskManagers.get("slotsNumber").asInt();
                // 获取container freeSlots
                Integer freeSlots = taskManagers.get("freeSlots").asInt();
                // 获取container
                String totalResource = JSONUtil.toJsonString(taskManagers.get("totalResource"));
                // totalResource
                // 获取container
                String freeResource = JSONUtil.toJsonString(taskManagers.get("freeResource"));
                // freeResource
                // 获取container hardware
                String hardware = JSONUtil.toJsonString(taskManagers.get("hardware"));
                // 获取container
                String memoryConfiguration =
                        JSONUtil.toJsonString(taskManagers.get("memoryConfiguration"));
                // memoryConfiguration
                Asserts.checkNull(containerId, "获取不到 containerId , containerId不能为空");
                // 获取taskManager metrics
                JsonNode taskManagerMetrics = flinkAPI.getTaskManagerMetrics(containerId);
                // 获取taskManager日志
                String taskManagerLog = flinkAPI.getTaskManagerLog(containerId);
                // 获取taskManager线程dumps
                String taskManagerThreadDumps =
                        JSONUtil.toJsonString(
                                flinkAPI.getTaskManagerThreadDump(containerId).get("threadInfos"));
                // 获取taskManager标准输出日志
                String taskManagerStdOut = flinkAPI.getTaskManagerStdOut(containerId);

                // 获取taskManager metrics
                Map<String, String> taskManagerMetricsMap = new HashMap<>(8);
                List<LinkedHashMap> taskManagerMetricsItemsList =
                        JSONUtil.toList(
                                JSONUtil.toJsonString(taskManagerMetrics), LinkedHashMap.class);
                taskManagerMetricsItemsList.forEach(
                        mapItems -> {
                            String configKey = (String) mapItems.get("id");
                            String configValue = (String) mapItems.get("value");
                            if (Asserts.isNotNullString(configKey)
                                    && Asserts.isNotNullString(configValue)) {
                                taskManagerMetricsMap.put(configKey, configValue);
                            }
                        });

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
                taskContainerConfigInfo.setMetrics(taskManagerMetricsMap);
                taskContainerConfigInfo.setTaskManagerLog(taskManagerLog);
                taskContainerConfigInfo.setTaskManagerThreadDump(taskManagerThreadDumps);
                taskContainerConfigInfo.setTaskManagerStdout(taskManagerStdOut);

                taskManagerConfiguration.setTaskContainerConfigInfo(taskContainerConfigInfo);

                // 将taskManagerConfiguration添加到set集合中
                taskManagerConfigurationList.add(taskManagerConfiguration);
            }
        }
    }
}
