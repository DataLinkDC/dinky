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

import { Authorized } from '@/hooks/useAccess';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import {
  PermissionConstants,
  YES_OR_NO_ENUM,
  YES_OR_NO_FILTERS_MAPPING
} from '@/types/Public/constants';
import { l } from '@/utils/intl';
import { ActionType, ProColumns, ProTable } from '@ant-design/pro-components';
import { Button, Drawer } from 'antd';
import React, { useEffect, useRef } from 'react';

type TenantUserListProps = {
  tenant: Partial<UserBaseInfo.Tenant>;
  userList: UserBaseInfo.User[];
  open: boolean;
  loading: boolean;
  onClose: () => void;
  onSubmit: (values: Partial<UserBaseInfo.User>) => void;
};
const TenantUserList: React.FC<TenantUserListProps> = (props) => {
  const actionRef = useRef<ActionType>();
  const { tenant, loading, userList, open, onClose, onSubmit } = props;

  useEffect(() => {
    actionRef.current?.reload?.();
  }, [userList, tenant]);

  /**
   * user infon list
   * @type {({dataIndex: string, title: string, key: string} | {dataIndex: string, title: string, key: string} | {dataIndex: string, title: string, key: string} | {hideInSearch: boolean, dataIndex: string, valueEnum: {true: {text: JSX.Element, status: string}, false: {text: JSX.Element, status: string}}, filters: ({text: string, value: number} | {text: string, value: number})[], title: string, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueEnum: {true: {text: JSX.Element, status: string}, false: {text: JSX.Element, status: string}}, filters: ({text: string, value: number} | {text: string, value: number})[], title: string, filterMultiple: boolean} | {valueType: string, width: string, fixed: string, title: string, render: (_: any, record: UserBaseInfo.User) => JSX.Element[]})[]}
   */
  const userColumns: ProColumns<UserBaseInfo.User>[] = [
    {
      title: l('user.username'),
      dataIndex: 'username',
      key: 'username'
    },
    {
      title: l('user.nickname'),
      dataIndex: 'nickname',
      key: 'nickname'
    },
    {
      title: l('user.superAdminFlag'),
      dataIndex: 'superAdminFlag',
      valueEnum: YES_OR_NO_ENUM,
      hideInSearch: true,
      filters: YES_OR_NO_FILTERS_MAPPING,
      filterMultiple: false
    },
    {
      title: l('user.tenantAdminFlag'),
      dataIndex: 'tenantAdminFlag',
      valueEnum: YES_OR_NO_ENUM,
      hideInSearch: true,
      filters: YES_OR_NO_FILTERS_MAPPING,
      filterMultiple: false
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '10%',
      fixed: 'right',
      render: (_: any, record: UserBaseInfo.User) => [
        <Authorized
          key={`${record.id}_set_tenant_m_auth`}
          path={PermissionConstants.AUTH_TENANT_SET_USER_TO_TENANT_ADMIN}
        >
          <Button
            type={'link'}
            key={`${record.id}_set_tenant_m`}
            title={record.tenantAdminFlag ? l('tenant.cancel.admin') : l('tenant.set.admin')}
            onClick={() => onSubmit(record)}
          >
            {record.tenantAdminFlag ? l('tenant.cancel.admin') : l('tenant.set.admin')}
          </Button>
        </Authorized>
      ]
    }
  ];

  return (
    <>
      <Drawer
        title={`${tenant.tenantCode} - ${l('tenant.user.list')}`}
        width={'50%'}
        open={open}
        maskClosable={false}
        onClose={onClose}
      >
        <ProTable<UserBaseInfo.User>
          search={false}
          pagination={false}
          options={false}
          rowKey='id'
          actionRef={actionRef}
          loading={loading}
          dataSource={userList}
          columns={userColumns}
        />
      </Drawer>
    </>
  );
};

export default TenantUserList;
