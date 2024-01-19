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

import { FLINK_CONFIG_LIST } from '@/pages/RegCenter/Cluster/Configuration/components/contants';
import { l } from '@/utils/intl';
import { ProFormGroup, ProFormText } from '@ant-design/pro-components';
import { Divider } from 'antd';

const HighPriorityConfig = () => {
  const renderHighPriorityConfig = () => {
    return FLINK_CONFIG_LIST.map((item) => (
      <>
        <ProFormText
          name={['config', 'flinkConfig', 'configuration', item.name]}
          label={item.label}
          width={400}
          placeholder={item.placeholder}
          tooltip={item.tooltip}
        />
      </>
    ));
  };

  return (
    <>
      <Divider>{l('rc.cc.defineConfig')}</Divider>
      <ProFormGroup direction={'horizontal'} align={'baseline'}>
        {renderHighPriorityConfig()}
      </ProFormGroup>
    </>
  );
};

export default HighPriorityConfig;
