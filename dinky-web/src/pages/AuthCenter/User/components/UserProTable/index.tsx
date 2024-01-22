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
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { BackIcon } from '@/components/Icons/CustomIcons';
import { Authorized, HasAuthority, useAccess } from '@/hooks/useAccess';
import { UserType, USER_TYPE_ENUM } from '@/pages/AuthCenter/User/components/constants';
import PasswordModal from '@/pages/AuthCenter/User/components/PasswordModal';
import UserModalForm from '@/pages/AuthCenter/User/components/UserModalForm';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
  handleOption,
  handlePutData,
  handlePutDataByParams,
  handleRemoveById,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { InitUserListState } from '@/types/AuthCenter/init.d';
import { UserListState } from '@/types/AuthCenter/state.d';
import {
  PermissionConstants,
  YES_OR_NO_ENUM,
  YES_OR_NO_FILTERS_MAPPING
} from '@/types/Public/constants';
import { l } from '@/utils/intl';
import { SuccessMessage, WarningMessage } from '@/utils/messages';
import { LockTwoTone, RedoOutlined } from '@ant-design/icons';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import { Button, Popconfirm } from 'antd';
import { useRef, useState } from 'react';
import RoleModalTransfer from '../RoleModalTransfer';

const UserProTable = () => {
  const [userState, setUserState] = useState<UserListState>(InitUserListState);

  const actionRef = useRef<ActionType>(); // table action
  const access = useAccess(); // access control

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setUserState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setUserState((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  /**
   * edit user
   * @param value
   */
  const handleEditVisible = (value: UserBaseInfo.User) => {
    setUserState((prevState) => ({ ...prevState, value: value, editOpen: true }));
  };

  /**
   * assign role
   * @param value
   */
  const handleAssignRole = (value: UserBaseInfo.User) => {
    setUserState((prevState) => ({ ...prevState, value: value, assignRoleOpen: true }));
  };

  /**
   * change password
   * @param value
   */
  const handleChangePassword = (value: UserBaseInfo.User) => {
    setUserState((prevState) => ({ ...prevState, value: value, editPasswordOpen: true }));
  };

  /**
   * delete user
   * @param value
   */
  const handleDeleteUser = async (value: UserBaseInfo.User) => {
    await executeAndCallbackRefresh(async () =>
      handleRemoveById(API_CONSTANTS.USER_DELETE, value.id)
    );
  };

  /**
   * user add role to submit
   */
  const handleGrantRoleSubmit = async () => {
    await executeAndCallbackRefresh(async () =>
      handlePutData(API_CONSTANTS.USER_ASSIGN_ROLE, {
        userId: userState.value.id,
        roleIds: userState.roleIds
      })
    );
    setUserState((prevState) => ({ ...prevState, assignRoleOpen: false }));
  };

  /**
   * user enable or disable
   * @param value
   */
  const handleChangeEnable = async (value: UserBaseInfo.User) => {
    await executeAndCallbackRefresh(async () =>
      updateDataByParam(API_CONSTANTS.USER_ENABLE, { id: value.id })
    );
  };

  /**
   * change password submit
   * @param value
   */
  const handlePasswordChangeSubmit = async (value: UserBaseInfo.ChangePasswordParams) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.USER_MODIFY_PASSWORD, l('button.changePassword'), value);
      setUserState((prevState) => ({ ...prevState, editPasswordOpen: false }));
    });
  };

  /**
   * edit user submit
   * @param value
   */
  const handleSubmitUser = async (value: Partial<UserBaseInfo.User>) => {
    await executeAndCallbackRefresh(async () => handleAddOrUpdate(API_CONSTANTS.USER, value));
    setUserState((prevState) => ({ ...prevState, addedOpen: false }));
  };

  const handleRecoveryUser = async (value: UserBaseInfo.User) => {
    await executeAndCallbackRefresh(async () =>
      handlePutDataByParams(API_CONSTANTS.USER_RECOVERY, l('button.recovery'), {
        id: value.id
      })
    );
  };

  const handleResetPassword = async (value: UserBaseInfo.User) => {
    if (value.isDelete) {
      WarningMessage(l('user.isdelete'));
      return;
    } else {
      await handlePutDataByParams(API_CONSTANTS.USER_RESET_PASSWORD, l('user.resetPassword'), {
        id: value.id
      }).then((res) => {
        const {
          data: { user, originalPassword }
        } = res;
        SuccessMessage(
          l('user.resetPasswordSuccess', '', {
            username: user.username,
            password: originalPassword
          }),
          5
        );
      });
    }
  };

  /**
   * table columns
   */
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
      title: l('user.status'),
      dataIndex: 'isDelete',
      valueEnum: YES_OR_NO_ENUM,
      hideInSearch: true,
      filters: YES_OR_NO_FILTERS_MAPPING,
      filterMultiple: false,
      sorter: (a, b) => Number(a.isDelete) - Number(b.isDelete)
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
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInSearch: true,
      render: (_: any, record: UserBaseInfo.User) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            record={record}
            disabled={!HasAuthority(PermissionConstants.AUTH_USER_EDIT)}
            onChange={() => handleChangeEnable(record)}
          />
        );
      },
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      valueEnum: STATUS_ENUM()
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true,
      hideInSearch: true
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '12%',
      fixed: 'right',
      render: (_: any, record: UserBaseInfo.User) => [
        <Authorized key={`${record.id}_edit_auth`} path={PermissionConstants.AUTH_USER_EDIT}>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)} />
        </Authorized>,
        <Authorized
          key={`${record.id}_assign_auth`}
          path={PermissionConstants.AUTH_USER_ASSIGN_ROLE}
        >
          <AssignBtn
            key={`${record.id}_assign`}
            onClick={() => handleAssignRole(record)}
            title={l('user.assignRole')}
          />
        </Authorized>,
        <Authorized
          key={`${record.id}_change_auth`}
          path={PermissionConstants.AUTH_USER_CHANGE_PASSWORD}
        >
          <>
            {record.userType === UserType.LOCAL && (
              <Button
                className={'options-button'}
                key={`${record.id}_change`}
                icon={<LockTwoTone />}
                title={l('button.changePassword')}
                onClick={() => {
                  handleChangePassword(record);
                }}
              />
            )}
          </>
        </Authorized>,
        <Authorized key={`${record.id}_delete_auth`} path={PermissionConstants.AUTH_USER_DELETE}>
          <>
            {access.isAdmin && !record.isDelete && !record.superAdminFlag && (
              <PopconfirmDeleteBtn
                key={`${record.id}_delete`}
                onClick={() => handleDeleteUser(record)}
                description={l('user.deleteConfirm')}
              />
            )}
          </>
        </Authorized>,
        <Authorized
          key={`${record.id}_recovery_auth`}
          path={PermissionConstants.AUTH_USER_RECOVERY}
        >
          <>
            {access.isAdmin && record.isDelete && (
              <Popconfirm
                placement='topRight'
                title={l('button.recovery')}
                description={<div className={'needWrap'}>{l('user.recovery')} </div>}
                onConfirm={() => handleRecoveryUser(record)}
                okText={l('button.confirm')}
                cancelText={l('button.cancel')}
              >
                <Button title={l('button.recovery')} key={'recovery'} icon={<BackIcon />} />
              </Popconfirm>
            )}
          </>
        </Authorized>,
        <Authorized
          key={`${record.id}_reset_auth`}
          path={PermissionConstants.AUTH_USER_RESET_PASSWORD}
        >
          <>
            {access.isAdmin && (
              <Popconfirm
                placement='topRight'
                title={l('button.reset')}
                description={<div className={'needWrap'}>{l('user.reset')} </div>}
                onConfirm={() => handleResetPassword(record)}
                okText={l('button.confirm')}
                cancelText={l('button.cancel')}
              >
                <Button
                  title={l('button.reset')}
                  key={'reset'}
                  className={'blue-icon'}
                  icon={<RedoOutlined />}
                />
              </Popconfirm>
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
      <ProTable<UserBaseInfo.User>
        {...PROTABLE_OPTIONS_PUBLIC}
        headerTitle={l('user.manager')}
        actionRef={actionRef}
        loading={userState.loading}
        toolBarRender={() => [
          <Authorized key={`CreateUser`} path={PermissionConstants.AUTH_USER_ADD}>
            <CreateBtn
              key={'CreateUser'}
              onClick={() => setUserState((prevState) => ({ ...prevState, addedOpen: true }))}
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.USER, {
            ...params,
            sorter,
            filter
          })
        }
        columns={columns}
      />

      <UserModalForm
        key={'handleSubmitUser'}
        onSubmit={handleSubmitUser}
        onCancel={() => setUserState((prevState) => ({ ...prevState, addedOpen: false }))}
        modalVisible={userState.addedOpen}
        values={{}}
      />
      <PasswordModal
        key={'handlePasswordChangeSubmit'}
        onSubmit={handlePasswordChangeSubmit}
        onCancel={() => setUserState((prevState) => ({ ...prevState, editPasswordOpen: false }))}
        modalVisible={userState.editPasswordOpen}
        values={userState.value}
      />
      {Object.keys(userState.value).length > 0 && (
        <>
          <UserModalForm
            key={'handleUpdateUser'}
            onSubmit={handleSubmitUser}
            onCancel={() =>
              setUserState((prevState) => ({ ...prevState, editOpen: false, value: {} }))
            }
            modalVisible={userState.editOpen}
            values={userState.value}
          />
        </>
      )}

      {/* assign role to user */}
      {Object.keys(userState.value).length > 0 && (
        <>
          <RoleModalTransfer
            user={userState.value}
            modalVisible={userState.assignRoleOpen}
            onChange={(value) => setUserState((prevState) => ({ ...prevState, roleIds: value }))}
            onCancel={() =>
              setUserState((prevState) => ({ ...prevState, assignRoleOpen: false, value: {} }))
            }
            onSubmit={() => handleGrantRoleSubmit()}
          />
        </>
      )}
    </>
  );
};

export default UserProTable;
