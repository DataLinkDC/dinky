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

import org.dinky.assertion.Asserts;
import org.dinky.data.annotations.Log;
import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioLineageDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.Column;
import org.dinky.data.model.Schema;
import org.dinky.data.result.IResult;
import org.dinky.data.result.Result;
import org.dinky.data.result.SelectResult;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.service.StudioService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * StudioController
 *
 * @since 2021/5/30 11:05
 */
@Slf4j
@RestController
@Api(tags = "Data Studio Controller")
@RequestMapping("/api/studio")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @PostMapping("/executeDDL")
    @ApiOperation("Execute SQL DDL")
    @Log(title = "Execute SQL DDL", businessType = BusinessType.EXECUTE)
    @ApiImplicitParam(
            name = "studioDDLDTO",
            value = "Execute SQL DDL",
            required = true,
            dataType = "StudioDDLDTO",
            paramType = "body")
    public Result<IResult> executeDDL(@RequestBody StudioDDLDTO studioDDLDTO) {
        IResult result = studioService.executeDDL(studioDDLDTO);
        return Result.succeed(result, Status.EXECUTE_SUCCESS);
    }

    /** 根据jobId获取数据 */
    @GetMapping("/getJobData")
    @ApiOperation("Get Job Plan")
    @ApiImplicitParam(name = "jobId", value = "Get Job Plan", required = true, dataType = "String", paramType = "query")
    public Result<SelectResult> getJobData(@RequestParam String jobId) {
        return Result.succeed(studioService.getJobData(jobId));
    }

    /** 获取单任务实例的血缘分析 */
    @PostMapping("/getLineage")
    @ApiOperation("Get Job Lineage")
    @ApiImplicitParam(
            name = "studioCADTO",
            value = "Get Job Lineage",
            required = true,
            dataType = "StudioCADTO",
            paramType = "body")
    public Result<LineageResult> getLineage(@RequestBody StudioLineageDTO studioCADTO) {
        LineageResult lineage = studioService.getLineage(studioCADTO);
        return Asserts.isNull(lineage) ? Result.failed("血缘分析异常") : Result.succeed(lineage, "血缘分析成功");
    }

    /** 获取flinkJobs列表 */
    @GetMapping("/listJobs")
    @ApiOperation("List Flink Jobs")
    @ApiImplicitParam(
            name = "clusterId",
            value = "List Flink Jobs",
            required = true,
            dataType = "Integer",
            paramType = "query")
    public Result<JsonNode[]> listFlinkJobs(@RequestParam Integer clusterId) {
        List<JsonNode> jobs = studioService.listFlinkJobs(clusterId);
        return Result.succeed(jobs.toArray(new JsonNode[0]));
    }

    /** 获取 Meta Store Catalog 和 Database */
    @PostMapping("/getMSCatalogs")
    @ApiOperation("Get Catalog List")
    @ApiImplicitParam(
            name = "studioMetaStoreDTO",
            value = "Get Catalog List",
            required = true,
            dataType = "StudioMetaStoreDTO",
            paramType = "body")
    public Result<List<Catalog>> getMSCatalogs(@RequestBody StudioMetaStoreDTO studioMetaStoreDTO) {
        return Result.succeed(studioService.getMSCatalogs(studioMetaStoreDTO));
    }

    /** 获取 Meta Store Schema/Database 信息 */
    @PostMapping("/getMSSchemaInfo")
    @ApiOperation("Get Schema Info")
    @ApiImplicitParam(
            name = "studioMetaStoreDTO",
            value = "Get Schema Info",
            required = true,
            dataType = "StudioMetaStoreDTO",
            paramType = "body")
    public Result<Schema> getMSSchemaInfo(@RequestBody StudioMetaStoreDTO studioMetaStoreDTO) {
        return Result.succeed(studioService.getMSSchemaInfo(studioMetaStoreDTO));
    }

    /** 获取 Meta Store Flink Column 信息 */
    @PostMapping("/getMSColumns")
    @ApiOperation("Get Flink Column List")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "envId", value = "envId", required = true, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(
                name = "catalog",
                value = "catalog",
                required = true,
                dataType = "String",
                paramType = "query"),
        @ApiImplicitParam(
                name = "database",
                value = "database",
                required = true,
                dataType = "String",
                paramType = "query"),
        @ApiImplicitParam(name = "table", value = "table", required = true, dataType = "String", paramType = "query")
    })
    public Result<List<Column>> getMSColumns(@RequestBody StudioMetaStoreDTO studioMetaStoreDTO) {
        return Result.succeed(studioService.getMSColumns(studioMetaStoreDTO));
    }
}
