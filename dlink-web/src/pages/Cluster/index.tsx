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


import {ClearOutlined, DownOutlined, HeartOutlined, PlusOutlined} from '@ant-design/icons';
import {Button, Drawer, Input, message, Modal} from 'antd';
import React, {useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import type {ClusterTableListItem} from './data.d';

import Dropdown from "antd/es/dropdown/dropdown";
import Menu from "antd/es/menu";
import {
  getData,
  handleAddOrUpdate,
  handleOption,
  handleRemove,
  queryData,
  updateEnabled
} from "@/components/Common/crud";
import {showCluster, showSessionCluster} from "@/components/Studio/StudioEvent/DDL";
import {RUN_MODE} from "@/components/Studio/conf";
import ClusterForm from "@/pages/Cluster/components/ClusterForm";
import {useIntl} from 'umi';

const TextArea = Input.TextArea;
const url = '/api/cluster';


const ClusterTableList: React.FC<{}> = (props: any) => {

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const {dispatch} = props;
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<ClusterTableListItem>();
  const [selectedRowsState, setSelectedRows] = useState<ClusterTableListItem[]>([]);

  const editAndDelete = (key: string | number, currentItem: ClusterTableListItem) => {
    if (key === 'edit') {
      handleUpdateModalVisible(true);
      setFormValues(currentItem);
    } else if (key === 'delete') {
      Modal.confirm({
        title: '删除集群',
        content: '确定删除该集群吗？',
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleRemove(url, [currentItem]);
          actionRef.current?.reloadAndRest?.();
        }
      });
    }
  };

  const checkHeartBeats = async () => {
    await handleOption(url + '/heartbeats', '心跳检测', null);
    actionRef.current?.reloadAndRest?.();
  };

  const clearCluster = async () => {

    Modal.confirm({
      title: '回收集群',
      content: '确定回收所有自动创建且过期的集群吗？',
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const {datas} = await getData(url + '/clear', '回收集群', null);
        message.success(`成功回收${datas}个集群`);
        actionRef.current?.reloadAndRest?.();
      }
    });
  };

  const MoreBtn: React.FC<{
    item: ClusterTableListItem;
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

  const columns: ProColumns<ClusterTableListItem>[] = [
    {
      title: l('global.table.instanceName'),
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
      title: l('global.table.instanceId'),
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('global.table.nickName'),
      sorter: true,
      dataIndex: 'alias',
      hideInTable: false,
    },
    {
      title: l('global.table.type'),
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: 'Yarn Session',
          value: RUN_MODE.YARN_SESSION,
        },
        {
          text: 'Standalone',
          value: RUN_MODE.STANDALONE,
        },
        {
          text: 'Yarn Per-Job',
          value: RUN_MODE.YARN_PER_JOB,
        },
        {
          text: 'Yarn Application',
          value: RUN_MODE.YARN_APPLICATION,
        },
        {
          text: 'Kubernetes Session',
          value: RUN_MODE.KUBERNETES_SESSION,
        },
        {
          text: 'Kubernetes Application',
          value: RUN_MODE.KUBERNETES_APPLICATION,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        'yarn-session': {text: 'Yarn Session'},
        'standalone': {text: 'Standalone'},
        'yarn-per-job': {text: 'Yarn Per-Job'},
        'yarn-application': {text: 'Yarn Application'},
        'kubernetes-session': {text: 'Kubernetes Session'},
        'kubernetes-application': {text: 'Kubernetes Application'},
      },
    },
    {
      title: l('global.table.jobManagerHaAddress'),
      sorter: true,
      dataIndex: 'hosts',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
      renderFormItem: (item, {defaultRender, ...rest}, form) => {
        return <TextArea
          placeholder="添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081"
          allowClear autoSize={{minRows: 3, maxRows: 10}}/>;
      },
    },
    {
      title: l('global.table.jobManagerAddress'),
      sorter: true,
      dataIndex: 'jobManagerHost',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
    }, {
      title: l('global.table.version'),
      sorter: true,
      dataIndex: 'version',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
    },
    {
      title: l('global.table.status'),
      dataIndex: 'status',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: l('global.table.status.normal'),
          value: 1,
        },
        {
          text: l('global.table.status.abnormal'),
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        1: {text: l('global.table.status.normal'), status: 'Success'},
        0: {text: l('global.table.status.abnormal'), status: 'Error'},
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
          text: l('global.table.enabled'),
          value: 1,
        },
        {
          text: l('global.table.disabled'),
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: l('global.table.enabled'), status: 'Success'},
        false: {text: l('global.table.disabled'), status: 'Error'},
      },
    },
    {
      title: l('global.table.registType'),
      dataIndex: 'autoRegisters',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: l('global.table.registType.automatic'),
          value: 1,
        },
        {
          text: l('global.table.registType.manual'),
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {
          text: l('global.table.registType.automatic'),
          status: 'Success'
        },
        false: {
          text: l('global.table.registType.manual'),
          status: 'Error'
        },
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
      tooltip: 'FLinkWebUI连接 当集群状态为`可用`时! 支持 KUBERNETES 之外的模式',
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
        ((record.status && (record.type === RUN_MODE.YARN_SESSION
            || record.type === RUN_MODE.STANDALONE
            || record.type === RUN_MODE.YARN_APPLICATION
            || record.type === RUN_MODE.YARN_PER_JOB
          )) ?
            <>
              <Button type="link" title={`http://${record.jobManagerHost}/#/overview`}
                      href={`http://${record.jobManagerHost}/#/overview`}
                      target="_blank"
              >
                FlinkWebUI
              </Button>
            </>
            : undefined
        ),
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<ClusterTableListItem>
        headerTitle={l('global.table.clusterManagement')}
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> {l('button.create')}
          </Button>,
          <Button type="primary" onClick={() => checkHeartBeats()}>
            <HeartOutlined/> {l('global.table.heartbeat')}
          </Button>,
          <Button type="primary" onClick={() => clearCluster()}>
            <ClearOutlined/> {l('global.table.recycle')}
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
                被禁用的集群共 {selectedRowsState.length - selectedRowsState.reduce((pre, item) => pre + (item.enabled ? 1 : 0), 0)} 个
              </span>
            </div>
          }
        >
          <Button type="primary" danger
                  onClick={() => {
                    Modal.confirm({
                      title: '删除集群',
                      content: '确定删除选中的集群吗？',
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
                      title: '启用集群',
                      content: '确定启用选中的集群吗？',
                      okText: l('button.confirm'),
                      cancelText: l('button.cancel'),
                      onOk: async () => {
                        await updateEnabled(url + '/enable', selectedRowsState, true);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          >{l('button.batchEnable')}</Button>
          <Button danger
                  onClick={() => {
                    Modal.confirm({
                      title: '禁用集群',
                      content: '确定禁用选中的集群吗？',
                      okText: l('button.confirm'),
                      cancelText: l('button.cancel'),
                      onOk: async () => {
                        await updateEnabled(url + '/enable', selectedRowsState, false);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }
                    });
                  }}
          >{l('button.batchDisable')}</Button>
        </FooterToolbar>
      )}
      <ClusterForm
        onSubmit={async (value) => {
          const success = await handleAddOrUpdate(url, value);
          if (success) {
            handleModalVisible(false);
            setFormValues({});
            if (actionRef.current) {
              actionRef.current.reload();
            }
            showCluster(dispatch);
            showSessionCluster(dispatch);
          }
        }}
        onCancel={() => handleModalVisible(false)}
        modalVisible={modalVisible}
        values={{}}
      >
      </ClusterForm>
      {formValues && Object.keys(formValues).length ? (
        <ClusterForm
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate(url, value);
            if (success) {
              handleUpdateModalVisible(false);
              setFormValues({});
              if (actionRef.current) {
                actionRef.current.reload();
              }
              showCluster(dispatch);
              showSessionCluster(dispatch);
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setFormValues({});
          }}
          modalVisible={updateModalVisible}
          values={formValues}
        />
      ) : undefined}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<ClusterTableListItem>
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

export default ClusterTableList;
