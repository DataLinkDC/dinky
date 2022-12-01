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


import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import {Button, Drawer, Input, Modal} from 'antd';
import React, {useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import type {DocumentTableListItem} from "@/pages/RegistrationCenter/data";
import Dropdown from "antd/es/dropdown/dropdown";
import Menu from "antd/es/menu";
import {handleAddOrUpdate, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import DocumentForm from "./components/DocumentForm";
import {l} from "@/utils/intl";

const url = '/api/document';

const DocumentTableList: React.FC<{}> = (props: any) => {

  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<DocumentTableListItem>();
  const [selectedRowsState, setSelectedRows] = useState<DocumentTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: DocumentTableListItem) => {
    if (key === 'edit') {
      handleUpdateModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: l('pages.rc.doc.delete'),
        content: l('pages.rc.doc.deleteConfirm'),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleRemove(url, [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };

  const MoreBtn: React.FC<{
    item: DocumentTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => editAndDelete(key, item)}>
          <Menu.Item key="edit">{l('button.edit')}</Menu.Item>
          <Menu.Item key="delete">{l('button.delete')}</Menu.Item>
        </Menu>
      }
    >
      <a>
        {l('button.more')} <DownOutlined/>
      </a>
    </Dropdown>
  );

  const columns: ProColumns<DocumentTableListItem>[] = [
    {
      title: l('pages.rc.doc.name'),
      dataIndex: 'name',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.rc.doc.id'),
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('pages.rc.doc.category'),
      sorter: true,
      dataIndex: 'category',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: 'Method',
          value: 'Method',
        }, {
          text: 'Function',
          value: 'Function',
        }, {
          text: 'Constructor',
          value: 'Constructor',
        }, {
          text: 'Field',
          value: 'Field',
        }, {
          text: 'Variable',
          value: 'Variable',
        }, {
          text: 'Class',
          value: 'Class',
        }, {
          text: 'Struct',
          value: 'Struct',
        }, {
          text: 'Interface',
          value: 'Interface',
        }, {
          text: 'Module',
          value: 'Module',
        }, {
          text: 'Property',
          value: 'Property',
        }, {
          text: 'Event',
          value: 'Event',
        }, {
          text: 'Operator',
          value: 'Operator',
        }, {
          text: 'Unit',
          value: 'Unit',
        }, {
          text: 'Value',
          value: 'Value',
        }, {
          text: 'Constant',
          value: 'Constant',
        }, {
          text: 'Enum',
          value: 'Enum',
        }, {
          text: 'EnumMember',
          value: 'EnumMember',
        }, {
          text: 'Keyword',
          value: 'Keyword',
        }, {
          text: 'Text',
          value: 'Text',
        }, {
          text: 'Color',
          value: 'Color',
        }, {
          text: 'File',
          value: 'File',
        }, {
          text: 'Reference',
          value: 'Reference',
        }, {
          text: 'Customcolor',
          value: 'Customcolor',
        }, {
          text: 'Folder',
          value: 'Folder',
        }, {
          text: 'TypeParameter',
          value: 'TypeParameter',
        }, {
          text: 'User',
          value: 'User',
        }, {
          text: 'Issue',
          value: 'Issue',
        }, {
          text: 'Snippet',
          value: 'Snippet',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        'Method': {text: 'Method'},
        'Function': {text: 'Function'},
        'Constructor': {text: 'Constructor'},
        'Field': {text: 'Field'},
        'Variable': {text: 'Variable'},
        'Class': {text: 'Class'},
        'Struct': {text: 'Struct'},
        'Interface': {text: 'Interface'},
        'Module': {text: 'Module'},
        'Property': {text: 'Property'},
        'Event': {text: 'Event'},
        'Operator': {text: 'Operator'},
        'Unit': {text: 'Unit'},
        'Value': {text: 'Value'},
        'Constant': {text: 'Constant'},
        'Enum': {text: 'Enum'},
        'EnumMember': {text: 'EnumMember'},
        'Keyword': {text: 'Keyword'},
        'Text': {text: 'Text'},
        'Color': {text: 'Color'},
        'File': {text: 'File'},
        'Reference': {text: 'Reference'},
        'Customcolor': {text: 'Customcolor'},
        'Folder': {text: 'Folder'},
        'TypeParameter': {text: 'TypeParameter'},
        'User': {text: 'User'},
        'Issue': {text: 'Issue'},
        'Snippet': {text: 'Snippet'},
      },
    },
    {
      title: l('pages.rc.doc.functionType'),
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: false,
      hideInTable: false,
      filters: [
        {
          text: '优化参数',
          value: '优化参数',
        },
        {
          text: '建表语句',
          value: '建表语句',
        },
        {
          text: 'CataLog',
          value: 'CataLog',
        },
        {
          text: '设置参数',
          value: '设置参数',
        },
        {
          text: '内置函数',
          value: '内置函数',
        },
        {
          text: 'UDF',
          value: 'UDF',
        },
        {
          text: 'Other',
          value: 'Other',
        }
      ],
      filterMultiple: false,
      valueEnum: {
        '优化参数': {text: '优化参数'},
        '建表语句': {text: '建表语句'},
        'CataLog': {text: 'CataLog'},
        '设置参数': {text: '设置参数'},
        '内置函数': {text: '内置函数'},
        'UDF': {text: 'UDF'},
        'Other': {text: 'Other'},
      },
    },
    {
      title: l('pages.rc.doc.subFunctionType'),
      sorter: true,
      dataIndex: 'subtype',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: 'Batch/Streaming',
          value: 'Batch/Streaming',
        }, {
          text: 'Batch',
          value: 'Batch',
        }, {
          text: 'Streaming',
          value: 'Streaming',
        }, {
          text: 'Other',
          value: 'Other',
        }, {
          text: '比较函数',
          value: '比较函数',
        }, {
          text: '逻辑函数',
          value: '逻辑函数',
        }, {
          text: '算术函数',
          value: '算术函数',
        }, {
          text: '字符串函数',
          value: '字符串函数',
        }, {
          text: '时间函数',
          value: '时间函数',
        }, {
          text: '类型转换函数功能',
          value: '类型转换函数功能',
        }, {
          text: '条件函数',
          value: '条件函数',
        }, {
          text: 'Collection 函数',
          value: 'Collection 函数',
        }, {
          text: 'Value Construction函数',
          value: 'Value Construction函数',
        }, {
          text: 'Value Access函数',
          value: 'Value Access函数',
        }, {
          text: '分组函数',
          value: '分组函数',
        }, {
          text: 'hash函数',
          value: 'hash函数',
        }, {
          text: '聚合函数',
          value: '聚合函数',
        }, {
          text: '列函数',
          value: '列函数',
        }, {
          text: '表值聚合函数',
          value: '表值聚合函数',
        }, {
          text: '其他函数',
          value: '其他函数',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        'Batch/Streaming': {text: 'Batch/Streaming'},
        'Batch': {text: 'Batch'},
        'Streaming': {text: 'Streaming'},
        'Other': {text: 'Other'},
        '比较函数': {text: '比较函数'},
        '逻辑函数': {text: '逻辑函数'},
        '算术函数': {text: '算术函数'},
        '字符串函数': {text: '字符串函数'},
        '时间函数': {text: '时间函数'},
        '条件函数': {text: '条件函数'},
        '类型转换函数功能': {text: '类型转换函数功能'},
        'Collection 函数': {text: 'Collection 函数'},
        'Value Construction函数': {text: 'Value Construction函数'},
        'Value Access函数': {text: 'Value Access函数'},
        '分组函数': {text: '分组函数'},
        'hash函数': {text: 'hash函数'},
        '聚合函数': {text: '聚合函数'},
        '列函数': {text: '列函数'},
        '表值聚合函数': {text: '表值聚合函数'},
        '其他函数': {text: '其他函数'},
      },
    },
    {
      title: l('pages.rc.doc.description'),
      sorter: true,
      dataIndex: 'description',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: false,
      hideInTable: true,
    }, {
      title: l('pages.rc.doc.fillValue'),
      sorter: true,
      dataIndex: 'fillValue',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
      hideInDescriptions: false,
    },
    {
      title: l('pages.rc.doc.version'),
      sorter: true,
      dataIndex: 'version',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: l('status.enabled'),
          value: 1,
        },
        {
          text: l('status.disabled'),
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: l('status.enabled'), status: 'Success'},
        false: {text: l('status.disabled'), status: 'Error'},
      },
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInTable: true,
      renderFormItem: (item, {defaultRender, ...rest}, form) => {
        const status = form.getFieldValue('status');
        if (`${status}` === '0') {
          return false;
        }
        if (`${status}` === '3') {
          return <Input {...rest} placeholder="请输入异常原因！"/>;
        }
        return defaultRender(item);
      },
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      renderFormItem: (item, {defaultRender, ...rest}, form) => {
        const status = form.getFieldValue('status');
        if (`${status}` === '0') {
          return false;
        }
        if (`${status}` === '3') {
          return <Input {...rest} placeholder="请输入异常原因！"/>;
        }
        return defaultRender(item);
      },
    },
    {
      title: l('global.table.operate'),
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            handleUpdateModalVisible(true);
            setFormValues(record);
          }}
        >
          {l('button.config')}
        </a>,
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<DocumentTableListItem>
        headerTitle={l('pages.rc.doc.Management')}
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> {l('button.create')}
          </Button>,
        ]}
        request={(params, sorter, filter) => queryData(url, {...params, sorter, filter})}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => setSelectedRows(selectedRows),
        }}
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              {l('tips.selected', '',
                {
                  total: <a
                    style={{fontWeight: 600}}>{selectedRowsState.length}</a>
                })}  &nbsp;&nbsp;
              <span>
                {l('pages.rc.doc.disableTotalOf', '',
                  {
                    total: (selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0))
                  })}
              </span>
            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: l('pages.rc.doc.delete'),
                      content: l('pages.rc.doc.deleteConfirm'),
                      okText: l('button.confirm'),
                      cancelText: l('button.cancel'),
                      onOk: async () => {
                        await handleRemove(url, selectedRowsState);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          >
            {l('button.batchDelete')}
          </Button>
          <Button type="primary"
                  onClick={() => {
                    Modal.confirm({
                      title: l('pages.rc.doc.enable'),
                      content: l('pages.rc.doc.enableConfirm'),
                      okText: l('button.confirm'),
                      cancelText: l('button.cancel'),
                      onOk: async () => {
                        await updateEnabled(url, selectedRowsState, true);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          >{l('button.batchEnable')}</Button>
          <Button danger
                  onClick={() => {
                    Modal.confirm({
                      title: l('pages.rc.doc.disable'),
                      content: l('pages.rc.doc.disableConfirm'),
                      okText: l('button.confirm'),
                      cancelText: l('button.cancel'),
                      onOk: async () => {
                        await updateEnabled(url, selectedRowsState, false);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          >{l('button.batchDisable')}</Button>
        </FooterToolbar>
      )}
      <DocumentForm
        onSubmit={async (value) => {
          const success = await handleAddOrUpdate(url, value);
          if (success) {
            handleModalVisible(false);
            setFormValues({});
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleModalVisible(false);
        }}
        modalVisible={modalVisible}
        values={{}}
      />
      {
        formValues && Object.keys(formValues).length ? (
          <DocumentForm
            onSubmit={async (value) => {
              const success = await handleAddOrUpdate(url, value);
              if (success) {
                handleUpdateModalVisible(false);
                setFormValues({});
                if (actionRef.current) {
                  actionRef.current.reload();
                }
              }
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
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<DocumentTableListItem>
            column={2}
            title={row?.name}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.name,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  )
    ;
};

export default DocumentTableList;
