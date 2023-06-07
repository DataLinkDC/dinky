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

import org.dinky.data.model.Role;
import org.dinky.data.model.UserRole;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.RoleService;
import org.dinky.service.UserRoleService;
import org.dinky.utils.I18nMsgUtils;

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
     * create or update role , this method will be {@link Deprecated} in the future, please use
     * {@link #addedOrUpdateRole(Role)}
     *
     * @return delete result code
     */
    @PutMapping
    @Deprecated
    public Result<Void> saveOrUpdateRole(@RequestBody Role role) {
        return roleService.saveOrUpdateRole(role);
    }

    /**
     * create or update role
     *
     * @param role {@link Role}
     * @return {@link Role} of {@link Void}
     */
    @PutMapping("/addedOrUpdateRole")
    public Result<Void> addedOrUpdateRole(@RequestBody Role role) {
        return roleService.addedOrUpdateRole(role);
    }

    /**
     * delete role by ids , this method will be {@link Deprecated} in the future, please use {@link
     * #deleteRoleById(Integer)}
     *
     * @return delete result code
     */
    @DeleteMapping
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        return roleService.deleteRoles(para);
    }

    /**
     * delete role by id
     *
     * @return delete result code
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteRoleById(@RequestParam Integer id) {
        return roleService.deleteRoleById(id);
    }

    /** query role list */
    @PostMapping
    public ProTableResult<Role> listRoles(@RequestBody JsonNode para) {
        return roleService.selectForProTable(para, true);
    }

    /** 获取所有的角色列表以及当前用户的角色 ids */
    @GetMapping(value = "/getRolesAndIdsByUserId")
    public Result<Dict> getRolesAndIdsByUserId(@RequestParam Integer id) {
        List<Role> roleList = roleService.list();

        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(id);
        List<Integer> userRoleIds = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            userRoleIds.add(userRole.getRoleId());
        }
        Dict result = Dict.create().set("roles", roleList).set("roleIds", userRoleIds);
        return Result.succeed(result, I18nMsgUtils.getMsg("response.get.success"));
    }
}
