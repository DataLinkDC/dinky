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

import org.dinky.data.dto.RoleMenuDto;
import org.dinky.data.model.RoleMenu;
import org.dinky.mapper.RoleMenuMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.RoleMenuService;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
public class RoleMenuServiceImpl extends SuperServiceImpl<RoleMenuMapper, RoleMenu>
        implements RoleMenuService {
    @Override
    public boolean assignMenuToRole(Integer roleId, Integer[] menuId) {
        int insertSize = 0;
        // 先删除原有的关系
        List<RoleMenu> roleMenus =
                getBaseMapper()
                        .selectList(
                                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        getBaseMapper().deleteBatchIds(roleMenus);
        // 重新建立关系

        for (Integer id : menuId) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(id);
            insertSize += getBaseMapper().insert(roleMenu);
        }
        return menuId.length == insertSize;
    }

    @Override
    public RoleMenuDto queryMenusByRoleId(Integer roleId) {
        List<RoleMenu> roleMenuList =
                getBaseMapper()
                        .selectList(
                                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));

        RoleMenuDto roleMenuDto = new RoleMenuDto();
        roleMenuDto.setRoleId(roleId);
        roleMenuList.forEach(
                roleMenu -> roleMenuDto.getSelectedMenuIds().add(roleMenu.getMenuId()));

        return roleMenuDto;
    }
}
