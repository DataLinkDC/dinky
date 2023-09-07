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

import org.dinky.data.result.Result;
import org.dinky.data.vo.CascaderVO;
import org.dinky.utils.CascaderOptionsUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "Flink Conf Controller", hidden = true)
@RequestMapping("/api/flinkConf")
public class FlinkConfController {
    @GetMapping("/configOptions")
    public Result<List<CascaderVO>> loadDataByGroup() {
        List<CascaderVO> dataList = new ArrayList<>();
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.CoreOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.RestOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.PipelineOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.SecurityOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.YarnConfigOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.WebOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.JobManagerOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.TaskManagerOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.HighAvailabilityOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.KubernetesConfigOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ClusterOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.StateBackendOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.QueryableStateOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.CheckpointingOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.JMXServerOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.HeartbeatManagerOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.OptimizerOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.AkkaOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.AlgorithmOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.BlobServerOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ExecutionOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ExternalResourceOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ResourceManagerOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.HistoryServerOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.MetricOptions"));
        dataList.addAll(CascaderOptionsUtils.buildCascadeOptions(
                "org.apache.flink.configuration.NettyShuffleEnvironmentOptions"));
        dataList.addAll(
                CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.RestartStrategyOptions"));
        return Result.succeed(dataList);
    }
}
