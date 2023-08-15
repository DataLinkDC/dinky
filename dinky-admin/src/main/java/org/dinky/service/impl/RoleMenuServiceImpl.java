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

package org.dinky.service.impl;

import org.dinky.data.dto.AssignMenuToRoleDto;
import org.dinky.data.enums.Status;
import org.dinky.data.model.RoleMenu;
import org.dinky.data.result.Result;
import org.dinky.mapper.RoleMenuMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.RoleMenuService;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.collection.CollUtil;

@Service
public class RoleMenuServiceImpl extends SuperServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    @Override
    public Result<Void> assignMenuToRole(AssignMenuToRoleDto assignMenuToRoleDto) {

        if (CollUtil.isEmpty(assignMenuToRoleDto.getMenuIds())) {
            return Result.failed(Status.SELECT_MENU);
        }

        int insertSize = 0;
        // 先删除原有的关系
        List<RoleMenu> roleMenus = getBaseMapper()
                .selectList(
                        new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, assignMenuToRoleDto.getRoleId()));

        // if not empty , delete all role menus
        if (CollUtil.isNotEmpty(roleMenus)) {
            roleMenus.forEach(rm -> {
                getBaseMapper().delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, rm.getMenuId()));
            });
        }

        // then insert new role menus
        for (Integer id : assignMenuToRoleDto.getMenuIds()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(assignMenuToRoleDto.getRoleId());
            roleMenu.setMenuId(id);
            insertSize += getBaseMapper().insert(roleMenu);
        }

        if (assignMenuToRoleDto.getMenuIds().size() == insertSize) {
            return Result.succeed(Status.ASSIGN_MENU_SUCCESS);
        }
        return Result.failed(Status.ASSIGN_MENU_FAILED);
    }
}
