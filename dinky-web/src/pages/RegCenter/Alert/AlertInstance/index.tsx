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


import React, {useEffect, useRef, useState} from "react";
import {EditTwoTone, PlusOutlined} from "@ant-design/icons";
import {ActionType} from "@ant-design/pro-table";
import {Button, Descriptions, Modal, Space, Switch, Tag, Tooltip} from "antd";
import {PageContainer} from "@ant-design/pro-layout";
import {l} from "@/utils/intl";
import {Alert} from "@/types/RegCenter/data.d";
import {queryList} from "@/services/api";
import {handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {ProList} from "@ant-design/pro-components";
import {API_CONSTANTS, PRO_LIST_CARD_OPTIONS, PROTABLE_OPTIONS_PUBLIC, SWITCH_OPTIONS} from "@/services/constants";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";
import {getAlertIcon} from "@/pages/RegCenter/Alert/AlertInstance/function";
import DescriptionsItem from "antd/es/descriptions/Item";
import AlertTypeChoose from "./components/AlertTypeChoose";


const AlertInstanceTableList: React.FC = () => {
  /**
   * status
   */
  const actionRef = useRef<ActionType>();
  const [formValues, setFormValues] = useState<Alert.AlertInstance>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
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
  }

  /**
   * handle delete alert instance
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l("rc.ai.delete"),
      content: l("rc.ai.deleteConfirm"),
      okText: l("button.confirm"),
      cancelText: l("button.cancel"),
      onOk: async () => {
       await executeAndCallbackRefresh(async () => {
          await handleRemoveById(API_CONSTANTS.ALERT_INSTANCE_DELETE, id);
        })
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
    })
  };

  /**
   * query alert instance list
   */
  useEffect(() => {
    queryAlertInstanceList();
  }, [modalVisible]);

  /**
   * render alert instance sub title
   * @param item
   */
  const renderAlertInstanceSubTitle = (item: Alert.AlertInstance) => {
    return (
      <Descriptions size={"small"} layout={"vertical"} column={1}>
        <DescriptionsItem
          className={"hidden-overflow"}
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
    handleModalVisible(!modalVisible);
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertInstanceActionButton = (item: Alert.AlertInstance) => {
    return [
      <Button
        className={"options-button"}
        key={"AlertInstanceEdit"}
        icon={<EditTwoTone/>}
        title={l("button.edit")}
        onClick={() => editClick(item)}
      />,
      <Button
        className={"options-button"}
        key={"DeleteAlertInstanceIcon"}
        icon={<DangerDeleteIcon/>}
        onClick={() => handleDeleteSubmit(item.id)}
      />,
    ];
  };


  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertInstanceContent = (item: Alert.AlertInstance) => {
    return (
      <Space className={"hidden-overflow"}>
        <Tag color="#5BD8A6">{item.type}</Tag>
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
      <Button key={"CreateAlertInstance"} type="primary" onClick={() => handleModalVisible(true)}>
        <PlusOutlined/> {l("button.create")}
      </Button>,
    ];
  };

  /**
   * click cancel button callback
   */
  const cancelHandler = () => {
    handleModalVisible(!modalVisible);
    setFormValues(undefined);
  };


  /**
   * click submit button callback
   */
  const chooseSubmitHandler = () => {
    actionRef.current?.reloadAndRest?.();
  };

  /**
   * render main list
   */
  return (
    <PageContainer title={false}>
      {/* alert instance list */}
      <ProList<Alert.AlertInstance>
        {...PROTABLE_OPTIONS_PUBLIC}
        {...PRO_LIST_CARD_OPTIONS as any}
        loading={loading}
        actionRef={actionRef}
        headerTitle={l("rc.ai.management")}
        toolBarRender={renderToolBar()}
        dataSource={renderDataSource}
      />

      {/* render choose alert type list */}
      <AlertTypeChoose
        onCancel={cancelHandler}
        modalVisible={modalVisible}
        onSubmit={chooseSubmitHandler}
        values={formValues}
      />
    </PageContainer>
  );
};

export default AlertInstanceTableList;
