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

import { BackIcon } from '@/components/Icons/CustomIcons';
import { TagAlignLeft } from '@/components/StyledComponents';
import { IconRender } from '@/pages/AuthCenter/Menu/function';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import * as Icons from '@ant-design/icons';
import { DeleteTwoTone, PlusCircleTwoTone } from '@ant-design/icons';
import { Space } from 'antd';
import { DefaultOptionType } from 'antd/es/select';

/**
 * menu icon options
 * @returns {DefaultOptionType[]}
 * @constructor
 */
export const MENU_ICON_OPTIONS = (): DefaultOptionType[] => {
  const iconNameList = Object.keys(Icons);
  Array.from(iconNameList).sort((a, b) => a.localeCompare(b));

  return iconNameList.map((iconName) => {
    return {
      label: (
        <TagAlignLeft>
          <Space>
            <IconRender icon={iconName} key={iconName} />
            {iconName}
          </Space>
        </TagAlignLeft>
      ), // render icon component
      key: iconName,
      value: iconName
    };
  });
};

/**
 *  the right context menu
 * @param {boolean} isDisabled - is disabled or not , if disabled , the menu will be disabled too
 * @returns {[{icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}, {icon: JSX.Element, disabled: boolean, label: string, key: string}]}
 * @constructor
 */
export const RIGHT_CONTEXT_MENU = (isDisabled = false) => [
  {
    key: 'addSub',
    icon: <PlusCircleTwoTone />,
    label: l('right.menu.addSub'),
    disabled: isDisabled,
    path: PermissionConstants.AUTH_MENU_ADD_SUB
  },
  {
    key: 'delete',
    icon: <DeleteTwoTone twoToneColor={'red'} />,
    label: l('button.delete'),
    path: PermissionConstants.AUTH_MENU_DELETE
  },
  {
    key: 'cancel',
    icon: <BackIcon />,
    label: l('button.cancel')
  }
];

export const MENU_TYPE_OPTIONS = [
  {
    title: l('menu.type.dir'),
    label: l('menu.type.dir'),
    value: 'M'
  },
  {
    title: l('menu.type.menu'),
    label: l('menu.type.menu'),
    value: 'C'
  },
  {
    title: l('menu.type.button'),
    label: l('menu.type.button'),
    value: 'F'
  }
];
