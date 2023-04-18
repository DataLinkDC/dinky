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
import {Button, Divider, Form, Radio, Space} from "antd";
import {l} from "@/utils/intl";
import {Alert, ALERT_TYPE} from "@/types/RegCenter/data.d";
import {buildJSONData, getJSONData} from "@/pages/RegCenter/Alert/AlertInstance/function";
import {
  ModalForm,
  ProForm,
  ProFormDigit, ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";
import {MODAL_FORM_STYLE, SWITCH_OPTIONS} from "@/services/constants";
import {AlertInstanceFormProps} from "@/pages/RegCenter/Alert/AlertInstance/constans";


const Email: React.FC<AlertInstanceFormProps> = (props) => {
  /**
   * state
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertInstance>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.EMAIL,
    params: props.values?.params,
    enabled: props.values?.enabled,
  });

  /**
   * extract props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    onTest: handleTest,
    modalVisible,
  } = props;

  /**
   * values change callback
   * @param change
   */
  const onValuesChange = (change: any) => {
    setFormVals({...formVals, ...change});
  };

  /**
   * send test email callback
   */
  const sendTestMsg = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleTest(buildJSONData(formVals, fieldsValue));
  };

  /**
   * submit form callback
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleSubmit(buildJSONData(formVals, fieldsValue));
  };


  /**
   * render email content
   * @param vals
   */
  const renderEmailForm = (vals: any) => {
    return (
      <>
        <Divider>{l("rc.ai.email")}</Divider>
        {/* base columns */}
        <ProForm.Group>
          <ProFormText
            width="md"
            name="name"
            label={l("rc.ai.name")}
            rules={[{required: true, message: l("rc.ai.namePleaseHolder")}]}
            placeholder={l("rc.ai.namePleaseHolder")}
          />

          <ProFormText
            width="md"
            name="serverHost"
            label={l("rc.ai.serverHost")}
            rules={[{required: true, message: l("rc.ai.serverHostPleaseHolder")}]}
            placeholder={l("rc.ai.serverHostPleaseHolder")}
          />

          <ProFormDigit
            width="md"
            name="serverPort"
            label={l("rc.ai.serverPort")}
            rules={[{required: true, message: l("rc.ai.serverPortPleaseHolder")}]}
            placeholder={l("rc.ai.serverPortPleaseHolder")}
          />
          <ProFormText
            width="md"
            name="sender"
            label={l("rc.ai.sender")}
            rules={[{required: true, message: l("rc.ai.senderPleaseHolder")}]}
            placeholder={l("rc.ai.senderPleaseHolder")}
          />

          <ProFormTextArea
            width="md"
            name="receivers"
            label={l("rc.ai.receivers")}
            rules={[{required: true, message: l("rc.ai.receiversPleaseHolder")}]}
            placeholder={l("rc.ai.receiversPleaseHolder")}
          />
          <ProFormTextArea
            width="md"
            name="receiverCcs"
            label={l("rc.ai.receiverCcs")}
            placeholder={l("rc.ai.receiverCcsPleaseHolder")}
          />


        </ProForm.Group>

        {/* switch */}
        <ProForm.Group>
          <ProFormSwitch
            width="xs"
            name="enableSmtpAuth"
            label={l("rc.ai.enableSmtpAuth")}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            width="xs"
            name="starttlsEnable"
            label={l("rc.ai.starttlsEnable")}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            width="xs"
            name="sslEnable"
            label={l("rc.ai.sslEnable")}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            width="xs"
            name="enabled"
            label={l("global.table.isEnable")}
            {...SWITCH_OPTIONS()}
          />
          {/* ssl  */}
          {(vals.sslEnable) &&
            <ProFormText
              name="smtpSslTrust"
              width={"sm"}
              label={l("rc.ai.smtpSslTrust")}
              rules={[{required: true, message: l("rc.ai.smtpSslTrustPleaseHolder")}]}
              placeholder={l("rc.ai.smtpSslTrustPleaseHolder")}
            />
          }

          {/* msgtype */}
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
          {/* if choose attachment || table attachment , this input is render */}
          {(vals.msgtype === "attachment" || vals.msgtype === "table attachment") &&
            <ProFormText
              name="xls.file.path"
              width={"md"}
              label={l("rc.ai.xls.file.path")}
              placeholder={l("rc.ai.xls.file.pathPleaseHolder")} allowClear
            />}

        </ProForm.Group>

        {/* proxy */}
        <ProForm.Group>
          {vals.enableSmtpAuth &&
            <>
              <ProFormText
                name="User"
                width={"md"}
                label={l("rc.ai.emailUser")}
                rules={[{required: true, message: l("rc.ai.emailUserPleaseHolder")}]}
                allowClear placeholder={l("rc.ai.emailUserPleaseHolder")}
              />
              <ProFormText.Password
                name="Password"
                width={"md"}
                label={l("rc.ai.emailPassword")}
                rules={[{required: true, message: l("rc.ai.emailPasswordPleaseHolder")}]}
                allowClear placeholder={l("rc.ai.emailPasswordPleaseHolder")}
              />
            </>}
        </ProForm.Group>
      </>
    );
  };


  /**
   * render footer button
   */
  const renderFooter = () => {
    return [
      <Button key={"AlertCancel"} onClick={() => handleModalVisible(false)}>{l("button.cancel")}</Button>,
      <Button key={"AlertTest"} type="primary" onClick={() => sendTestMsg()}>{l("button.test")}</Button>,
      <Button key={"AlertFinish"} type="primary" onClick={() => submitForm()}>{l("button.finish")}</Button>,
    ];
  };


  return (
    <Space>
      <ModalForm<Alert.AlertInstance>
        {...MODAL_FORM_STYLE}
        title={formVals.id ? l("rc.ai.modify") : l("rc.ai.create")}
        open={modalVisible}
        submitter={{render: () => [...renderFooter()]}}
      >
        <ProForm
          form={form}
          initialValues={getJSONData(formVals)}
          onValuesChange={onValuesChange}
          submitter={false}
        >
          {renderEmailForm(getJSONData(formVals))}
        </ProForm>
      </ModalForm>
    </Space>
  );
};

export default Email;
