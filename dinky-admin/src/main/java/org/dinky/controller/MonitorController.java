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

import org.dinky.data.MetricsLayoutVo;
import org.dinky.data.annotations.Log;
import org.dinky.data.dto.MetricsLayoutDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.MetricsType;
import org.dinky.data.model.Metrics;
import org.dinky.data.result.Result;
import org.dinky.data.vo.MetricsVO;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Monitor Controller")
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;
    private final JobInstanceService jobInstanceService;

    @GetMapping("/getSysData")
    @ApiOperation("Get System Data")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "Start Time", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "endTime", value = "End Time", required = false, dataType = "Long")
    })
    public Result<List<MetricsVO>> getData(@RequestParam Long startTime, Long endTime) {
        List<MetricsVO> data = monitorService.getData(
                DateUtil.date(startTime),
                DateUtil.date(Opt.ofNullable(endTime).orElse(DateUtil.date().getTime())),
                CollUtil.newArrayList(MetricsType.LOCAL.getType()));
        return Result.succeed(data);
    }

    @GetMapping("/getFlinkData")
    @ApiOperation("Get Flink Data")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "Start Time", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "endTime", value = "End Time", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "taskIds", value = "Task Ids", required = true, dataType = "String")
    })
    public Result<List<MetricsVO>> getFlinkData(@RequestParam Long startTime, Long endTime, String flinkJobIds) {
        return Result.succeed(monitorService.getData(
                DateUtil.date(startTime),
                DateUtil.date(Opt.ofNullable(endTime).orElse(DateUtil.date().getTime())),
                Arrays.asList(flinkJobIds.split(","))));
    }

    @PutMapping("/saveFlinkMetrics/{layout}")
    @ApiOperation("Save Flink Metrics")
    @Log(title = "Save Flink Metrics", businessType = BusinessType.INSERT)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "layout", value = "Layout Name", required = true, dataType = "String"),
        @ApiImplicitParam(
                name = "metricsList",
                value = "Metrics List",
                required = true,
                dataType = "List<MetricsLayoutDTO>")
    })
    public Result<Void> saveFlinkMetricLayout(
            @PathVariable(value = "layout") String layoutName, @RequestBody List<MetricsLayoutDTO> metricsList) {
        monitorService.saveFlinkMetricLayout(layoutName, metricsList);
        jobInstanceService.refreshJobByTaskIds(
                metricsList.stream().map(MetricsLayoutDTO::getTaskId).distinct().toArray(Integer[]::new));
        return Result.succeed();
    }

    @GetMapping("/getMetricsLayout")
    @ApiOperation("Get Metrics Layout to Display")
    public Result<List<MetricsLayoutVo>> getMetricsLayout() {
        return Result.succeed(monitorService.getMetricsLayout());
    }

    @GetMapping("/getMetricsLayoutByName")
    @ApiOperation("Get Metrics Layout by task to Display")
    @ApiImplicitParam(name = "layoutName", value = "Layout Name", required = true, dataType = "String")
    public Result<List<Metrics>> getMetricsLayoutByName(@RequestParam String layoutName) {
        return Result.succeed(monitorService.getMetricsLayoutByName(layoutName));
    }

    @GetMapping("/getJvmInfo")
    @ApiOperation("Get Jvm Data Display")
    public SseEmitter getJvmInfo() {
        return monitorService.sendJvmInfo();
    }
}
