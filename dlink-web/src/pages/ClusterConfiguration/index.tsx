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
import {ClusterConfigurationTableListItem} from "@/pages/ClusterConfiguration/data";
import {handleAddOrUpdate, handleRemove, queryData, updateEnabled} from "@/components/Common/crud";
import {showClusterConfiguration} from "@/components/Studio/StudioEvent/DDL";
import ClusterConfigurationForm from "@/pages/ClusterConfiguration/components/ClusterConfigurationForm";
import {useIntl} from "umi";

const url = '/api/clusterConfiguration';
const ClusterConfigurationTableList: React.FC<{}> = (props: any) => {


  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const {dispatch} = props;
  const [row, setRow] = useState<ClusterConfigurationTableListItem>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [selectedRowsState, setSelectedRows] = useState<ClusterConfigurationTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: ClusterConfigurationTableListItem) => {
    if (key === 'edit') {
      setFormValues(currentItem);
      handleUpdateModalVisible(true);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除集群配置',
        content: '确定删除该集群配置吗？',
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
    item: ClusterConfigurationTableListItem;
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => editAndDelete(key, item)}>
          <Menu.Item key="edit">{l('button.edit')}</Menu.Item>
          <Menu.Item key="delete">删除</Menu.Item>
        </Menu>
      }
    >
      <a>
        {l('button.more')} <DownOutlined/>
      </a>
    </Dropdown>
  );

  const columns: ProColumns<ClusterConfigurationTableListItem>[] = [
    {
      title: '名称',
      dataIndex: 'name',
      tip: '名称是唯一的',
      sorter: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '名称为必填项',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '集群配置ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '别名',
      sorter: true,
      dataIndex: 'alias',
      hideInTable: false,
    },
    {
      title: '类型',
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: 'Yarn',
          value: 'Yarn',
        },
        {
          text: 'Standalone',
          value: 'Standalone',
        },
        {
          text: 'Others',
          value: 'Others',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        'Yarn': {text: 'Yarn'},
        'Standalone': {text: 'Standalone'},
        'Others': {text: 'Others'},
      },
    },
    {
      title: '是否可用',
      dataIndex: 'isAvailable',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: '可用',
          value: 1,
        },
        {
          text: '不可用',
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: '可用', status: 'Success'},
        false: {text: '不可用', status: 'Error'},
      },
    },
    {
      title: '注释',
      sorter: true,
      valueType: 'textarea',
      dataIndex: 'note',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInForm: true,
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
      hideInTable: true,
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
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
      <ProTable<ClusterConfigurationTableListItem>
        headerTitle="集群配置管理"
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
                被禁用的集群配置共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)} 人
              </span>
            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: '删除集群配置',
                      content: '确定删除选中的集群配置吗？',
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
                      title: '启用集群配置',
                      content: '确定启用选中的集群配置吗？',
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
                      title: '禁用集群配置',
                      content: '确定禁用选中的集群配置吗？',
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
      <ClusterConfigurationForm
        onSubmit={async (value) => {
          const success = await handleAddOrUpdate("api/clusterConfiguration", value);
          if (success) {
            handleModalVisible(false);
            setFormValues({});
            if (actionRef.current) {
              actionRef.current.reload();
            }
            showClusterConfiguration(dispatch);
          }
        }}
        onCancel={() => {
          handleModalVisible(false);
        }}
        modalVisible={modalVisible}
        values={{}}
      />
      {formValues && Object.keys(formValues).length ? (
        <ClusterConfigurationForm
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate("api/clusterConfiguration", value);
            if (success) {
              handleUpdateModalVisible(false);
              setFormValues({});
              if (actionRef.current) {
                actionRef.current.reload();
              }
              showClusterConfiguration(dispatch);
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setFormValues({});
          }}
          modalVisible={updateModalVisible}
          values={formValues}
        />
      ) : null}
      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<ClusterConfigurationTableListItem>
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
  );
};

export default ClusterConfigurationTableList;
