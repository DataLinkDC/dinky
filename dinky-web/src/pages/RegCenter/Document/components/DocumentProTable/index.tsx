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


import React, {useRef, useState} from 'react';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {l} from "@/utils/intl";
import {Document} from "@/types/RegCenter/data";
import {queryList} from "@/services/api";
import {
  DOCUMENT_CATEGORY,
  DOCUMENT_CATEGORY_ENUMS,
  DOCUMENT_FUNCTION_ENUMS,
  DOCUMENT_FUNCTION_TYPE,
  DOCUMENT_SUBTYPE,
  DOCUMENT_SUBTYPE_ENUMS
} from "@/pages/RegCenter/Document/constans";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING,} from "@/services/constants";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import TextArea from "antd/es/input/TextArea";
import {EnableSwitchBtn} from "@/components/CallBackButton/EnableSwitchBtn";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import DocumentDrawer from "@/pages/RegCenter/Document/components/DocumentDrawer";
import DocumentModalForm from "@/pages/RegCenter/Document/components/DocumentModal";

const DocumentTableList: React.FC = () => {

  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState<Partial<Document>>({});
  const actionRef = useRef<ActionType>();
  const [loading, setLoading] = useState<boolean>(false);
  const [drawerOpen, setDrawerOpen] = useState<boolean>(false);


  const handleCancel = () => {
    handleModalVisible(false);
    handleUpdateModalVisible(false);
    setDrawerOpen(false);
  }

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setLoading(true);
    await callback();
    setLoading(false);
    handleCancel();
    actionRef.current?.reload?.();
  }

  /**
   * delete document by id
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallbackRefresh(async () => {
      await handleRemoveById(API_CONSTANTS.DOCUMENT_DELETE, id);
    })
  }

  /**
   * enable or disable document
   * @param value
   */
  const handleChangeEnable = async (value: Partial<Document>) => {
    await executeAndCallbackRefresh(async () => {
      await updateEnabled(API_CONSTANTS.DOCUMENT_ENABLE, {id: value.id});
    })
  };


  /**
   * added or update document var
   * @param value
   */
  const handleAddOrUpdateSubmit = async (value: Partial<Document>) => {
    await executeAndCallbackRefresh(async () => {
      await handleAddOrUpdate(API_CONSTANTS.DOCUMENT, value);
    })
  }

  const handleClickEdit = (record: Partial<Document>) => {
    setFormValues(record);
    handleUpdateModalVisible(true);
  }

  const handleOpenDrawer = (record: Partial<Document>) => {
    setFormValues(record)
    setDrawerOpen(true)
  }


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
      },
    },
    {
      title: l('rc.doc.category'),
      sorter: true,
      dataIndex: 'category',
      filterMultiple: false,
      filters: DOCUMENT_CATEGORY,
      valueEnum: DOCUMENT_CATEGORY_ENUMS,
    },
    {
      title: l('rc.doc.functionType'),
      sorter: true,
      dataIndex: 'type',
      filterMultiple: false,
      filters: DOCUMENT_FUNCTION_TYPE,
      valueEnum: DOCUMENT_FUNCTION_ENUMS
    },
    {
      title: l('rc.doc.subFunctionType'),
      sorter: true,
      dataIndex: 'subtype',
      filters: DOCUMENT_SUBTYPE,
      filterMultiple: false,
      valueEnum: DOCUMENT_SUBTYPE_ENUMS,
    },
    {
      title: l('rc.doc.description'),
      dataIndex: 'description',
      ellipsis: true,
      hideInTable: true,
      renderText: (text: string) => {
        return <TextArea value={text} autoSize readOnly/>;
      }
    }, {
      title: l('rc.doc.fillValue'),
      dataIndex: 'fillValue',
      hideInTable: true,
      hideInSearch: true,
      render: (_, record) => {
        return <CodeShow width={"85vh"} code={record.fillValue}/>
      }
    },
    {
      title: l('rc.doc.version'),
      sorter: true,
      dataIndex: 'version',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInSearch: true,
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      valueEnum: STATUS_ENUM(),
      render: (_, record) => {
        return <EnableSwitchBtn key={`${record.id}_enable`} disabled={drawerOpen} record={record}
                                onChange={() => handleChangeEnable(record)}/>
      },
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      hideInTable: true,
      hideInSearch: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      hideInTable: true,
      hideInSearch: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '10vh',
      render: (_, record) => [
        <EditBtn key={`${record.id}_edit`} onClick={() => handleClickEdit(record)}/>,
        <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                             description={l("rc.doc.deleteConfirm")}/>,
      ],
    },
  ];


  return <>
    {/*TABLE*/}
    <ProTable<Document>
      {...PROTABLE_OPTIONS_PUBLIC}
      loading={loading}
      headerTitle={l('rc.doc.management')}
      actionRef={actionRef}
      toolBarRender={() => [<CreateBtn key={"doctable"} onClick={() => handleModalVisible(true)}/>,]}
      request={(params, sorter, filter: any) => queryList(API_CONSTANTS.DOCUMENT, {...params, sorter, filter})}
      columns={columns}
    />
    {/*ADDED*/}
    <DocumentModalForm
      key={formValues.id}
      onSubmit={(value) => handleAddOrUpdateSubmit(value)}
      onCancel={() => handleCancel()}
      modalVisible={modalVisible}
      values={{}}
    />
    {/*UPDATED*/}
    <DocumentModalForm
      key={formValues.id}
      onSubmit={(value) => handleAddOrUpdateSubmit(value)}
      onCancel={() => handleCancel()}
      modalVisible={updateModalVisible}
      values={formValues}
    />
    {/*DRAWER*/}
    <DocumentDrawer
      key={formValues.id}
      onCancel={() => handleCancel()} modalVisible={drawerOpen} values={formValues}
      columns={columns}
    />
  </>
    ;
};

export default DocumentTableList;
