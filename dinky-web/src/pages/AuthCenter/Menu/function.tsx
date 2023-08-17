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
import {renderIcon, searchTreeNode} from "@/utils/function";
import * as Icons from '@ant-design/icons';
import * as React from "react";
import {Space} from "antd";
import {l} from "@/utils/intl";

export const IconRender = ({icon}:{icon: string}) => {
    // @ts-ignore
    return icon ? React.createElement(Icons[icon]) : null;
}

const renderMenuType = (menuYype: string) => {
    switch (menuYype) {
        case 'F':
            return <>{l('menu.type.button')}</>
        case 'M':
            return <>{l('menu.type.dir')}</>
        case 'C':
            return <>{l('menu.type.menu')}</>
        default:
            return null
    }
}

const renderTitle = (value: SysMenu) => (<Space>
        {value.perms && <span style={{color: 'grey'}}>&nbsp;&nbsp;&nbsp;{value.perms}</span>}
        {value.type && <span style={{color: 'grey'}}>&nbsp;&nbsp;&nbsp;{renderMenuType(value.type)}</span>}
        {value.note && <span style={{color: 'grey'}}>&nbsp;&nbsp;&nbsp;{value.note}</span>}
        {value.path && <span style={{color: 'grey'}}>&nbsp;&nbsp;&nbsp;{value.path}</span>}
    </Space>
);


export const sortTreeData = (treeData: SysMenu[]) : SysMenu[] => {
    return treeData.slice().sort((a, b) => a.orderNum - b.orderNum)
        .map(item => ({ ...item, children: item.children ? sortTreeData(item.children) : [] }));
};


/**
 * 规则
 * level 1 -> 100000 平级下新增 获取当前级别最大的 + 100000 下级新增 获取当前级别最大的 + 10000
 *        level 2 -> 110000 平级下新增 获取当前级别最大的 + 10000 下级新增 获取当前级别最大的 + 1000
 *           level 3 -> 111000 平级下新增 获取当前级别最大的 + 1000 下级新增 获取当前级别最大的 + 100
 *             level 4 -> 111100 平级下新增 获取当前级别最大的 + 100 下级新增 获取当前级别最大的 + 10
 *                 level 5 -> 111110 平级下新增 获取当前级别最大的 + 10 下级新增 获取当前级别最大的 + 1
 */



/**
 * build menu tree
 * @param {SysMenu[]} data
 * @param {string} searchValue
 * @param level
 * @param nextOrderNum
 * @returns {any}
 */
export const buildMenuTree = (data: SysMenu[], searchValue: string = '' ,level = 1, nextOrderNum = 0): any => data.filter((sysMenu: SysMenu) => (sysMenu.name.toLowerCase().indexOf(searchValue.toLowerCase()) > -1)).map((item: SysMenu) => {

    // 获取第一级别的 orderNum 的长度
    const levelOrderNumLength = data.pop()?.orderNum.toString().length ?? 0;

    let increment = 0;
    if (level <= levelOrderNumLength -1) {
        // 获取当前级别最大的 orderNum
        increment = Math.pow(10, (levelOrderNumLength - 1 ) - level) ;
    }


    // 获取当前级别最大的 orderNum
    let maxChildOrderNum = item.children && item.children.length > 0 ? Math.max(...item.children.map(item => item.orderNum)) : item.orderNum ;

    // 更新当前级别最大的 orderNum
    const updatedNextOrderNum = nextOrderNum + increment;
    // 计算差值
    const  diff = maxChildOrderNum - updatedNextOrderNum ;
    // 更新最大的 orderNum 为下级的 orderNum
    const lastUpdatedNextOrderNum = updatedNextOrderNum + diff + increment

    return {
        isLeaf: !item.children || item.children.length === 0,
        name: item.name,
        parentId: item.parentId,
        label: searchTreeNode(item.name, searchValue),
        icon: <IconRender icon={item.icon}/>,
        content: item.note,
        path: item.path,
        nextOrderNum: lastUpdatedNextOrderNum,
        orderNum: item.orderNum,
        level: level,
        value: item.id,
        type: item.type,
        title: <>{searchTreeNode(item.name, searchValue)}{renderTitle(item)}</>,
        fullInfo: item,
        key: item.id,
        children: buildMenuTree(item.children, searchValue,level + 1 , updatedNextOrderNum),
    }
});

/**
 * build menu form tree (filter button)
 * @param {SysMenu[]} data
 * @param {string} searchValue
 * @param {boolean} filterButton
 * @returns {any}
 */

export const buildMenuFormTree = (data: SysMenu[], searchValue: string = '', filterButton = false): any => data.filter((sysMenu: SysMenu) => (sysMenu.name.toLowerCase().indexOf(searchValue.toLowerCase()) > -1)).filter((sysMenu: SysMenu) => (filterButton ? sysMenu.type !== 'F' : false)).map((item: SysMenu) => {
    // const renderTitle = (value: SysMenu) =>( <>{value.name} {value.perms && <span style={{color: 'grey'}}> ----- {value.perms}</span>}</>)

    return {
        isLeaf: !item.children || item.children.length === 0,
        name: item.name,
        parentId: item.parentId,
        label: searchTreeNode(item.name, searchValue),
        icon: <IconRender icon={item.icon}/>,
        content: item.note,
        path: item.path,
        value: item.id,
        title: <>{searchTreeNode(item.name, searchValue)}{renderTitle(item)}</>,
        fullInfo: item,
        key: item.id,
        children: buildMenuFormTree(item.children, searchValue, filterButton),
    }
});
