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

import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { NormalDeleteBtn } from '@/components/CallBackButton/NormalDeleteBtn';
import { DataAction } from '@/components/StyledComponents';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import { getAlertIcon, getSmsType } from '@/pages/RegCenter/Alert/AlertInstance/function';
import {
  createOrModifyAlertInstance,
  sendTest
} from '@/pages/RegCenter/Alert/AlertInstance/service';
import { handleRemoveById, queryDataByParams, updateDataByParam } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, PRO_LIST_CARD_OPTIONS } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { Alert } from '@/types/RegCenter/data.d';
import { InitAlertInstance, InitAlertInstanceState } from '@/types/RegCenter/init.d';
import { AlertInstanceState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { ProList } from '@ant-design/pro-components';
import { ActionType } from '@ant-design/pro-table';
import { Descriptions, Input, Modal, Space, Tag, Tooltip } from 'antd';
import DescriptionsItem from 'antd/es/descriptions/Item';
import React, { useEffect, useRef, useState } from 'react';
import AlertTypeChoose from '../AlertTypeChoose';

const AlertInstanceList: React.FC = () => {
  /**
   * status
   */
  const [alertInstanceState, setAlertInstanceState] =
    useState<AlertInstanceState>(InitAlertInstanceState);

  const actionRef = useRef<ActionType>();

  /**
   * execute query alert instance list
   * set alert instance list
   */
  const queryAlertInstanceList = async (keyword = '') => {
    queryDataByParams(API_CONSTANTS.ALERT_INSTANCE, { keyword }).then((res) =>
      setAlertInstanceState((prevState) => ({
        ...prevState,
        alertInstanceList: res as Alert.AlertInstance[]
      }))
    );
  };

  useEffect(() => {
    queryAlertInstanceList();
  }, []);

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setAlertInstanceState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setAlertInstanceState((prevState) => ({ ...prevState, loading: false }));
  };

  /**
   * handle delete alert instance
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l('rc.ai.delete'),
      content: l('rc.ai.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await executeAndCallbackRefresh(
          async () => await handleRemoveById(API_CONSTANTS.ALERT_INSTANCE_DELETE, id)
        );
        await queryAlertInstanceList();
      }
    });
  };

  /**
   * handle enable alert instance
   * @param item
   */
  const handleEnable = async (item: Alert.AlertInstance) => {
    await executeAndCallbackRefresh(async () =>
      updateDataByParam(API_CONSTANTS.ALERT_INSTANCE_ENABLE, {
        id: item.id
      })
    );
    await queryAlertInstanceList();
  };

  /**
   * render alert instance sub title
   * @param item
   */
  const renderAlertInstanceSubTitle = (item: Alert.AlertInstance) => {
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
  const editClick = (item: Alert.AlertInstance) => {
    setAlertInstanceState((prevState) => ({
      ...prevState,
      value: item,
      editOpen: true
    }));
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertInstanceActionButton = (item: Alert.AlertInstance) => {
    return [
      <Authorized
        key={`${item.id}_auth_edit`}
        path={PermissionConstants.REGISTRATION_ALERT_INSTANCE_EDIT}
      >
        <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)} />
      </Authorized>,
      <Authorized
        key={`${item.id}_auth_delete`}
        path={PermissionConstants.REGISTRATION_ALERT_INSTANCE_DELETE}
      >
        <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)} />
      </Authorized>
    ];
  };

  const renderSubType = (item: Alert.AlertInstance) => {
    const params = item.params as Alert.AlertInstanceParamsSms;
    if (params.suppliers) {
      return ` - ${getSmsType(params.suppliers)}`;
    }
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertInstanceContent = (item: Alert.AlertInstance) => {
    return (
      <Space className={'hidden-overflow'}>
        <Tag color='#5BD8A6'>
          {item.type} {renderSubType(item)}
        </Tag>
        <EnableSwitchBtn
          key={`${item.id}_enable`}
          disabled={!HasAuthority(PermissionConstants.REGISTRATION_ALERT_INSTANCE_EDIT)}
          record={item}
          onChange={() => handleEnable(item)}
        />
      </Space>
    );
  };

  /**
   * render data source
   */
  const renderDataSource = alertInstanceState.alertInstanceList.map((item) => {
    return {
      subTitle: renderAlertInstanceSubTitle(item),
      actions: <DataAction>{renderAlertInstanceActionButton(item)}</DataAction>,
      avatar: getAlertIcon(item.type, 60),
      content: renderAlertInstanceContent(item)
    };
  });

  /**
   * render right tool bar
   */
  const renderToolBar = () => {
    return () => [
      <Input.Search
        loading={alertInstanceState.loading}
        key={`_search`}
        allowClear
        placeholder={l('rc.ai.search')}
        onSearch={(value) => queryAlertInstanceList(value)}
      />,
      <Authorized key='create' path={PermissionConstants.REGISTRATION_ALERT_INSTANCE_ADD}>
        <CreateBtn
          key={'CreateAlertInstanceBtn'}
          onClick={() => setAlertInstanceState((prevState) => ({ ...prevState, addedOpen: true }))}
        />
      </Authorized>
    ];
  };

  /**
   * click cancel button callback
   */
  const cancelHandler = () => {
    setAlertInstanceState((prevState) => ({
      ...prevState,
      addedOpen: false,
      editOpen: false,
      value: InitAlertInstance
    }));
    actionRef.current?.reload();
  };

  /**
   * submit form
   * @param values
   */
  const handleSubmit = async (values: any) => {
    const success = await createOrModifyAlertInstance(values);
    if (success) {
      cancelHandler();
      await queryAlertInstanceList();
    }
  };
  /**
   * test alert msg
   * @param values
   */
  const handleTestSend = async (values: any) => {
    await executeAndCallbackRefresh(async () => await sendTest(values));
  };

  /**
   * render main list
   */
  return (
    <>
      {/* alert instance list */}
      <ProList<Alert.AlertInstance>
        {...PROTABLE_OPTIONS_PUBLIC}
        {...(PRO_LIST_CARD_OPTIONS as any)}
        loading={alertInstanceState.loading}
        actionRef={actionRef}
        headerTitle={l('rc.ai.management')}
        toolBarRender={renderToolBar()}
        dataSource={renderDataSource}
      />

      {/* added */}
      {alertInstanceState.addedOpen && (
        <AlertTypeChoose
          onTest={handleTestSend}
          onCancel={cancelHandler}
          modalVisible={alertInstanceState.addedOpen}
          onSubmit={handleSubmit}
          loading={alertInstanceState.loading}
          values={InitAlertInstance}
        />
      )}
      {/* modify */}

      {alertInstanceState.editOpen && (
        <AlertTypeChoose
          onTest={handleTestSend}
          onCancel={cancelHandler}
          modalVisible={alertInstanceState.editOpen}
          onSubmit={handleSubmit}
          values={alertInstanceState.value}
          loading={alertInstanceState.loading}
        />
      )}
    </>
  );
};

export default AlertInstanceList;
