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

import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.result.Result;
import org.dinky.data.vo.CascaderVO;
import org.dinky.utils.CascaderOptionsUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "Flink Conf Controller", hidden = true)
@RequestMapping("/api/flinkConf")
public class FlinkConfController {
    @GetMapping("/configOptions")
    public Result<List<CascaderVO>> loadDataByGroup() {
        List<CascaderVO> dataList = new ArrayList<>();

        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.CoreOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.RestOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.PipelineOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.SecurityOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.YarnConfigOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.WebOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.JobManagerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.TaskManagerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.HighAvailabilityOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.KubernetesConfigOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ClusterOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.StateBackendOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.QueryableStateOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.CheckpointingOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.JMXServerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.HeartbeatManagerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.OptimizerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.AkkaOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.AlgorithmOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.BlobServerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ExecutionOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ExternalResourceOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.ResourceManagerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.HistoryServerOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.MetricOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions(
                "org.apache.flink.configuration.NettyShuffleEnvironmentOptions", dataList);
        CascaderOptionsUtils.buildCascadeOptions("org.apache.flink.configuration.RestartStrategyOptions", dataList);
        return Result.succeed(dataList);
    }
}
