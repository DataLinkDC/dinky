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

import org.dinky.common.result.Result;
import org.dinky.dto.LoginDTO;
import org.dinky.dto.UserDTO;
import org.dinky.enums.Status;
import org.dinky.model.Tenant;
import org.dinky.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AdminController
 *
 * @since 2021/5/28 15:52
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController{

    private final UserService userService;

    /**
     * user login
     *
     * @param loginDTO basic information for user login
     * @return obtain the user's UserDTO
     */
    @PostMapping("/login")
    public Result<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO);
    }

    /**
     * user logout
     *
     * @return user loginout status information
     */
    @DeleteMapping("/outLogin")
    public Result<Void> outLogin() {
        userService.outLogin();
        return Result.getResultFromStatus(Status.SIGN_OUT_SUCESS);
    }

    /**
     * get current user info
     *
     * @return obtain the current user's UserDTO
     */
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUserInfo() {
        return userService.queryCurrentUserInfo();
    }

    /**
     * choose tenant by tenantId
     *
     * @param tenantId
     * @return the specified tenant
     */
    @PostMapping("/chooseTenant")
    public Result<Tenant> getTenantByTenantId(@RequestParam("tenantId") Integer tenantId) {
        return userService.chooseTenant(tenantId);
    }
}
