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

package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Role;
import com.dlink.model.UserRole;
import com.dlink.service.RoleService;
import com.dlink.service.UserRoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * create or update role
     *
     * @return delete result code
     */
    @PutMapping
    public Result saveOrUpdateRole(@RequestBody Role role) {
        return roleService.saveOrUpdateRole(role);
    }

    /**
     * delete tenant by id
     *
     * @return delete result code
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        return roleService.deleteRoles(para);
    }

    /**
     * query role list
     */
    @PostMapping
    public ProTableResult<Role> listRoles(@RequestBody JsonNode para) {
        return roleService.selectForProTable(para, true);
    }

    /**
     * 获取所有的角色列表以及当前用户的角色 ids
     */
    @GetMapping(value = "/getRolesAndIdsByUserId")
    public Result getRolesAndIdsByUserId(@RequestParam Integer id) {
        List<Role> roleList = roleService.list();
        Map result = new HashMap();
        result.put("roles", roleList);
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(id);
        List<Integer> userRoleIds = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            userRoleIds.add(userRole.getRoleId());
        }
        result.put("roleIds", userRoleIds);
        return Result.succeed(result, "获取成功");
    }
}