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
import com.dlink.common.result.Result;
import com.dlink.dto.LoginUTO;
import com.dlink.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * AdminController
 *
 * @author wenmo
 * @since 2021/5/28 15:52
 **/
@Slf4j
@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginUTO loginUTO) {
        if (Asserts.isNull(loginUTO.isAutoLogin())) {
            loginUTO.setAutoLogin(false);
        }
        return userService.loginUser(loginUTO);
    }

    /**
     * 退出
     */
    @DeleteMapping("/outLogin")
    public Result outLogin() {
        StpUtil.logout();
        return Result.succeed("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result current() throws Exception {
        try {
            return Result.succeed(StpUtil.getSession().get("user"), "获取成功");
        } catch (Exception e) {
            return Result.failed("获取失败");
        }
    }

    /**
     * get tenant
     */
    @RequestMapping("/geTenants")
    public Result getTenants(@RequestParam("username") String username) {
        return userService.getTenants(username);
    }
}
