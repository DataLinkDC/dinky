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


import React, {useState} from "react";
import {Card, List, Modal} from "antd";

import {connect} from "umi";
import {l} from "@/utils/intl";
import {Alert, ALERT_CONFIG_LIST, ALERT_TYPE, AlertConfig} from "@/types/RegCenter/data.d";
import {createOrModifyAlertInstance, sendTest} from "@/pages/RegCenter/Alert/AlertInstance/service";
import {AlertStateType} from "@/pages/RegCenter/Alert/AlertInstance/model";
import {getAlertIcon} from "@/pages/RegCenter/Alert/AlertInstance/function";
import {NORMAL_MODAL_OPTIONS} from "@/services/constants";
import DingTalk from "../DingTalk";
import WeChat from "../WeChat";
import FeiShu from "../FeiShu";
import Email from "../Email";

/**
 * update form props
 */
export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<Alert.AlertInstance>) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
};


const AlertTypeChoose: React.FC<UpdateFormProps> = (props) => {
  /**
   * state
   */
  const [alertType, setAlertType] = useState<string>();
  /**
   * extract props
   */
  const {
    onSubmit: handleUpdate,
    onCancel: handleChooseModalVisible,
    modalVisible,
    values
  } = props;


  /**
   * choose one type
   * @param item
   */
  const chooseAlertType = (item: AlertConfig) => {
    setAlertType(item.type);
  };

  /**
   * submit form
   * @param value
   */
  const onSubmit = async (value: any) => {
    const success = await createOrModifyAlertInstance(value);
    if (success) {
      handleChooseModalVisible();
      setAlertType(undefined);
      handleUpdate(value);
    }
  };
  /**
   * test alert msg
   * @param value
   */
  const onTest = async (value: any) => {
    await sendTest(value);
  };
  /**
   * cancel choose
   */
  function handleCancel() {
    setAlertType(undefined);
    handleChooseModalVisible();
  }



  /**
   * render card item list
   * @param item
   */
  const renderCardItem = (item: AlertConfig) => {
    return (
      <List.Item onClick={() => chooseAlertType(item)}>
        <Card>{getAlertIcon(item.type)}</Card>
      </List.Item>
    );
  };

  /**
   * get alert type
   * @param assertsType
   */
  const getAlertType = (assertsType: string) => {
    return values?.type === assertsType || alertType === assertsType;
  };


  /**
   * choose modal props
   */
  const chooseProp = {
    onCancel: handleCancel,
    modalVisible: modalVisible,
    values: values,
    onSubmit: (value: any) => onSubmit(value),
    onTest: (value: any) => onTest(value)
  };


  return (
    <>
      <Modal
        {...NORMAL_MODAL_OPTIONS}
        title={values?.id ? l("rc.ai.modify") : l("rc.ai.create")}
        open={modalVisible}
        onCancel={handleCancel}
        footer={null}
      >
        {
          (!alertType && !values?.id) &&
          <List grid={{gutter: 16, column: 4}} dataSource={ALERT_CONFIG_LIST}
                renderItem={(item: AlertConfig) => renderCardItem(item)}/>
        }
        {getAlertType(ALERT_TYPE.DINGTALK) && <DingTalk{...chooseProp}/>}
        {getAlertType(ALERT_TYPE.WECHAT) && <WeChat{...chooseProp}/>}
        {getAlertType(ALERT_TYPE.FEISHU) && <FeiShu{...chooseProp}/>}
        {getAlertType(ALERT_TYPE.EMAIL) && <Email{...chooseProp}/>}
      </Modal>
    </>


  );
};

export default connect(({Alert}: { Alert: AlertStateType }) => ({
  instance: Alert.instance,
}))(AlertTypeChoose);
