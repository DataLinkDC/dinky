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


package com.dlink.job;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.model.JobManagerConfiguration;
import com.dlink.model.TaskContainerConfigInfo;
import com.dlink.model.TaskManagerConfiguration;
import com.dlink.utils.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * @program: dlink
 * @description:
 * @author: zhumingye
 * @create: 2022-06-28 19:00
 */

public class BuildConfiguration {

    /**
     * @return void
     * @Author: zhumingye
     * @date: 2022/6/27
     * @Description: buildJobManagerConfiguration
     * @Params: [jobManagerConfiguration, flinkAPI]
     */
    public static void buildJobManagerConfiguration(JobManagerConfiguration jobManagerConfiguration, FlinkAPI flinkAPI) {

        Map<String, String> jobManagerMetricsMap = new HashMap<String, String>(); //获取jobManager metrics
        List<LinkedHashMap> jobManagerMetricsItemsList = JSONUtil.toList(JSONUtil.toJsonString(flinkAPI.getJobManagerMetrics()), LinkedHashMap.class);
        jobManagerMetricsItemsList.forEach(mapItems -> {
            String configKey = (String) mapItems.get("id");
            String configValue = (String) mapItems.get("value");
            if (Asserts.isNotNullString(configKey) && Asserts.isNotNullString(configValue)) {
                jobManagerMetricsMap.put(configKey, configValue);
            }
        });
        Map<String, String> jobManagerConfigMap = new HashMap<String, String>();//获取jobManager配置信息
        List<LinkedHashMap> jobManagerConfigMapItemsList = JSONUtil.toList(JSONUtil.toJsonString(flinkAPI.getJobManagerConfig()), LinkedHashMap.class);
        jobManagerConfigMapItemsList.forEach(mapItems -> {
            String configKey = (String) mapItems.get("key");
            String configValue = (String) mapItems.get("value");
            if (Asserts.isNotNullString(configKey) && Asserts.isNotNullString(configValue)) {
                jobManagerConfigMap.put(configKey, configValue);
            }
        });
        String jobMangerLog = flinkAPI.getJobManagerLog(); //获取jobManager日志
        String jobManagerStdOut = flinkAPI.getJobManagerStdOut(); //获取jobManager标准输出日志

        jobManagerConfiguration.setMetrics(jobManagerMetricsMap);
        jobManagerConfiguration.setJobManagerConfig(jobManagerConfigMap);
        jobManagerConfiguration.setJobManagerLog(jobMangerLog);
        jobManagerConfiguration.setJobManagerStdout(jobManagerStdOut);
    }

    /**
     * @Author: zhumingye
     * @date: 2022/6/27
     * @Description: buildTaskManagerConfiguration
     * @Params: [taskManagerConfigurationList, flinkAPI, taskManagerContainers]
     * @return void
     */
    public static void buildTaskManagerConfiguration(Set<TaskManagerConfiguration> taskManagerConfigurationList, FlinkAPI flinkAPI, JsonNode taskManagerContainers) {

        if (Asserts.isNotNull(taskManagerContainers)) {
            JsonNode taskmanagers = taskManagerContainers.get("taskmanagers");
            for (JsonNode taskManagers : taskmanagers) {
                TaskManagerConfiguration taskManagerConfiguration = new TaskManagerConfiguration();

                /**
                 * 解析 taskManager 的配置信息
                 */
                String containerId = taskManagers.get("id").asText();// 获取container id
                String containerPath =  taskManagers.get("path").asText(); // 获取container path
                Integer dataPort = taskManagers.get("dataPort").asInt(); // 获取container dataPort
                Integer jmxPort =taskManagers.get("jmxPort").asInt(); // 获取container jmxPort
                Long timeSinceLastHeartbeat =taskManagers.get("timeSinceLastHeartbeat").asLong(); // 获取container timeSinceLastHeartbeat
                Integer slotsNumber =taskManagers.get("slotsNumber").asInt(); // 获取container slotsNumber
                Integer freeSlots = taskManagers.get("freeSlots").asInt(); // 获取container freeSlots
                String totalResource =  JSONUtil.toJsonString(taskManagers.get("totalResource")); // 获取container totalResource
                String freeResource =  JSONUtil.toJsonString(taskManagers.get("freeResource") ); // 获取container freeResource
                String hardware = JSONUtil.toJsonString(taskManagers.get("hardware") ); // 获取container hardware
                String memoryConfiguration = JSONUtil.toJsonString(taskManagers.get("memoryConfiguration") ); // 获取container memoryConfiguration
                Asserts.checkNull(containerId, "获取不到 containerId , containerId不能为空");
                JsonNode taskManagerMetrics = flinkAPI.getTaskManagerMetrics(containerId);//获取taskManager metrics
                String taskManagerLog = flinkAPI.getTaskManagerLog(containerId);//获取taskManager日志
                String taskManagerThreadDumps = JSONUtil.toJsonString(flinkAPI.getTaskManagerThreadDump(containerId).get("threadInfos"));//获取taskManager线程dumps
                String taskManagerStdOut = flinkAPI.getTaskManagerStdOut(containerId);//获取taskManager标准输出日志

                Map<String, String> taskManagerMetricsMap = new HashMap<String, String>(); //获取taskManager metrics
                List<LinkedHashMap> taskManagerMetricsItemsList = JSONUtil.toList(JSONUtil.toJsonString(taskManagerMetrics), LinkedHashMap.class);
                taskManagerMetricsItemsList.forEach(mapItems -> {
                    String configKey = (String) mapItems.get("id");
                    String configValue = (String) mapItems.get("value");
                    if (Asserts.isNotNullString(configKey) && Asserts.isNotNullString(configValue)) {
                        taskManagerMetricsMap.put(configKey, configValue);
                    }
                });

                /**
                 * TaskManagerConfiguration 赋值
                 */
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

                /**
                 * TaskContainerConfigInfo 赋值
                 */
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
