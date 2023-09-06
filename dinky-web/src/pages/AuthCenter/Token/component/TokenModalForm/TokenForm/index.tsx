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

import { TOKEN_EXPIRE_TYPE } from '@/pages/AuthCenter/Token/component/constants';
import {
  buildRoleOptions,
  buildTenantOptions,
  buildUserOptions
} from '@/pages/AuthCenter/Token/component/function';
import { TokenStateType } from '@/pages/AuthCenter/Token/component/model';
import { UserBaseInfo } from '@/types/AuthCenter/data';
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
          label='Token'
          width={'xl'}
          rules={[{ required: true, message: '请生成Token' }]}
          fieldProps={{
            suffix: <a onClick={() => buildToken()}>生成Token</a>
          }}
        />
      </ProFormGroup>

      <ProFormGroup>
        <ProFormSelect
          name='userId'
          label='用户'
          width='xl'
          allowClear
          showSearch
          options={buildUserOptions(users)}
          rules={[{ required: true, message: '请选择用户' }]}
        />
        <ProFormSelect
          name='roleId'
          label='角色'
          width='xl'
          allowClear
          showSearch
          options={buildRoleOptions(roles)}
          rules={[{ required: true, message: '请选择角色' }]}
        />
        <ProFormSelect
          name='tenantId'
          label='租户'
          width='xl'
          allowClear
          showSearch
          options={buildTenantOptions(tenants)}
          rules={[{ required: true, message: '请选择租户' }]}
        />
      </ProFormGroup>

      <ProFormGroup>
        <ProFormRadio.Group
          name='expireType'
          label='过期类型'
          initialValue={expireType}
          width='xl'
          rules={[{ required: true, message: '请选择过期类型' }]}
          options={TOKEN_EXPIRE_TYPE}
        />
        {expireType === 2 && (
          <>
            <ProFormDateTimePicker
              allowClear
              name={'expireTime'}
              label='过期时间'
              width='xl'
              placeholder={'过期结束时间'}
              rules={[{ required: true, message: '请选择过期时间' }]}
            />
          </>
        )}
        {expireType === 3 && (
          <>
            <ProFormDateTimeRangePicker
              name={'expireTime'}
              label='过期时间区间'
              width='xl'
              allowClear
              // transform={(value: any) => {
              //   return {
              //     startTime: value[0],
              //     endTime: value[1],
              //   };
              // }}
              // convertValue={(value: any) => {
              //   return [value.startTime, value.endTime];
              // }}
              placeholder={['开始时间', '结束时间']}
              rules={[{ required: true, message: '过期时间区间' }]}
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
