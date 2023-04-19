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


import React, {useEffect, useRef, useState} from "react";
import {EditTwoTone, PlusOutlined} from "@ant-design/icons";
import {ActionType} from "@ant-design/pro-table";
import {Button, Descriptions, Modal, Space, Switch, Tag, Tooltip} from "antd";
import {PageContainer} from "@ant-design/pro-layout";
import {l} from "@/utils/intl";
import {Alert, ALERT_TYPE} from "@/types/RegCenter/data.d";
import {queryList} from "@/services/api";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {showAlertInstance} from "@/pages/RegCenter/Alert/AlertGroup/service";
import {connect} from "@umijs/max";
import {ProList} from "@ant-design/pro-components";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC, SWITCH_OPTIONS} from "@/services/constants";
import {PRO_LIST_CARD_OPTIONS} from "@/pages/RegCenter/Alert/AlertInstance/constans";
import {getAlertIcon} from "@/pages/RegCenter/Alert/AlertInstance/function";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";
import DescriptionsItem from "antd/es/descriptions/Item";
import AlertGroupForm from "@/pages/RegCenter/Alert/AlertGroup/components/AlertGroupForm";

const AlertGroupTableList: React.FC = (props: any) => {
  /**
   * state
   */
  const {dispatch} = props;
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const [alertGroupList, setAlertGroupList] = useState<Alert.AlertGroup[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();


  /**
   * execute query alert instance list
   * set alert instance list
   */
  const queryAlertGroupList = async () => {
    await queryList(API_CONSTANTS.ALERT_GROUP).then(res => {
      setAlertGroupList(res.data);
    });
  };

  /**
   * mount data
   */
  useEffect(() => {
    queryAlertGroupList();
    showAlertInstance(dispatch);
  }, [modalVisible]);

  /**
   * execute and refresh loading
   * @param callback
   */
  const exexuteWithRefreshLoading = async (callback: any) => {
    setLoading(true);
    await callback();
    await queryAlertGroupList();
    setLoading(false);
  };


  /**
   * handle delete alert instance
   * @param id
   */
  const handleDeleteSubmit = async (id: number) => {
    Modal.confirm({
      title: l("rc.ag.delete"),
      content: l("rc.ag.deleteConfirm"),
      okText: l("button.confirm"),
      cancelText: l("button.cancel"),
      onOk: async () => {
        await exexuteWithRefreshLoading(async () => {
          await handleRemoveById(API_CONSTANTS.ALERT_GROUP_DELETE, id);
        });
      }
    });
  };

  /**
   * handle enable alert instance
   * @param item
   */
  const handleEnable = async (item: Alert.AlertGroup) => {
    await exexuteWithRefreshLoading(async () => {
      await updateEnabled(API_CONSTANTS.ALERT_GROUP_ENABLE, {id: item.id});
    });
  };


  /**
   * cancel callback
   */
  const handleCleanState = () => {
    setFormValues({});
    handleModalVisible(false);
    handleUpdateModalVisible(false);
  };


  /**
   * handle add alert instance
   */
  const handleSubmit = async (value: Alert.AlertGroup) => {
    await exexuteWithRefreshLoading(async () => {
      await handleAddOrUpdate(API_CONSTANTS.ALERT_GROUP, value);
    });
    handleCleanState();
  };

  /**
   * render right tool bar
   */
  const renderToolBar = () => {
    return () => [
      <Button key={"CreateAlertGroup"} type="primary" onClick={() => handleModalVisible(true)}>
        <PlusOutlined/> {l("button.create")}
      </Button>,
    ];
  };

  /**
   * edit click callback
   * @param item
   */
  const editClick = (item: Alert.AlertGroup) => {
    setFormValues(item);
    handleUpdateModalVisible(!updateModalVisible);
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertGroupActionButton = (item: Alert.AlertGroup) => {
    return [
      <Button
        className={"options-button"}
        key={"AlertGroupEdit"}
        icon={<EditTwoTone/>}
        title={l("button.edit")}
        onClick={() => editClick(item)}
      />,
      <Button
        className={"options-button"}
        key={"DeleteAlertGroupIcon"}
        icon={<DangerDeleteIcon/>}
        onClick={() => handleDeleteSubmit(item.id)}
      />,
    ];
  };

  /**
   * render alert instance action button
   * @param item
   */
  const renderAlertGroupContent = (item: Alert.AlertGroup) => {
   const instanceCnt= item.alertInstanceIds.split(",").length || 0
    return <>
      <Space className={"hidden-overflow"}>
        <Switch
          key={item.id}
          {...SWITCH_OPTIONS()}
          checked={item.enabled}
          onChange={() => handleEnable(item)}
        />
        <Tag color="warning">
          {l("rc.ag.alertCount", "", {count: instanceCnt})}
        </Tag>
      </Space>
    </>;
  };

  /**
   * render alert instance sub title
   * @param item
   */
  const renderAlertGroupSubTitle = (item: Alert.AlertGroup) => {
    return (
      <Descriptions size={"small"} layout={"vertical"} column={1}>
        <DescriptionsItem
          className={"hidden-overflow"}
          key={item.id}
        >
          <Tooltip key={item.id} title={item.name}>
            <Tag color="success">{item.name}</Tag>
          </Tooltip>
        </DescriptionsItem>
      </Descriptions>
    );
  };


  /**
   * render data source
   */
  const renderDataSource = alertGroupList.map((item: Alert.AlertGroup) => ({
    subTitle: renderAlertGroupSubTitle(item),
    actions: renderAlertGroupActionButton(item),
    avatar: getAlertIcon(ALERT_TYPE.GROUP, 60),
    content: renderAlertGroupContent(item),
  }));


  return (
    <PageContainer title={false}>
      {/* alert group list */}
      <ProList<Alert.AlertGroup>
        {...PROTABLE_OPTIONS_PUBLIC}
        {...PRO_LIST_CARD_OPTIONS as any}
        actionRef={actionRef}
        loading={loading}
        headerTitle={l("rc.ag.management")}
        toolBarRender={renderToolBar()}
        dataSource={renderDataSource}
      />

      <AlertGroupForm
        onSubmit={handleSubmit}
        onCancel={handleCleanState}
        modalVisible={modalVisible}
        values={{}}
      />
      {updateModalVisible &&
          <AlertGroupForm
            onSubmit={handleSubmit}
            onCancel={handleCleanState}
            modalVisible={updateModalVisible}
            values={formValues}
          />
      }

    </PageContainer>
  );
};

export default connect()(AlertGroupTableList);
