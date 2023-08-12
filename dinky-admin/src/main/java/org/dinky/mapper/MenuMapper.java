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

package org.dinky.mapper;

import org.dinky.data.model.Menu;
import org.dinky.mybatis.mapper.SuperMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** MenuMapper */
@Mapper
public interface MenuMapper extends SuperMapper<Menu> {

    List<Menu> listAllMenus();

    List<Menu> selectMenuList(Menu menu);

    List<String> selectMenuPerms();

    List<Menu> selectMenuListByUserId(Menu menu);

    List<String> selectMenuPermsByUserId(Integer userId);

    List<Menu> listMenus4SuperAdmin();

    List<Menu> selectMenuTreeByUserId(@Param("userId") Integer userId);

    List<Integer> selectMenuListByRoleId(Integer roleId);

    int hasChildByMenuId(Integer menuId);

    Menu checkMenuNameUnique(@Param("name") String name, @Param("parentId") Integer parentId);

    List<String> selectMenuPermsByRoleId(Integer roleId);
}
