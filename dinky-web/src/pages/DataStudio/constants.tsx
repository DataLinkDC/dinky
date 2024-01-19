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

import { l } from '@/utils/intl';
import { CloseCircleTwoTone, IssuesCloseOutlined } from '@ant-design/icons';
import { Space } from 'antd';
import { DefaultOptionType } from 'antd/es/select';
import { MenuItemType } from 'rc-menu/lib/interface';

export const STUDIO_TAG_RIGHT_CONTEXT_MENU: MenuItemType[] = [
  {
    key: 'closeAll',
    label: (
      <Space>
        <CloseCircleTwoTone />
        {l('right.menu.closeAll')}
      </Space>
    )
  },
  {
    key: 'closeOther',
    label: (
      <Space>
        <IssuesCloseOutlined className={'blue-icon'} />
        {l('right.menu.closeOther')}
      </Space>
    )
  }
];

export const SAVE_POINT_TYPE: DefaultOptionType[] = [
  {
    label: l('global.savepoint.strategy.disabled'),
    value: 0
  },
  {
    label: l('global.savepoint.strategy.latest'),
    value: 1
  },
  {
    label: l('global.savepoint.strategy.earliest'),
    value: 2
  },
  {
    label: l('global.savepoint.strategy.custom'),
    value: 3
  }
];
