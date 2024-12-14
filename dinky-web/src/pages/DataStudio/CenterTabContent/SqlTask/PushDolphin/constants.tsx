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
import { Badge, Space } from 'antd';
import { CheckboxOptionType } from 'antd/es/checkbox/Group';
import { DefaultOptionType } from 'antd/es/select';

/**
 * priority list for select | 优先级列表
 */
export const PriorityList: DefaultOptionType[] = [
  {
    label: (
      <Space>
        <Badge color={'red'} />
        Highest
      </Space>
    ),
    value: 'HIGHEST',
    key: 'HIGHEST'
  },
  {
    label: (
      <Space>
        <Badge color={'orange'} />
        High
      </Space>
    ),
    value: 'HIGH',
    key: 'HIGH'
  },
  {
    label: (
      <Space>
        <Badge color={'blue'} />
        Medium
      </Space>
    ),
    value: 'MEDIUM',
    key: 'MEDIUM'
  },
  {
    label: (
      <Space>
        <Badge color={'cyan'} />
        Low
      </Space>
    ),
    value: 'LOW',
    key: 'LOW'
  },
  {
    label: (
      <Space>
        <Badge color={'purple'} />
        Lowest
      </Space>
    ),
    value: 'LOWEST',
    key: 'LOWEST'
  }
];

export const TimeoutNotifyStrategy: CheckboxOptionType[] = [
  {
    label: l('datastudio.header.pushdolphin.timeoutFlag.warn'),
    value: 'WARN'
  },
  {
    label: l('datastudio.header.pushdolphin.timeoutFlag.failed'),
    value: 'FAILED'
  }
];
