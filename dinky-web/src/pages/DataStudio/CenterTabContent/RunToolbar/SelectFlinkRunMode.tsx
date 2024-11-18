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

import { ProFormCascader } from '@ant-design/pro-form/lib';
import { CascaderProps, Tag } from 'antd';
import { DefaultOptionType } from 'antd/es/select';
import { FlinkCluster } from '@/pages/DataStudio/type';
import { memo } from 'react';

type Option = {
  value: number;
  label: string;
  enabled?: boolean;
  children?: Option[];
};
export const SelectFlinkRunMode = memo((props: { data: FlinkCluster[] }) => {
  const { data } = props;
  const optionDict = {
    local: [],
    standalone: [],
    'yarn-session': [],
    'yarn-per-job': [],
    'yarn-application': [],
    'kubernetes-session': [],
    'kubernetes-application': [],
    'kubernetes-application-operator': []
  } as Record<string, Option[]>;
  data.forEach((item) => {
    if (item.type === 'yarn-application') {
      optionDict['yarn-per-job'].push({
        value: item.id,
        label: item.name,
        enabled: item.enabled
      });
    }
    optionDict[item.type].push({
      value: item.id,
      label: item.name,
      enabled: item.enabled
    });
  });
  //optionDict转换options
  const options = [
    {
      value: 'local',
      label: 'local'
    },
    ...Object.keys(optionDict)
      .filter((key) => optionDict[key].length > 0)
      .map((key) => {
        return {
          value: key,
          label: key,
          children: optionDict[key]
        };
      })
  ];
  const displayRender: CascaderProps<DefaultOptionType>['displayRender'] = (
    labels,
    selectedOptions = []
  ) =>
    labels.map((label, i) => {
      const option = selectedOptions[i];
      return (
        option &&
        i === labels.length - 1 && (
          <span key={label}>
            {label}{' '}
            {labels.length > 1 && (
              <Tag color={option.enabled ? 'processing' : 'error'}>{selectedOptions[0].label}</Tag>
            )}
          </span>
        )
      );
    });
  return (
    <ProFormCascader
      name={['flinkMode']}
      rules={[{ required: true }]}
      fieldProps={{
        options: options,
        displayRender: displayRender,
        allowClear: false
      }}
    />
  );
});
