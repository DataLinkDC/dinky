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


import React, {useState} from 'react';
import {Button, Divider, Form, Input, message,  Radio} from 'antd';
import {l} from "@/utils/intl";
import {Alert, ALERT_TYPE} from "@/types/RegCenter/data.d";
import {buildJSONData} from "@/pages/RegCenter/Alert/AlertInstance/function";
import {
  ModalForm,
  ProForm,
  ProFormDigit, ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";

export type AlertInstanceFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  onTest: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
};


const DingTalkForm: React.FC<AlertInstanceFormProps> = (props) => {
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertInstance>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.DINGTALK,
    params: props.values?.params,
    enabled: props.values?.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    onTest: handleTest,
    modalVisible,
  } = props;

  const onValuesChange = (change: any) => {
    setFormVals({...formVals, ...change});
  };

  const sendTestForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleTest(buildJSONData(formVals, fieldsValue));
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleSubmit(buildJSONData(formVals, fieldsValue));
  };

  const renderContent = (vals: any) => {
    return (
      <>
        <Divider>{l('rc.ai.dingTalk')}</Divider>
        <ProForm.Group>
          <ProFormText
            width="md"
            name="name"
            label={l('rc.ai.name')}
            rules={[{required: true, message: l('rc.ai.namePleaseHolder')}]}
            placeholder={l('rc.ai.namePleaseHolder')}
          />
          <ProFormText
            width="md"
            name="keyword"
            label={l('rc.ai.keyword')}
            placeholder={l('rc.ai.keywordPleaseHolder')}
          />
          <ProFormTextArea
            width="md"
            name="webhook"
            label={l('rc.ai.webhook')}
            rules={[{required: true, message: l('rc.ai.webhookPleaseHolder')}]}
            placeholder={l('rc.ai.webhookPleaseHolder')}
          />
          <ProFormText
            width="md"
            name="secret"
            label={l('rc.ai.secret')}
            placeholder={l('rc.ai.secretPleaseHolder')}
          />
        </ProForm.Group>

        <ProForm.Group>
          <ProFormRadio.Group
            name="msgtype"
            width={'xs'}
            label={l('rc.ai.msgtype')}
            rules={[{required: true, message: l('rc.ai.msgtypePleaseHolder')}]}
          >
            <Radio.Group>
              <Radio value='markdown'>{l('rc.ai.markdown')}</Radio>
              <Radio value='text'>{l('rc.ai.text')}</Radio>
            </Radio.Group>
          </ProFormRadio.Group>

          <ProFormSwitch
            width="xs"
            name="isEnableProxy"
            label={l('rc.ai.isEnableProxy')}
            checkedChildren={l('button.enable')}
            unCheckedChildren={l('button.disable')}
          />
          <ProFormSwitch
            width="xs"
            name="isAtAll"
            label={l('rc.ai.isAtAll')}
            checkedChildren={l('button.enable')}
            unCheckedChildren={l('button.disable')}
          />
          <ProFormSwitch
            width="xs"
            name="enabled"
            label={l('global.table.isEnable')}
            checkedChildren={l('button.enable')}
            unCheckedChildren={l('button.disable')}
          />

        </ProForm.Group>

        <ProForm.Group>
          {vals.isEnableProxy ? <>
            <ProFormText
              width="md"
              name="proxy"
              label={l('rc.ai.proxy')}
              rules={[{required: true, message: l('rc.ai.proxyPleaseHolder')}]}
            />

            <ProFormDigit
              width="xs"
              name="port"
              label={l('rc.ai.port')}
              rules={[{required: true, message: l('rc.ai.portPleaseHolder')}]}
            />
            <ProFormText
              width="xs"
              name="user"
              label={l('rc.ai.user')}
              rules={[{required: true, message: l('rc.ai.userPleaseHolder')}]}
            />
            <ProForm.Item
              name="password"
              label={l('rc.ai.password')}
              rules={[{required: true, message: l('rc.ai.passwordPleaseHolder')}]}
            >
              <Input.Password placeholder={l('rc.ai.passwordPleaseHolder')}/>
            </ProForm.Item></> : undefined
          }
        </ProForm.Group>

        <ProForm.Group>
          {vals.isAtAll &&
            <>
              <ProFormTextArea
                width="md"
                name="atMobiles"
                label={l('rc.ai.atMobiles')}
                rules={[{required: true, message: l('rc.ai.atMobilesPleaseHolder') }]}
              />
            </>
          }
        </ProForm.Group>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l('button.cancel')}</Button>
        <Button type="primary" onClick={() => sendTestForm()}>{l('button.test')}</Button>
        <Button type="primary" onClick={() => submitForm()}>{l('button.finish')}</Button>
      </>
    );
  };

  return (

    <ModalForm
      title={formVals.id ? l('rc.ai.modify') : l('rc.ai.create')}
      open={modalVisible}
      form={form}
      initialValues={formVals}
      onValuesChange={onValuesChange}
      onFinish={async () => handleSubmit(formVals)}
      // onOpenChange={handleModalVisible}
    >
      {renderContent(formVals)}
    </ModalForm>

    // <Modal
    //   width={"40%"}
    //   bodyStyle={{padding: '32px 40px 48px',height: '600px', overflowY: 'auto'}}
    //   destroyOnClose
    //   title={formVals.id ? l('rc.ai.modify') : l('rc.ai.create')}
    //   open={modalVisible}
    //   footer={renderFooter()}
    //   onCancel={() => handleModalVisible()}
    // >
    //   <Form
    //     {...formLayout}
    //     form={form}
    //     initialValues={getJSONData(formVals )}
    //     onValuesChange={onValuesChange}
    //   >
    //     {renderContent(getJSONData(formVals))}
    //   </Form>
    // </Modal>
  );
};

export default DingTalkForm;
