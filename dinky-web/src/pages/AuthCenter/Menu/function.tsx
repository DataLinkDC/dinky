/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {SysMenu} from "@/types/RegCenter/data";
import {folderSeparator, searchTreeNode} from "@/utils/function";

export  const buildMenuTree = (data: SysMenu[],searchValue = ''): any => data.map((item: SysMenu) => {
    const renderTitle = (value: SysMenu) =>( <>{value.name} {value.perms && <span style={{color: 'grey'}}> ----- {value.perms}</span>}</>)

    return {
        isLeaf: false,
        name: item.name,
        parentId: item.name,
        // icon: parse(`${item.icon}`),
        content: item.name,
        path: item.name,
        title: searchTreeNode(item.name, searchValue),
        fullInfo: item,
        key: item.id,
        children: item.children
            // filter table by search value and map table to tree node
            .filter((sub: SysMenu) => (sub.name.indexOf(searchValue) > -1))
            .map((sub: SysMenu) => {
                return {
                    isLeaf: true,
                    name: sub.name,
                    parentId: item.name,
                    // icon: parse(item.icon),
                    content: sub.name,
                    path: item.name + folderSeparator() + sub.name,
                    title: searchTreeNode(sub.name, searchValue),
                    key: sub.id,
                    fullInfo: item,
                };
            }),
    }
});
