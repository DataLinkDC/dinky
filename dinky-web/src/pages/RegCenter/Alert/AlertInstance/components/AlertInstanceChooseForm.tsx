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
import {Button, Card, List, Modal, Space, Steps} from "antd";

import {connect} from "umi";
import {l} from "@/utils/intl";
import {Alert, ALERT_CONFIG_LIST, ALERT_TYPE, AlertConfig} from "@/types/RegCenter/data.d";
import {createOrModifyAlertInstance, sendTest} from "@/pages/RegCenter/Alert/AlertInstance/service";
import DingTalkForm from "./DingTalkForm";
import WeChatForm from "@/pages/RegCenter/Alert/AlertInstance/components/WeChatForm";
import FeiShuForm from "./FeiShuForm";
import EmailForm from "./EmailForm";
import {AlertStateType} from "@/pages/RegCenter/Alert/AlertInstance/model";
import {getAlertIcon} from "@/pages/RegCenter/Alert/AlertInstance/function";
import {NORMAL_MODAL_OPTIONS} from "@/services/constants";
import {ProCard} from "@ant-design/pro-components";
import RcResizeObserver from "rc-resize-observer";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<Alert.AlertInstance>) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
};


const {Step} = Steps;


const AlertInstanceChooseForm: React.FC<UpdateFormProps> = (props) => {

  const [current, setCurrent] = useState(0);
  const [responsive, setResponsive] = useState(false);


  const {
    onSubmit: handleUpdate,
    onCancel: handleChooseModalVisible,
    modalVisible,
    values
  } = props;

  const [alertType, setAlertType] = useState<string>();

  const chooseOne = (item: AlertConfig) => {
    setAlertType(item.type);
  };

  const onSubmit = async (value: any) => {
    const success = await createOrModifyAlertInstance(value);
    if (success) {
      handleChooseModalVisible();
      setAlertType(undefined);
      handleUpdate(value);
    }
  };

  const onTest = async (value: any) => {
    await sendTest(value);
  };


  const renderCardItem = (item: AlertConfig)=> {
    return (
      <List.Item onClick={() => {
        chooseOne(item);
      }}>
        <Card>
          {getAlertIcon(item.type)}
        </Card>
      </List.Item>
    );
  }

  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={values?.id ? l("rc.ai.modify") : l("rc.ai.create")}
      open={modalVisible}
      onCancel={() => {
        setAlertType(undefined);
        handleChooseModalVisible();
      }}
      maskClosable={false}
      destroyOnClose={true}
      footer={null}
    >
      {
        (!alertType && !values?.id) && (
          <List
            grid={{gutter: 16, column: 4}}
            dataSource={ALERT_CONFIG_LIST}
            renderItem={(item: AlertConfig) => renderCardItem(item)}
          />
        )
      }

      {(values?.type === ALERT_TYPE.DINGTALK || alertType === ALERT_TYPE.DINGTALK) ?
        <DingTalkForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type === ALERT_TYPE.DINGTALK || alertType === ALERT_TYPE.DINGTALK}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
      {(values?.type === ALERT_TYPE.WECHAT || alertType === ALERT_TYPE.WECHAT) ?
        <WeChatForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type === ALERT_TYPE.WECHAT || alertType === ALERT_TYPE.WECHAT}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
      {(values?.type === ALERT_TYPE.FEISHU || alertType === ALERT_TYPE.FEISHU) ?
        <FeiShuForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type === ALERT_TYPE.FEISHU || alertType === ALERT_TYPE.FEISHU}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
      {(values?.type === ALERT_TYPE.EMAIL || alertType === ALERT_TYPE.EMAIL) ?
        <EmailForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type === ALERT_TYPE.EMAIL || alertType === ALERT_TYPE.EMAIL}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
    </Modal>
  );
};

export default connect(({Alert}: { Alert: AlertStateType }) => ({
  instance: Alert.instance,
}))(AlertInstanceChooseForm);
