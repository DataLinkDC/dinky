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
import { NormalDeleteBtn } from '@/components/CallBackButton/NormalDeleteBtn';
import { RunningBtn } from '@/components/CallBackButton/RunningBtn';
import { ClusterConfigIcon } from '@/components/Icons/HomeIcon';
import { DataAction } from '@/components/StyledComponents';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import { imgStyle } from '@/pages/Home/constants';
import ConfigurationModal from '@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal';
import { CLUSTER_CONFIG_TYPE } from '@/pages/RegCenter/Cluster/Configuration/components/contants';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
  handleOption,
  handlePutDataByParams,
  handleRemoveById,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, PRO_LIST_CARD_OPTIONS } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Cluster } from '@/types/RegCenter/data';
import { InitClusterConfigState } from '@/types/RegCenter/init.d';
import { ClusterConfigState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { CheckCircleOutlined, ExclamationCircleOutlined, HeartTwoTone } from '@ant-design/icons';
import { ActionType, ProList } from '@ant-design/pro-components';
import { Button, Descriptions, Modal, Space, Tag, Tooltip } from 'antd';
import DescriptionsItem from 'antd/es/descriptions/Item';
import { useEffect, useRef, useState } from 'react';

export default () => {
  /**
   * state
   */
  const [clusterConfigState, setClusterConfigState] =
    useState<ClusterConfigState>(InitClusterConfigState);

  const actionRef = useRef<ActionType>();

  // const { data, run  } = useRequest({
  //   url: API_CONSTANTS.CLUSTER_CONFIGURATION,
  //   method: 'POST',
  //   data: {}
  // });

  const queryClusterConfigList = async () => {
    queryList(API_CONSTANTS.CLUSTER_CONFIGURATION).then((res) =>
      setClusterConfigState((prevState) => ({ ...prevState, configList: res.data }))
    );
  };

  useEffect(() => {
    queryClusterConfigList();
  }, []);

  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallbackRefresh = async (callback: () => void) => {
    setClusterConfigState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    await queryClusterConfigList();
    setClusterConfigState((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload?.();
  };

  /**
   * handle delete
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l('rc.cc.delete'),
      content: l('rc.cc.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () =>
        executeAndCallbackRefresh(async () =>
          handleRemoveById(API_CONSTANTS.CLUSTER_CONFIGURATION_DELETE, id)
        )
    });
  };

  /**
   * handle enable
   * @param item
   */
  const handleEnable = async (item: Cluster.Config) => {
    await executeAndCallbackRefresh(async () =>
      updateDataByParam(API_CONSTANTS.CLUSTER_CONFIGURATION_ENABLE, {
        id: item.id
      })
    );
  };

  /**
   * START CLUSTER
   * @param item
   */
  const handleStartCluster = async (item: Cluster.Config) => {
    await executeAndCallbackRefresh(async () =>
      handlePutDataByParams(API_CONSTANTS.CLUSTER_CONFIGURATION_START, l('rc.cc.start'), {
        id: item.id
      })
    );
  };

  /**
   * cancel
   */
  const handleCancel = async () => {
    setClusterConfigState((prevState) => ({
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
  const handleSubmit = async (value: Partial<Cluster.Config>) => {
    await executeAndCallbackRefresh(async () => {
      await handleAddOrUpdate(API_CONSTANTS.CLUSTER_CONFIGURATION, value);
      await handleCancel();
    });
  };

  /**
   * render sub title
   * @param item
   */
  const renderDataSubTitle = (item: Cluster.Config) => {
    return (
      <Descriptions size={'small'} layout={'vertical'} column={1}>
        <DescriptionsItem className={'hidden-overflow'} key={item.id}>
          <Tooltip key={item.name} title={item.name}>
            {item.name}
          </Tooltip>
        </DescriptionsItem>
      </Descriptions>
    );
  };

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: Cluster.Config) => {
    setClusterConfigState((prevState) => ({
      ...prevState,
      editOpen: true,
      value: { ...item }
    }));
  };

  /**
   * handle check heart
   * @param item
   */
  const handleCheckHeartBeat = async (item: Cluster.Config) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.CLUSTER_CONFIGURATION_TEST, l('button.heartbeat'), item);
    });
  };

  /**
   * render action button
   * @param item
   */
  const renderDataActionButton = (item: Cluster.Config) => {
    return [
      <Authorized key={`${item.id}_edit`} path='/registration/cluster/config/edit'>
        <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)} />
      </Authorized>,
      <Authorized key={`${item.id}_delete`} path='/registration/cluster/config/delete'>
        <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)} />
      </Authorized>,
      <Authorized key={`${item.id}_delete`} path='/registration/cluster/config/deploy'>
        <RunningBtn
          key={`${item.id}_running`}
          title={l('rc.cc.start')}
          onClick={() => handleStartCluster(item)}
        />
      </Authorized>,
      <Authorized key={`${item.id}_heart`} path='/registration/cluster/config/heartbeat'>
        <Button
          className={'options-button'}
          key={`${item.id}_heart`}
          onClick={() => handleCheckHeartBeat(item)}
          title={l('button.heartbeat')}
          icon={<HeartTwoTone twoToneColor={item.isAvailable ? '#1ac431' : '#e10d0d'} />}
        />
      </Authorized>
    ];
  };
  /**
   * render content
   * @param item
   */
  const renderDataContent = (item: Cluster.Config) => {
    return (
      <Space size={4} align={'baseline'} className={'hidden-overflow'}>
        <EnableSwitchBtn
          record={item}
          onChange={() => handleEnable(item)}
          disabled={!HasAuthority('/registration/cluster/config/edit')}
        />
        <Tag color='cyan'>
          {CLUSTER_CONFIG_TYPE.find((record) => item.type === record.value)?.label}
        </Tag>
        <Tag
          icon={item.isAvailable ? <CheckCircleOutlined /> : <ExclamationCircleOutlined />}
          color={item.isAvailable ? 'success' : 'warning'}
        >
          {item.isAvailable ? l('global.table.status.normal') : l('global.table.status.abnormal')}
        </Tag>
      </Space>
    );
  };

  /**
   * render data list
   */
  const renderData = (list: Cluster.Config[]) =>
    list.map((item: Cluster.Config) => {
      return {
        subTitle: renderDataSubTitle(item),
        actions: <DataAction>{renderDataActionButton(item)}</DataAction>,
        avatar: <ClusterConfigIcon style={imgStyle} />,
        content: renderDataContent(item),
        key: item.id
      };
    });

  /**
   * tool bar render
   */
  const toolBarRender = () => [
    <Authorized key='new' path='/registration/cluster/config/add'>
      <CreateBtn
        key={'configcreate'}
        onClick={() => setClusterConfigState((prevState) => ({ ...prevState, addedOpen: true }))}
      />
    </Authorized>
  ];

  /**
   * render
   */
  return (
    <>
      <ProList<Cluster.Config>
        {...PROTABLE_OPTIONS_PUBLIC}
        {...(PRO_LIST_CARD_OPTIONS as any)}
        loading={clusterConfigState.loading}
        actionRef={actionRef}
        headerTitle={l('rc.cc.management')}
        toolBarRender={toolBarRender}
        dataSource={renderData(clusterConfigState.configList)}
      />

      {/*added*/}
      <ConfigurationModal
        visible={clusterConfigState.addedOpen}
        onClose={handleCancel}
        value={{}}
        onSubmit={handleSubmit}
      />
      {/*modify*/}
      {clusterConfigState.editOpen && (
        <ConfigurationModal
          visible={clusterConfigState.editOpen}
          onClose={handleCancel}
          value={clusterConfigState.value}
          onSubmit={handleSubmit}
        />
      )}
    </>
  );
};
