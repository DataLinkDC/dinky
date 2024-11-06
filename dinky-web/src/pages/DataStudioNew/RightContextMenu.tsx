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
import { DataStudioState } from '@/pages/DataStudioNew/model';
import { l } from '@/utils/intl';

/**
 * 右键菜单组件props | Right-click menu component props
 * @param onClick 点击事件 | Click event
 * @param items 菜单项 | Menu item
 * @param contextMenuPosition 右键菜单位置 | Right-click menu position
 * @param open 是否打开 | Whether to open
 * @param openChange 打开事件 | Open event
 */
type RightContextMenuProps = {
  onClick: (values: MenuInfo) => void;
  items: MenuItemType[];
  contextMenuPosition: any;
  open: boolean;
  openChange: () => void;
};

/**
 * zh: 右键菜单组件
 * en: Right-click menu component.
 * @param props RightContextMenuProps
 * @constructor
 */
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

/**
 * 自定义钩子函数 | Custom hook function.
 * 获取右键按钮菜单项 | Get right-click button menu items.
 * @param props RightMenuItemProps
 */
export const useRightMenuItem = (props: RightMenuItemProps) => {
  const { dataStudioState } = props;
  const menuItem: MenuItemType[] = [];

  // 显示工具窗口名称 | Show toolbar window name.
  if (dataStudioState.toolbar.showDesc) {
    menuItem.push({
      key: 'hideToolbarDesc',
      label: l('datastudio.toolbar.rightClick.hideToolbarDesc')
    });
  } else {
    menuItem.push({
      key: 'showToolbarDesc',
      label: l('datastudio.toolbar.rightClick.showToolbarDesc')
    });
  }

  // 显示紧凑模式
  if (dataStudioState.theme.compact) {
    menuItem.push({
      key: 'closeCompact',
      label: l('datastudio.toolbar.rightClick.closeCompact')
    });
  } else {
    menuItem.push({
      key: 'openCompact',
      label: l('datastudio.toolbar.rightClick.openCompact')
    });
  }
  return menuItem;
};
