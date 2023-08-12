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

package org.dinky.service;

import org.dinky.data.model.Menu;
import org.dinky.data.model.User;
import org.dinky.data.result.Result;
import org.dinky.data.vo.RouterVo;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;
import java.util.Set;

public interface MenuService extends ISuperService<Menu> {

    /**
     * Delete menu by id
     *
     * @param id menu id
     * @return boolean {@code true} if success, {@code false} if failed
     */
    Result<Void> deleteMenuById(Integer id);

    /**
     * query menu list by user
     *
     * @param user login user
     * @return {@link List<Menu>} menu list
     */
    public List<Menu> selectMenuList(User user);

    /**
     * query menu list by menu and user
     *
     * @param menu menu
     * @return {@link List<Menu>} menu list
     */
    public List<Menu> selectMenuList(Menu menu, User user);

    List<Menu> listMenusBySuperFlag(Integer superFlag);

    /**
     * query menu permission by user id
     *
     * @param userId user id
     * @return {@link Set<String>} menu permission set
     */
    Set<String> selectMenuPermsByUserId(Integer userId);

    /**
     * query menu by id
     *
     * @param user login user
     * @return {@link Menu} menu
     */
    List<Menu> selectMenuTreeByUserId(User user);

    /**
     * query menu list by role id
     *
     * @param roleId role id
     * @return {@link List<Integer>}
     */
    List<Integer> selectMenuListByRoleId(Integer roleId);

    /**
     * build menu tree of router
     *
     * @param menus menu list
     * @return {@link List<RouterVo>} router list
     */
    List<RouterVo> buildMenus(List<Menu> menus);

    /**
     * build menu tree
     *
     * @param menus menu list
     * @return {@link List<Menu>} menu list
     */
    List<Menu> buildMenuTree(List<Menu> menus);

    /**
     * build menu tree
     *
     * @param menus menu list
     * @return menu list tree
     */
    List<Menu> buildMenuTreeSelect(List<Menu> menus);

    /**
     * check menu has child menu
     *
     * @param menuId menu ID
     * @return {@link Boolean} if true, menu has child menu else not
     */
    boolean hasChildByMenuId(Integer menuId);

    /**
     * check menu is assigned to role
     *
     * @param menuId menu ID
     * @return {@link Boolean} if true, menu is assigned to role else not assigned to role
     */
    boolean checkMenuExistRole(Integer menuId);

    /**
     * Query permissions by role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    Set<String> selectMenuPermsByRoleId(Integer roleId);
}
