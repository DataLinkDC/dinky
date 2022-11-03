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
import {Button, Divider, Form, Input, Modal, Radio, Switch} from 'antd';
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";
import {buildJSONData, getJSONData} from "@/pages/AlertInstance/function";
import {ALERT_TYPE} from "@/pages/AlertInstance/conf";
import {useIntl} from "umi";

export type AlertInstanceFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<AlertInstanceTableListItem>) => void;
  onTest: (values: Partial<AlertInstanceTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<AlertInstanceTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const WeChatForm: React.FC<AlertInstanceFormProps> = (props) => {


  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<AlertInstanceTableListItem>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.WECHAT,
    params: props.values?.params,
    enabled: props.values?.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    onTest: handleTest,
    modalVisible,
  } = props;

  const onValuesChange = (change: any, all: any) => {
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

  const renderContent = (vals) => {
    return (
      <>
        <Divider>{l('pages.registerCenter.alert.instance.wechat')}</Divider>
        <Form.Item
          name="name"
          label={l('pages.registerCenter.alert.instance.name')}
          rules={[{required: true, message: l('pages.registerCenter.alert.instance.namePleaseHolder')}]}
        >
          <Input placeholder={l('pages.registerCenter.alert.instance.namePleaseHolder')}/>
        </Form.Item>
        <Form.Item
          name="sendType"
          label={l('pages.registerCenter.alert.instance.sendType')}
          validateTrigger={['onChange', 'onBlur']}
          rules={[{required: true, message: l('pages.registerCenter.alert.instance.sendTypePleaseHolder')}]}
        >
          <Radio.Group defaultValue="应用">
            <Radio value='应用'>{l('pages.registerCenter.alert.instance.sendType.app')}</Radio>
            <Radio value='群聊'>{l('pages.registerCenter.alert.instance.sendType.wechat')}</Radio>
          </Radio.Group>
        </Form.Item>
        {(vals.sendType == "群聊") ?
          <>
            <Form.Item
              name="webhook"
              label={l('pages.registerCenter.alert.instance.webhook')}
              rules={[{required: true, message: l('pages.registerCenter.alert.instance.webhookPleaseHolder')}]}
            >
              <Input placeholder={l('pages.registerCenter.alert.instance.webhookPleaseHolder')}/>
            </Form.Item>
            <Form.Item
              name="keyword"
              label={l('pages.registerCenter.alert.instance.keyword')}
            >
              <Input placeholder={l('pages.registerCenter.alert.instance.keywordPleaseHolder')}/>
            </Form.Item>
            <Form.Item
              name="isAtAll"
              validateTrigger={['onChange', 'onBlur']}
              label={l('pages.registerCenter.alert.instance.isAtAll')}>
              <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                      defaultChecked={vals.isAtAll}/>
            </Form.Item>
            {(!vals.isAtAll) &&
              <Form.Item
                name="users"
                label={l('pages.registerCenter.alert.instance.atUsers')}
                rules={[{required: true, message: l('pages.registerCenter.alert.instance.wechatAtUsersPleaseHolder'),}]}
              >
                <Input placeholder={l('pages.registerCenter.alert.instance.wechatAtUsersPleaseHolder')}/>
              </Form.Item>
            }
          </>
          :
          <>
            <Form.Item
              name="corpId"
              label={l('pages.registerCenter.alert.instance.corpId')}
              rules={[{required: true, message: l('pages.registerCenter.alert.instance.corpIdPleaseHolder')}]}
            >
              <Input placeholder={l('pages.registerCenter.alert.instance.corpIdPleaseHolder')}/>
            </Form.Item>
            <Form.Item
              name="secret"
              label={l('pages.registerCenter.alert.instance.secret')}
              rules={[{required: true, message: l('pages.registerCenter.alert.instance.secretPleaseHolder')}]}
            >
              <Input placeholder={l('pages.registerCenter.alert.instance.secretPleaseHolder')}/>
            </Form.Item>
            <Form.Item
              name="users"
              label={l('pages.registerCenter.alert.instance.user')}
              rules={[{required: true, message: l('pages.registerCenter.alert.instance.userPleaseHolder')}]}
            >
              <Input placeholder={l('pages.registerCenter.alert.instance.userPleaseHolder')}/>
            </Form.Item>
            <Form.Item
              name="agentId"
              label={l('pages.registerCenter.alert.instance.agentId')}
              rules={[{required: true, message:l('pages.registerCenter.alert.instance.agentIdPleaseHolder')}]}
            >
              <Input placeholder={l('pages.registerCenter.alert.instance.agentIdPleaseHolder')}/>
            </Form.Item>
          </>
        }
        <Form.Item
          name="msgtype"
          label={l('pages.registerCenter.alert.instance.msgtype')}
          rules={[{required: true, message: l('pages.registerCenter.alert.instance.msgtypePleaseHolder')}]}
        >
          <Radio.Group>
            <Radio value='markdown'>{l('pages.registerCenter.alert.instance.markdown')}</Radio>
            <Radio value='text'>{l('pages.registerCenter.alert.instance.text')}</Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}>
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={vals.enabled}/>
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
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? l('pages.registerCenter.alert.instance.modify') : l('pages.registerCenter.alert.instance.create')}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getJSONData(formVals as AlertInstanceTableListItem)}
        onValuesChange={onValuesChange}
      >
        {renderContent(getJSONData(formVals as AlertInstanceTableListItem))}
      </Form>
    </Modal>
  );
};

export default WeChatForm;
