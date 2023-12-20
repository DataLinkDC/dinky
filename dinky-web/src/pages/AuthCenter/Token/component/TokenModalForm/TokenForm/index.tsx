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

import { TOKEN_EXPIRE_TYPE } from '@/pages/AuthCenter/Token/component/constants';
import {
  buildRoleOptions,
  buildTenantOptions,
  buildUserOptions
} from '@/pages/AuthCenter/Token/component/function';
import { TokenStateType } from '@/pages/AuthCenter/Token/component/model';
import { UserBaseInfo } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import {
  ProFormDateTimePicker,
  ProFormDateTimeRangePicker,
  ProFormGroup,
  ProFormRadio,
  ProFormSelect,
  ProFormText
} from '@ant-design/pro-components';
import { connect } from '@umijs/max';
import React from 'react';

type TokenFormProps = {
  users: UserBaseInfo.User[];
  roles: UserBaseInfo.Role[];
  tenants: UserBaseInfo.Tenant[];
  expireType: number;
  tokenValue: string;
  buildToken: () => void;
};
const TokenForm: React.FC<TokenFormProps & connect> = (props) => {
  const { users, roles, tenants, expireType, buildToken } = props;

  return (
    <>
      <ProFormGroup>
        <ProFormText
          name='tokenValue'
          label={l('token.value')}
          width={'xl'}
          placeholder={l('token.generate.placeholder')}
          rules={[{ required: true, message: l('token.generate.placeholder') }]}
          fieldProps={{
            suffix: <a onClick={() => buildToken()}>{l('token.generate')}</a>
          }}
        />
      </ProFormGroup>

      <ProFormGroup>
        <ProFormSelect
          name='userId'
          label={l('token.username')}
          width='xl'
          allowClear
          showSearch
          placeholder={l('token.user.choose')}
          options={buildUserOptions(users)}
          rules={[{ required: true, message: l('token.user.choose') }]}
        />
        <ProFormSelect
          name='roleId'
          label={l('token.role')}
          width='xl'
          allowClear
          showSearch
          placeholder={l('token.role.choose')}
          options={buildRoleOptions(roles)}
          rules={[{ required: true, message: l('token.role.choose') }]}
        />
        <ProFormSelect
          name='tenantId'
          label={l('token.tenant')}
          width='xl'
          allowClear
          showSearch
          placeholder={l('token.tenant.choose')}
          options={buildTenantOptions(tenants)}
          rules={[{ required: true, message: l('token.tenant.choose') }]}
        />
      </ProFormGroup>

      <ProFormGroup>
        <ProFormRadio.Group
          name='expireType'
          label={l('token.expireType')}
          initialValue={expireType}
          width='xl'
          rules={[{ required: true, message: l('token.choose.expireType') }]}
          options={TOKEN_EXPIRE_TYPE}
        />
        {expireType === 2 && (
          <>
            <ProFormDateTimePicker
              allowClear
              name={'expireTime'}
              label={l('token.expireTime')}
              width='xl'
              placeholder={l('token.expireEndTime')}
              rules={[{ required: true, message: l('token.expireTime.placeholder') }]}
            />
          </>
        )}
        {expireType === 3 && (
          <>
            <ProFormDateTimeRangePicker
              name={'expireTime'}
              label={l('token.expireType.3')}
              width='xl'
              allowClear
              placeholder={[l('token.expireStartTime'), l('token.expireEndTime')]}
              rules={[{ required: true, message: l('token.expireTime.placeholder') }]}
            />
          </>
        )}
      </ProFormGroup>
    </>
  );
};

export default connect(({ Token }: { Token: TokenStateType }) => ({
  users: Token.users,
  roles: Token.roles,
  tenants: Token.tenants,
  tokenValue: Token.token
}))(TokenForm);
