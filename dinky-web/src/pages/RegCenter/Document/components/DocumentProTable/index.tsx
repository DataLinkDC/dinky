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
import DocumentDrawer from '@/pages/RegCenter/Document/components/DocumentDrawer';
import DocumentModalForm from '@/pages/RegCenter/Document/components/DocumentModal';
import {
  DOCUMENT_CATEGORY_ENUMS,
  DOCUMENT_FUNCTION_TYPE_ENUMS,
  DOCUMENT_TYPE_ENUMS
} from '@/pages/RegCenter/Document/constans';
import { queryList } from '@/services/api';
import { handleAddOrUpdate, handleRemoveById, updateDataByParam } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { Document } from '@/types/RegCenter/data.d';
import { InitDocumentState } from '@/types/RegCenter/init.d';
import { DocumentState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { ProTable } from '@ant-design/pro-components';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import TextArea from 'antd/es/input/TextArea';
import React, { useRef, useState } from 'react';

const DocumentTableList: React.FC = () => {
  const [documentState, setDocumentState] = useState<DocumentState>(InitDocumentState);

  const actionRef = useRef<ActionType>();

  const handleCancel = () => {
    setDocumentState(InitDocumentState);
  };

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setDocumentState({ ...documentState, loading: true });
    await callback();
    handleCancel();
    actionRef.current?.reload?.();
  };

  /**
   * delete document by id
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallbackRefresh(async () =>
      handleRemoveById(API_CONSTANTS.DOCUMENT_DELETE, id)
    );
  };

  /**
   * enable or disable document
   * @param value
   */
  const handleChangeEnable = async (value: Partial<Document>) => {
    await executeAndCallbackRefresh(async () =>
      updateDataByParam(API_CONSTANTS.DOCUMENT_ENABLE, { id: value.id })
    );
  };

  /**
   * added or update document var
   * @param value
   */
  const handleAddOrUpdateSubmit = async (value: Partial<Document>) => {
    await executeAndCallbackRefresh(async () => handleAddOrUpdate(API_CONSTANTS.DOCUMENT, value));
  };

  const handleClickEdit = (record: Partial<Document>) => {
    setDocumentState((prevState) => ({ ...prevState, value: record, editOpen: true }));
  };

  const handleOpenDrawer = (record: Partial<Document>) => {
    setDocumentState((prevState) => ({ ...prevState, value: record, drawerOpen: true }));
  };

  /**
   * columns
   */
  const columns: ProColumns<Document>[] = [
    {
      title: l('rc.doc.name'),
      dataIndex: 'name',
      sorter: true,
      width: '20vw',
      render: (dom, record) => {
        return <a onClick={() => handleOpenDrawer(record)}>{dom}</a>;
      }
    },
    {
      title: l('rc.doc.functionType'),
      sorter: true,
      dataIndex: 'type',
      filterMultiple: true,
      filters: true,
      valueEnum: DOCUMENT_TYPE_ENUMS
    },
    {
      title: l('rc.doc.subFunctionType'),
      sorter: true,
      dataIndex: 'subtype',
      filters: true,
      filterMultiple: true,
      valueEnum: DOCUMENT_FUNCTION_TYPE_ENUMS
    },
    {
      title: l('rc.doc.category'),
      sorter: true,
      dataIndex: 'category',
      filterMultiple: true,
      filters: true,
      valueEnum: DOCUMENT_CATEGORY_ENUMS
    },
    {
      title: l('rc.doc.description'),
      dataIndex: 'description',
      ellipsis: true,
      hideInTable: true,
      renderText: (text: string) => {
        return <TextArea value={text} autoSize readOnly />;
      }
    },
    {
      title: l('rc.doc.fillValue'),
      dataIndex: 'fillValue',
      hideInTable: true,
      hideInSearch: true,
      render: (_, record) => {
        return <CodeShow language={'sql'} width={'40vw'} code={record.fillValue} />;
      }
    },
    {
      title: l('rc.doc.version'),
      sorter: true,
      dataIndex: 'version',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInSearch: true,
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      hideInDescriptions: true,
      valueEnum: STATUS_ENUM(),
      render: (_, record) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            disabled={!HasAuthority(PermissionConstants.REGISTRATION_DOCUMENT_EDIT)}
            record={record}
            onChange={() => handleChangeEnable(record)}
          />
        );
      }
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      hideInTable: true,
      hideInSearch: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      hideInTable: true,
      hideInSearch: true,
      valueType: 'dateTime'
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '10%',
      fixed: 'right',
      hideInDescriptions: true,
      render: (_, record) => [
        <Authorized key={`${record.id}_edit`} path={PermissionConstants.REGISTRATION_DOCUMENT_EDIT}>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleClickEdit(record)} />
        </Authorized>,
        <Authorized
          key={`${record.id}_delete`}
          path={PermissionConstants.REGISTRATION_DOCUMENT_DELETE}
        >
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={() => handleDeleteSubmit(record.id)}
            description={l('rc.doc.deleteConfirm')}
          />
        </Authorized>
      ]
    }
  ];

  return (
    <>
      {/*TABLE*/}
      <ProTable<Document>
        {...PROTABLE_OPTIONS_PUBLIC}
        loading={documentState.loading}
        headerTitle={l('rc.doc.management')}
        actionRef={actionRef}
        toolBarRender={() => [
          <Authorized key='create' path={PermissionConstants.REGISTRATION_DOCUMENT_ADD}>
            <CreateBtn
              key={'doctable'}
              onClick={() =>
                setDocumentState((prevState) => ({
                  ...prevState,
                  addedOpen: true
                }))
              }
            />
          </Authorized>
        ]}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.DOCUMENT, { ...params, sorter, filter })
        }
        columns={columns}
      />
      {/*ADDED*/}
      <DocumentModalForm
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={documentState.addedOpen}
        values={{}}
      />
      {/*UPDATED*/}
      <DocumentModalForm
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        onCancel={() => handleCancel()}
        modalVisible={documentState.editOpen}
        values={documentState.value}
      />
      {/*DRAWER*/}
      <DocumentDrawer
        onCancel={() => handleCancel()}
        modalVisible={documentState.drawerOpen}
        values={documentState.value}
        columns={columns}
      />
    </>
  );
};

export default DocumentTableList;
