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

import org.dinky.data.model.RoleSelectPermissions;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.RoleSelectPermissionsService;
import org.dinky.utils.I18nMsgUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/rowPermissions")
public class RowPermissionsController {

    @Autowired private RoleSelectPermissionsService roleSelectPermissionsService;

    /**
     * save or update roleSelectPermissions
     *
     * @param roleSelectPermissions {@link RoleSelectPermissions}
     * @return {@link Result}
     */
    @PutMapping
    public Result saveOrUpdateRole(@RequestBody RoleSelectPermissions roleSelectPermissions) {
        if (roleSelectPermissionsService.saveOrUpdate(roleSelectPermissions)) {
            return Result.succeed(I18nMsgUtils.getMsg("save.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("save.success"));
        }
    }

    /**
     * delete roleSelectPermissions , this method is {@link Deprecated} in the future , please use
     * {@link #delete(Integer id)}
     *
     * @param para {@link JsonNode}
     * @return {@link Result}
     */
    @DeleteMapping
    @Deprecated
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!roleSelectPermissionsService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed(
                        "删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * delete rowPermissions by id
     *
     * @param id {@link Integer}
     * @return {@link Result}
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestParam("id") Integer id) {

        if (roleSelectPermissionsService.removeById(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        }
        return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
    }

    /**
     * query roleSelectPermissions list
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link RoleSelectPermissions}
     */
    @PostMapping
    public ProTableResult<RoleSelectPermissions> listRoles(@RequestBody JsonNode para) {
        return roleSelectPermissionsService.selectForProTable(para);
    }
}
