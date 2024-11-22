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

import React, { memo } from 'react';
import { l } from '@/utils/intl';
import { ProFormSelect } from '@ant-design/pro-components';

import '../index.less';
import { EnvType } from '@/pages/DataStudio/type';

export const SelectFlinkEnv = memo((params: { flinkEnv: EnvType[] }) => {
  const { flinkEnv } = params;
  const options = [
    { label: l('button.disable'), value: -1 },
    ...flinkEnv.map((env) => ({
      label: env.name,
      value: env.id
    }))
  ];
  return (
    <ProFormSelect
      name='envId'
      tooltip={l('pages.datastudio.label.jobConfig.flinksql.env.tip1')}
      options={options}
      rules={[{ required: true }]}
      showSearch
      allowClear={false}
      fieldProps={{
        popupMatchSelectWidth: false
      }}
    />
  );
});
