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
import org.dinky.data.dto.AssignMenuToRoleDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.result.Result;
import org.dinky.service.RoleMenuService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Role Menu Controller")
@RequestMapping("/api/roleMenu")
@SaCheckLogin
@RequiredArgsConstructor
public class RoleMenuController {

    private final RoleMenuService roleMenuService;

    /**
     * assign menus to role
     *
     * @param assignMenuToRoleDTO
     * @return {@link Result} with {@link Void}
     */
    @PostMapping("assignMenuToRole")
    @Log(title = "Assign Menus to Role ", businessType = BusinessType.GRANT)
    @ApiOperation("Assign Menus to Role")
    @ApiImplicitParam(
            name = "assignMenuToRoleDto",
            value = "Assign Menu To Role Dto",
            required = true,
            dataType = "AssignMenuToRoleDto",
            paramType = "body")
    @SaCheckPermission(PermissionConstants.AUTH_ROLE_ASSIGN_MENU)
    public Result<Void> assignMenuToRole(@RequestBody AssignMenuToRoleDTO assignMenuToRoleDTO) {
        return roleMenuService.assignMenuToRole(assignMenuToRoleDTO);
    }
}
