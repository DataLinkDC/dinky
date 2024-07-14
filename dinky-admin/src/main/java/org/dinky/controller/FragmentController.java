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
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.FragmentVariable;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.FragmentVariableService;
import org.dinky.utils.FragmentVariableUtils;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** FragmentController */
@Slf4j
@RestController
@Api(tags = "Fragment Controller")
@RequestMapping("/api/fragment")
@RequiredArgsConstructor
@SaCheckLogin
public class FragmentController {

    private final FragmentVariableService fragmentVariableService;

    /**
     * save or update
     *
     * @param fragmentVariable {@link FragmentVariable}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping
    @Log(title = "Insert Or Update Fragment", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update Fragment")
    @ApiImplicitParam(
            name = "fragmentVariable",
            value = "FragmentVariable",
            required = true,
            dataType = "FragmentVariable",
            paramType = "body",
            dataTypeClass = FragmentVariable.class)
    @SaCheckPermission(
            value = {PermissionConstants.REGISTRATION_FRAGMENT_ADD, PermissionConstants.REGISTRATION_FRAGMENT_EDIT},
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateFragment(@RequestBody FragmentVariable fragmentVariable) {
        if (fragmentVariableService.saveOrUpdate(fragmentVariable)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * query list
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link FragmentVariable}
     */
    @PostMapping
    @Log(title = "FragmentVariable List", businessType = BusinessType.QUERY)
    @ApiOperation("FragmentVariable List")
    @ApiImplicitParam(
            name = "para",
            value = "JsonNode",
            required = true,
            dataType = "JsonNode",
            paramType = "body",
            dataTypeClass = JsonNode.class)
    public ProTableResult<FragmentVariable> listFragmentVariable(@RequestBody JsonNode para) {
        final ProTableResult<FragmentVariable> result = fragmentVariableService.selectForProTable(para);
        // 敏感值不返回
        if (result != null && result.getData() != null) {
            for (FragmentVariable variable : result.getData()) {
                if (FragmentVariableUtils.isSensitive(variable.getName())) {
                    variable.setFragmentValue(null);
                }
            }
        }
        return result;
    }

    /**
     * delete by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    @Log(title = "FragmentVariable Delete", businessType = BusinessType.DELETE)
    @ApiOperation("FragmentVariable Delete")
    @ApiImplicitParam(
            name = "id",
            value = "FragmentVariable Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_FRAGMENT_DELETE)
    public Result<Void> deleteById(@RequestParam Integer id) {
        if (fragmentVariableService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * enable or disable
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/enable")
    @Log(title = "Update FragmentVariable Status", businessType = BusinessType.UPDATE)
    @ApiOperation("Update FragmentVariable Status")
    @ApiImplicitParam(
            name = "id",
            value = "FragmentVariable Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_FRAGMENT_EDIT)
    public Result<Void> modifyFragmentStatus(@RequestParam Integer id) {
        if (fragmentVariableService.modifyFragmentStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }
}
