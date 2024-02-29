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

import { ClusterType, CLUSTER_CONFIG_TYPE } from '@/pages/RegCenter/Cluster/constants';
import { l } from '@/utils/intl';
import {
  ProFormDigit,
  ProFormGroup,
  ProFormSelect,
  ProFormSwitch,
  ProFormText
} from '@ant-design/pro-components';
import { Divider } from 'antd';
import React from 'react';
const BaseConfig: React.FC = () => {
  return (
    <>
      <Divider>{l('rc.cc.baseConfig')}</Divider>
      <ProFormGroup>
        <ProFormDigit name='id' hidden={true} />
        <ProFormSelect
          name='type'
          label={l('rc.cc.type')}
          width='md'
          options={CLUSTER_CONFIG_TYPE([
            ClusterType.KUBERNETES_OPERATOR,
            ClusterType.KUBERNETES_APPLICATION,
            ClusterType.YARN
          ])}
          rules={[{ required: true, message: l('rc.cc.typePlaceholder') }]}
          placeholder={l('rc.cc.typePlaceholder')}
        />
        <ProFormText
          name='name'
          label={l('rc.cc.name')}
          width='md'
          rules={[{ required: true, message: l('rc.cc.namePlaceholder') }]}
          placeholder={l('rc.cc.namePlaceholder')}
        />

        <ProFormText
          name='note'
          label={l('global.table.note')}
          width='lg'
          placeholder={l('global.table.notePlaceholder')}
        />
        <ProFormSwitch
          name='enabled'
          label={l('global.table.isEnable')}
          initialValue={false}
          checkedChildren={l('button.enable')}
          unCheckedChildren={l('button.disable')}
        />
      </ProFormGroup>
    </>
  );
};

export default BaseConfig;
