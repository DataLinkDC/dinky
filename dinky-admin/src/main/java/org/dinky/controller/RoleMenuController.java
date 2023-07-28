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

import org.dinky.annotation.Log;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.result.Result;
import org.dinky.service.impl.RoleMenuServiceImpl;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/roleMenu")
@RequiredArgsConstructor
public class RoleMenuController {

    private RoleMenuServiceImpl roleMenuService;

    /**
     * assign menus to role
     *
     * @param roleId
     * @param menus
     * @return {@link Result} with {@link Void}
     * @throws Exception {@link Exception}
     */
    @PutMapping
    @Log(title = "Assign Menus to Role ", businessType = BusinessType.GRANT)
    @ApiOperation("Assign Menus to Role")
    public Result<Void> assignMenuToRole(
            @RequestParam Integer roleId, @RequestParam Integer[] menus) throws Exception {
        if (roleMenuService.assignMenuToRole(roleId, menus)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }
}
