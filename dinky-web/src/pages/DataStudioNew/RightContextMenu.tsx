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

import { Dropdown } from 'antd';
import { MenuItemType } from 'antd/es/menu/interface';
import { MenuInfo } from 'rc-menu/es/interface';
import React from 'react';
import { RightMenuItemProps } from '@/pages/DataStudioNew/data.d';

type RightContextMenuProps = {
  onClick: (values: MenuInfo) => void;
  items: MenuItemType[];
  contextMenuPosition: any;
  open: boolean;
  openChange: () => void;
};

const RightContextMenu: React.FC<RightContextMenuProps> = (props) => {
  const { onClick, items, openChange, open, contextMenuPosition } = props;

  return (
    <Dropdown
      arrow
      autoAdjustOverflow
      destroyPopupOnHide
      trigger={['contextMenu']}
      overlayStyle={{ ...contextMenuPosition }}
      menu={{ onClick: onClick, items: items }}
      open={open}
      onOpenChange={openChange}
    >
      {/*占位*/}
      <div style={{ ...contextMenuPosition }} />
    </Dropdown>
  );
};

export default RightContextMenu;

// 获取右键按钮菜单
export const useRightMenuItem = (props: RightMenuItemProps) => {
  const { layoutState } = props;
  const menuItem: MenuItemType[] = [];
  menuItem.push({
    key: 'saveLayout',
    label: '保存布局'
  });

  // 显示工具窗口名称
  if (layoutState.toolbar.showDesc) {
    menuItem.push({
      key: 'hideToolbarDesc',
      label: '隐藏工具栏窗口名称'
    });
  } else {
    menuItem.push({
      key: 'showToolbarDesc',
      label: '显示工具栏窗口名称'
    });
  }
  return menuItem;
};
