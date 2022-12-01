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
import type {ClusterTableListItem} from "@/pages/RegistrationCenter/data";

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
import ClusterForm from "@/pages/RegistrationCenter/ClusterManage/Cluster/components/ClusterForm";
import {l} from "@/utils/intl";

const TextArea = Input.TextArea;
const url = '/api/cluster';


const ClusterTableList: React.FC<{}> = (props: any) => {

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
        title: l('pages.rc.cluster.delete'),
        content: l('pages.rc.cluster.deleteConfirm'),
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
      title: l('pages.rc.cluster.recycle'),
      content: l('pages.rc.cluster.recycleConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const {datas} = await getData(url + '/clear', l('pages.rc.cluster.recycle'), null);
        message.success(l('pages.rc.cluster.recycle.success', '', {total: datas}));
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
      title: l('pages.rc.cluster.instanceName'),
      dataIndex: 'name',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('pages.rc.cluster.instanceId'),
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: l('pages.rc.cluster.alias'),
      sorter: true,
      dataIndex: 'alias',
      hideInTable: false,
    },
    {
      title: l('pages.rc.cluster.type'),
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
      title: l('pages.rc.cluster.jobManagerHaAddress'),
      sorter: true,
      dataIndex: 'hosts',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('pages.rc.cluster.jobManagerAddress'),
      sorter: true,
      dataIndex: 'jobManagerHost',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: false,
    }, {
      title: l('pages.rc.cluster.version'),
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
      title: l('global.table.note'),
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
        headerTitle={l('pages.rc.clusterManagement')}
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
            <HeartOutlined/> {l('button.heartbeat')}
          </Button>,
          <Button type="primary" onClick={() => clearCluster()}>
            <ClearOutlined/> {l('button.recycle')}
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
                {l('pages.rc.cluster.disableTotalOf', '',
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
                      title: l('pages.rc.cluster.delete'),
                      content: l('pages.rc.cluster.deleteConfirm'),
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
                      title: l('pages.rc.cluster.enable'),
                      content: l('pages.rc.cluster.enableConfirm'),
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
                      title: l('pages.rc.cluster.disable'),
                      content: l('pages.rc.cluster.disableConfirm'),
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
