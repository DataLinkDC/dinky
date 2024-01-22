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
import {
  CODE_TYPE_ENUM,
  CODE_TYPE_FILTER,
  FUNCTION_TYPE_ENUM,
  FUNCTION_TYPE_FILTER
} from '@/pages/RegCenter/UDF/constants';
import { queryList } from '@/services/api';
import { handleAddOrUpdate, handleRemoveById, updateDataByParam } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { UDFTemplate } from '@/types/RegCenter/data';
import { InitTemplateState } from '@/types/RegCenter/init.d';
import { TemplateState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { ProTable } from '@ant-design/pro-components';
import { ActionType, ProColumns } from '@ant-design/pro-table';
import React, { useRef, useState } from 'react';
import TemplateModal from '../TemplateModal';
import UDFTemplateDrawer from '../UDFTemplateDrawer';

const CodeShowProps: any = {
  height: '40vh',
  width: '40vw',
  lineNumbers: 'on',
  language: 'java'
};

const TemplateTable: React.FC = () => {
  const actionRef = useRef<ActionType>();

  const [templateState, setTemplateState] = useState<TemplateState>(InitTemplateState);

  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallback = async (callback: () => void) => {
    setTemplateState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setTemplateState((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  /**
   * cancel all status
   */
  const handleCancel = () => {
    setTemplateState(InitTemplateState);
    actionRef.current?.reload?.();
  };

  /**
   * change ModalVisible
   * @param value
   */
  const handleEdit = async (value: Partial<UDFTemplate>) => {
    setTemplateState({ ...templateState, editOpen: true, value: value });
  };

  /**
   * added or update submit
   * @param {Partial<UDFTemplate>} value
   * @returns {Promise<void>}
   */
  const handleAddOrUpdateSubmit = async (value: Partial<UDFTemplate>) => {
    await handleAddOrUpdate(API_CONSTANTS.UDF_TEMPLATE_ADD_UPDATE, value);
    handleCancel();
  };

  /**
   * update enabled
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleChangeEnable = async (value: Partial<UDFTemplate>) => {
    await executeAndCallback(async () =>
      updateDataByParam(API_CONSTANTS.UDF_TEMPLATE_ENABLE, {
        id: value.id
      })
    );
  };

  /**
   * delete udf template by id
   * @param {number} id
   * @returns {Promise<void>}
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallback(
      async () => await handleRemoveById(API_CONSTANTS.UDF_TEMPLATE_DELETE, id)
    );
  };

  /**
   * handle open drawer
   * @param {Partial<UDFTemplate>} record
   */
  const handleOpenDrawer = (record: Partial<UDFTemplate>) => {
    setTemplateState({ ...templateState, drawerOpen: true, value: record });
  };

  const columns: ProColumns<UDFTemplate>[] = [
    {
      title: l('rc.template.name'),
      dataIndex: 'name',
      render: (dom: any, record: UDFTemplate) => {
        return <a onClick={() => handleOpenDrawer(record)}>{dom}</a>;
      }
    },
    {
      title: l('rc.template.codeType'),
      dataIndex: 'codeType',
      filters: CODE_TYPE_FILTER,
      valueEnum: CODE_TYPE_ENUM,
      filterMultiple: false,
      onFilter: false
    },
    {
      title: l('rc.template.functionType'),
      dataIndex: 'functionType',
      filters: FUNCTION_TYPE_FILTER,
      valueEnum: FUNCTION_TYPE_ENUM,
      filterMultiple: false,
      onFilter: false
    },
    {
      title: l('rc.template.templateCode'),
      dataIndex: 'templateCode',
      filters: FUNCTION_TYPE_FILTER,
      valueEnum: FUNCTION_TYPE_ENUM,
      hideInTable: true,
      filterMultiple: false,
      onFilter: false,
      render: (dom: any, record: UDFTemplate) => {
        return <CodeShow {...CodeShowProps} code={record.templateCode} showFloatButton />;
      }
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInSearch: true,
      hideInDescriptions: true,
      render: (_: any, record: UDFTemplate) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            disabled={!HasAuthority(PermissionConstants.REGISTRATION_UDF_TEMPLATE_EDIT)}
            record={record}
            onChange={() => handleChangeEnable(record)}
          />
        );
      }
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      sorter: true,
      hideInSearch: true
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      sorter: true,
      hideInSearch: true
    },
    {
      title: l('global.table.operate'),
      width: '8%',
      fixed: 'right',
      hideInSearch: true,
      hideInDescriptions: true,
      render: (text: any, record: UDFTemplate) => [
        <Authorized
          key={`${record.id}_edit`}
          path={PermissionConstants.REGISTRATION_UDF_TEMPLATE_EDIT}
        >
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEdit(record)} />
        </Authorized>,
        <Authorized
          key={`${record.id}_delete`}
          path={PermissionConstants.REGISTRATION_UDF_TEMPLATE_DELETE}
        >
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={() => handleDeleteSubmit(record.id)}
            description={l('rc.template.deleteConfirm')}
          />
        </Authorized>
      ]
    }
  ];

  return (
    <>
      <ProTable<UDFTemplate>
        {...PROTABLE_OPTIONS_PUBLIC}
        loading={templateState.loading}
        actionRef={actionRef}
        headerTitle={l('rc.udf.template.management')}
        toolBarRender={() => [
          <Authorized key='create' path={PermissionConstants.REGISTRATION_UDF_TEMPLATE_ADD}>
            <CreateBtn
              key={'template'}
              onClick={() => setTemplateState((prevState) => ({ ...prevState, addedOpen: true }))}
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.UDF_TEMPLATE, { ...params, sorter, filter })
        }
        columns={columns}
      />
      {/* added */}
      {templateState.addedOpen && (
        <TemplateModal
          values={templateState.value}
          visible={templateState.addedOpen}
          onCancel={handleCancel}
          onSubmit={(value: Partial<UDFTemplate>) => handleAddOrUpdateSubmit(value)}
        />
      )}

      {/* modify */}
      {templateState.editOpen && (
        <TemplateModal
          values={templateState.value}
          visible={templateState.editOpen}
          onCancel={handleCancel}
          onSubmit={(value: Partial<UDFTemplate>) => handleAddOrUpdateSubmit(value)}
        />
      )}

      {/* drawer */}
      {templateState.drawerOpen && (
        <UDFTemplateDrawer
          onCancel={() => handleCancel()}
          values={templateState.value}
          modalVisible={templateState.drawerOpen}
          columns={columns}
        />
      )}
    </>
  );
};

export default TemplateTable;
