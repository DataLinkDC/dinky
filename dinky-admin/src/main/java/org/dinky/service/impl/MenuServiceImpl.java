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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import org.dinky.data.model.Menu;
import org.dinky.data.model.RoleMenu;
import org.dinky.mapper.MenuMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.MenuService;

import org.dinky.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends SuperServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private RoleMenuService roleMenuService;
    @Override
    public boolean deleteMenuById(Integer id) {
        Menu menu = getById(id);
        // todo： 如果是父菜单，提示先删除子菜单

        return false;
    }

    /**
     * query menu list by role id
     * @param roleId role id
     * @return {@link List<Integer>}
     */
    @Override
    public List<Integer> selectMenuListByRoleId(Long roleId) {
        List<RoleMenu> roleMenuList = roleMenuService.list(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        return roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }
}
