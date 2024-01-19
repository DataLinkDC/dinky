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
import org.dinky.service.FlinkService;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "Flink Conf Controller", hidden = true)
@RequestMapping("/api/flinkConf")
@RequiredArgsConstructor
public class FlinkController {

    protected static final CheckpointRead INSTANCE = new CheckpointRead();
    private final FlinkService flinkService;

    @GetMapping("/readCheckPoint")
    @ApiOperation("Read Checkpoint")
    public Result<Map<String, Map<String, CheckPointReadTable>>> readCheckPoint(String path, String operatorId) {
        return Result.data(INSTANCE.readCheckpoint(path, operatorId));
    }

    @GetMapping("/configOptions")
    @ApiOperation("Query Flink Configuration Options")
    public Result<List<CascaderVO>> loadDataByGroup() {
        return Result.succeed(flinkService.loadConfigOptions());
    }
}
