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
import org.dinky.data.model.SysToken;
import org.dinky.data.model.udf.UDFTemplate;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.TokenService;

import org.springframework.transaction.annotation.Transactional;
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
import cn.dev33.satoken.stp.StpLogic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TokenController
 */
@Slf4j
@Api(tags = "Token Controller")
@RestController
@RequestMapping("/api/token")
@SaCheckLogin
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final StpLogic stpLogic;

    /**
     * get udf template list
     *
     * @param params {@link JsonNode}
     * @return {@link ProTableResult} <{@link UDFTemplate}>
     */
    @PostMapping("/list")
    @ApiOperation("Get Token List")
    @ApiImplicitParam(name = "params", value = "params", dataType = "JsonNode", paramType = "body", required = true)
    public ProTableResult<SysToken> listToken(@RequestBody JsonNode params) {
        return tokenService.selectForProTable(params);
    }

    /**
     * save or update udf template
     *
     * @param sysToken {@link SysToken}
     * @return {@link Result} <{@link String}>
     */
    @PutMapping("/saveOrUpdateToken")
    @ApiOperation("Insert or Update Token")
    @Log(title = "Insert or Update Token", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(name = "sysToken", value = "sysToken", dataType = "SysToken", paramType = "body", required = true)
    @SaCheckPermission(
            value = {PermissionConstants.AUTH_TOKEN_ADD, PermissionConstants.AUTH_TOKEN_EDIT},
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateToken(@RequestBody SysToken sysToken) {
        sysToken.setSource(SysToken.Source.CUSTOM);
        return tokenService.saveOrUpdate(sysToken)
                ? Result.succeed(Status.SAVE_SUCCESS)
                : Result.failed(Status.SAVE_FAILED);
    }

    /**
     * delete Token by id
     *
     * @param id {@link Integer}
     * @return {@link Result} <{@link Void}>
     */
    @DeleteMapping("/delete")
    @Log(title = "Delete Token By Id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete Token By Id")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @Transactional(rollbackFor = Exception.class)
    @SaCheckPermission(value = PermissionConstants.AUTH_TOKEN_DELETE)
    public Result<Void> deleteToken(@RequestParam Integer id) {
        if (tokenService.removeTokenById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * delete Token by id
     *
     * @return {@link Result} <{@link Void}>
     */
    @PostMapping("/buildToken")
    @Log(title = "Build Token", businessType = BusinessType.OTHER)
    @ApiOperation("Build Token")
    public Result<String> buildToken() {
        return Result.succeed(stpLogic.createTokenValue(null, null, 1, null), Status.SUCCESS);
    }
}
