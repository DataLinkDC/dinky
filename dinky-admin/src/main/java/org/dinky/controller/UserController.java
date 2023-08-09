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

import org.dinky.assertion.Asserts;
import org.dinky.data.annotation.Log;
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.User;
import org.dinky.data.params.AssignRoleParams;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.UserService;

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

/**
 * UserController
 *
 * @since 2021/11/28 13:43
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * add or update user
     *
     * @param user {@link User}
     * @return {@link Result} with {@link Void}
     */
    @PutMapping
    @ApiOperation("Insert Or Update User")
    @Log(title = "Insert Or Update User", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result<Void> saveOrUpdateUser(@RequestBody User user) {
        if (Asserts.isNull(user.getId())) {
            return userService.registerUser(user);
        } else {
            userService.modifyUser(user);
            return Result.succeed(Status.MODIFY_SUCCESS);
        }
    }

    /**
     * disable or enable user
     *
     * @param id : user id
     * @return {@link Result} with {@link Void}
     */
    @PutMapping("/enable")
    @ApiOperation("Modify User Status")
    @Log(title = "Modify User Status", businessType = BusinessType.UPDATE)
    public Result<Void> modifyUserStatus(@RequestParam("id") Integer id) {
        if (userService.checkSuperAdmin(id)) {
            return Result.failed(Status.USER_SUPERADMIN_CANNOT_DISABLE);
        } else {
            if (userService.modifyUserStatus(id)) {
                return Result.succeed(Status.MODIFY_SUCCESS);
            } else {
                return Result.failed(Status.MODIFY_FAILED);
            }
        }
    }

    /**
     * get user list
     *
     * @param para
     * @return {@link Result} with {@link ProTableResult}
     */
    @PostMapping
    @ApiOperation("Get User List")
    public ProTableResult<User> listUser(@RequestBody JsonNode para) {
        return userService.selectForProTable(para, true);
    }

    /**
     * delete user by id
     *
     * @param id
     * @return {@link Result} with {@link Void}
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete User By Id")
    @Log(title = "Delete User By Id", businessType = BusinessType.DELETE)
    public Result<Void> deleteUserById(@RequestParam("id") Integer id) {
        if (userService.removeUser(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * modify password
     *
     * @param modifyPasswordDTO {@link ModifyPasswordDTO}
     * @return {@link Result} with {@link Void}
     */
    @PostMapping("/modifyPassword")
    @ApiOperation("Modify Password")
    @Log(title = "Modify Password", businessType = BusinessType.UPDATE)
    public Result<Void> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return userService.modifyPassword(modifyPasswordDTO);
    }

    /**
     * give user assign role
     *
     * @param assignRoleParams {@link AssignRoleParams}
     * @return {@link Result} with {@link Void}
     */
    @PostMapping(value = "/assignRole")
    public Result<Void> assignRole(@RequestBody AssignRoleParams assignRoleParams) {
        return userService.assignRole(assignRoleParams);
    }

    /**
     * get user list by tenant id
     *
     * @param id
     * @return {@link Result} with {@link Dict}
     */
    @GetMapping("/getUserListByTenantId")
    @ApiOperation("Get User List By Tenant Id")
    public Result<Dict> getUserListByTenantId(@RequestParam("id") Integer id) {
        List<User> userList = userService.list();
        List<Integer> userIds = userService.getUserIdsByTenantId(id);
        Dict result = Dict.create().set("users", userList).set("userIds", userIds);
        return Result.succeed(result);
    }

    @PutMapping("/updateUserToTenantAdmin")
    @ApiOperation("Update User To Tenant Admin")
    @Log(title = "Update User To Tenant Admin", businessType = BusinessType.UPDATE)
    public Result<Void> modifyUserToTenantAdmin(
            @RequestParam Integer userId,
            @RequestParam Integer tenantId,
            @RequestParam Boolean tenantAdminFlag) {
        return userService.modifyUserToTenantAdmin(userId, tenantId, tenantAdminFlag);
    }
}
