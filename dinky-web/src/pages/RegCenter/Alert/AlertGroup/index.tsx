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

import SlowlyAppear from '@/components/Animation/SlowlyAppear';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { NormalDeleteBtn } from '@/components/CallBackButton/NormalDeleteBtn';
import { DataAction } from '@/components/StyledComponents';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import AlertGroupForm from '@/pages/RegCenter/Alert/AlertGroup/components/AlertGroupForm';
import { getAlertIcon } from '@/pages/RegCenter/Alert/AlertInstance/function';
import { ALERT_MODEL_ASYNC } from '@/pages/RegCenter/Alert/AlertInstance/model';
import {
  handleAddOrUpdate,
  handleRemoveById,
  queryDataByParams,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, PRO_LIST_CARD_OPTIONS } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { Alert, ALERT_TYPE } from '@/types/RegCenter/data.d';
import { InitAlertGroupState } from '@/types/RegCenter/init.d';
import { AlertGroupState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { PlusOutlined } from '@ant-design/icons';
import { ProList } from '@ant-design/pro-components';
import { PageContainer } from '@ant-design/pro-layout';
import { ActionType } from '@ant-design/pro-table';
import { connect, Dispatch } from '@umijs/max';
import { Button, Descriptions, Input, Modal, Space, Tag, Tooltip } from 'antd';
import React, { useEffect, useRef, useState } from 'react';

const AlertGroupTableList: React.FC = (props: any) => {
  /**
   * state
   */
  const [alertGroupState, setAlertGroupState] = useState<AlertGroupState>(InitAlertGroupState);

  const actionRef = useRef<ActionType>();

  /**
   * execute query alert instance list
   * set alert instance list
   */
  const queryAlertGroupList = async (keyword = '') => {
    queryDataByParams(API_CONSTANTS.ALERT_GROUP, { keyword }).then((res) =>
      setAlertGroupState((prevState) => ({
        ...prevState,
        alertGroupList: res as Alert.AlertGroup[]
      }))
    );
  };

  /**
   * mount data
   */
  useEffect(() => {
    queryAlertGroupList();
    props.queryInstance();
  }, [alertGroupState.loading]);

  /**
   * execute and refresh loading
   * @param callback
   */
  const executeWithRefreshLoading = async (callback: any) => {
    setAlertGroupState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    await queryAlertGroupList();
    setAlertGroupState((prevState) => ({ ...prevState, loading: false }));
  };

  /**
   * handle delete alert instance
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l('rc.ag.delete'),
      content: l('rc.ag.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () =>
        executeWithRefreshLoading(async () =>
          handleRemoveById(API_CONSTANTS.ALERT_GROUP_DELETE, id)
        )
    });
  };

  /**
   * handle enable alert instance
   * @param item
   */
  const handleEnable = async (item: Alert.AlertGroup) => {
    await executeWithRefreshLoading(async () =>
      updateDataByParam(API_CONSTANTS.ALERT_GROUP_ENABLE, { id: item.id })
    );
  };

  /**
   * cancel callback
   */
  const handleCleanState = () => {
    setAlertGroupState((prevState) => ({
      ...prevState,
      value: {},
      addedOpen: false,
      editOpen: false
    }));
  };

  /**
   * handle add alert instance
   */
  const handleSubmit = async (value: Alert.AlertGroup) => {
    await executeWithRefreshLoading(async () =>
      handleAddOrUpdate(
        API_CONSTANTS.ALERT_GROUP_ADD_OR_UPDATE,
        value,
        () => {},
        () => handleCleanState()
      )
    );
  };

  /**
   * render right tool bar
   */
  const renderToolBar = () => {
    return () => [
      <Input.Search
        loading={alertGroupState.loading}
        key={`_search`}
        allowClear
        placeholder={l('rc.ag.search')}
        onSearch={(value) => queryAlertGroupList(value)}
      />,
      <Authorized key='create' path={PermissionConstants.REGISTRATION_ALERT_GROUP_ADD}>
        <Button
          key={'CreateAlertGroup'}
          type='primary'
          onClick={() => setAlertGroupState((prevState) => ({ ...prevState, addedOpen: true }))}
        >
          <PlusOutlined /> {l('button.create')}
        </Button>
      </Authorized>
    ];
  };

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: Alert.AlertGroup) => {
    setAlertGroupState((prevState) => ({
      ...prevState,
      editOpen: !prevState.editOpen,
      value: item
    }));
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertGroupActionButton = (item: Alert.AlertGroup) => {
    return [
      <Authorized
        key={`${item.id}_auth_edit`}
        path={PermissionConstants.REGISTRATION_ALERT_GROUP_EDIT}
      >
        <EditBtn key={`${item.id}_edit`} onClick={() => editClick(item)} />
      </Authorized>,
      <Authorized
        key={`${item.id}_auth_delete`}
        path={PermissionConstants.REGISTRATION_ALERT_GROUP_DELETE}
      >
        <NormalDeleteBtn key={`${item.id}_delete`} onClick={() => handleDeleteSubmit(item.id)} />
      </Authorized>
    ];
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertGroupContent = (item: Alert.AlertGroup) => {
    const instanceCnt = item.alertInstanceIds.split(',').length ?? 0;
    return (
      <>
        <Space className={'hidden-overflow'}>
          <EnableSwitchBtn
            key={`${item.id}_enable`}
            disabled={!HasAuthority(PermissionConstants.REGISTRATION_ALERT_GROUP_EDIT)}
            record={item}
            onChange={() => handleEnable(item)}
          />
          <Tag color='warning'>{l('rc.ag.alertCount', '', { count: instanceCnt })}</Tag>
        </Space>
      </>
    );
  };

  /**
   * render alert instance sub title
   * @param item
   */
  const renderAlertGroupSubTitle = (item: Alert.AlertGroup) => {
    return (
      <Descriptions size={'small'} layout={'vertical'} column={1}>
        <Descriptions.Item className={'hidden-overflow'} key={item.id}>
          <Tooltip key={item.id} title={item.name}>
            <Tag color='success'>{item.name}</Tag>
          </Tooltip>
        </Descriptions.Item>
      </Descriptions>
    );
  };

  /**
   * render data source
   */
  const renderDataSource = alertGroupState.alertGroupList.map((item: Alert.AlertGroup) => ({
    subTitle: renderAlertGroupSubTitle(item),
    actions: <DataAction>{renderAlertGroupActionButton(item)}</DataAction>,
    avatar: getAlertIcon(ALERT_TYPE.GROUP, 60),
    content: renderAlertGroupContent(item)
  }));

  return (
    <SlowlyAppear>
      <PageContainer title={false}>
        {/* alert group list */}
        <ProList<Alert.AlertGroup>
          {...PROTABLE_OPTIONS_PUBLIC}
          {...(PRO_LIST_CARD_OPTIONS as any)}
          actionRef={actionRef}
          loading={alertGroupState.loading}
          headerTitle={l('rc.ag.management')}
          toolBarRender={renderToolBar()}
          dataSource={renderDataSource}
        />

        <AlertGroupForm
          onSubmit={handleSubmit}
          onCancel={handleCleanState}
          modalVisible={alertGroupState.addedOpen}
          values={{}}
        />
        {alertGroupState.value && Object.keys(alertGroupState.value).length > 0 && (
          <AlertGroupForm
            onSubmit={handleSubmit}
            onCancel={handleCleanState}
            modalVisible={alertGroupState.editOpen}
            values={alertGroupState.value}
          />
        )}
      </PageContainer>
    </SlowlyAppear>
  );
};

const mapDispatchToProps = (dispatch: Dispatch) => ({
  queryInstance: () =>
    dispatch({
      type: ALERT_MODEL_ASYNC.queryInstance,
      payload: {}
    })
});

export default connect(() => ({}), mapDispatchToProps)(AlertGroupTableList);
