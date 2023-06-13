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

import org.dinky.data.enums.Status;
import org.dinky.data.model.Cluster;
import org.dinky.data.model.JobInstance;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.JobInstanceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Result<Void> saveOrUpdate(@RequestBody Cluster cluster) throws Exception {
        cluster.setAutoRegisters(false);
        Integer id = cluster.getId();
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
    public Result<Void> enableCluster(@RequestParam Integer id) {
        Boolean enabled = clusterInstanceService.enableClusterInstance(id);
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
    public ProTableResult<Cluster> listClusters(@RequestBody JsonNode para) {
        return clusterInstanceService.selectForProTable(para);
    }

    /**
     * batch delete cluster instances , this method is {@link Deprecated}, please use {@link
     * ClusterInstanceController#deleteClusterInstanceById(Integer id) }
     *
     * @param para {@link JsonNode} cluster instance ids
     * @return {@link Result}<{@link Void}>
     */
    @DeleteMapping
    @Deprecated
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<JobInstance> instances = jobInstanceService.listJobInstanceActive();
            Set<Integer> ids =
                    instances.stream().map(JobInstance::getClusterId).collect(Collectors.toSet());
            List<String> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (ids.contains(id) || !clusterInstanceService.removeById(id)) {
                    error.add(clusterInstanceService.getById(id).getName());
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                if (para.size() > error.size()) {
                    return Result.succeed(
                            "删除部分成功，但"
                                    + error
                                    + "删除失败，共"
                                    + error.size()
                                    + "次失败。\n请检查集群实例是否已被集群使用！");
                } else {
                    return Result.succeed(
                            error + "删除失败，共" + error.size() + "次失败。\n请检查集群实例是否已被集群使用！");
                }
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * get all enable cluster instances
     *
     * @return {@link Result}<{@link List}<{@link Cluster}>>
     */
    @GetMapping("/listEnabledAll")
    public Result<List<Cluster>> listEnabledAll() {
        List<Cluster> clusters = clusterInstanceService.listEnabledAll();
        return Result.succeed(clusters);
    }

    /**
     * get session enable cluster instances , this method is {@link Deprecated}
     *
     * @return {@link Result}<{@link List}<{@link Cluster}>>
     */
    @GetMapping("/listSessionEnable")
    @Deprecated
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
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> recycleCluster() {
        return Result.succeed(
                clusterInstanceService.recycleCluster(), Status.CLUSTER_INSTANCE_RECYCLE_SUCCESS);
    }

    /**
     * kill cluster instance
     *
     * @param id {@link Integer} cluster instance id
     * @return {@link Result}<{@link Void}>
     */
    @GetMapping("/killCluster")
    public Result<Void> killCluster(@RequestParam("id") Integer id) {
        clusterInstanceService.killCluster(id);
        return Result.succeed("Kill Cluster Succeed.");
    }

    /**
     * batch kill cluster instances , this method is {@link Deprecated}
     *
     * @param para {@link JsonNode} cluster instance ids
     * @return {@link Result}<{@link Void}>
     */
    @DeleteMapping("/killMulCluster")
    @Deprecated
    public Result<Void> killMulCluster(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            for (final JsonNode item : para) {
                clusterInstanceService.killCluster(item.asInt());
            }
        }
        return Result.succeed(Status.CLUSTER_INSTANCE_KILL);
    }

    /**
     * deploy session cluster by id
     *
     * @param id {@link Integer} cluster instance id
     * @return {@link Result}<{@link Cluster}>
     */
    @GetMapping("/deploySessionCluster")
    public Result<Cluster> deploySessionCluster(@RequestParam("id") Integer id) {
        return Result.succeed(
                clusterInstanceService.deploySessionCluster(id), Status.CLUSTER_INSTANCE_DEPLOY);
    }
}
