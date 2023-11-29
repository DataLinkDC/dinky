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

import { FORM_LAYOUT_PUBLIC } from '@/services/constants';
import { UserBaseInfo } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { ProForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import React from 'react';

type TenantFormProps = {
  values: Partial<UserBaseInfo.Tenant>;
  form: FormInstance<Values>;
};

const TenantForm: React.FC<TenantFormProps> = (props) => {
  /**
   * props
   */
  const { values, form } = props;

  /**
   * render form
   * @constructor
   */
  const tenantFormRender = () => {
    return (
      <>
        <ProFormText
          name='tenantCode'
          label={l('tenant.TenantCode')}
          placeholder={l('tenant.EnterTenantCode')}
          rules={[{ required: true, message: l('tenant.EnterTenantCode') }]}
        />
        <ProFormTextArea
          name='note'
          allowClear
          label={l('global.table.note')}
          placeholder={l('tenant.EnterTenantNote')}
          rules={[{ required: true, message: l('tenant.EnterTenantNote') }]}
        />
      </>
    );
  };

  return (
    <>
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        initialValues={values}
        submitter={false}
        layout={'horizontal'}
      >
        {tenantFormRender()}
      </ProForm>
    </>
  );
};

export default TenantForm;
