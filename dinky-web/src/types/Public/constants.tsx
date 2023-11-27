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
import { Tag } from 'antd';

/**
 * the table filter enum
 * @type {{true: {text: JSX.Element, status: string}, false: {text: JSX.Element, status: string}}}
 */
export const YES_OR_NO_ENUM = {
  true: {
    text: <Tag color={'success'}>{l('global.yes')}</Tag>,
    status: 'Success'
  },
  false: { text: <Tag color={'error'}>{l('global.no')}</Tag>, status: 'Error' }
};

/**
 * the table filter mapping
 * @type {({text: string, value: boolean} | {text: string, value: boolean})[]}
 */
export const YES_OR_NO_FILTERS_MAPPING = [
  {
    value: 1,
    text: l('global.yes')
  },
  {
    value: 0,
    text: l('global.no')
  }
];
