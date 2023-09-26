/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import {Authorized, HasAuthority} from '@/hooks/useAccess';
import { CLUSTER_INSTANCE_STATUS_ENUM } from '@/pages/RegCenter/Cluster/Instance/components/contants';
import { renderWebUiRedirect } from '@/pages/RegCenter/Cluster/Instance/components/function';
import InstanceModal from '@/pages/RegCenter/Cluster/Instance/components/InstanceModal';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
  handleOption,
  handleRemoveById,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { YES_OR_NO_ENUM } from '@/types/Public/constants';
import { Cluster } from '@/types/RegCenter/data.d';
import { InitClusterInstanceState } from '@/types/RegCenter/init.d';
import { ClusterInstanceState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { ClearOutlined, HeartTwoTone } from '@ant-design/icons';
import { ActionType, ProTable } from '@ant-design/pro-components';
import { ProColumns } from '@ant-design/pro-table';
import { Button, Popconfirm } from 'antd';
import React, { useRef, useState } from 'react';

export default () => {
  /**
   * state
   */
  const [clusterInstanceStatus, setClusterInstanceStatus] =
    useState<ClusterInstanceState>(InitClusterInstanceState);
  const actionRef = useRef<ActionType>();

  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallback = async (callback: () => void) => {
    setClusterInstanceStatus((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setClusterInstanceStatus((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  /**
   * cancel
   */
  const handleCancel = async () => {
    setClusterInstanceStatus((prevState) => ({
      ...prevState,
      addedOpen: false,
      editOpen: false,
      value: {}
    }));
  };

  /**
   * submit add or update
   * @param value
   */
  const handleSubmit = async (value: Partial<Cluster.Instance>) => {
    await executeAndCallback(async () => {
      await handleAddOrUpdate(API_CONSTANTS.CLUSTER_INSTANCE, value);
      await handleCancel();
    });
  };

  /**
   * edit open
   * @param value
   */
  const handleEdit = async (value: Partial<Cluster.Instance>) => {
    setClusterInstanceStatus((prevState) => ({
      ...prevState,
      editOpen: true,
      value: value
    }));
  };

  /**
   * delete by id
   * @param id
   */
  const handleDelete = async (id: number) => {
    await executeAndCallback(async () =>
      handleRemoveById(API_CONSTANTS.CLUSTER_INSTANCE_DELETE, id)
    );
  };

  /**
   * enable or disable
   * @param record
   */
  const handleChangeEnable = async (record: Partial<Cluster.Instance>) => {
    await executeAndCallback(async () =>
      updateDataByParam(API_CONSTANTS.CLUSTER_INSTANCE_ENABLE, { id: record.id })
    );
  };

  /**
   * check heart beat
   */
  const handleHeartBeat = async () => {
    await executeAndCallback(async () =>
      handleOption(API_CONSTANTS.CLUSTER_INSTANCE_HEARTBEATS, l('rc.ci.heartbeat'), null)
    );
  };

  /**
   * recycle instance
   */
  const handleRecycle = async () => {
    await executeAndCallback(async () =>
      handleRemoveById(API_CONSTANTS.CLUSTER_INSTANCE_RECYCLE, 0)
    );
  };

  /**
   * columns
   */
  const columns: ProColumns<Cluster.Instance>[] = [
    {
      title: l('rc.ci.name'),
      dataIndex: 'name',
      ellipsis: true
    },
    {
      title: l('rc.ci.alias'),
      dataIndex: 'alias',
      ellipsis: true
    },
    {
      title: l('rc.ci.type'),
      dataIndex: 'type',
      hideInSearch: true,
      width: '8%'
    },
    {
      title: l('rc.ci.jma'),
      dataIndex: 'jobManagerHost',
      copyable: true,
      hideInSearch: true
    },
    {
      title: l('rc.ci.ar'),
      dataIndex: 'autoRegisters',
      hideInSearch: true,
      width: '8%',
      valueEnum: YES_OR_NO_ENUM
    },
    {
      title: l('rc.ci.version'),
      dataIndex: 'version',
      hideInSearch: true,
      width: '5%'
    },
    {
      title: l('rc.ci.status'),
      dataIndex: 'status',
      hideInSearch: true,
      width: '8%',
      valueEnum: CLUSTER_INSTANCE_STATUS_ENUM
    },
    {
      title: l('global.table.note'),
      dataIndex: 'note',
      width: '5%',
      ellipsis: true
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      width: '6%',
      hideInSearch: true,
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      valueEnum: STATUS_ENUM(),
      render: (_: any, record: Cluster.Instance) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            disabled={!HasAuthority('/registration/cluster/instance/edit')}
            record={record}
            onChange={() => handleChangeEnable(record)}
          />
        );
      }
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      hideInSearch: true,
      hideInTable: true
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      hideInSearch: true,
      hideInTable: true
    },
    {
      title: l('global.table.operate'),
      hideInSearch: true,
      valueType: 'option',
      width: '8vw',
      render: (_: any, record: Cluster.Instance) => [
        <Authorized key={`${record.id}_edit`} path='/registration/cluster/instance/edit'>
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEdit(record)} />
        </Authorized>,
        <Authorized key={`${record.id}_delete`} path='/registration/cluster/instance/delete'>
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={() => handleDelete(record.id)}
            description={l('rc.ci.deleteConfirm')}
          />
        </Authorized>,
        renderWebUiRedirect(record)
      ]
    }
  ];

  /**
   * tool bar render
   */
  const toolBarRender = () => [
    <Authorized key='/registration/cluster/instance/add' path='/registration/cluster/instance/add'>
      <CreateBtn
        key={'instancecreate'}
        onClick={() => setClusterInstanceStatus((prevState) => ({ ...prevState, addedOpen: true }))}
      />
    </Authorized>,
    <Authorized key='/registration/cluster/instance/heartbeat' path='/registration/cluster/instance/heartbeat'>
    <Button
      key={'heartbeat_all'}
      type={'primary'}
      icon={<HeartTwoTone />}
      onClick={() => handleHeartBeat()}
    >
      {l('button.heartbeat')}
    </Button>
      </Authorized>,
    <Authorized
      key='/registration/cluster/instance/recovery'
      path='/registration/cluster/instance/recovery'
    >
      <Popconfirm
        key={'recycle'}
        title={l('rc.ci.recycle')}
        description={l('rc.ci.recycleConfirm')}
        onConfirm={handleRecycle}
      >
        <Button key={'recycle_btn'} type={'primary'} icon={<ClearOutlined />}>
          {l('button.recycle')}
        </Button>
      </Popconfirm>
    </Authorized>
  ];

  /**
   * render
   */
  return (
    <>
      <ProTable<Cluster.Instance>
        headerTitle={l('rc.ci.management')}
        {...PROTABLE_OPTIONS_PUBLIC}
        columns={columns}
        actionRef={actionRef}
        loading={clusterInstanceStatus.loading}
        toolBarRender={toolBarRender}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.CLUSTER_INSTANCE, {
            ...params,
            sorter,
            filter
          })
        }
      />
      {/*added*/}
      <InstanceModal
        visible={clusterInstanceStatus.addedOpen}
        onClose={handleCancel}
        value={{}}
        onSubmit={handleSubmit}
      />
      {/*modify*/}
      <InstanceModal
        visible={clusterInstanceStatus.editOpen}
        onClose={handleCancel}
        value={clusterInstanceStatus.value}
        onSubmit={handleSubmit}
      />
    </>
  );
};
