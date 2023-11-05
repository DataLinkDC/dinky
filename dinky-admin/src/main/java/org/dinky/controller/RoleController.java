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
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.RoleDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.model.Role;
import org.dinky.data.model.User;
import org.dinky.data.model.UserRole;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.RoleService;
import org.dinky.service.UserRoleService;

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

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Role Controller")
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final UserRoleService userRoleService;

    /**
     * create or update role
     *
     * @param roleDTO {@link Role}
     * @return {@link Role} of {@link Void}
     */
    @PutMapping("/addedOrUpdateRole")
    @ApiOperation("Insert Or Update Role")
    @Log(title = "Insert Or Update Role", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "roleDTO",
            value = "RoleDTO",
            required = true,
            dataType = "RoleDTO",
            paramType = "body",
            dataTypeClass = RoleDTO.class)
    @SaCheckPermission(
            value = {PermissionConstants.AUTH_ROLE_ADD, PermissionConstants.AUTH_ROLE_EDIT},
            mode = SaMode.OR)
    public Result<Void> addedOrUpdateRole(@RequestBody RoleDTO roleDTO) {
        return roleService.addedOrUpdateRole(roleDTO);
    }

    /**
     * delete role by id
     *
     * @return delete result code
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete Role By Id")
    @Log(title = "Delete Role By Id", businessType = BusinessType.DELETE)
    @ApiImplicitParam(
            name = "id",
            value = "id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    @SaCheckPermission(value = PermissionConstants.AUTH_ROLE_DELETE)
    public Result<Void> deleteRoleById(@RequestParam Integer id) {
        return roleService.deleteRoleById(id);
    }

    /**
     * query role list
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult}<{@link Role}>
     */
    @PostMapping
    @ApiOperation("Query Role List")
    @ApiImplicitParam(
            name = "para",
            value = "para",
            required = true,
            dataType = "JsonNode",
            paramType = "body",
            dataTypeClass = JsonNode.class)
    public ProTableResult<Role> listRoles(@RequestBody JsonNode para) {
        return roleService.selectForProTable(para, true);
    }

    /**
     * query all role list by user id
     *
     * @param id {@link Integer} user id
     * @return {@link Result}<{@link Dict}>
     */
    @GetMapping(value = "/getRolesAndIdsByUserId")
    @ApiOperation("Query Role List By UserId")
    @ApiImplicitParam(
            name = "id",
            value = "id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    public Result<Dict> getRolesAndIdsByUserId(@RequestParam Integer id) {
        List<Role> roleList = roleService.list();

        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(id);
        List<Integer> userRoleIds = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            userRoleIds.add(userRole.getRoleId());
        }
        Dict result = Dict.create().set("roles", roleList).set("roleIds", userRoleIds);
        return Result.succeed(result);
    }

    @GetMapping(value = "/getUserListByRoleId")
    @ApiOperation("Query User List By RoleId")
    @ApiImplicitParam(
            name = "roleId",
            value = "roleId",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class)
    @SaCheckPermission(value = PermissionConstants.AUTH_ROLE_VIEW_USER_LIST)
    public Result<List<User>> getUserListByRoleId(@RequestParam Integer roleId) {
        List<User> userRoleList = roleService.getUserListByRoleId(roleId);
        return Result.succeed(userRoleList);
    }
}
