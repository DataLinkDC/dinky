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

import {
  DeleteTwoTone,
  EditOutlined, MenuOutlined,
  PlusCircleTwoTone,
} from "@ant-design/icons";
import {l} from "@/utils/intl";
import React from "react";
import {BackIcon} from "@/components/Icons/CustomIcons";
import {MenuItemType} from "antd/es/menu/hooks/useItems";


/**
 *  the right context menu
 * @param {boolean} isDisabled - is disabled or not , if disabled , the menu will be disabled too
 * @returns {[{icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}]}
 * @constructor
 */
export const RIGHT_CONTEXT_MENU = (isDisabled = false): MenuItemType[] => [
  {
    key: 'addSub',
    icon: <PlusCircleTwoTone/>,
    label: '添加子菜单',
    disabled: isDisabled,
  },
  {
    key: 'delete',
    icon: <DeleteTwoTone twoToneColor={'red'}/>,
    label: l('button.delete'),
    disabled: isDisabled,
  },
  {
    key: 'cancel',
    icon: <BackIcon/>,
    label: l('button.cancel'),
    disabled: isDisabled,
  },
];



export const MENU_ICON_OPTIONS = [
  {
    key: 'Menu',
    icon: <MenuOutlined/>,
    label: <><MenuOutlined/> 菜单</>,
    value: 'Menu',
  },
  {
    key: 'Edit',
    icon: <EditOutlined/>,
    label: <><EditOutlined/> 编辑</>,
    value: 'Edit',
  }
]