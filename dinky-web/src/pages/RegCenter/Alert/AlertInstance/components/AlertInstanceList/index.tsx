/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import React, {useEffect, useRef, useState} from 'react';
import {EditTwoTone} from '@ant-design/icons';
import {ActionType} from '@ant-design/pro-table';
import {Button, Descriptions, Modal, Space, Switch, Tag, Tooltip} from 'antd';
import {l} from '@/utils/intl';
import {Alert} from '@/types/RegCenter/data.d';
import {queryList} from '@/services/api';
import {handleRemoveById, updateEnabled} from '@/services/BusinessCrud';
import {ProList} from '@ant-design/pro-components';
import {API_CONSTANTS, PRO_LIST_CARD_OPTIONS, PROTABLE_OPTIONS_PUBLIC, SWITCH_OPTIONS} from '@/services/constants';
import {DangerDeleteIcon} from '@/components/Icons/CustomIcons';
import {getAlertIcon, getJSONData, getSmsType} from '@/pages/RegCenter/Alert/AlertInstance/function';
import DescriptionsItem from 'antd/es/descriptions/Item';
import AlertTypeChoose from '../AlertTypeChoose';
import {CreateBtn} from '@/components/CallBackButton/CreateBtn';
import {createOrModifyAlertInstance, sendTest} from '@/pages/RegCenter/Alert/AlertInstance/service';


const AlertInstanceList: React.FC = () => {
  /**
   * status
   */
  const actionRef = useRef<ActionType>();
  const [formValues, setFormValues] = useState<Partial<Alert.AlertInstance>>();
  const [addVisible, handleAddVisible] = useState<boolean>(false);
  const [updateVisible, handleUpdateVisible] = useState<boolean>(false);
  const [alertInstanceList, setAlertInstanceList] = useState<Alert.AlertInstance[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  /**
   * execute query alert instance list
   * set alert instance list
   */
  const queryAlertInstanceList = async () => {
    await queryList(API_CONSTANTS.ALERT_INSTANCE).then(res => {
      setAlertInstanceList(res.data);
    });
  };


  const executeAndCallbackRefresh = async (callback: () => void) => {
    setLoading(true);
    await callback();
    await queryAlertInstanceList();
    setLoading(false);
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
        await executeAndCallbackRefresh(async () => {
          await handleRemoveById(API_CONSTANTS.ALERT_INSTANCE_DELETE, id);
        });
      }
    });
  };

  /**
   * handle enable alert instance
   * @param item
   */
  const handleEnable = async (item: Alert.AlertInstance) => {
    await executeAndCallbackRefresh(async () => {
      await updateEnabled(API_CONSTANTS.ALERT_INSTANCE_ENABLE, {id: item.id});
    });
  };

  /**
   * query alert instance list
   */
  useEffect(() => {
    queryAlertInstanceList();
  }, [addVisible, updateVisible]);

  /**
   * render alert instance sub title
   * @param item
   */
  const renderAlertInstanceSubTitle = (item: Alert.AlertInstance) => {
    return (
      <Descriptions size={'small'} layout={'vertical'} column={1}>
        <DescriptionsItem
          className={'hidden-overflow'}
          key={item.id}>
          <Tooltip key={item.name} title={item.name}>{item.name}</Tooltip>
        </DescriptionsItem>
      </Descriptions>
    );
  };

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: Alert.AlertInstance) => {
    setFormValues(item);
    handleUpdateVisible(!updateVisible);
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertInstanceActionButton = (item: Alert.AlertInstance) => {
    return [
      <Button
        className={'options-button'}
        key={'AlertInstanceEdit'}
        icon={<EditTwoTone/>}
        title={l('button.edit')}
        onClick={() => editClick(item)}
      />,
      <Button
        className={'options-button'}
        key={'DeleteAlertInstanceIcon'}
        icon={<DangerDeleteIcon/>}
        onClick={() => handleDeleteSubmit(item.id)}
      />,
    ];
  };

  const renderSubType = (item: Alert.AlertInstance) => {
    if (JSON.parse(item.params).manufacturers) {
      return ` - ${getSmsType(JSON.parse(item.params).manufacturers)}`;
    }
  };


  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertInstanceContent = (item: Alert.AlertInstance) => {
    return (
      <Space className={'hidden-overflow'}>
        <Tag color="#5BD8A6">
          {item.type} {renderSubType(item)}
        </Tag>
        <Switch
          key={item.id}
          {...SWITCH_OPTIONS()}
          checked={item.enabled}
          onChange={() => handleEnable(item)}
        />
      </Space>
    );
  };


  /**
   * render data source
   */
  const renderDataSource = alertInstanceList.map((item) => ({
    subTitle: renderAlertInstanceSubTitle(item),
    actions: renderAlertInstanceActionButton(item),
    avatar: getAlertIcon(item.type, 60),
    content: renderAlertInstanceContent(item),
  }));


  /**
   * render right tool bar
   */
  const renderToolBar = () => {
    return () => [
      <CreateBtn key={'CreateAlertInstanceBtn'} onClick={() => handleAddVisible(true)}/>
    ];
  };

  /**
   * click cancel button callback
   */
  const cancelHandler = () => {
    handleAddVisible(false);
    handleUpdateVisible(false);
    actionRef.current?.reload();
    setFormValues(undefined);
  };

  /**
   * submit form
   * @param values
   */
  const handleSubmit = async (values: any) => {
    const success = await createOrModifyAlertInstance(getJSONData(values));
    if (success) {
      cancelHandler();
    }
  };
  /**
   * test alert msg
   * @param values
   */
  const handleTestSend = async (values: any) => {
    await sendTest(getJSONData(values));
  };

  /**
   * render main list
   */
  return <>
    {/* alert instance list */}
    <ProList<Alert.AlertInstance>
      {...PROTABLE_OPTIONS_PUBLIC}
      {...PRO_LIST_CARD_OPTIONS as any}
      loading={loading}
      actionRef={actionRef}
      headerTitle={l('rc.ai.management')}
      toolBarRender={renderToolBar()}
      dataSource={renderDataSource}
    />

    {/* added */}
    {addVisible && <AlertTypeChoose onTest={handleTestSend} onCancel={cancelHandler} modalVisible={addVisible}
                                    onSubmit={handleSubmit}
                                    values={{}}/>}
    {/* modify */}

    {updateVisible &&
      <AlertTypeChoose onTest={handleTestSend} onCancel={cancelHandler} modalVisible={updateVisible}
                                       onSubmit={handleSubmit}
                                       values={formValues}/>}
  </>;
};

export default AlertInstanceList;
