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

package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioCADTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.dto.StudioMetaStoreDTO;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.job.JobResult;
import com.dlink.model.Catalog;
import com.dlink.model.FlinkColumn;
import com.dlink.model.Schema;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.service.StudioService;
import com.dlink.session.SessionInfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

/**
 * StudioController
 *
 * @author wenmo
 * @since 2021/5/30 11:05
 */
@Slf4j
@RestController
@RequestMapping("/api/studio")
public class StudioController {

    private final StudioService studioService;

    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }

    /**
     * 执行Sql
     */
    @PostMapping("/executeSql")
    public Result<JobResult> executeSql(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        JobResult jobResult = studioService.executeSql(studioExecuteDTO);
        return Result.succeed(jobResult, "执行成功");
    }

    /**
     * 解释Sql
     */
    @PostMapping("/explainSql")
    public Result<List<SqlExplainResult>> explainSql(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        return Result.succeed(studioService.explainSql(studioExecuteDTO), "解释成功");
    }

    /**
     * 获取执行图
     */
    @PostMapping("/getStreamGraph")
    public Result<ObjectNode> getStreamGraph(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        return Result.succeed(studioService.getStreamGraph(studioExecuteDTO), "获取执行图成功");
    }

    /**
     * 获取sql的jobplan
     */
    @PostMapping("/getJobPlan")
    public Result<ObjectNode> getJobPlan(@RequestBody StudioExecuteDTO studioExecuteDTO) {
        try {
            return Result.succeed(studioService.getJobPlan(studioExecuteDTO), "获取作业计划成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 进行DDL操作
     */
    @PostMapping("/executeDDL")
    public Result<IResult> executeDDL(@RequestBody StudioDDLDTO studioDDLDTO) {
        IResult result = studioService.executeDDL(studioDDLDTO);
        return Result.succeed(result, "执行成功");
    }

    /**
     * 根据jobId获取数据
     */
    @GetMapping("/getJobData")
    public Result<SelectResult> getJobData(@RequestParam String jobId) {
        return Result.succeed(studioService.getJobData(jobId), "获取成功");
    }

    /**
     * 获取单任务实例的血缘分析
     */
    @PostMapping("/getLineage")
    public Result<LineageResult> getLineage(@RequestBody StudioCADTO studioCADTO) {
        return Result.succeed(studioService.getLineage(studioCADTO), "刷新成功");
    }

    /**
     * 创建session
     */
    @PutMapping("/createSession")
    public Result<SessionInfo> createSession(@RequestBody SessionDTO sessionDTO) {
        return Result.succeed(studioService.createSession(sessionDTO, "admin"), "创建成功");
    }

    /**
     * 清除指定session
     */
    @DeleteMapping("/clearSession")
    public Result<String> clearSession(@RequestBody JsonNode para) {
        if (para.size() <= 0) {
            return Result.failed("请选择要清除的记录");
        }

        List<String> error = new ArrayList<>();
        for (final JsonNode item : para) {
            String session = item.asText();
            if (!studioService.clearSession(session)) {
                error.add(session);
            }
        }

        if (error.isEmpty()) {
            return Result.succeed("清除成功");
        } else {
            return Result.succeed(String.format("清除部分成功，但%s清除失败，共%d次失败。", error, error.size()));
        }
    }

    /**
     * 获取session列表
     */
    @GetMapping("/listSession")
    public Result<List<SessionInfo>> listSession() {
        return Result.succeed(studioService.listSession("admin"), "获取成功");
    }

    /**
     * 获取flinkJobs列表
     */
    @GetMapping("/listJobs")
    public Result<JsonNode[]> listJobs(@RequestParam Integer clusterId) {
        List<JsonNode> jobs = studioService.listJobs(clusterId);
        return Result.succeed(jobs.toArray(new JsonNode[0]), "获取成功");
    }

    /**
     * 停止任务
     */
    @GetMapping("/cancel")
    public Result<Boolean> cancel(@RequestParam Integer clusterId, @RequestParam String jobId) {
        return Result.succeed(studioService.cancel(clusterId, jobId), "停止成功");
    }

    /**
     * savepoint
     */
    @GetMapping("/savepoint")
    public Result<Boolean> savepoint(@RequestParam Integer clusterId, @RequestParam String jobId, @RequestParam String savePointType, @RequestParam String name, @RequestParam Integer taskId) {
        return Result.succeed(studioService.savepoint(taskId, clusterId, jobId, savePointType, name), "savepoint 成功");
    }

    /**
     * 获取 Meta Store Catalog 和 Database
     */
    @PostMapping("/getMSCatalogs")
    public Result<List<Catalog>> getMSCatalogs(@RequestBody StudioMetaStoreDTO studioMetaStoreDTO) {
        return Result.succeed(studioService.getMSCatalogs(studioMetaStoreDTO), "获取成功");
    }

    /**
     * 获取 Meta Store Schema/Database 信息
     */
    @PostMapping("/getMSSchemaInfo")
    public Result<Schema> getMSSchemaInfo(@RequestBody StudioMetaStoreDTO studioMetaStoreDTO) {
        return Result.succeed(studioService.getMSSchemaInfo(studioMetaStoreDTO), "获取成功");
    }

    /**
     * 获取 Meta Store Flink Column 信息
     */
    @GetMapping("/getMSFlinkColumns")
    public Result<List<FlinkColumn>> getMSFlinkColumns(@RequestParam Integer envId, @RequestParam String catalog, @RequestParam String database, @RequestParam String table) {
        StudioMetaStoreDTO studioMetaStoreDTO = new StudioMetaStoreDTO();
        studioMetaStoreDTO.setEnvId(envId);
        studioMetaStoreDTO.setCatalog(catalog);
        studioMetaStoreDTO.setDatabase(database);
        studioMetaStoreDTO.setTable(table);
        return Result.succeed(studioService.getMSFlinkColumns(studioMetaStoreDTO), "获取成功");
    }
}
