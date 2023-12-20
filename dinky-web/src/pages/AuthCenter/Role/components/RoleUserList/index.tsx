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

import { USER_TYPE_ENUM } from '@/pages/AuthCenter/User/components/constants';
import { UserBaseInfo } from '@/types/AuthCenter/data';
import { YES_OR_NO_ENUM, YES_OR_NO_FILTERS_MAPPING } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import { ActionType, ProColumns, ProTable } from '@ant-design/pro-components';
import { Drawer } from 'antd';
import React, { useEffect, useRef } from 'react';

type RoleUserListProps = {
  role: Partial<UserBaseInfo.Role>;
  userList: UserBaseInfo.User[];
  open: boolean;
  loading: boolean;
  onClose: () => void;
};
const RoleUserList: React.FC<RoleUserListProps> = (props) => {
  const actionRef = useRef<ActionType>();
  const { role, loading, userList, open, onClose } = props;

  useEffect(() => {
    actionRef.current?.reload?.();
  }, [userList, role]);

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
      title: l('user.jobnumber'),
      dataIndex: 'worknum'
    },
    {
      title: l('user.phone'),
      dataIndex: 'mobile',
      hideInSearch: true
    },
    {
      title: l('user.type'),
      dataIndex: 'userType',
      valueEnum: USER_TYPE_ENUM(),
      hideInSearch: true
    },
    {
      title: l('user.superAdminFlag'),
      dataIndex: 'superAdminFlag',
      valueEnum: YES_OR_NO_ENUM,
      hideInSearch: true,
      filters: YES_OR_NO_FILTERS_MAPPING,
      filterMultiple: false
    }
  ];

  return (
    <>
      <Drawer
        title={`${role.roleName} - ${l('role.user.list')}`}
        width={'60%'}
        open={open}
        maskClosable={false}
        onClose={onClose}
      >
        <ProTable<UserBaseInfo.User>
          search={false}
          pagination={false}
          options={false}
          rowKey={(record) => record.id}
          actionRef={actionRef}
          loading={loading}
          dataSource={userList}
          columns={userColumns}
        />
      </Drawer>
    </>
  );
};

export default RoleUserList;
