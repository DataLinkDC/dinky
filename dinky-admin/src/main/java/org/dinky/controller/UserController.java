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
import org.dinky.data.dto.ModifyPasswordDTO;
import org.dinky.data.model.User;
import org.dinky.data.model.UserTenant;
import org.dinky.data.params.AssignRoleParams;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.UserService;
import org.dinky.service.UserTenantService;
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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.lang.Dict;
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

    private final UserTenantService userTenantService;

    /**
     * add or update user
     *
     * @param user {@link User}
     * @return {@link Result} with {@link Void}
     */
    @PutMapping
    public Result<Void> saveOrUpdateUser(@RequestBody User user) {
        if (Asserts.isNull(user.getId())) {
            return userService.registerUser(user);
        } else {
            userService.modifyUser(user);
            return Result.succeed("修改成功");
        }
    }

    /**
     * disable or enable user
     *
     * @param id : user id
     * @return {@link Result} with {@link Void}
     */
    @PutMapping("/enable")
    public Result<Void> enable(@RequestParam("id") Integer id) {
        if (userService.checkAdmin(id)) {
            return Result.failed(I18nMsgUtils.getMsg("user.superadmin.cannot.disable"));
        } else {
            if (userService.enable(id)) {
                return Result.succeed(I18nMsgUtils.getMsg("modify.success"));
            } else {
                return Result.failed(I18nMsgUtils.getMsg("modify.failed"));
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
    public Result<Void> deleteUserById(@RequestParam("id") Integer id) {
        if (userService.removeUser(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    /**
     * delete or batch delete user , this method is will be {@link Deprecated} in the future ，
     * please use {@link #deleteUserById(Integer)}
     *
     * @param para
     * @return {@link Result} with {@link Void}
     */
    @DeleteMapping
    @Deprecated
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (userService.checkAdmin(id)) {
                    error.add(id);
                    continue;
                }
                if (!userService.removeUser(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * get user by id
     *
     * @param user {@link User}
     * @return {@link Result} with {@link User}
     */
    @PostMapping("/getOneById")
    public Result<User> getOneById(@RequestBody User user) {
        user = userService.getById(user.getId());
        user.setPassword(null);
        return Result.succeed(user, I18nMsgUtils.getMsg("response.get.success"));
    }

    /**
     * modify password
     *
     * @param modifyPasswordDTO {@link ModifyPasswordDTO}
     * @return {@link Result} with {@link Void}
     */
    @PostMapping("/modifyPassword")
    public Result<Void> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return userService.modifyPassword(modifyPasswordDTO);
    }

    /**
     * give user grant role ，the interface will be {@link Deprecated} in the future，please use
     * {@link #assignRole(AssignRoleParams)}
     *
     * @param para {@link JsonNode}
     * @return {@link Result} with {@link Void}
     */
    @PutMapping(value = "/grantRole")
    @Deprecated
    public Result<Void> grantRole(@RequestBody JsonNode para) {
        return userService.grantRole(para);
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
    public Result<Dict> getUserListByTenantId(@RequestParam("id") Integer id) {
        List<User> userList = userService.list();
        List<UserTenant> userTenants =
                userTenantService
                        .getBaseMapper()
                        .selectList(
                                new LambdaQueryWrapper<UserTenant>()
                                        .eq(UserTenant::getTenantId, id));
        List<Integer> userIds = new ArrayList<>();
        for (UserTenant userTenant : userTenants) {
            userIds.add(userTenant.getUserId());
        }
        Dict result = Dict.create().set("users", userList).set("userIds", userIds);
        return Result.succeed(result, I18nMsgUtils.getMsg("response.get.success"));
    }
}
