/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import React from 'react';
import {FormInstance} from 'antd/es/form/hooks/useForm';
import {Values} from 'async-validator';
import {Cluster} from '@/types/RegCenter/data';
import {ProForm, ProFormGroup, ProFormSelect, ProFormText, ProFormTextArea} from '@ant-design/pro-components';
import {CLUSTER_INSTANCE_TYPE} from '@/pages/RegCenter/Cluster/Instance/components/contants';
import {MODAL_FORM_OPTIONS} from '@/services/constants';
import {l} from '@/utils/intl';
import {validatorJMHAAdderess} from '@/pages/RegCenter/Cluster/Instance/components/function';
import {ts} from "@hapi/hoek";


type InstanceFormProps = {
  form: FormInstance<Values>
  value: Partial<Cluster.Instance>;
}
const InstanceForm: React.FC<InstanceFormProps> = (props) => {
  const {form, value} = props;


  const renderForm = () => {
    return <>
      <ProFormGroup>
        <ProFormText
          name="name"
          label={l('rc.ci.name')}
          width="md"
          rules={[{required: true, message: l('rc.ci.namePlaceholder')}]}
          placeholder={l('rc.ci.namePlaceholder')}
        />

        <ProFormText
          name="alias"
          label={l('rc.ci.alias')}
          width="sm"
          placeholder={l('rc.ci.aliasPlaceholder')}
        />

        <ProFormSelect
          name="type"
          label={l('rc.ci.type')}
          width="sm"
          options={CLUSTER_INSTANCE_TYPE}
          rules={[{required: true, message: l('rc.ci.typePlaceholder')}]}
          placeholder={l('rc.ci.typePlaceholder')}
        />

        <ProFormTextArea
          name="hosts"
          label={l('rc.ci.jmha')}
          width="md"
          tooltip={l('rc.ci.jmha.tips')}
          validateTrigger={['onChange']}
          rules={[{required: true, validator: (rule, hostsValue) => validatorJMHAAdderess(rule, hostsValue)}]}
          placeholder={l('rc.ci.jmhaPlaceholder')}
        />
        <ProFormTextArea
          name="note"
          label={l('global.table.note')}
          width="md"
          placeholder={l('global.table.notePlaceholder')}
        />

      </ProFormGroup>

    </>;
  };


  return <>
    {/*// @ts-ignore*/}
    <ProForm
      {...MODAL_FORM_OPTIONS}
      form={form}
      initialValues={value}
      submitter={false}
    >
      {renderForm()}
    </ProForm>
  </>;


};

export default InstanceForm;
