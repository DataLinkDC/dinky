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
import {Button, Divider, Form, Input, Modal, Radio, Switch} from "antd";
import {l} from "@/utils/intl";
import {Alert, ALERT_TYPE} from "@/types/RegCenter/data.d";
import {buildJSONData, getJSONData} from "@/pages/RegCenter/Alert/AlertInstance/function";

export type AlertInstanceFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  onTest: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const Email: React.FC<AlertInstanceFormProps> = (props) => {
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertInstance>>({
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
        <Divider>{l("rc.ai.email")}</Divider>
        <Form.Item
          name="name"
          label={l("rc.ai.name")}
          rules={[{required: true, message: l("rc.ai.namePleaseHolder")}]}
        >
          <Input placeholder={l("rc.ai.namePleaseHolder")}/>
        </Form.Item>
        <Form.Item
          name="receivers"
          label={l("rc.ai.receivers")}
          rules={[{required: true, message: l("rc.ai.receiversPleaseHolder")}]}
        >
          <Input.TextArea placeholder={l("rc.ai.receiversPleaseHolder")} allowClear
                          autoSize={{minRows: 1, maxRows: 5}}/>
        </Form.Item>
        <Form.Item
          name="receiverCcs"
          label={l("rc.ai.receiverCcs")}
        >
          <Input.TextArea placeholder={l("rc.ai.receiverCcsPleaseHolder")} allowClear
                          autoSize={{minRows: 1, maxRows: 5}}/>
        </Form.Item>
        <Form.Item
          name="serverHost"
          label={l("rc.ai.serverHost")}
          rules={[{required: true, message: l("rc.ai.serverHostPleaseHolder")}]}
        >
          <Input placeholder={l("rc.ai.serverHostPleaseHolder")}/>
        </Form.Item>
        <Form.Item
          name="serverPort"
          label={l("rc.ai.receivers")}
          rules={[{required: true, message: l("rc.ai.serverPortPleaseHolder")}]}
        >
          <Input placeholder={l("rc.ai.serverPortPleaseHolder")}/>
        </Form.Item>
        <Form.Item
          name="sender"
          label={l("rc.ai.sender")}
          rules={[{required: true, message: l("rc.ai.senderPleaseHolder")}]}
        >
          <Input placeholder={l("rc.ai.senderPleaseHolder")}/>
        </Form.Item>
        <Form.Item
          name="enableSmtpAuth"
          label={l("rc.ai.enableSmtpAuth")}
        >
          <Switch checkedChildren={l("button.enable")} unCheckedChildren={l("button.disable")}
                  defaultChecked={vals.enableSmtpAuth}/>
        </Form.Item>
        {vals.enableSmtpAuth &&
          <>
            <Form.Item
              name="User"
              label={l("rc.ai.emailUser")}
              rules={[{required: true, message: l("rc.ai.emailUserPleaseHolder")}]}
            >
              <Input allowClear placeholder={l("rc.ai.emailUserPleaseHolder")}/>
            </Form.Item>
            <Form.Item
              name="Password"
              label={l("rc.ai.emailPassword")}
              rules={[{required: true, message: l("rc.ai.emailPasswordPleaseHolder")}]}
            >
              <Input.Password allowClear placeholder={l("rc.ai.emailPasswordPleaseHolder")}/>
            </Form.Item>
          </>}
        <Form.Item
          name="starttlsEnable"
          label={l("rc.ai.starttlsEnable")}
        >
          <Switch checkedChildren={l("button.enable")} unCheckedChildren={l("button.disable")}
                  defaultChecked={vals.starttlsEnable}/>
        </Form.Item>
        <Form.Item
          name="sslEnable"
          label={l("rc.ai.sslEnable")}
        >
          <Switch checkedChildren={l("button.enable")} unCheckedChildren={l("button.disable")}
                  defaultChecked={vals.sslEnable}/>
        </Form.Item>
        {(vals.sslEnable) &&
          <Form.Item
            name="smtpSslTrust"
            label={l("rc.ai.smtpSslTrust")}
            rules={[{required: true, message: l("rc.ai.smtpSslTrustPleaseHolder")}]}
          >
            <Input placeholder={l("rc.ai.smtpSslTrustPleaseHolder")}/>
          </Form.Item>
        }
        <Form.Item
          name="enabled"
          label={l("global.table.isEnable")}>
          <Switch checkedChildren={l("button.enable")} unCheckedChildren={l("button.disable")}
                  defaultChecked={vals.enabled}/>
        </Form.Item>
        <Form.Item
          name="msgtype"
          label={l("rc.ai.msgtype")}
          rules={[{required: true, message: l("rc.ai.msgtypePleaseHolder")}]}
        >
          <Radio.Group>
            <Radio value="text">{l("rc.ai.text")}</Radio>
            <Radio value="table">{l("rc.ai.table")}</Radio>
            <Radio value="attachment">{l("rc.ai.attachment")}</Radio>
            <Radio value="table attachment">{l("rc.ai.tableAttachment")}</Radio>
          </Radio.Group>
        </Form.Item>
        {(vals.msgtype === "attachment" || vals.msgtype === "table attachment") &&
          <>
            <Form.Item
              name="xls.file.path"
              label={l("rc.ai.xls.file.path")}
            >
              <Input.TextArea placeholder={l("rc.ai.xls.file.pathPleaseHolder")} allowClear
                              autoSize={{minRows: 1, maxRows: 5}}/>
            </Form.Item>

          </>}

      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l("button.cancel")}</Button>
        <Button type="primary" onClick={() => sendTestForm()}>{l("button.test")}</Button>
        <Button type="primary" onClick={() => submitForm()}>{l("button.finish")}</Button>

      </>
    );
  };


  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: "32px 40px 48px", height: "600px", overflowY: "auto"}}
      destroyOnClose
      title={formVals.id ? l("rc.ai.modify") : l("rc.ai.create")}
      open={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getJSONData(formVals)}
        onValuesChange={onValuesChange}
      >
        {renderContent(getJSONData(formVals))}
      </Form>
    </Modal>
  );
};

export default Email;
