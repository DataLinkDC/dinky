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


import {EditTwoTone, PlusOutlined} from '@ant-design/icons';
import {Button, Drawer, Popconfirm, Space, Switch} from 'antd';
import React, {useRef, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import DocumentForm from "./components/DocumentForm";
import {l} from "@/utils/intl";
import {Document} from "@/types/RegCenter/data";
import {queryList} from "@/services/api";
import {
  DOCUMENT_CATEGORY,
  DOCUMENT_CATEGORY_ENUMS,
  DOCUMENT_FUNCTION_ENUMS,
  DOCUMENT_FUNCTION_TYPE, DOCUMENT_SUBTYPE, DOCUMENT_SUBTYPE_ENUMS
} from "@/pages/RegCenter/Document/constans";
import {
  API_CONSTANTS,
  PROTABLE_OPTIONS_PUBLIC,
  STATUS_ENUM,
  STATUS_MAPPING,
  SWITCH_OPTIONS
} from "@/services/constants";
import CodeShow from "@/components/CustomMonacoEditor/CodeShow";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import TextArea from "antd/es/input/TextArea";

const DocumentTableList: React.FC = (props: any) => {

  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<Document>();
  const [loading, setLoading] = useState<boolean>(false);


  /**
   * delete document by id
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    await handleRemoveById(API_CONSTANTS.DOCUMENT_DELETE, id);
    actionRef.current?.reload?.();
  }

  /**
   * enable or disable document
   * @param value
   */
  const handleChangeEnable = async (value: Partial<Document>) => {
    setLoading(true);
    await updateEnabled(API_CONSTANTS.DOCUMENT_ENABLE, {id: value.id});
    setLoading(false);
    actionRef.current?.reload?.();
  };


  /**
   * added document var
   * @param value
   */
  const handleAddSubmit = async (value: Partial<Document>) => {
    const success = await handleAddOrUpdate(API_CONSTANTS.DOCUMENT, value);
    if (success) {
      handleModalVisible(!modalVisible);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
  }

  /**
   * update document var
   * @param value
   */
  const handleUpdateSubmit = async (value: Partial<Document>) => {
    const success = await handleAddOrUpdate(API_CONSTANTS.DOCUMENT, value);
    if (success) {
      handleUpdateModalVisible(!updateModalVisible);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
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
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
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
        return <TextArea value={text} autoSize readOnly />;
      }
    }, {
      title: l('rc.doc.fillValue'),
      dataIndex: 'fillValue',
      hideInTable: true,
      hideInSearch: true,
      render: (_,record) => {
        return <CodeShow code={record.fillValue} />
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
        return <>
          <Space>
            <Switch
              key={record.id}
              {...SWITCH_OPTIONS()}
              checked={record.enabled}
              onChange={() => handleChangeEnable(record)}/>
          </Space>
        </>;
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
      render: (_, record) => [
        <Button
          className={"options-button"}
          key={"DocumentEdit"}
          icon={<EditTwoTone/>}
          title={l("button.edit")}
          onClick={() => {
            setFormValues(record);
            handleUpdateModalVisible(true);
          }}
        />,
        <Popconfirm
          key={'DocumentDelete'}
          placement="topRight"
          title={l("button.delete")}
          description={l("rc.doc.deleteConfirm")}
          onConfirm={() => {
            handleDeleteSubmit(record.id);
          }}
          okText={l("button.confirm")}
          cancelText={l("button.cancel")}
        >
          <Button key={'deleteDocumentIcon'} icon={<DangerDeleteIcon/>}/>
        </Popconfirm>
      ],

    },
  ];



  return (
    <PageContainer title={false}>
      <ProTable<Document>
        {...PROTABLE_OPTIONS_PUBLIC}
        loading={loading}
        headerTitle={l('rc.doc.Management')}
        actionRef={actionRef}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> {l('button.create')}
          </Button>,
        ]}
        request={(params, sorter, filter:any) => queryList(API_CONSTANTS.DOCUMENT, {...params, sorter, filter})}
        columns={columns}
      />
      <DocumentForm
        onSubmit={(value) => {
         handleAddSubmit(value)
        }}
        onCancel={() => {
          handleModalVisible(false);
        }}
        modalVisible={modalVisible}
        values={{}}
      />
      {formValues && Object.keys(formValues).length ? (
          <DocumentForm
            onSubmit={(value) => {
               handleUpdateSubmit(value)
            }}
            onCancel={() => {
              handleUpdateModalVisible(false);
              setFormValues({});
            }}
            modalVisible={updateModalVisible}
            values={formValues}
          />
        ) : null
      }
      <Drawer
        width={'50%'}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.id && (
          <ProDescriptions<Document>
            column={1}
            title={row.name}
            request={async () => ({
              data: row,
            })}
            params={{
              id: row.id,
            }}
            columns={columns as any}
          />
        )}
      </Drawer>
    </PageContainer>
  )
    ;
};

export default DocumentTableList;
