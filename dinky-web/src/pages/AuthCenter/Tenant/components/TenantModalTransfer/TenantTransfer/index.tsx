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

import TableTransfer from '@/components/TableTransfer';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { InitTenantTransferState } from '@/types/AuthCenter/init.d';
import { TenantTransferState } from '@/types/AuthCenter/state.d';
import { l } from '@/utils/intl';
import { ProColumns } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';

type TenantTransferFromProps = {
  tenant: Partial<UserBaseInfo.Tenant>;
  onChange: (values: string[]) => void;
};

const TenantTransfer: React.FC<TenantTransferFromProps> = (props) => {
  const { tenant, onChange: handleChange } = props;

  const [tenantTransferState, setTenantTransferState] =
    useState<TenantTransferState>(InitTenantTransferState);

  const onSelectChange = (sourceSelectedKeys: string[], targetSelectedKeys: string[]) => {
    const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
    setTenantTransferState((prevState) => ({ ...prevState, selectedKeys: newSelectedKeys }));
  };

  useEffect(() => {
    getData(API_CONSTANTS.GET_USER_LIST_BY_TENANTID, { id: tenant.id }).then((result) => {
      setTenantTransferState((prevState) => ({
        ...prevState,
        userList: result.data.users,
        targetKeys: result.data.userIds
      }));
      handleChange(result.data.userIds);
    });
  }, []);

  const columns: ProColumns<UserBaseInfo.User>[] = [
    {
      title: l('user.username'),
      dataIndex: 'username'
    },
    {
      title: l('user.nickname'),
      dataIndex: 'nickname'
    },
    {
      title: l('user.jobnumber'),
      dataIndex: 'worknum'
    }
  ];

  const onChange = (nextTargetKeys: string[]) => {
    setTenantTransferState((prevState) => ({ ...prevState, targetKeys: nextTargetKeys }));
    handleChange(nextTargetKeys);
  };

  return (
    <>
      <TableTransfer
        dataSource={tenantTransferState.userList}
        targetKeys={tenantTransferState.targetKeys}
        selectedKeys={tenantTransferState.selectedKeys}
        rowKey={(item) => item.id as any}
        onChange={onChange}
        onSelectChange={onSelectChange}
        filterOption={(inputValue, item: UserBaseInfo.User) => {
          if (!item.username || !item.nickname || !item.worknum) return false;
          return (
            item.username.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1 ||
            item.nickname.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1 ||
            item.worknum.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
          );
        }}
        leftColumns={columns}
        rightColumns={columns}
      />
    </>
  );
};
export default TenantTransfer;
