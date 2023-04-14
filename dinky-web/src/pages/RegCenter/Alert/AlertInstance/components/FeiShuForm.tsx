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
import {Button, Divider, Form, Input, Modal, Radio, Switch} from 'antd';
import {l} from "@/utils/intl";
import {Alert, ALERT_TYPE} from "@/types/RegCenter/data.d";
import {buildJSONData, getJSONData} from "@/pages/RegCenter/Alert/AlertInstance/function";

export type AlertInstanceFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values:Partial< Alert.AlertInstance>) => void;
  onTest: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const FeiShuForm: React.FC<AlertInstanceFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertInstance>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.FEISHU,
    params: props.values?.params,
    enabled: props.values?.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onTest: handleTest,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const onValuesChange = (change: any) => {
    setFormVals({...formVals, ...change});
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleSubmit(buildJSONData(formVals, fieldsValue));
  };

  const sendTestForm = async () => {
    const fieldsValue = await form.getFieldsValue();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleTest(buildJSONData(formVals, fieldsValue));
  };

  const renderContent = (vals: any) => {
    return (
      <>
        <Divider>{l('rc.ai.feishu')}</Divider>
        <Form.Item
          name="name"
          label={l('rc.ai.name')}
          rules={[{required: true, message: l('rc.ai.namePleaseHolder')}]}
        >
          <Input placeholder={l('rc.ai.namePleaseHolder')}/>
        </Form.Item>
        <Form.Item
          name="webhook"
          label={l('rc.ai.webhook')}
          rules={[{required: true, message: l('rc.ai.webhookPleaseHolder')}]}
        >
          <Input.TextArea placeholder={l('rc.ai.webhookPleaseHolder')} allowClear
                          autoSize={{minRows: 1, maxRows: 5}}/>
        </Form.Item>
        <Form.Item
          name="keyword"
          label={l('rc.ai.keyword')}
        >
          <Input placeholder={l('rc.ai.keywordPleaseHolder')}/>
        </Form.Item>
        <Form.Item
          name="secret"
          label={l('rc.ai.secret')}
        >
          <Input placeholder={l('rc.ai.secretPleaseHolder')}/>
        </Form.Item>
        <Form.Item
          name="isEnableProxy"
          label={l('rc.ai.isEnableProxy')}>
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={vals.isEnableProxy}/>
        </Form.Item>
        {vals.isEnableProxy ? <>
          <Form.Item
            name="proxy"
            label={l('rc.ai.proxy')}
          >
            <Input placeholder={l('rc.ai.proxyPleaseHolder')}/>
          </Form.Item>
          <Form.Item
            name="port"
            label={l('rc.ai.port')}
          >
            <Input placeholder={l('rc.ai.portPleaseHolder')}/>
          </Form.Item>
          <Form.Item
            name="user"
            label={l('rc.ai.user')}
          >
            <Input placeholder={l('rc.ai.userPleaseHolder')}/>
          </Form.Item>
          <Form.Item
            name="password"
            label={l('rc.ai.password')}
          >
            <Input.Password placeholder={l('rc.ai.passwordPleaseHolder')}/>
          </Form.Item></> : undefined
        }
        <Form.Item
          name="isAtAll"
          label={l('rc.ai.isAtAll')}>
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={vals.isAtAll}/>
        </Form.Item>
        {(!vals.isAtAll) &&
          <Form.Item
            name="users"
            label={l('rc.ai.atUsers')}
            rules={[{required: true, message: l('rc.ai.atUsersPleaseHolder')}]}
          >
            <Input placeholder={l('rc.ai.atUsersPleaseHolder')}/>
          </Form.Item>
        }
        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}>
          <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={vals.enabled}/>
        </Form.Item>
        <Form.Item
          name="msgtype"
          label={l('rc.ai.msgtype')}
          rules={[{required: true, message: l('rc.ai.msgtypePleaseHolder')}]}
        >
          <Radio.Group>
            <Radio value='post'>{l('rc.ai.post')}</Radio>
            <Radio value='text'>{l('rc.ai.text')}</Radio>
          </Radio.Group>
        </Form.Item>
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
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px',height: '600px', overflowY: 'auto'}}
      destroyOnClose
      title={formVals.id ? l('rc.ai.modify') : l('rc.ai.create')}
      open={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getJSONData(formVals )}
        onValuesChange={onValuesChange}
      >
        {renderContent(getJSONData(formVals))}
      </Form>
    </Modal>
  );
};

export default FeiShuForm;
