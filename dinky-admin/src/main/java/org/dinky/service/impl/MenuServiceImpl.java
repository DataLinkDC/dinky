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

import org.dinky.data.constant.BaseConstant;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Menu;
import org.dinky.data.model.RoleMenu;
import org.dinky.data.model.User;
import org.dinky.data.result.Result;
import org.dinky.data.vo.MetaVo;
import org.dinky.data.vo.RouterVo;
import org.dinky.mapper.MenuMapper;
import org.dinky.mapper.RoleMenuMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.MenuService;
import org.dinky.service.RoleMenuService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class MenuServiceImpl extends SuperServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired private RoleMenuService roleMenuService;

    @Autowired private MenuMapper menuMapper;
    @Autowired private RoleMenuMapper roleMenuMapper;

    /**
     * Delete menu by id
     *
     * @param id menu id
     * @return boolean {@code true} if success, {@code false} if failed
     */
    @Override
    public Result<Void> deleteMenuById(Integer id) {
        Menu menu = getById(id);
        // if it is parent menu, prompt to delete child menu first
        if (hasChildByMenuId(menu.getId())) {
            return Result.failed(Status.MENU_HAS_CHILD);
        }
        // if the menu is assigned, it is not allowed to delete
        if (checkMenuExistRole(id)) {
            return Result.failed(Status.MENU_HAS_ASSIGN);
        }
        if (removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        }
        return Result.failed(Status.DELETE_FAILED);
    }

    /**
     * query menu list by role id
     *
     * @param roleId role id
     * @return {@link List<Integer>}
     */
    @Override
    public List<Integer> selectMenuListByRoleId(Integer roleId) {
        List<RoleMenu> roleMenuList =
                roleMenuService.list(
                        new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        return roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }

    /**
     * query menu list by user
     *
     * @param user login user
     * @return {@link List<Menu>} menu list
     */
    @Override
    public List<Menu> selectMenuList(User user) {
        return selectMenuList(new Menu(), user);
    }

    /**
     * query menu list by menu and user
     *
     * @param menu menu
     * @return {@link List<Menu>} menu list
     */
    @Override
    public List<Menu> selectMenuList(Menu menu, User user) {
        if (user.getSuperAdminFlag()) { // super-admin获取所有超管菜单（super_flag=1）
            return menuMapper.listAllMenus();
        }
        return menuMapper.selectMenuListByUserId(menu);
    }

    @Override
    public List<Menu> listMenusBySuperFlag(Integer superFlag) {
        return this.list();
    }

    /**
     * query menu permission by user id
     *
     * @param userId user id
     * @return {@link Set<String>} menu permission set
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Integer userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * query menu by id
     *
     * @param user login user
     * @return {@link Menu} menu
     */
    @Override
    public List<Menu> selectMenuTreeByUserId(User user) {
        List<Menu> menus;

        if (user.getSuperAdminFlag()) {
            menus = menuMapper.listMenus4SuperAdmin();
            return getChildPerms(menus, 0);
        }

        menus = menuMapper.selectMenuTreeByUserId(user.getId());
        return getChildPerms(menus, 0);
    }

    /**
     * build menu tree of router
     *
     * @param menus menu list
     * @return {@link List<RouterVo>} router list
     */
    @Override
    public List<RouterVo> buildMenus(List<Menu> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (Menu menu : menus) {
            RouterVo router = new RouterVo();
            //            router.setHidden("1".equals(menu.getVisible())); // todo: 添加表结构 ddl
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<Menu> cMenus = menu.getChildren();
            if (CollectionUtil.isNotEmpty(cMenus) && BaseConstant.TYPE_DIR.equals(menu.getType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * build menu tree
     *
     * @param menus menu list
     * @return {@link List<Menu>} menu list
     */
    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        // sort
        if (CollectionUtil.isNotEmpty(menus)) {
            menus =
                    menus.stream()
                            .sorted(Comparator.comparing(Menu::getId))
                            .collect(Collectors.toList());
        }

        List<Menu> returnList = new ArrayList<>();
        for (Iterator<Menu> iterator = menus.iterator(); iterator.hasNext(); ) {
            Menu menu = iterator.next();
            //  get all child menu of parent menu id , the -1 is root menu
            if (menu.getParentId() == -1) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * build menu tree
     *
     * @param menus menu list
     * @return menu list tree
     */
    @Override
    public List<Menu> buildMenuTreeSelect(List<Menu> menus) {
        return buildMenuTree(menus);
    }

    /**
     * check menu has child menu
     *
     * @param menuId menu ID
     * @return {@link Boolean} if true, menu has child menu else not
     */
    @Override
    public boolean hasChildByMenuId(Integer menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * check menu is assigned to role
     *
     * @param menuId menu ID
     * @return {@link Boolean} if true, menu is assigned to role else not assigned to role
     */
    @Override
    public boolean checkMenuExistRole(Integer menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * Query permissions by role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Integer roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * get route name
     *
     * @param menu menu info
     * @return route name
     */
    public String getRouteName(Menu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // is not external link and is root menu (type is dir)
        if (isMenuFrame(menu)) {
            routerName = StrUtil.EMPTY;
        }
        return routerName;
    }

    /**
     * get router path
     *
     * @param menu menu info
     * @return router path
     */
    public String getRouterPath(Menu menu) {
        String routerPath = menu.getPath();
        //  is not external link and is root menu (type is dir)
        if (menu.isRootMenu() && BaseConstant.TYPE_DIR.equals(menu.getType())) {
            routerPath = "/" + menu.getPath();
        }
        //   is not external link and is root menu (type is menu)
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * get component name
     *
     * @param menu menu info
     * @return component name
     */
    public String getComponent(Menu menu) {
        String component = BaseConstant.LAYOUT;
        if (StrUtil.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        }
        return component;
    }

    /**
     * is menu frame jump or not
     *
     * @param menu menu info
     * @return if true, menu is frame jump else not
     */
    public boolean isMenuFrame(Menu menu) {
        return menu.isRootMenu() && BaseConstant.TYPE_MENU.equals(menu.getType());
    }

    /**
     * get all child menu by parent menu id
     *
     * @param list all menu list
     * @param parentId parent menu id
     * @return {@link List<Menu>} child menu list
     */
    public List<Menu> getChildPerms(List<Menu> list, int parentId) {
        List<Menu> returnList = new ArrayList<Menu>();
        for (Menu t : list) {
            // According to a parent node ID passed in, traverse all child nodes of the parent node
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * get child menus by recursion
     *
     * @param list menu list
     * @param menu menu
     */
    private void recursionFn(List<Menu> list, Menu menu) {
        // 得到子节点列表
        List<Menu> childList = getChildList(list, menu);
        menu.setChildren(childList);
        for (Menu tChild : childList) {
            if (hasChild(list, tChild)) {
                // Determine whether there are child nodes
                for (Menu n : childList) {
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * get child menu list
     *
     * @param list menu list
     * @param menu menu
     * @return {@link List<Menu>} child menu list
     */
    private List<Menu> getChildList(List<Menu> list, Menu menu) {
        List<Menu> tlist = new ArrayList<Menu>();
        for (Menu n : list) {
            if (n.getParentId().longValue() == menu.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * Determine whether there are child nodes
     *
     * @param list menu list
     * @param menu menu
     * @return {@link Boolean} if true, has child menu else not
     */
    private boolean hasChild(List<Menu> list, Menu menu) {
        return getChildList(list, menu).size() > 0;
    }
}
