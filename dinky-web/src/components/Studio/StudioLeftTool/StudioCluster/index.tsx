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


import {Button, Drawer, Empty, Modal, Table, Tooltip} from "antd";
import ProDescriptions from '@ant-design/pro-descriptions';
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
import {PlusOutlined, ReloadOutlined} from '@ant-design/icons';
import {showCluster} from "../../StudioEvent/DDL";
import {handleAddOrUpdate, handleRemove} from "@/components/Common/crud";
import ClusterForm from "@/pages/RegistrationCenter/ClusterManage/Cluster/components/ClusterForm";
import {Scrollbars} from 'react-custom-scrollbars';
import {l} from "@/utils/intl";

const url = '/api/cluster';

const StudioCluster = (props: any) => {

  const {cluster, toolHeight, dispatch} = props;
  const [createModalVisible, handleCreateModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [row, setRow] = useState<{}>({});

  const getColumns = () => {
    return [{
      title: "集群名",
      dataIndex: "alias",
      key: "alias",
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    }];
  };

  const getAllColumns = () => {
    return [{
      title: "ID",
      dataIndex: "id",
      key: "id",
    }, {
      title: '唯一标识',
      dataIndex: 'name',
    }, {
      title: "集群名",
      dataIndex: "alias",
    },
      {
        title: '类型',
        dataIndex: 'type',
        filters: [
          {
            text: 'Yarn Session',
            value: 'yarn-session',
          },
          {
            text: 'Standalone',
            value: 'standalone',
          },
          {
            text: 'Yarn Per-Job',
            value: 'yarn-per-job',
          },
          {
            text: 'Yarn Application',
            value: 'yarn-application',
          },
          {
            text: 'Kubernetes Session',
            value: 'kubernetes-session',
          },
          {
            text: 'Kubernetes Application',
            value: 'kubernetes-application',
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
        title: 'JobManager HA 地址',
        dataIndex: 'hosts',
        valueType: 'textarea',
      },
      {
        title: '当前 JobManager 地址',
        dataIndex: 'jobManagerHost',
      }, {
        title: '版本',
        dataIndex: 'version',
      },
      {
        title: '状态',
        dataIndex: 'status',
        filters: [
          {
            text: '正常',
            value: 1,
          },
          {
            text: '异常',
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          1: {text: '正常', status: 'Success'},
          0: {text: '异常', status: 'Error'},
        },
      },
      {
        title: l('global.table.note'),
        valueType: 'textarea',
        dataIndex: 'note',
      },
      {
        title: l('global.table.isEnable'),
        dataIndex: 'enabled',
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
        title: '注册方式',
        dataIndex: 'autoRegisters',
        filters: [
          {
            text: '自动',
            value: 1,
          },
          {
            text: '手动',
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          true: {text: '自动', status: 'Success'},
          false: {text: '手动', status: 'Error'},
        },
      },
      {
        title: '集群配置ID',
        dataIndex: 'clusterConfigurationId',
      }, {
        title: '作业ID',
        dataIndex: 'taskId',
      },
      {
        title: l('global.table.createTime'),
        dataIndex: 'createTime',
        valueType: 'dateTime',
      },
      {
        title: l('global.table.lastUpdateTime'),
        dataIndex: 'updateTime',
        valueType: 'dateTime',
      },
      {
        title: l('global.table.operate'),
        dataIndex: 'option',
        valueType: 'option',
        render: (_, record) => [
          <Button type="dashed" onClick={() => onModifyCluster(record)}>
            {l('button.edit')}
          </Button>, <Button danger onClick={() => onDeleteCluster(record)}>
            {l('button.delete')}
          </Button>
        ],
      },];
  };

  const onRefreshCluster = () => {
    showCluster(dispatch);
  };

  const onCreateCluster = () => {
    handleCreateModalVisible(true);
  };

  const onModifyCluster = (record) => {
    setRow(record);
    handleUpdateModalVisible(true);
  };

  const onDeleteCluster = (record) => {
    Modal.confirm({
      title: '删除集群',
      content: `确定删除该集群【${record.alias}】吗？`,
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await handleRemove(url, [record]);
        setRow({});
        onRefreshCluster();
      }
    });
  };

  return (
    <>
      <Tooltip title="新建 Flink 集群">
        <Button
          type="text"
          icon={<PlusOutlined/>}
          onClick={onCreateCluster}
        />
      </Tooltip>
      <Tooltip title="刷新 Flink 集群">
        <Button
          type="text"
          icon={<ReloadOutlined/>}
          onClick={onRefreshCluster}
        />
      </Tooltip>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        {cluster.length > 0 ? (
          <Table dataSource={cluster} columns={getColumns()} size="small"/>) : (
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
        <ClusterForm
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate("api/cluster", value);
            if (success) {
              handleCreateModalVisible(false);
              showCluster(dispatch);
            }
          }}
          onCancel={() => {
            handleCreateModalVisible(false);
          }}
          modalVisible={createModalVisible}
          values={{}}
        />
        {row && Object.keys(row).length ? (<ClusterForm
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate("api/cluster", value);
            if (success) {
              handleUpdateModalVisible(false);
              setRow({});
              onRefreshCluster();
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
          }}
          modalVisible={updateModalVisible}
          values={row}
        />) : undefined}
        <Drawer
          width={600}
          visible={!!row?.id}
          onClose={() => {
            setRow({});
          }}
          closable={false}
        >
          {row?.name && (
            <ProDescriptions
              column={2}
              title={row?.name}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.name,
              }}
              columns={getAllColumns()}
            />
          )}
        </Drawer>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
  toolHeight: Studio.toolHeight,
}))(StudioCluster);
