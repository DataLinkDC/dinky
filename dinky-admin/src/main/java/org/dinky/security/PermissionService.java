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

package org.dinky.security;

import org.dinky.data.constant.BaseConstant;
import org.dinky.service.MenuService;
import org.dinky.service.RoleService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollectionUtil;

/** Permission service by sa-token. TODO add cache */
@Component
public class PermissionService implements StpInterface {
    @Autowired private RoleService roleService;

    @Autowired private MenuService menuService;

    /**
     * Get permission list by user ID.
     *
     * @param userId user id
     * @param loginType login type
     * @return permission list
     */
    @Override
    public List<String> getPermissionList(Object userId, String loginType) {
        Preconditions.checkArgument(userId != null);
        int userIdNum = Integer.parseInt(userId.toString());
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (userIdNum == BaseConstant.ADMIN_ID) {
            perms.add(BaseConstant.ALL_PERMISSION);
        } else {
            List<Integer> roles = roleService.selectRoleListByUserId(userIdNum);
            if (CollectionUtil.isNotEmpty(roles)) {
                for (int roleId : roles) {
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(roleId);
                    perms.addAll(rolePerms);
                }
            } else {
                perms.addAll(menuService.selectMenuPermsByUserId(userIdNum));
            }
        }
        return new ArrayList<>(perms);
    }

    /**
     * Get role list by user ID.
     *
     * @param userId user ID
     * @param loginType login type
     * @return role list
     */
    @Override
    public List<String> getRoleList(Object userId, String loginType) {
        Preconditions.checkArgument(userId != null);
        Set<String> roles =
                new HashSet<String>(
                        roleService.selectRolePermissionByUserId(
                                Integer.valueOf(userId.toString())));
        return new ArrayList<>(roles);
    }
}
