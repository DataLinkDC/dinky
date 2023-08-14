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

import org.dinky.data.annotation.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Cluster;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.JobInstanceService;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** ClusterInstanceController */
@Slf4j
@RestController
@RequestMapping("/api/cluster")
@RequiredArgsConstructor
public class ClusterInstanceController {

    private final ClusterInstanceService clusterInstanceService;
    private final JobInstanceService jobInstanceService;

    /**
     * added or updated cluster instance
     *
     * @param cluster {@link Cluster} cluster instance
     * @return {@link Result}<{@link Void}>
     * @throws Exception exception
     */
    @PutMapping
    @Log(title = "Insert Or Update Cluster Instance", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update Cluster Instance")
    public Result<Void> saveOrUpdateClusterInstance(@RequestBody Cluster cluster) throws Exception {
        cluster.setAutoRegisters(false);
        clusterInstanceService.registersCluster(cluster);
        return Result.succeed(Status.SAVE_SUCCESS);
    }

    /**
     * enable cluster instance or disable cluster instance
     *
     * @param id {@link Integer} cluster instance id
     * @return {@link Result}<{@link Void}>
     */
    @PutMapping("/enable")
    @Log(title = "Update Cluster Instance Status", businessType = BusinessType.UPDATE)
    @ApiOperation("Update Cluster Instance Status")
    public Result<Void> modifyClusterInstanceStatus(@RequestParam Integer id) {
        Boolean enabled = clusterInstanceService.modifyClusterInstanceStatus(id);
        if (enabled) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * delete cluster instance by id
     *
     * @param id {@link Integer} cluster instance id
     * @return {@link Result}<{@link Void}>
     */
    @DeleteMapping("/delete")
    @Log(title = "Delete Cluster Instance by id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete Cluster Instance by id")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteClusterInstanceById(@RequestParam Integer id) {
        Boolean deleted = clusterInstanceService.deleteClusterInstanceById(id);
        if (deleted) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * list cluster instances
     *
     * @param para {@link JsonNode} query parameters
     * @return {@link ProTableResult}<{@link Cluster}>
     */
    @PostMapping
    @ApiOperation("Cluster Instance List")
    public ProTableResult<Cluster> listClusters(@RequestBody JsonNode para) {
        return clusterInstanceService.selectForProTable(para);
    }

    /**
     * get all enable cluster instances
     *
     * @return {@link Result}<{@link List}<{@link Cluster}>>
     */
    @GetMapping("/listEnabledAll")
    @ApiOperation("Get all enable cluster instances")
    public Result<List<Cluster>> listEnabledAllClusterInstance() {
        List<Cluster> clusters = clusterInstanceService.listEnabledAllClusterInstance();
        return Result.succeed(clusters);
    }

    /**
     * get session enable cluster instances , this method is {@link Deprecated}
     *
     * @return {@link Result}<{@link List}<{@link Cluster}>>
     */
    @GetMapping("/listSessionEnable")
    @ApiOperation("Get session enable cluster instances")
    public Result<List<Cluster>> listSessionEnable() {
        List<Cluster> clusters = clusterInstanceService.listSessionEnable();
        return Result.succeed(clusters);
    }

    /**
     * heartbeat all cluster instances
     *
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/heartbeats")
    @Log(title = "Cluster Instance Heartbeat", businessType = BusinessType.UPDATE)
    @ApiOperation("Cluster Instance Heartbeat")
    public Result<Void> heartbeat() {
        List<Cluster> clusters = clusterInstanceService.list();
        for (Cluster cluster : clusters) {
            clusterInstanceService.registersCluster(cluster);
        }
        return Result.succeed(Status.CLUSTER_INSTANCE_HEARTBEAT_SUCCESS);
    }

    /**
     * recycle cluster instances
     *
     * @return {@link Result}<{@link Integer}>
     */
    @DeleteMapping("/recycle")
    @Log(title = "Cluster Instance Recycle", businessType = BusinessType.DELETE)
    @ApiOperation("Cluster Instance Recycle")
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> recycleCluster() {
        return Result.succeed(clusterInstanceService.recycleCluster(), Status.CLUSTER_INSTANCE_RECYCLE_SUCCESS);
    }

    /**
     * kill cluster instance
     *
     * @param id {@link Integer} cluster instance id
     * @return {@link Result}<{@link Void}>
     */
    @GetMapping("/killCluster")
    @Log(title = "Cluster Instance Kill", businessType = BusinessType.UPDATE)
    @ApiOperation("Cluster Instance Kill")
    public Result<Void> killClusterInstance(@RequestParam("id") Integer id) {
        clusterInstanceService.killCluster(id);
        return Result.succeed("Kill Cluster Succeed.");
    }

    @PutMapping("/deploySessionClusterInstance")
    @Log(title = "Deploy Session Cluster Instance", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Deploy Session Cluster Instance")
    public Result<Cluster> deploySessionClusterInstance(@RequestParam("id") Integer id) {
        return Result.succeed(clusterInstanceService.deploySessionCluster(id), Status.CLUSTER_INSTANCE_DEPLOY);
    }
}
