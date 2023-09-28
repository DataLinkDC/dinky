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

import org.dinky.data.model.CheckPointReadTable;
import org.dinky.data.result.Result;
import org.dinky.data.vo.CascaderVO;
import org.dinky.flink.checkpoint.CheckpointRead;
import org.dinky.utils.CascaderOptionsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "Flink Conf Controller", hidden = true)
@RequestMapping("/api/flinkConf")
public class FlinkController {
    protected static final CheckpointRead INSTANCE = new CheckpointRead();

    @GetMapping("/readCheckPoint")
    @ApiOperation("Read Checkpoint")
    public Result<Map<String, Map<String, CheckPointReadTable>>> readCheckPoint(String path, String operatorId) {
        return Result.data(INSTANCE.readCheckpoint(path, operatorId));
    }

    @GetMapping("/configOptions")
    @ApiOperation("Query Flink Configuration Options")
    public Result<List<CascaderVO>> loadDataByGroup() {
        final String[] nameList = {
            "org.apache.flink.configuration.CoreOptions",
            "org.apache.flink.configuration.RestOptions",
            "org.apache.flink.configuration.PipelineOptions",
            "org.apache.flink.configuration.SecurityOptions",
            "org.apache.flink.configuration.YarnConfigOptions",
            "org.apache.flink.configuration.WebOptions",
            "org.apache.flink.configuration.JobManagerOptions",
            "org.apache.flink.configuration.TaskManagerOptions",
            "org.apache.flink.configuration.HighAvailabilityOptions",
            "org.apache.flink.configuration.KubernetesConfigOptions",
            "org.apache.flink.configuration.ClusterOptions",
            "org.apache.flink.configuration.StateBackendOptions",
            "org.apache.flink.configuration.QueryableStateOptions",
            "org.apache.flink.configuration.CheckpointingOptions",
            "org.apache.flink.configuration.JMXServerOptions",
            "org.apache.flink.configuration.HeartbeatManagerOptions",
            "org.apache.flink.configuration.OptimizerOptions",
            "org.apache.flink.configuration.AkkaOptions",
            "org.apache.flink.configuration.AlgorithmOptions",
            "org.apache.flink.configuration.BlobServerOptions",
            "org.apache.flink.configuration.ExecutionOptions",
            "org.apache.flink.configuration.ExternalResourceOptions",
            "org.apache.flink.configuration.ResourceManagerOptions",
            "org.apache.flink.configuration.HistoryServerOptions",
            "org.apache.flink.configuration.MetricOptions",
            "org.apache.flink.configuration.NettyShuffleEnvironmentOptions",
            "org.apache.flink.configuration.RestartStrategyOptions",
            "org.apache.flink.yarn.configuration.YarnConfigOptions",
            "org.apache.flink.kubernetes.configuration.KubernetesConfigOptions",
            "org.dinky.constant.CustomerConfigureOptions"
        };
        List<CascaderVO> dataList = new ArrayList<>();
        Arrays.stream(nameList).map(CascaderOptionsUtils::buildCascadeOptions).forEach(dataList::addAll);

        return Result.succeed(dataList);
    }
}
