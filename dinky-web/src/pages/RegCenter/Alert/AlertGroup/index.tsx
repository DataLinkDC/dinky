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

import Pop from '@/components/Animation/Pop';
import { DangerDeleteIcon } from '@/components/Icons/CustomIcons';
import AlertGroupForm from '@/pages/RegCenter/Alert/AlertGroup/components/AlertGroupForm';
import { getAlertIcon } from '@/pages/RegCenter/Alert/AlertInstance/function';
import { ALERT_MODEL_ASYNC } from '@/pages/RegCenter/Alert/AlertInstance/model';
import { queryList } from '@/services/api';
import { handleAddOrUpdate, handleRemoveById, updateDataByParam } from '@/services/BusinessCrud';
import {
  PROTABLE_OPTIONS_PUBLIC,
  PRO_LIST_CARD_OPTIONS,
  SWITCH_OPTIONS
} from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Alert, ALERT_TYPE } from '@/types/RegCenter/data.d';
import { InitAlertGroupState } from '@/types/RegCenter/init.d';
import { AlertGroupState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { EditTwoTone, PlusOutlined } from '@ant-design/icons';
import { ProList } from '@ant-design/pro-components';
import { PageContainer } from '@ant-design/pro-layout';
import { ActionType } from '@ant-design/pro-table';
import { connect, Dispatch } from '@umijs/max';
import { Button, Descriptions, Modal, Space, Switch, Tag, Tooltip } from 'antd';
import DescriptionsItem from 'antd/es/descriptions/Item';
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
  const queryAlertGroupList = async () => {
    await queryList(API_CONSTANTS.ALERT_GROUP).then((res) =>
      setAlertGroupState((prevState) => ({ ...prevState, alertGroupList: res.data }))
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
  const exexuteWithRefreshLoading = async (callback: any) => {
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
      onOk: async () => {
        await exexuteWithRefreshLoading(
          async () => await handleRemoveById(API_CONSTANTS.ALERT_GROUP_DELETE, id)
        );
      }
    });
  };

  /**
   * handle enable alert instance
   * @param item
   */
  const handleEnable = async (item: Alert.AlertGroup) => {
    await exexuteWithRefreshLoading(async () => {
      await updateDataByParam(API_CONSTANTS.ALERT_GROUP_ENABLE, { id: item.id });
    });
  };

  /**
   * cancel callback
   */
  const handleCleanState = () => {
    setAlertGroupState((prevState) => ({
      ...prevState,
      value: {},
      addedAlertGroupOpen: false,
      editAlertGroupOpen: false
    }));
  };

  /**
   * handle add alert instance
   */
  const handleSubmit = async (value: Alert.AlertGroup) => {
    await exexuteWithRefreshLoading(async () => {
      await handleAddOrUpdate(
        API_CONSTANTS.ALERT_GROUP,
        value,
        () => {},
        () => handleCleanState()
      );
    });
  };

  /**
   * render right tool bar
   */
  const renderToolBar = () => {
    return () => [
      <Button
        key={'CreateAlertGroup'}
        type='primary'
        onClick={() =>
          setAlertGroupState((prevState) => ({ ...prevState, addedAlertGroupOpen: true }))
        }
      >
        <PlusOutlined /> {l('button.create')}
      </Button>
    ];
  };

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: Alert.AlertGroup) => {
    setAlertGroupState((prevState) => ({
      ...prevState,
      editAlertGroupOpen: !prevState.editAlertGroupOpen,
      value: item
    }));
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertGroupActionButton = (item: Alert.AlertGroup) => {
    return [
      <Button
        className={'options-button'}
        key={'AlertGroupEdit'}
        icon={<EditTwoTone />}
        title={l('button.edit')}
        onClick={() => editClick(item)}
      />,
      <Button
        className={'options-button'}
        key={'DeleteAlertGroupIcon'}
        icon={<DangerDeleteIcon />}
        onClick={() => handleDeleteSubmit(item.id)}
      />
    ];
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertGroupContent = (item: Alert.AlertGroup) => {
    const instanceCnt = item.alertInstanceIds.split(',').length || 0;
    return (
      <>
        <Space className={'hidden-overflow'}>
          <Switch
            key={item.id}
            {...SWITCH_OPTIONS()}
            checked={item.enabled}
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
        <DescriptionsItem className={'hidden-overflow'} key={item.id}>
          <Tooltip key={item.id} title={item.name}>
            <Tag color='success'>{item.name}</Tag>
          </Tooltip>
        </DescriptionsItem>
      </Descriptions>
    );
  };

  /**
   * render data source
   */
  const renderDataSource = alertGroupState.alertGroupList.map((item: Alert.AlertGroup) => ({
    subTitle: renderAlertGroupSubTitle(item),
    actions: renderAlertGroupActionButton(item),
    avatar: getAlertIcon(ALERT_TYPE.GROUP, 60),
    content: renderAlertGroupContent(item)
  }));

  return (
    <Pop>
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
          modalVisible={alertGroupState.addedAlertGroupOpen}
          values={{}}
        />
        {alertGroupState.value && Object.keys(alertGroupState.value).length > 0 && (
          <AlertGroupForm
            onSubmit={handleSubmit}
            onCancel={handleCleanState}
            modalVisible={alertGroupState.editAlertGroupOpen}
            values={alertGroupState.value}
          />
        )}
      </PageContainer>
    </Pop>
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
