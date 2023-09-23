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
import org.dinky.data.annotation.Log;
import org.dinky.data.dto.StudioCADTO;
import org.dinky.data.dto.StudioDDLDTO;
import org.dinky.data.dto.StudioExecuteDTO;
import org.dinky.data.dto.StudioMetaStoreDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.FlinkColumn;
import org.dinky.data.model.Schema;
import org.dinky.data.result.IResult;
import org.dinky.data.result.Result;
import org.dinky.data.result.SelectResult;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.explainer.lineage.LineageResult;
import org.dinky.job.JobResult;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.service.StudioService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    /** 执行Sql */
    @PostMapping("/executeSql")
    @ApiOperation("Execute Sql")
    @Log(title = "Execute Sql", businessType = BusinessType.EXECUTE)
    @ApiImplicitParam(
            name = "studioExecuteDTO",
            value = "Execute Sql",
            required = true,
            dataType = "StudioExecuteDTO",
            paramType = "body")
    public Result<JobResult> executeSql(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        try {
            JobResult jobResult = studioService.executeSql(studioExecuteDTO);
            return Result.succeed(jobResult, Status.EXECUTE_SUCCESS);
        } catch (Exception ex) {
            JobResult jobResult = new JobResult();
            jobResult.setJobConfig(studioExecuteDTO.getJobConfig());
            jobResult.setSuccess(false);
            jobResult.setStatement(studioExecuteDTO.getStatement());
            jobResult.setError(ex.toString());
            return Result.failed(jobResult, Status.EXECUTE_FAILED);
        }
    }

    /** 解释Sql */
    @PostMapping("/explainSql")
    @ApiOperation("Explain Sql")
    @ApiImplicitParam(
            name = "studioExecuteDTO",
            value = "Explain Sql",
            required = true,
            dataType = "StudioExecuteDTO",
            paramType = "body")
    public Result<List<SqlExplainResult>> explainSql(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        return Result.succeed(studioService.explainSql(studioExecuteDTO), "解释成功");
    }

    /** 获取执行图 */
    @PostMapping("/getStreamGraph")
    @ApiOperation("Get Stream Graph")
    @ApiImplicitParam(
            name = "studioExecuteDTO",
            value = "Get Stream Graph",
            required = true,
            dataType = "StudioExecuteDTO",
            paramType = "body")
    public Result<ObjectNode> getStreamGraph(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        return Result.succeed(studioService.getStreamGraph(studioExecuteDTO));
    }

    /** 获取sql的jobplan */
    @PostMapping("/getJobPlan")
    @ApiOperation("Get Job Execute Plan")
    @ApiImplicitParam(
            name = "studioExecuteDTO",
            value = "Get Job Execute Plan",
            required = true,
            dataType = "StudioExecuteDTO",
            paramType = "body")
    public Result<ObjectNode> getJobPlan(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        try {
            return Result.succeed(studioService.getJobPlan(studioExecuteDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    /** 进行DDL操作 */
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
    /** 根据jobId获取数据 */
    @GetMapping("/getCommonSqlData")
    @ApiOperation("Get Common Sql Data")
    @ApiImplicitParam(
            name = "taskId",
            value = "Get Common Sql Data",
            required = true,
            dataType = "Integer",
            paramType = "query")
    public Result<JdbcSelectResult> getJobData(@RequestParam Integer taskId) {
        return Result.succeed(studioService.getCommonSqlData(taskId));
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
    public Result<LineageResult> getLineage(@RequestBody StudioCADTO studioCADTO) {
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

    /** 停止任务 */
    @GetMapping("/cancel")
    @ApiOperation("Cancel Flink Job")
    @Log(title = "Cancel Flink Job", businessType = BusinessType.REMOTE_OPERATION)
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "clusterId",
                value = "clusterId",
                required = true,
                dataType = "Integer",
                paramType = "query"),
        @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "String", paramType = "query")
    })
    public Result<Boolean> cancelFlinkJob(@RequestParam Integer clusterId, @RequestParam String jobId) {
        return Result.succeed(studioService.cancelFlinkJob(clusterId, jobId), Status.STOP_SUCCESS);
    }

    /** savepoint */
    @GetMapping("/savepoint")
    @ApiOperation("Savepoint Trigger")
    @Log(title = "Savepoint Trigger", businessType = BusinessType.REMOTE_OPERATION)
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "clusterId",
                value = "clusterId",
                required = true,
                dataType = "Integer",
                paramType = "query"),
        @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(
                name = "savePointType",
                value = "savePointType",
                required = true,
                dataType = "String",
                paramType = "query"),
        @ApiImplicitParam(name = "name", value = "name", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "taskId", value = "taskId", required = true, dataType = "Integer", paramType = "query")
    })
    public Result<Boolean> savepointTrigger(
            @RequestParam Integer clusterId,
            @RequestParam String jobId,
            @RequestParam String savePointType,
            @RequestParam String name,
            @RequestParam Integer taskId) {
        return Result.succeed(
                studioService.savepointTrigger(taskId, clusterId, jobId, savePointType, name), "savepoint 成功");
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
    @GetMapping("/getMSFlinkColumns")
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
    public Result<List<FlinkColumn>> getMSFlinkColumns(
            @RequestParam Integer envId,
            @RequestParam String catalog,
            @RequestParam String database,
            @RequestParam String table) {
        StudioMetaStoreDTO studioMetaStoreDTO = new StudioMetaStoreDTO();
        studioMetaStoreDTO.setEnvId(envId);
        studioMetaStoreDTO.setCatalog(catalog);
        studioMetaStoreDTO.setDatabase(database);
        studioMetaStoreDTO.setTable(table);
        return Result.succeed(studioService.getMSFlinkColumns(studioMetaStoreDTO));
    }
}
