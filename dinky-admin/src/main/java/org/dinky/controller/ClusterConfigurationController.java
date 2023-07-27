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

import io.swagger.annotations.ApiOperation;
import org.dinky.annotation.Log;
import org.dinky.assertion.Asserts;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.gateway.result.TestResult;
import org.dinky.service.ClusterConfigurationService;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterConfigController
 *
 * @since 2021/11/6 21:16
 */
@Slf4j
@RestController
@RequestMapping("/api/clusterConfiguration")
@RequiredArgsConstructor
public class ClusterConfigurationController {

    private final ClusterConfigurationService clusterConfigurationService;

    /** 新增或者更新 */
    @PutMapping
    @Log(title = "Cluster Config Save Or Update", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Cluster Config Save Or Update")
    public Result<Void> saveOrUpdate(@RequestBody ClusterConfiguration clusterConfiguration) {
        Integer id = clusterConfiguration.getId();
        TestResult testResult = clusterConfigurationService.testGateway(clusterConfiguration);
        clusterConfiguration.setIsAvailable(testResult.isAvailable());
        if (clusterConfigurationService.saveOrUpdate(clusterConfiguration)) {
            return Result.succeed(Asserts.isNotNull(id) ? "修改成功" : "新增成功");
        } else {
            return Result.failed(Asserts.isNotNull(id) ? "修改失败" : "新增失败");
        }
    }

    /** 动态查询列表 */
    @PostMapping
    @Log(title = "Cluster Config List", businessType = BusinessType.QUERY)
    @ApiOperation("Cluster Config List")
    public ProTableResult<ClusterConfiguration> listClusterConfigs(@RequestBody JsonNode para) {
        return clusterConfigurationService.selectForProTable(para);
    }

    /** 批量删除 */
    @DeleteMapping
    @Deprecated
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!clusterConfigurationService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /** 获取指定ID的信息 */
    @PostMapping("/getOneById")
    @Deprecated
    public Result<ClusterConfiguration> getOneById(
            @RequestBody ClusterConfiguration clusterConfiguration) {
        clusterConfiguration = clusterConfigurationService.getById(clusterConfiguration.getId());
        return Result.succeed(clusterConfiguration);
    }

    /** 获取可用的集群列表 */
    @GetMapping("/listEnabledAll")
    @Log(title = "Cluster Config List Enabled All", businessType = BusinessType.QUERY)
    @ApiOperation("Cluster Config List Enabled All")
    public Result<List<ClusterConfiguration>> listEnabledAll() {
        List<ClusterConfiguration> clusters = clusterConfigurationService.listEnabledAll();
        return Result.succeed(clusters);
    }

    /**
     * delete by id
     *
     * @param id {@link Integer}
     * @return {@link Result}<{@link Void}>
     */
    @DeleteMapping("/delete")
    @Log(title = "Cluster Config Delete by id", businessType = BusinessType.DELETE)
    @ApiOperation("Cluster Config Delete by id")
    public Result<Void> deleteById(@RequestParam("id") Integer id) {
        boolean removeById = clusterConfigurationService.removeById(id);
        if (removeById) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * enable by id
     *
     * @param id {@link Integer}
     * @return {@link Result}<{@link Void}>
     */
    @PutMapping("/enable")
    @Log(title = "Update Cluster Config Status", businessType = BusinessType.UPDATE)
    @ApiOperation("Update Cluster Config Status")
    public Result<Void> enable(@RequestParam("id") Integer id) {
        if (clusterConfigurationService.enable(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * test connection
     *
     * @param clusterConfiguration {@link ClusterConfiguration}
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/testConnect")
    @Log(title = "Test Connection", businessType = BusinessType.TEST)
    @ApiOperation("Test Connection")
    public Result<Void> testConnect(@RequestBody ClusterConfiguration clusterConfiguration) {
        TestResult testResult = clusterConfigurationService.testGateway(clusterConfiguration);
        if (testResult.isAvailable()) {
            return Result.succeed(Status.TEST_CONNECTION_SUCCESS);
        } else {
            return Result.failed(testResult.getError());
        }
    }
}
