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

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.dto.ModifyPasswordDTO;
import com.dlink.model.User;
import com.dlink.model.UserTenant;
import com.dlink.service.UserService;
import com.dlink.service.UserTenantService;

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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

/**
 * UserController
 *
 * @author wenmo
 * @since 2021/11/28 13:43
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserTenantService userTenantService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody User user) {
        if (Asserts.isNull(user.getId())) {
            return userService.registerUser(user);
        } else {
            userService.modifyUser(user);
            return Result.succeed("修改成功");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<User> listClusterConfigs(@RequestBody JsonNode para) {
        return userService.selectForProTable(para, true);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (checkAdmin(id)) {
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
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    private static boolean checkAdmin(Integer id) {
        return id == 0;
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody User user) {
        user = userService.getById(user.getId());
        user.setPassword(null);
        return Result.succeed(user, "获取成功");
    }

    /**
     * 修改密码
     */
    @PostMapping("/modifyPassword")
    public Result modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return userService.modifyPassword(modifyPasswordDTO.getUsername(), modifyPasswordDTO.getPassword(),
            modifyPasswordDTO.getNewPassword());
    }

    /**
     * give user grant role
     *
     * @return
     */
    @PutMapping(value = "/grantRole")
    public Result grantRole(@RequestBody JsonNode para) {
        return userService.grantRole(para);
    }

    @GetMapping("/getUserListByTenantId")
    public Result getUserListByTenantId(@RequestParam("id") Integer id) {
        List<User> userList = userService.list();
        Map result = new HashMap();
        result.put("users", userList);
        List<UserTenant> userTenants = userTenantService.getBaseMapper().selectList(new QueryWrapper<UserTenant>().eq("tenant_id", id));
        List<Integer> userIds = new ArrayList<>();
        for (UserTenant userTenant : userTenants) {
            userIds.add(userTenant.getUserId());
        }
        result.put("userIds", userIds);
        return Result.succeed(result, "获取成功");
    }
}
