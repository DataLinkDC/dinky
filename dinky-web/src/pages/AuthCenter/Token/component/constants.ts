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

import { CheckboxOptionType } from 'antd/es/checkbox/Group';

export const TOKEN_EXPIRE_TYPE: CheckboxOptionType[] = [
  {
    label: '永不过期',
    value: 1,
    title: '永不过期'
  },
  {
    label: '指定过期时间',
    value: 2,
    title: '指定过期时间'
  },
  {
    label: '指定过期时间区间',
    value: 3,
    title: '指定过期时间区间'
  }
];
