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

import org.dinky.data.annotations.Log;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.AuthException;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.Result;
import org.dinky.service.LdapService;
import org.dinky.service.UserService;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "LDAP Controller")
@RequestMapping("/api/ldap")
@RequiredArgsConstructor
public class LdapController {

    @Autowired
    LdapService ldapService;

    @Autowired
    UserService userService;

    @GetMapping("/ldapEnableStatus")
    @SaIgnore
    @ApiOperation("Get LDAP enable status")
    public Result<Boolean> ldapStatus() {
        return Result.succeed(SystemConfiguration.getInstances().getLdapEnable().getValue());
    }

    @GetMapping("/testConnection")
    @ApiOperation("Test connection to LDAP server")
    @Log(title = "Test connection to LDAP server", businessType = BusinessType.TEST)
    public Result<Integer> testConnection() {
        List<User> users = ldapService.listUsers();
        if (users.size() > 0) {
            return Result.succeed(users.size());
        } else {
            return Result.failed(Status.LDAP_NO_USER_FOUND);
        }
    }

    @GetMapping("/listUser")
    @ApiOperation("List user from LDAP server")
    public Result<List<User>> listUser() {
        List<User> users = ldapService.listUsers();
        List<User> localUsers = userService.list();

        // 已经存在的用户不可导入 | Existing users cannot be imported
        users.stream()
                .filter(ldapUser ->
                        localUsers.stream().anyMatch(user -> user.getUsername().equals(ldapUser.getUsername())))
                .forEach(user -> user.setEnabled(false));

        return Result.succeed(users);
    }

    @PostMapping("/importUsers")
    @ApiOperation("Import users from LDAP server")
    @Log(title = "Import users from LDAP server", businessType = BusinessType.IMPORT)
    @ApiImplicitParam(name = "users", value = "User list", required = true, dataType = "List<User>")
    public Result<Void> importUsers(@RequestBody List<User> users) {
        boolean b = userService.saveBatch(users);
        if (b) {
            return Result.succeed();
        }
        return Result.failed();
    }

    /**
     * ldap test login
     *
     * @param loginDTO basic information for user login
     * @return {@link Result}{@link UserDTO} obtain the user's UserDTO
     */
    @PostMapping("/testLogin")
    @ApiOperation("Test login to LDAP server")
    @Log(title = "Test login to LDAP server", businessType = BusinessType.TEST)
    @ApiImplicitParam(name = "loginDTO", value = "Login information", required = true, dataType = "LoginDTO")
    public Result<User> login(@RequestBody LoginDTO loginDTO) {
        try {
            return Result.succeed(ldapService.authenticate(loginDTO));
        } catch (AuthException e) {
            return Result.failed(e.getStatus());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
