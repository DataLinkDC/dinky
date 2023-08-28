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

package org.dinky.controller;

import org.dinky.data.vo.CascaderVO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "Flink Conf Controller", hidden = true)
@RequestMapping("api/flinkConf")
public class FlinkConfController {
    @GetMapping("/")
    public List<CascaderVO> loadDataByGroup() {
        List<CascaderVO> dataList = new ArrayList<>();

        loadDataByGroup("org.apache.flink.configuration.CoreOptions", dataList, "Core");
        loadDataByGroup("org.apache.flink.configuration.RestOptions", dataList, "Rest");
        loadDataByGroup("org.apache.flink.configuration.PipelineOptions", dataList, "Pipeline");
        loadDataByGroup("org.apache.flink.configuration.SecurityOptions", dataList, "Security");
        loadDataByGroup("org.apache.flink.configuration.YarnConfigOptions", dataList, "YARN");
        loadDataByGroup("org.apache.flink.configuration.WebOptions", dataList, "Web");
        loadDataByGroup("org.apache.flink.configuration.JobManagerOptions", dataList, "HobManager");
        loadDataByGroup("org.apache.flink.configuration.TaskManagerOptions", dataList, "TaskManager");
        loadDataByGroup("org.apache.flink.configuration.HighAvailabilityOptions", dataList, "HighAvailability");
        loadDataByGroup("org.apache.flink.configuration.KubernetesConfigOptions", dataList, "KubernetesConfig");
        loadDataByGroup("org.apache.flink.configuration.ClusterOptions", dataList, "Cluster");
        loadDataByGroup("org.apache.flink.configuration.StateBackendOptions", dataList, "StateBackend");
        loadDataByGroup("org.apache.flink.configuration.QueryableStateOptions", dataList, "QueryableState");
        loadDataByGroup("org.apache.flink.configuration.CheckpointingOptions", dataList, "Checkpointing");
        loadDataByGroup("org.apache.flink.configuration.JMXServerOptions", dataList, "JMXServer");
        loadDataByGroup("org.apache.flink.configuration.HeartbeatManagerOptions", dataList, "HeartbeatManager");
        loadDataByGroup("org.apache.flink.configuration.OptimizerOptions", dataList, "Optimizer");
        loadDataByGroup("org.apache.flink.configuration.AkkaOptions", dataList, "Akka");
        loadDataByGroup("org.apache.flink.configuration.AlgorithmOptions", dataList, "Algorithm");
        loadDataByGroup("org.apache.flink.configuration.BlobServerOptions", dataList, "BlobServer");
        loadDataByGroup("org.apache.flink.configuration.ExecutionOptions", dataList, "Execution");
        loadDataByGroup("org.apache.flink.configuration.ExternalResourceOptions", dataList, "ExternalResource");
        loadDataByGroup("org.apache.flink.configuration.ResourceManagerOptions", dataList, "ResourceManager");
        loadDataByGroup("org.apache.flink.configuration.HistoryServerOptions", dataList, "HistoryServer");
        loadDataByGroup("org.apache.flink.configuration.MetricOptions", dataList, "Metric");
        loadDataByGroup(
                "org.apache.flink.configuration.NettyShuffleEnvironmentOptions", dataList, "NettyShuffleEnvironment");
        loadDataByGroup("org.apache.flink.configuration.RestartStrategyOptions", dataList, "RestartStrategy");
        return dataList;
    }

    private static void loadDataByGroup(String clazz, List<CascaderVO> dataList, String group) {
        try {
            Class<?> loadClass = ClassLoaderUtil.getContextClassLoader().loadClass(clazz);
            Field[] fields = ReflectUtil.getFields(loadClass, f -> {
                try {
                    return f.getType().isAssignableFrom(Class.forName("org.apache.flink.configuration.ConfigOption"))
                            && Modifier.isStatic(f.getModifiers());
                } catch (ClassNotFoundException e) {
                    return false;
                }
            });
            List<CascaderVO> configList = new ArrayList<>();
            for (Field field : fields) {
                CascaderVO config = new CascaderVO();
                Object fieldValue = ReflectUtil.getStaticFieldValue(field);
                String key = (String) ReflectUtil.invoke(fieldValue, "key");
                config.setValue(key);
                config.setLabel(key);
                configList.add(config);
            }
            CascaderVO cascaderVO = new CascaderVO();
            cascaderVO.setChildren(configList);
            cascaderVO.setLabel(group);
            cascaderVO.setValue(group);
            dataList.add(cascaderVO);
        } catch (ClassNotFoundException ignored) {
        }
    }
}
