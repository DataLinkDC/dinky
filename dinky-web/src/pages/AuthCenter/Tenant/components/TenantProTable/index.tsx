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
import TenantForm from '@/pages/AuthCenter/Tenant/components/TenantModalForm';
import TenantModalTransfer from '@/pages/AuthCenter/Tenant/components/TenantModalTransfer';
import TenantUserList from '@/pages/AuthCenter/Tenant/components/TenantUserList';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
  handleRemoveById,
  queryDataByParams,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { InitTenantListState } from '@/types/AuthCenter/init.d';
import { TenantListState } from '@/types/AuthCenter/state.d';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import React, { useRef, useState } from 'react';

const TenantProTable: React.FC = () => {
  /**
   * status
   */
  const [tenantState, setTenantState] = useState<TenantListState>(InitTenantListState);
  const actionRef = useRef<ActionType>();

  const queryUserListByTenantId = async (id: number) => {
    queryDataByParams(API_CONSTANTS.TENANT_USERS, { id }).then((res) =>
      setTenantState((prevState) => ({ ...prevState, tenantUserList: res as UserBaseInfo.User[] }))
    );
  };

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setTenantState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setTenantState((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  /**
   * add tenant
   * @param value
   */
  const handleAddOrUpdateSubmit = async (value: Partial<UserBaseInfo.Tenant>) => {
    await executeAndCallbackRefresh(async () =>
      handleAddOrUpdate(
        API_CONSTANTS.TENANT,
        value,
        () => {},
        () => setTenantState((prevState) => ({ ...prevState, addedOpen: false }))
      )
    );
  };

  /**
   * delete tenant
   * @param id tenant id
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallbackRefresh(async () => handleRemoveById(API_CONSTANTS.TENANT_DELETE, id));
  };

  /**
   * assign user to tenant
   */
  const handleAssignUserSubmit = async () => {
    await executeAndCallbackRefresh(async () =>
      handleAddOrUpdate(API_CONSTANTS.ASSIGN_USER_TO_TENANT, {
        tenantId: tenantState.value.id,
        userIds: tenantState.tenantUserIds
      })
    );
    setTenantState((prevState) => ({ ...prevState, assignUserOpen: false }));
  };
  const handleCancel = () => {
    setTenantState((prevState) => ({
      ...prevState,
      addedOpen: false,
      editOpen: false,
      assignUserOpen: false
    }));
  };

  const handleAssignUserChange = (value: string[]) => {
    setTenantState((prevState) => ({ ...prevState, tenantUserIds: value }));
  };

  /**
   * edit visible change
   * @param record
   */
  const handleEditVisible = (record: Partial<UserBaseInfo.Tenant>) => {
    setTenantState((prevState) => ({ ...prevState, editOpen: true, value: record }));
  };
  /**
   * assign user visible change
   * @param record
   */
  const handleAssignVisible = (record: Partial<UserBaseInfo.Tenant>) => {
    setTenantState((prevState) => ({ ...prevState, assignUserOpen: true, value: record }));
  };

  const handleShowUser = async (record: Partial<UserBaseInfo.Tenant>) => {
    await queryUserListByTenantId(record.id as number);
    setTenantState((prevState) => ({ ...prevState, viewUsersOpen: true, value: record }));
  };

  const handleSetTenantAdmin = async (value: Partial<UserBaseInfo.User>) => {
    let tenantAdmin = false;
    if (value.tenantAdminFlag) {
      tenantAdmin = true;
    }
    await executeAndCallbackRefresh(async () => {
      await updateDataByParam(API_CONSTANTS.USER_SET_TENANT_ADMIN, {
        userId: value.id,
        tenantId: tenantState.value.id,
        tenantAdminFlag: tenantAdmin
      });
      await queryUserListByTenantId(tenantState.value.id as number);
    });
  };

  /**
   * columns
   */
  const columns: ProColumns<UserBaseInfo.Tenant>[] = [
    {
      title: l('tenant.TenantCode'),
      dataIndex: 'tenantCode',
      render: (text, record) => {
        return HasAuthority(PermissionConstants.AUTH_TENANT_VIEW_USER) ? (
          <a onClick={() => handleShowUser(record)}> {text} </a>
        ) : (
          <span> {text} </span>
        );
      }
    },
    {
      title: l('global.table.note'),
      dataIndex: 'note',
      ellipsis: true
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInSearch: true
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      hideInSearch: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '10%',
      fixed: 'right',
      render: (_: any, record: UserBaseInfo.Tenant) => [
        <Authorized key={`${record.id}_edit_auth`} path={PermissionConstants.AUTH_TENANT_EDIT}>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)} />
        </Authorized>,
        <Authorized
          key={`${record.id}_ass_auth`}
          path={PermissionConstants.AUTH_TENANT_ASSIGN_USER}
        >
          <AssignBtn
            key={`${record.id}_ass`}
            onClick={() => handleAssignVisible(record)}
            title={l('tenant.AssignUser')}
          />
        </Authorized>,
        <Authorized key={`${record.id}_delete_auth`} path={PermissionConstants.AUTH_TENANT_DELETE}>
          <>
            {record.id !== 1 && (
              <PopconfirmDeleteBtn
                key={`${record.id}_delete`}
                onClick={() => handleDeleteSubmit(record.id)}
                description={l('tenant.deleteConfirm')}
              />
            )}
          </>
        </Authorized>
      ]
    }
  ];

  /**
   * render
   */
  return (
    <>
      <ProTable<UserBaseInfo.Tenant>
        {...PROTABLE_OPTIONS_PUBLIC}
        key={'tenantTable'}
        loading={tenantState.loading}
        headerTitle={l('tenant.TenantManager')}
        actionRef={actionRef}
        toolBarRender={() => [
          <Authorized key={`CreateTenant_auth`} path={PermissionConstants.AUTH_TENANT_ADD}>
            <CreateBtn
              key={'tenantTable'}
              onClick={() => setTenantState((prevState) => ({ ...prevState, addedOpen: true }))}
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.TENANT, { ...params, sorter, filter })
        }
        columns={columns}
      />

      {/*add tenant form*/}
      <TenantForm
        key={'tenantFormAdd'}
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={tenantState.addedOpen}
        values={{}}
      />

      {/*update tenant form*/}
      <TenantForm
        key={'tenantFormUpdate'}
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={tenantState.editOpen}
        values={tenantState.value}
      />
      {/* assign user to tenant */}
      <TenantModalTransfer
        tenant={tenantState.value}
        modalVisible={tenantState.assignUserOpen}
        onChange={(values) => handleAssignUserChange(values)}
        onCancel={() => handleCancel()}
        onSubmit={() => handleAssignUserSubmit()}
      />

      {tenantState.value && Object.keys(tenantState.value).length > 0 && (
        <TenantUserList
          tenant={tenantState.value}
          open={tenantState.viewUsersOpen}
          userList={tenantState.tenantUserList}
          loading={tenantState.loading}
          onClose={() => setTenantState((prevState) => ({ ...prevState, viewUsersOpen: false }))}
          onSubmit={handleSetTenantAdmin}
        />
      )}
    </>
  );
};

export default TenantProTable;
