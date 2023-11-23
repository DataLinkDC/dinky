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
import { ProForm, ProFormText } from '@ant-design/pro-components';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import React from 'react';

type UserFormProps = {
  values: Partial<UserBaseInfo.Role>;
  form: FormInstance<Values>;
};
const UserForm: React.FC<UserFormProps> = (props) => {
  const { values, form } = props;

  /**
   * user form render
   * @returns {JSX.Element}
   */
  const userFormRender = () => {
    return (
      <>
        <ProFormText
          name='username'
          label={l('user.username')}
          placeholder={l('user.usernamePlaceholder')}
          rules={[
            {
              required: true,
              message: l('user.usernamePlaceholder')
            }
          ]}
        />

        <ProFormText
          name='nickname'
          label={l('user.nickname')}
          placeholder={l('user.nicknamePlaceholder')}
          rules={[
            {
              required: true,
              message: l('user.nicknamePlaceholder')
            }
          ]}
        />

        <ProFormText
          name='worknum'
          label={l('user.jobnumber')}
          placeholder={l('user.jobnumberPlaceholder')}
        />

        <ProFormText
          name='mobile'
          label={l('user.phone')}
          placeholder={l('user.phonePlaceholder')}
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
        layout={'horizontal'}
        submitter={false}
      >
        {userFormRender()}
      </ProForm>
    </>
  );
};

export default UserForm;
