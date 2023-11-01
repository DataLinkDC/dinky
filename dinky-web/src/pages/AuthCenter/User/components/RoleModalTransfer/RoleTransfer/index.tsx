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
import { InitRoleTransferState } from '@/types/AuthCenter/init.d';
import { RoleTransferState } from '@/types/AuthCenter/state.d';
import { l } from '@/utils/intl';
import { ProColumns } from '@ant-design/pro-components';
import { useEffect, useState } from 'react';

type TransferFromProps = {
  role: Partial<UserBaseInfo.Role>;
  onChange: (values: string[]) => void;
};

const RoleTransfer = (props: TransferFromProps) => {
  /**
   * status
   */
  const { role, onChange: handleChange } = props;

  const [roleTransferState, setRoleTransferState] =
    useState<RoleTransferState>(InitRoleTransferState);

  /**
   * select change
   * @param sourceSelectedKeys
   * @param targetSelectedKeys
   */
  const onSelectChange = (sourceSelectedKeys: string[], targetSelectedKeys: string[]) => {
    const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
    setRoleTransferState((prevState) => ({ ...prevState, selectedKeys: newSelectedKeys }));
  };

  /**
   * get data
   */
  useEffect(() => {
    getData(API_CONSTANTS.GET_ROLES_BY_USERID, { id: role.id }).then((result) => {
      setRoleTransferState((prevState) => ({
        ...prevState,
        roleList: result.data.roles,
        targetKeys: result.data.roleIds
      }));
      handleChange(result.data.roleIds);
    });
  }, []);

  /**
   * table columns
   */
  const columns: ProColumns<UserBaseInfo.Role>[] = [
    {
      dataIndex: 'roleCode',
      title: l('role.roleCode')
    },
    {
      dataIndex: 'roleName',
      title: l('role.roleName')
    },
    {
      dataIndex: 'note',
      title: l('global.table.note'),
      ellipsis: true
    }
  ];

  /**
   * transfer change
   * @param nextTargetKeys
   */
  const onChange = (nextTargetKeys: string[]) => {
    setRoleTransferState((prevState) => ({ ...prevState, targetKeys: nextTargetKeys }));
    handleChange(nextTargetKeys);
  };

  /**
   * render
   */
  return (
    <>
      <TableTransfer
        dataSource={roleTransferState.roleList}
        targetKeys={roleTransferState.targetKeys}
        selectedKeys={roleTransferState.selectedKeys}
        rowKey={(item) => item.id}
        onChange={onChange}
        onSelectChange={onSelectChange}
        filterOption={(inputValue, item: UserBaseInfo.Role) => {
          if (!item.roleCode || !item.roleName) return false;
          return (
            item.roleCode.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1 ||
            item.roleName.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
          );
        }}
        leftColumns={columns}
        rightColumns={columns}
      />
    </>
  );
};

export default RoleTransfer;
