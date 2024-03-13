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

import { ClusterType, CLUSTER_INSTANCE_TYPE } from '@/pages/RegCenter/Cluster/constants';
import { validatorJMHAAdderess } from '@/pages/RegCenter/Cluster/Instance/components/function';
import { Cluster } from '@/types/RegCenter/data.d';
import { l } from '@/utils/intl';
import {
  ProFormGroup,
  ProFormSelect,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { Alert } from 'antd';
import React from 'react';

type InstanceFormProps = {
  values: Partial<Cluster.Instance>;
};
const InstanceForm: React.FC<InstanceFormProps> = (props) => {
  const { values } = props;
  const renderForm = () => {
    return (
      <>
        {values && values.autoRegisters && (
          <Alert type={'warning'} message={l('rc.ci.autoRegisterCannotModify')} showIcon />
        )}
        <ProFormGroup>
          <ProFormText
            name='name'
            label={l('rc.ci.name')}
            width='md'
            disabled={values && values.autoRegisters}
            rules={[{ required: true, message: l('rc.ci.namePlaceholder') }]}
            placeholder={l('rc.ci.namePlaceholder')}
          />

          <ProFormText
            name='alias'
            label={l('rc.ci.alias')}
            width='sm'
            disabled={values && values.autoRegisters}
            placeholder={l('rc.ci.aliasPlaceholder')}
          />

          <ProFormSelect
            name='type'
            label={l('rc.ci.type')}
            width='sm'
            disabled={values && values.autoRegisters}
            options={CLUSTER_INSTANCE_TYPE([
              ClusterType.YARN_APPLICATION,
              ClusterType.KUBERNETES_OPERATOR,
              ClusterType.KUBERNETES_APPLICATION
            ])}
            rules={[{ required: true, message: l('rc.ci.typePlaceholder') }]}
            placeholder={l('rc.ci.typePlaceholder')}
          />

          <ProFormTextArea
            name='hosts'
            label={l('rc.ci.jmha')}
            width='md'
            tooltip={l('rc.ci.jmha.tips')}
            validateTrigger={['onChange']}
            rules={[
              {
                required: true,
                validator: (rule, hostsValue) => validatorJMHAAdderess(rule, hostsValue)
              }
            ]}
            placeholder={l('rc.ci.jmhaPlaceholder')}
          />
          <ProFormTextArea
            name='note'
            disabled={values && values.autoRegisters}
            label={l('global.table.note')}
            width='md'
            placeholder={l('global.table.notePlaceholder')}
          />
        </ProFormGroup>
      </>
    );
  };

  return <>{renderForm()}</>;
};

export default InstanceForm;
