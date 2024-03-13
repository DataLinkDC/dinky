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

import { AssignBtn } from '@/components/CallBackButton/AssignBtn';
import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import AssignMenu from '@/pages/AuthCenter/Role/components/AssignMenu';
import RoleUserList from '@/pages/AuthCenter/Role/components/RoleUserList';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
  handleOption,
  handleRemoveById,
  queryDataByParams
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { InitRoleListState } from '@/types/AuthCenter/init.d';
import { RoleListState } from '@/types/AuthCenter/state.d';
import { PermissionConstants } from '@/types/Public/constants';
import { getTenantByLocalStorage } from '@/utils/function';
import { l } from '@/utils/intl';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import { Tag } from 'antd';
import React, { Key, useRef, useState } from 'react';
import RoleModalForm from '../RoleModalForm';

const RoleProTable: React.FC = () => {
  /**
   * status
   */
  const [roleListState, setRoleListState] = useState<RoleListState>(InitRoleListState);

  const actionRef = useRef<ActionType>();

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setRoleListState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setRoleListState((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  /**
   * delete role by id
   * @param id role id
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallbackRefresh(async () => handleRemoveById(API_CONSTANTS.ROLE_DELETE, id));
  };

  /**
   * add or update role submit callback
   * @param value
   */
  const handleAddOrUpdateSubmit = async (value: any) => {
    await executeAndCallbackRefresh(async () => {
      await handleAddOrUpdate(
        API_CONSTANTS.ROLE_ADDED_OR_UPDATE,
        {
          ...value,
          tenantId: getTenantByLocalStorage()
        },
        () => {},
        () => setRoleListState((prevState) => ({ ...prevState, addedOpen: false }))
      );
    });
  };

  /**
   * cancel
   */
  const handleCancel = () => {
    setRoleListState((prevState) => ({
      ...prevState,
      addedOpen: false,
      editOpen: false,
      assignMenuOpen: false,
      viewUsersOpen: false
    }));
  };

  const handleAssignMenuSubmit = async (selectKeys: Key[]) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(
        API_CONSTANTS.ROLE_ASSIGN_MENU,
        l('role.assign'),
        {
          roleId: roleListState.value.id,
          menuIds: selectKeys
        },
        () => handleCancel()
      );
    });
  };

  /**
   * edit role status
   * @param record
   */
  const handleEditVisible = (record: Partial<UserBaseInfo.Role>) => {
    setRoleListState((prevState) => ({ ...prevState, value: record, editOpen: true }));
  };

  /**
   * assign user visible change
   * @param record
   */
  const handleAssignVisible = (record: Partial<UserBaseInfo.Role>) => {
    setRoleListState((prevState) => ({ ...prevState, value: record, assignMenuOpen: true }));
  };

  const handleClickViewUserList = (record: Partial<UserBaseInfo.Role>) => {
    queryDataByParams(API_CONSTANTS.ROLE_USER_LIST, { roleId: record.id }).then((res) =>
      setRoleListState((prevState) => ({
        ...prevState,
        roleUserList: res as UserBaseInfo.User[],
        value: record,
        viewUsersOpen: true
      }))
    );
  };

  /**
   * columns
   */
  const columns: ProColumns<UserBaseInfo.Role>[] = [
    {
      title: l('role.roleCode'),
      dataIndex: 'roleCode',
      render: (_, record: UserBaseInfo.Role) => {
        return HasAuthority(PermissionConstants.AUTH_ROLE_VIEW_USER_LIST) ? (
          <a onClick={() => handleClickViewUserList(record)}> {record.roleCode} </a>
        ) : (
          <span> {record.roleCode} </span>
        );
      }
    },
    {
      title: l('role.roleName'),
      dataIndex: 'roleName'
    },
    {
      title: l('role.belongTenant'),
      hideInSearch: true,
      render: (_: any, record: UserBaseInfo.Role) => {
        return <Tag color='blue'>{record.tenant.tenantCode}</Tag>;
      }
    },
    {
      title: l('global.table.note'),
      dataIndex: 'note',
      hideInSearch: true,
      ellipsis: true
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      hideInSearch: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      hideInSearch: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '10%',
      fixed: 'right',
      render: (_: any, record: UserBaseInfo.Role) => [
        <Authorized key={`${record.id}_add_auth`} path={PermissionConstants.AUTH_ROLE_EDIT}>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)} />
        </Authorized>,
        <Authorized key={`${record.id}_delete_auth`} path={PermissionConstants.AUTH_ROLE_DELETE}>
          <>
            {record.id !== 1 && (
              <PopconfirmDeleteBtn
                key={`${record.id}_delete`}
                onClick={() => handleDeleteSubmit(record.id)}
                description={l('role.deleteConfirm')}
              />
            )}
          </>
        </Authorized>,
        <Authorized
          key={`${record.id}_assignMenu_auth`}
          path={PermissionConstants.AUTH_ROLE_ASSIGN_MENU}
        >
          <AssignBtn
            key={`${record.id}_ass`}
            onClick={() => handleAssignVisible(record)}
            title={l('role.assignMenu', '', { roleName: record.roleName })}
          />
        </Authorized>
      ]
    }
  ];

  /**
   * render
   */
  return (
    <>
      <ProTable<UserBaseInfo.Role>
        {...PROTABLE_OPTIONS_PUBLIC}
        headerTitle={l('role.roleManagement')}
        actionRef={actionRef}
        loading={roleListState.loading}
        toolBarRender={() => [
          <Authorized key={'roleadd'} path={PermissionConstants.AUTH_ROLE_ADD}>
            <CreateBtn
              key={'toolBarRender'}
              onClick={() => setRoleListState((prevState) => ({ ...prevState, addedOpen: true }))}
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.ROLE, { ...params, sorter, filter })
        }
        columns={columns}
      />
      {/* create  */}
      <RoleModalForm
        onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={roleListState.addedOpen}
        values={{}}
      />
      {/* modify */}
      <RoleModalForm
        onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={roleListState.editOpen}
        values={roleListState.value}
      />
      {Object.keys(roleListState.value).length > 0 && (
        <AssignMenu
          values={roleListState.value}
          open={roleListState.assignMenuOpen}
          onSubmit={handleAssignMenuSubmit}
          onClose={() => setRoleListState((prevState) => ({ ...prevState, assignMenuOpen: false }))}
        />
      )}
      {Object.keys(roleListState.value).length > 0 && (
        <RoleUserList
          role={roleListState.value}
          open={roleListState.viewUsersOpen}
          userList={roleListState.roleUserList}
          loading={roleListState.loading}
          onClose={() => setRoleListState((prevState) => ({ ...prevState, viewUsersOpen: false }))}
        />
      )}
    </>
  );
};
export default RoleProTable;
