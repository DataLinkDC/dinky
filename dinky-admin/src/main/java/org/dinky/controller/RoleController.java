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

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final UserRoleService userRoleService;

    /**
     * create or update role
     *
     * @param role {@link Role}
     * @return {@link Role} of {@link Void}
     */
    @PutMapping("/addedOrUpdateRole")
    @ApiOperation("Insert Or Update Role")
    @Log(title = "Insert Or Update Role", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result<Void> addedOrUpdateRole(@RequestBody Role role) {
        return roleService.addedOrUpdateRole(role);
    }

    /**
     * delete role by id
     *
     * @return delete result code
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete Role By Id")
    @Log(title = "Delete Role By Id", businessType = BusinessType.DELETE)
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
    public Result<List<User>> getUserListByRoleId(@RequestParam Integer roleId) {
        List<User> userRoleList = roleService.getUserListByRoleId(roleId);
        return Result.succeed(userRoleList);
    }
}
