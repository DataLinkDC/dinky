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

import { SysMenu } from '@/types/AuthCenter/data';
import { searchTreeNode } from '@/utils/function';
import { l } from '@/utils/intl';
import * as Icons from '@ant-design/icons';
import { Space } from 'antd';
import * as React from 'react';

export const IconRender = ({ icon }: { icon: string }) => {
  // @ts-ignore
  return icon ? React.createElement(Icons[icon]) : null;
};

const renderMenuType = (menuType: string) => {
  switch (menuType) {
    case 'F':
      return <>{l('menu.type.button')}</>;
    case 'M':
      return <>{l('menu.type.dir')}</>;
    case 'C':
      return <>{l('menu.type.menu')}</>;
    default:
      return null;
  }
};

const renderTitle = (value: SysMenu) => (
  <Space>
    {value.perms && <span style={{ color: 'grey' }}>&nbsp;&nbsp;&nbsp;{value.perms}</span>}
    {value.type && (
      <span style={{ color: 'grey' }}>&nbsp;&nbsp;&nbsp;{renderMenuType(value.type)}</span>
    )}
    {value.note && <span style={{ color: 'grey' }}>&nbsp;&nbsp;&nbsp;{value.note}</span>}
    {value.path && <span style={{ color: 'grey' }}>&nbsp;&nbsp;&nbsp;{value.path}</span>}
  </Space>
);

export const sortTreeData = (treeData: SysMenu[]): SysMenu[] => {
  return treeData
    .slice()
    .sort((a, b) => a.orderNum - b.orderNum)
    .map((item) => ({
      ...item,
      children: item.children ? sortTreeData(item.children) : []
    }));
};

/**
 * 递归获取 tree 下的最大的 orderNum ,
 */
export const getMaxOrderNumToNextOrderNum = (tree: SysMenu[]): number => {
  let maxOrderNum = 0;
  tree.forEach((item) => {
    if (item.orderNum > maxOrderNum) {
      maxOrderNum = item.orderNum;
    }
    if (item.children) {
      maxOrderNum = Math.max(maxOrderNum, getMaxOrderNumToNextOrderNum(item.children));
    }
  });
  return maxOrderNum;
};

/**
 * build menu tree
 * @param {SysMenu[]} data
 * @param {string} searchValue
 * @param level
 * @param nextOrderNum
 * @returns {any}
 */
export const buildMenuTree = (data: SysMenu[], searchValue: string = '', level = 1): any =>
  data
    .filter(
      (sysMenu: SysMenu) => sysMenu.name.toLowerCase().indexOf(searchValue.toLowerCase()) > -1
    )
    .map((item: SysMenu) => {
      return {
        isLeaf: !item.children || item.children.length === 0,
        name: item.name,
        parentId: item.parentId,
        label: searchTreeNode(item.name, searchValue),
        icon: <IconRender icon={item.icon} />,
        content: item.note,
        path: item.path,
        orderNum: item.orderNum,
        level: level,
        value: item.id,
        type: item.type,
        title: (
          <>
            {searchTreeNode(item.name, searchValue)}
            {renderTitle(item)}
          </>
        ),
        fullInfo: item,
        key: item.id,
        children: buildMenuTree(item.children, searchValue, level + 1)
      };
    });

/**
 * build menu form tree (filter button)
 */

export const buildMenuFormTree = (
  data: SysMenu[],
  searchValue: string = '',
  filterButton = false
): any =>
  data
    .filter(
      (sysMenu: SysMenu) => sysMenu.name.toLowerCase().indexOf(searchValue.toLowerCase()) > -1
    )
    .filter((sysMenu: SysMenu) => (filterButton ? sysMenu.type !== 'F' : false))
    .map((item: SysMenu) => {
      // const renderTitle = (value: SysMenu) =>( <>{value.name} {value.perms && <span style={{color: 'grey'}}> ----- {value.perms}</span>}</>)

      return {
        isLeaf: !item.children || item.children.length === 0,
        name: item.name,
        parentId: item.parentId,
        label: searchTreeNode(item.name, searchValue),
        icon: <IconRender icon={item.icon} />,
        content: item.note,
        path: item.path,
        value: item.id,
        title: (
          <>
            {searchTreeNode(item.name, searchValue)}
            {renderTitle(item)}
          </>
        ),
        fullInfo: item,
        key: item.id,
        children: buildMenuFormTree(item.children, searchValue, filterButton)
      };
    });
