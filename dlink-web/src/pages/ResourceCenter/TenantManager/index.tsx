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


import React, {useRef, useState} from "react";
import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Drawer, Dropdown, Menu, Modal} from 'antd';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {handleAddOrUpdate, handleRemove, queryData} from "@/components/Common/crud";
import {TenantTableListItem} from "@/pages/ResourceCenter/TenantManager/data";
import TenantForm from "@/pages/ResourceCenter/TenantManager/components/TenantForm";

const url = '/api/tenant';
const TenantFormList: React.FC<{}> = (props: any) => {
  const [row, setRow] = useState<TenantTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<TenantTableListItem[]>([]);
  const [formValues, setFormValues] = useState({});

  const editAndDelete = (key: string | number, currentItem: TenantTableListItem) => {
    if (key === 'edit') {
      handleModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除租户',
        content: '确定删除该租户吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: async () => {
          await handleRemove(url, [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };

  const MoreBtn: React.FC<{
    item: TenantTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => editAndDelete(key, item)}>
          <Menu.Item key="edit">编辑</Menu.Item>
          <Menu.Item key="delete">删除</Menu.Item>
        </Menu>
      }
    >
      <a>
        更多 <DownOutlined/>
      </a>
    </Dropdown>
  );

  const columns: ProColumns<TenantTableListItem>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInSearch: true,
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '租户编码',
      tip: '编码是唯一的',
      dataIndex: 'tenantCode',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '是否删除',
      dataIndex: 'isDelete',
      hideInTable: false,
      filters: [
        {
          text: '未删除',
          value: 1,
        },
        {
          text: '已删除',
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: '未删除', status: 'Success'},
        false: {text: '已删除', status: 'Error'},
      },
    },
    {
      title: '备注',
      dataIndex: 'note',
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: '最近更新时间',
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            handleModalVisible(true);
            setFormValues(record);
          }}
        >
          配置
        </a>,
        <MoreBtn key="more" item={record}/>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<TenantTableListItem>
        headerTitle="租户管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> 新建
          </Button>,
        ]}
        request={(params, sorter, filter) => queryData(url, {...params, sorter, filter})}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => setSelectedRows(selectedRows),
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择 <a style={{fontWeight: 600}}>{selectedRowsState.length}</a> 项&nbsp;&nbsp;
              <span>
  被删除的租户共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.isDelete ? 1 : 0), 0)} 个
  </span>
            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: '删除租户',
                      content: '确定删除选中的租户吗？',
                      okText: '确认',
                      cancelText: '取消',
                      onOk: async () => {
                        await handleRemove(url, selectedRowsState);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          >
            批量删除
          </Button>
        </FooterToolbar>
      )}
      <TenantForm   onSubmit={async (value) => {
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
      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.tenantCode && (
          <ProDescriptions<TenantTableListItem>
            column={2}
            title={row?.tenantCode}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.tenantCode,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TenantFormList;
