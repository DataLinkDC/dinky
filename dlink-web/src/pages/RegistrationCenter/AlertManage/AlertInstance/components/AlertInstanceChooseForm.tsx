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


import React, {useState} from 'react';
import {Card, List, Modal} from 'antd';

import {AlertInstanceTableListItem} from "@/pages/RegistrationCenter/data";
import {connect} from "umi";
import {ALERT_CONFIG_LIST, ALERT_TYPE, AlertConfig} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/conf";
import {getAlertIcon} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/icon";
import {AlertStateType} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/model";
import DingTalkForm from "@/pages/RegistrationCenter/AlertManage/AlertInstance/components/DingTalkForm";
import {createOrModifyAlertInstance, sendTest} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/service";
import WeChatForm from "@/pages/RegistrationCenter/AlertManage/AlertInstance/components/WeChatForm";
import FeiShuForm from "@/pages/RegistrationCenter/AlertManage/AlertInstance/components/FeiShuForm";
import EmailForm from "@/pages/RegistrationCenter/AlertManage/AlertInstance/components/EmailForm";
import {l} from "@/utils/intl";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<AlertInstanceTableListItem>) => void;
  onSubmit: (values: Partial<AlertInstanceTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<AlertInstanceTableListItem>;
};

const AlertInstanceChooseForm: React.FC<UpdateFormProps> = (props) => {

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


  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px'}}
      title={values?.id ? l('pages.rc.alert.instance.modify') : l('pages.rc.alert.instance.create')}
      visible={modalVisible}
      onCancel={() => {
        setAlertType(undefined);
        handleChooseModalVisible();
      }}
      maskClosable={false}
      destroyOnClose={true}
      footer={null}
    >{
      (!alertType && !values?.id) && (<List
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 4,
          lg: 4,
          xl: 4,
          xxl: 4,
        }}
        dataSource={ALERT_CONFIG_LIST}
        renderItem={(item: AlertConfig) => (
          <List.Item onClick={() => {
            chooseOne(item)
          }}>
            <Card>
              {getAlertIcon(item.type)}
            </Card>
          </List.Item>
        )}
      />)
    }
      {(values?.type == ALERT_TYPE.DINGTALK || alertType == ALERT_TYPE.DINGTALK) ?
        <DingTalkForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type == ALERT_TYPE.DINGTALK || alertType == ALERT_TYPE.DINGTALK}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
      {(values?.type == ALERT_TYPE.WECHAT || alertType == ALERT_TYPE.WECHAT) ?
        <WeChatForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type == ALERT_TYPE.WECHAT || alertType == ALERT_TYPE.WECHAT}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
      {(values?.type == ALERT_TYPE.FEISHU || alertType == ALERT_TYPE.FEISHU) ?
        <FeiShuForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type == ALERT_TYPE.FEISHU || alertType == ALERT_TYPE.FEISHU}
          values={values}
          onSubmit={(value) => {
            onSubmit(value);
          }}
          onTest={(value) => {
            onTest(value);
          }}
        /> : undefined
      }
      {(values?.type == ALERT_TYPE.EMAIL || alertType == ALERT_TYPE.EMAIL) ?
        <EmailForm
          onCancel={() => {
            setAlertType(undefined);
            handleChooseModalVisible();
          }}
          modalVisible={values?.type == ALERT_TYPE.EMAIL || alertType == ALERT_TYPE.EMAIL}
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
