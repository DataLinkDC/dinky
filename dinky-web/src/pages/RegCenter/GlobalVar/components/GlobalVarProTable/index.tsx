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
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import CodeShow from '@/components/CustomEditor/CodeShow';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import GlobalVarDrawer from '@/pages/RegCenter/GlobalVar/components/GlobalVarDrawer';
import GlobalVarModal from '@/pages/RegCenter/GlobalVar/components/GlobalVarModal';
import { queryList } from '@/services/api';
import { handleAddOrUpdate, handleRemoveById, updateDataByParam } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { Document, GlobalVar } from '@/types/RegCenter/data.d';
import { InitGlobalVarState } from '@/types/RegCenter/init.d';
import { GlobalVarState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { useRef, useState } from 'react';

const GlobalVarProTable = () => {
  /**
   * state
   */
  const [globalVarState, setGlobalVarState] = useState<GlobalVarState>(InitGlobalVarState);

  const actionRef = useRef<ActionType>();

  /**
   * handle cancel all
   */
  const handleCancel = () => {
    setGlobalVarState(InitGlobalVarState);
  };

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setGlobalVarState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    handleCancel();
    actionRef.current?.reload?.();
  };

  /**
   * var enable or disable
   * @param value
   */
  const handleChangeEnable = async (value: Partial<GlobalVar>) => {
    await executeAndCallbackRefresh(async () =>
      updateDataByParam(API_CONSTANTS.GLOBAL_VARIABLE_ENABLE, {
        id: value.id
      })
    );
  };

  /**
   * delete role by id
   * @param id role id
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallbackRefresh(async () =>
      handleRemoveById(API_CONSTANTS.GLOBAL_VARIABLE_DELETE, id)
    );
  };

  /**
   * handle edit
   * @param {Partial<Document>} record
   */
  const handleClickEdit = (record: Partial<Document>) => {
    setGlobalVarState((prevState) => ({ ...prevState, value: record, editOpen: true }));
  };

  /**
   * handle open drawer
   * @param {Partial<Document>} record
   */
  const handleOpenDrawer = (record: Partial<Document>) => {
    setGlobalVarState((prevState) => ({ ...prevState, value: record, drawerOpen: true }));
  };

  /**
   * handle add or update submit
   * @param {Partial<GlobalVar>} value
   * @returns {Promise<void>}
   */
  const handleAddOrUpdateSubmit = async (value: Partial<GlobalVar>) => {
    await executeAndCallbackRefresh(async () =>
      handleAddOrUpdate(API_CONSTANTS.GLOBAL_VARIABLE, value)
    );
  };

  /**
   * columns
   */
  const columns: ProColumns<GlobalVar>[] = [
    {
      title: l('rc.gv.name'),
      dataIndex: 'name',
      sorter: true,
      render: (dom, record) => {
        return <a onClick={() => handleOpenDrawer(record)}>{dom}</a>;
      }
    },
    {
      title: l('rc.gv.value'),
      dataIndex: 'fragmentValue',
      hideInTable: true,
      render: (_, record) => {
        return <CodeShow width={'75vh'} code={record.fragmentValue} />;
      }
    },
    {
      title: l('global.table.note'),
      dataIndex: 'note',
      valueType: 'textarea'
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInSearch: true,
      width: '15vh',
      hideInDescriptions: true,
      render: (_, record) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            disabled={!HasAuthority(PermissionConstants.REGISTRATION_FRAGMENT_EDIT)}
            record={record}
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
      hideInSearch: true,
      width: '20vh',
      sorter: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      width: '20vh',
      hideInSearch: true,
      sorter: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.operate'),
      width: '8%',
      fixed: 'right',
      valueType: 'option',
      hideInDescriptions: true,
      render: (_, record) => [
        <Authorized key={`${record.id}_edit`} path={PermissionConstants.REGISTRATION_FRAGMENT_EDIT}>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleClickEdit(record)} />
        </Authorized>,
        <Authorized
          key={`${record.id}_delete`}
          path={PermissionConstants.REGISTRATION_FRAGMENT_DELETE}
        >
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={() => handleDeleteSubmit(record.id)}
            description={l('rc.gv.deleteConfirm')}
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
      {/*table*/}
      <ProTable<GlobalVar>
        headerTitle={l('rc.gv.Management')}
        actionRef={actionRef}
        loading={globalVarState.loading}
        {...PROTABLE_OPTIONS_PUBLIC}
        toolBarRender={() => [
          <Authorized key='create' path={PermissionConstants.REGISTRATION_FRAGMENT_ADD}>
            <CreateBtn
              key={'vartable'}
              onClick={() => setGlobalVarState((prevState) => ({ ...prevState, addedOpen: true }))}
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.GLOBAL_VARIABLE, {
            ...params,
            sorter,
            filter
          })
        }
        columns={columns}
      />

      {/*add*/}
      <GlobalVarModal
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={globalVarState.addedOpen}
        values={{}}
      />
      {/*update*/}
      <GlobalVarModal
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={globalVarState.editOpen}
        values={globalVarState.value}
      />

      {/*drawer render*/}
      <GlobalVarDrawer
        onCancel={() => handleCancel()}
        values={globalVarState.value}
        modalVisible={globalVarState.drawerOpen}
        columns={columns}
      />
    </>
  );
};
export default GlobalVarProTable;
