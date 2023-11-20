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
import org.dinky.data.annotations.Log;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.AssignRoleDTO;
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.data.vo.UserVo;
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

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "User Controller")
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
    @ApiImplicitParam(
            name = "user",
            value = "user",
            required = true,
            dataType = "User",
            paramType = "body",
            dataTypeClass = User.class)
    @SaCheckPermission(
            value = {PermissionConstants.AUTH_USER_ADD, PermissionConstants.AUTH_USER_EDIT},
            mode = SaMode.OR)
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
    @ApiImplicitParam(
            name = "id",
            value = "user id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.AUTH_USER_EDIT)
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
    @ApiImplicitParam(
            name = "para",
            value = "para",
            required = true,
            dataType = "JsonNode",
            paramType = "body",
            dataTypeClass = JsonNode.class)
    public ProTableResult<User> listUser(@RequestBody JsonNode para) {
        return userService.selectForProTable(para);
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
    @ApiImplicitParam(
            name = "id",
            value = "user id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.AUTH_USER_DELETE)
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
    @ApiImplicitParam(
            name = "modifyPasswordDTO",
            value = "modifyPasswordDTO",
            required = true,
            dataType = "ModifyPasswordDTO",
            paramType = "body",
            dataTypeClass = ModifyPasswordDTO.class)
    @SaCheckPermission(PermissionConstants.AUTH_USER_CHANGE_PASSWORD)
    public Result<Void> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return userService.modifyPassword(modifyPasswordDTO);
    }

    /**
     * give user assign role
     *
     * @param assignRoleDTO {@link AssignRoleDTO}
     * @return {@link Result} with {@link Void}
     */
    @PostMapping(value = "/assignRole")
    @ApiOperation("Assign Role")
    @Log(title = "Assign Role", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(
            name = "assignRoleDTO",
            value = "assignRoleDTO",
            required = true,
            dataType = "AssignRoleDTO",
            paramType = "body",
            dataTypeClass = AssignRoleDTO.class)
    @SaCheckPermission(PermissionConstants.AUTH_USER_ASSIGN_ROLE)
    public Result<Void> assignRole(@RequestBody AssignRoleDTO assignRoleDTO) {
        return userService.assignRole(assignRoleDTO);
    }

    /**
     * get user list by tenant id
     *
     * @param id
     * @return {@link Result} with {@link Dict}
     */
    @GetMapping("/getUserListByTenantId")
    @ApiOperation("Get User List By Tenant Id")
    @ApiImplicitParam(
            name = "id",
            value = "tenant id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    public Result<Dict> getUserListByTenantId(@RequestParam("id") Integer id) {
        List<User> userList = userService.list();
        List<Integer> userIds = userService.getUserIdsByTenantId(id);
        Dict result = Dict.create().set("users", userList).set("userIds", userIds);
        return Result.succeed(result);
    }

    @PutMapping("/updateUserToTenantAdmin")
    @ApiOperation("Update User To Tenant Admin")
    @Log(title = "Update User To Tenant Admin", businessType = BusinessType.UPDATE)
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "userId",
                value = "user id",
                required = true,
                dataType = "Integer",
                paramType = "path",
                dataTypeClass = Integer.class),
        @ApiImplicitParam(
                name = "tenantId",
                value = "tenant id",
                required = true,
                dataType = "Integer",
                paramType = "path",
                dataTypeClass = Integer.class),
        @ApiImplicitParam(
                name = "tenantAdminFlag",
                value = "tenant admin flag",
                required = true,
                dataType = "Boolean",
                paramType = "path",
                dataTypeClass = Boolean.class)
    })
    @SaCheckPermission(PermissionConstants.AUTH_TENANT_SET_USER_TO_TENANT_ADMIN)
    public Result<Void> modifyUserToTenantAdmin(
            @RequestParam Integer userId, @RequestParam Integer tenantId, @RequestParam Boolean tenantAdminFlag) {
        return userService.modifyUserToTenantAdmin(userId, tenantId, tenantAdminFlag);
    }

    @PutMapping("/recovery")
    @ApiOperation("Recovery User")
    @Log(title = "Recovery User", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(
            name = "id",
            value = "user id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.AUTH_USER_RECOVERY)
    public Result<Void> recoveryUser(@RequestParam("id") Integer userId) {
        return userService.recoveryUser(userId);
    }

    @PutMapping("/resetPassword")
    @ApiOperation("Reset Password")
    @Log(title = "Reset Password", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(
            name = "id",
            value = "user id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.AUTH_USER_RESET_PASSWORD)
    public Result<UserVo> resetPassword(@RequestParam("id") Integer userId) {
        return userService.resetPassword(userId);
    }
}
