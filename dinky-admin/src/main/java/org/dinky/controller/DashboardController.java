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

import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.DashboardDTO;
import org.dinky.data.model.Dashboard;
import org.dinky.data.result.Result;
import org.dinky.service.DashboardService;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "ClusterInstance Instance Controller")
@RequestMapping("/api/dashboard")
@SaCheckLogin
@AllArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @RequestMapping(
            value = "/saveOrUpdate",
            method = {RequestMethod.POST, RequestMethod.PUT})
    @SaCheckPermission(
            value = {PermissionConstants.DASHBOARD_LIST_ADD, PermissionConstants.DASHBOARD_LIST_EDIT},
            mode = SaMode.OR)
    public Result<Void> saveOrUpdate(@RequestBody @Validated DashboardDTO dashboard) {
        dashboardService.saveOrUpdate(BeanUtil.toBean(dashboard, Dashboard.class));
        return Result.succeed();
    }

    @GetMapping("/getDashboardList")
    public Result<List<Dashboard>> getDashboardList() {
        return Result.succeed(dashboardService.list());
    }

    @GetMapping("/getDashboardById")
    @SaCheckPermission(PermissionConstants.DASHBOARD_LIST_VIEW)
    public Result<Dashboard> getDashboardById(Integer id) {
        return Result.succeed(dashboardService.getById(id));
    }

    @DeleteMapping("/delete")
    @SaCheckPermission(PermissionConstants.DASHBOARD_LIST_DELETE)
    public Result<Void> delete(Integer id) {
        dashboardService.removeById(id);
        return Result.succeed();
    }
}
