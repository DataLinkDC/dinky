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

const EmailForm: React.FC<AlertInstanceFormProps> = (props) => {

  const international = useIntl();
  const l = (key: string, defaultMsg?: string) => international.formatMessage({id: key, defaultMessage: defaultMsg})

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<AlertInstanceTableListItem>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.EMAIL,
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
        <Divider>邮箱配置</Divider>
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入邮箱配置名称！'}]}
        >
          <Input placeholder="请输入邮件配置名称"/>
        </Form.Item>
        <Form.Item
          name="receivers"
          label="收件人"
          rules={[{required: true, message: '请输入收件人邮箱！多个英文逗号隔开'}]}
        >
          <Input.TextArea placeholder="请输入收件人邮箱！多个英文逗号隔开!" allowClear
                          autoSize={{minRows: 1, maxRows: 5}}/>
        </Form.Item>
        <Form.Item
          name="receiverCcs"
          label="抄送人"
        >
          <Input.TextArea placeholder="请输入抄送人邮箱！多个英文逗号隔开!" allowClear
                          autoSize={{minRows: 1, maxRows: 5}}/>
        </Form.Item>
        <Form.Item
          name="serverHost"
          label="邮件服务器Host"
          rules={[{required: true, message: '请输入收件人邮箱！多个英文逗号隔开'}]}
        >
          <Input placeholder="请输入邮件服务器Host"/>
        </Form.Item>
        <Form.Item
          name="serverPort"
          label="邮件服务器Port"
          rules={[{required: true, message: '请输入邮件服务器Port！'}]}
        >
          <Input placeholder="请输入邮件服务器Port"/>
        </Form.Item>
        <Form.Item
          name="sender"
          label="发送者sender昵称"
          rules={[{required: true, message: '请输入发送者sender昵称'}]}
        >
          <Input placeholder="请输入邮件服务器发送者sender"/>
        </Form.Item>
        <Form.Item
          name="enableSmtpAuth"
          label="是否开启邮箱验证"
        >
          <Switch checkedChildren="是" unCheckedChildren="否"
                  defaultChecked={vals.enableSmtpAuth}/>
        </Form.Item>
        {vals.enableSmtpAuth &&
          <>
            <Form.Item
              name="User"
              label="邮箱用户名"
              rules={[{required: true, message: '请输入邮箱用户名！'}]}
            >
              <Input allowClear placeholder="请输入邮箱用户名"/>
            </Form.Item>
            <Form.Item
              name="Password"
              label="邮箱密码"
              rules={[{required: true, message: '请输入邮箱密码/授权码！'}]}
            >
              <Input.Password allowClear placeholder="请输入邮箱密码! 注意:密码为授权码"/>
            </Form.Item>
          </>}
        <Form.Item
          name="starttlsEnable"
          label="是否开启tls证书验证"
        >
          <Switch checkedChildren="是" unCheckedChildren="否"
                  defaultChecked={vals.starttlsEnable}/>
        </Form.Item>
        <Form.Item
          name="sslEnable"
          label="是否开启SSL验证"
        >
          <Switch checkedChildren="是" unCheckedChildren="否"
                  defaultChecked={vals.sslEnable}/>
        </Form.Item>
        {(vals.sslEnable) &&
          <Form.Item
            name="smtpSslTrust"
            label="受信任域"
            rules={[{required: true, message: '你已经开启tls证书验证! 此项必填！'}]}
          >
            <Input placeholder="请输入受信任域"/>
          </Form.Item>
        }
        <Form.Item
          name="enabled"
          label="是否启用">
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={vals.enabled}/>
        </Form.Item>
        <Form.Item
          name="msgtype"
          label="展示方式"
          rules={[{required: true, message: '请选择展示方式！'}]}
        >
          <Radio.Group>
            <Radio value='text'>文本</Radio>
            <Radio value='table'>表格</Radio>
            <Radio value='attachment'>附件</Radio>
            <Radio value='table attachment'>表格附件</Radio>
          </Radio.Group>
        </Form.Item>
        {(vals.msgtype === "attachment" || vals.msgtype === "table attachment") &&
          <>
            <Form.Item
              name="xls.file.path"
              label="XLS存放目录"
            >
              <Input allowClear placeholder="请输入XLS存放目录! 默认为 /tmp/xls"/>
            </Form.Item>

          </>}

      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>取消</Button>
        <Button type="primary" onClick={() => sendTestForm()}>测试</Button>
        <Button type="primary" onClick={() => submitForm()}>
          完成
        </Button>
      </>
    );
  };


  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? "维护报警实例配置" : "创建报警实例配置"}
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

export default EmailForm;
