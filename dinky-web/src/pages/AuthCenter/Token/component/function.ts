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

import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { DefaultOptionType } from 'antd/es/select';

export const buildUserOptions = (userList: UserBaseInfo.User[]): DefaultOptionType[] => {
  return userList
    ? userList.map((user) => {
        return {
          label: user.username,
          value: user.id,
          disabled: user.isDelete || !user.enabled
        };
      })
    : [];
};

export const buildRoleOptions = (roleList: UserBaseInfo.Role[]): DefaultOptionType[] => {
  return roleList
    ? roleList.map((role) => {
        return {
          label: role.roleName,
          value: role.id,
          disabled: role.isDelete
        };
      })
    : [];
};

export const buildTenantOptions = (tenantList: UserBaseInfo.Tenant[]): DefaultOptionType[] => {
  return tenantList
    ? tenantList.map((tenant) => {
        return {
          label: tenant.tenantCode,
          value: tenant.id,
          disabled: tenant.isDelete
        };
      })
    : [];
};
