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

import org.dinky.data.annotations.Log;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.ClusterInstanceDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.result.Result;
import org.dinky.service.ClusterInstanceService;

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

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** ClusterInstanceController */
@Slf4j
@RestController
@Api(tags = "ClusterInstance Instance Controller")
@RequestMapping("/api/cluster")
@RequiredArgsConstructor
public class ClusterInstanceController {

    private final ClusterInstanceService clusterInstanceService;

    /**
     * added or updated cluster instance
     *
     * @param clusterInstanceDTO {@link ClusterInstanceDTO} cluster instance
     * @return {@link Result}<{@link Void}>
     */
    @PutMapping
    @Log(title = "Insert Or Update Cluster Instance", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update Cluster Instance")
    @ApiImplicitParam(
            name = "clusterInstanceDTO",
            value = "ClusterInstanceDTO Instance",
            dataType = "ClusterInstanceDTO",
            paramType = "body",
            required = true,
            dataTypeClass = ClusterInstanceDTO.class)
    @SaCheckPermission(
            value = {
                PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_EDIT,
                PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_ADD
            },
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateClusterInstance(@RequestBody ClusterInstanceDTO clusterInstanceDTO) {
        if (clusterInstanceDTO.getAutoRegisters() == null) {
            clusterInstanceDTO.setAutoRegisters(false);
        }
        clusterInstanceService.registersCluster(clusterInstanceDTO);
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
    @ApiImplicitParam(
            name = "id",
            value = "ClusterInstance Instance Id",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_EDIT})
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
    @ApiImplicitParam(
            name = "id",
            value = "Cluster Instance Id",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_DELETE})
    public Result<Void> deleteClusterInstanceById(@RequestParam Integer id) {
        Boolean deleted = clusterInstanceService.deleteClusterInstanceById(id);
        if (deleted) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    @GetMapping("/list")
    @ApiOperation("Cluster Instance List")
    @ApiImplicitParam(
            name = "keyword",
            value = "Query keyword",
            dataType = "String",
            paramType = "query",
            required = true,
            dataTypeClass = JsonNode.class)
    public Result<List<ClusterInstance>> listClusterInstance(
            @RequestParam(defaultValue = "") String searchKeyWord,
            @RequestParam(defaultValue = "false") boolean isAutoCreate) {
        return Result.succeed(clusterInstanceService.selectListByKeyWord(searchKeyWord, isAutoCreate));
    }

    /**
     * get all enable cluster instances
     *
     * @return {@link Result}<{@link List}<{@link ClusterInstance}>>
     */
    @GetMapping("/listEnabledAll")
    @ApiOperation("Get all enable cluster instances")
    public Result<List<ClusterInstance>> listEnabledAllClusterInstance() {
        List<ClusterInstance> clusterInstances = clusterInstanceService.listEnabledAllClusterInstance();
        return Result.succeed(clusterInstances);
    }

    /**
     * get session enable cluster instances , this method is {@link Deprecated}
     *
     * @return {@link Result}<{@link List}<{@link ClusterInstance}>>
     */
    @GetMapping("/listSessionEnable")
    @ApiOperation(
            value = "Get Enable Session ClusterInstance",
            notes = "Get All Enable Cluster Instances Of Session Type")
    public Result<List<ClusterInstance>> listSessionEnable() {
        List<ClusterInstance> clusterInstances = clusterInstanceService.listSessionEnable();
        return Result.succeed(clusterInstances);
    }

    /**
     * heartbeat all cluster instances
     *
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/heartbeats")
    @Log(title = "Cluster Instance Heartbeat", businessType = BusinessType.UPDATE)
    @ApiOperation("Cluster Instance Heartbeat")
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_HEARTBEATS})
    public Result<Long> heartbeat() {
        return Result.succeed(clusterInstanceService.heartbeat(), Status.CLUSTER_INSTANCE_HEARTBEAT_SUCCESS);
    }

    /**
     * kill cluster instance
     *
     * @param id {@link Integer} cluster instance id
     * @return {@link Result}<{@link Void}>
     */
    @PutMapping("/killCluster")
    @Log(title = "Cluster Instance Kill", businessType = BusinessType.UPDATE)
    @ApiOperation("Cluster Instance Kill")
    @ApiImplicitParam(
            name = "id",
            value = "ClusterInstance Instance Id",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_CLUSTER_INSTANCE_KILL})
    public Result<Void> killClusterInstance(@RequestParam("id") Integer id) {
        clusterInstanceService.killCluster(id);
        return Result.succeed("Kill ClusterInstance Succeed.");
    }

    @PutMapping("/deploySessionClusterInstance")
    @Log(title = "Deploy Session Cluster Instance", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Deploy Session Cluster Instance")
    @ApiImplicitParam(
            name = "id",
            value = "ClusterInstance Instance Id",
            dataType = "Integer",
            paramType = "query",
            required = true,
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(value = {PermissionConstants.REGISTRATION_CLUSTER_CONFIG_DEPLOY})
    public Result<ClusterInstance> deploySessionClusterInstance(@RequestParam("id") Integer id) {
        return Result.succeed(clusterInstanceService.deploySessionCluster(id), Status.CLUSTER_INSTANCE_DEPLOY);
    }
}
