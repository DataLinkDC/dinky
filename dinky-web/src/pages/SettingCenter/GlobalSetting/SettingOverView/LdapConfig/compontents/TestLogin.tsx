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

import { handleOption } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { LoginOutlined } from '@ant-design/icons';
import { ModalForm, ProFormText } from '@ant-design/pro-components';
import { Form, Tag } from 'antd';

export const TestLogin = () => {
  const [form] = Form.useForm();

  const testLogin = async (value: any) => {
    await handleOption(API_CONSTANTS.LDAP_TEST_LOGIN, l('sys.ldap.settings.testLogin'), value);
  };

  return (
    <>
      <ModalForm
        width={400}
        onFinish={testLogin}
        form={form}
        modalProps={{
          onCancel: () => form.resetFields(),
          okButtonProps: { htmlType: 'submit', autoFocus: true }
        }}
        trigger={
          <Tag icon={<LoginOutlined />} color='#108ee9'>
            {l('sys.ldap.settings.testLogin')}
          </Tag>
        }
      >
        <ProFormText name='username' label={l('login.username.placeholder')} />
        <ProFormText name='password' label={l('login.password.placeholder')} />
      </ModalForm>
    </>
  );
};
