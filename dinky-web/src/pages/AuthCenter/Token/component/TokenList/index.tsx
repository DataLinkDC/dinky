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

import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { Authorized } from '@/hooks/useAccess';
import { mapDispatchToProps } from '@/pages/AuthCenter/Token/component/model';
import TokenModalForm from '@/pages/AuthCenter/Token/component/TokenModalForm';
import { queryList } from '@/services/api';
import { handleAddOrUpdate, handleRemoveById } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { SysToken } from '@/types/AuthCenter/data';
import { InitTokenListState } from '@/types/AuthCenter/init.d';
import { TokenListState } from '@/types/AuthCenter/state.d';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import { connect } from '@umijs/max';
import { useRef, useState } from 'react';

const TokenList = (props: any) => {
  const actionRef = useRef<ActionType>(); // table action
  const [tokenState, setTokenState] = useState<TokenListState>(InitTokenListState); // token state

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setTokenState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setTokenState((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  function handleEditVisible(record: Partial<SysToken>) {
    setTokenState((prevState) => ({ ...prevState, editOpen: true, value: record }));
  }

  const handleDeleteToken = async (id: number) => {
    await executeAndCallbackRefresh(
      async () => await handleRemoveById(API_CONSTANTS.TOKEN_DELETE, id)
    );
  };

  /**
   * table columns
   */
  const columns: ProColumns<SysToken>[] = [
    {
      title: l('token.value'),
      dataIndex: 'tokenValue',
      copyable: true,
      ellipsis: true
    },
    {
      title: l('token.username'),
      dataIndex: 'userName'
    },
    {
      title: l('token.role'),
      dataIndex: 'roleName'
    },
    {
      title: l('token.tenant'),
      dataIndex: 'tenantCode'
    },
    {
      title: l('token.expireType'),
      dataIndex: 'expireType',
      valueEnum: {
        1: { text: l('token.expireType.1') },
        2: { text: l('token.expireType.2') },
        3: { text: l('token.expireType.3') }
      }
    },
    {
      title: l('token.expireStartTime'),
      dataIndex: 'expireStartTime',
      valueType: 'dateTime',
      hideInSearch: true
    },
    {
      title: l('token.expireEndTime'),
      dataIndex: 'expireEndTime',
      valueType: 'dateTime',
      hideInSearch: true
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
      width: '10%',
      fixed: 'right',
      render: (_, record: SysToken) => [
        <Authorized key={`${record.id}_edit_auth`} path={PermissionConstants.AUTH_TOKEN_EDIT}>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)} />
        </Authorized>,
        <Authorized key={`${record.id}_delete_auth`} path={PermissionConstants.AUTH_TOKEN_DELETE}>
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={() => handleDeleteToken(record?.id)}
            description={l('token.deleteConfirm')}
          />
        </Authorized>
      ]
    }
  ];

  const handleSubmitToken = async (value: SysToken) => {
    await executeAndCallbackRefresh(async () =>
      handleAddOrUpdate(
        API_CONSTANTS.TOKEN_SAVE_OR_UPDATE,
        value,
        () => {},
        () => setTokenState((prevState) => ({ ...prevState, ...InitTokenListState }))
      )
    );
  };

  /**
   * render
   */
  return (
    <>
      <ProTable<SysToken>
        {...PROTABLE_OPTIONS_PUBLIC}
        headerTitle={l('token.manager')}
        actionRef={actionRef}
        loading={tokenState.loading}
        toolBarRender={() => [
          <Authorized key={`CreateToken_auth`} path={PermissionConstants.AUTH_TOKEN_ADD}>
            <CreateBtn
              key={'CreateToken'}
              onClick={() => setTokenState((prevState) => ({ ...prevState, addedOpen: true }))}
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.TOKEN, {
            ...params,
            sorter,
            filter
          })
        }
        columns={columns}
      />

      <TokenModalForm
        key={'handleSubmitToken'}
        onSubmit={handleSubmitToken}
        onCancel={() => setTokenState((prevState) => ({ ...prevState, addedOpen: false }))}
        visible={tokenState.addedOpen}
        value={{}}
        loading={tokenState.loading}
      />
      {Object.keys(tokenState.value).length > 0 && (
        <>
          <TokenModalForm
            key={'handleUpdateToken'}
            onSubmit={handleSubmitToken}
            onCancel={() =>
              setTokenState((prevState) => ({ ...prevState, editOpen: false, value: {} }))
            }
            visible={tokenState.editOpen}
            value={tokenState.value}
            loading={tokenState.loading}
          />
        </>
      )}
    </>
  );
};

export default connect(() => ({}), mapDispatchToProps)(TokenList);
