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
  ProForm, ProFormDigit, ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";
import {MODAL_FORM_STYLE} from "@/services/constants";
import {AlertInstanceFormProps} from "@/pages/RegCenter/Alert/AlertInstance/constans";


const DingTalk: React.FC<AlertInstanceFormProps> = (props) => {
  /**
   * status of form
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertInstance>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.DINGTALK,
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
   * on values change
   * @param change
   */
  const onValuesChange = (change: any) => {
    setFormVals({...formVals, ...change});
  };

  /**
   * send test msg to dingtalk
   */
  const sendTestForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleTest(buildJSONData(formVals, fieldsValue));
  };

  /**
   * submit form data
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleSubmit(buildJSONData(formVals, fieldsValue));
  };

  /**
   * render dingtalk form
   * @param vals
   */
  const renderDingTalkForm = (vals: any) => {
    return (
      <>
        <Divider>{l("rc.ai.dingTalk")}</Divider>
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
            name="keyword"
            label={l("rc.ai.keyword")}
            placeholder={l("rc.ai.keywordPleaseHolder")}
          />
          <ProFormTextArea
            width="md"
            allowClear
            name="webhook"
            label={l("rc.ai.webhook")}
            rules={[{required: true, message: l("rc.ai.webhookPleaseHolder")}]}
            placeholder={l("rc.ai.webhookPleaseHolder")}

          />
          <ProFormText.Password
            width="md"
            allowClear
            name="secret"
            label={l("rc.ai.secret")}
            placeholder={l("rc.ai.secretPleaseHolder")}
          />
        </ProForm.Group>

        {/* advanced columns */}
        <ProForm.Group>
          <ProFormRadio.Group
            name="msgtype"
            width={"xs"}
            label={l("rc.ai.msgtype")}
            rules={[{required: true, message: l("rc.ai.msgtypePleaseHolder")}]}
          >
            <Radio.Group>
              <Radio value="markdown">{l("rc.ai.markdown")}</Radio>
              <Radio value="text">{l("rc.ai.text")}</Radio>
            </Radio.Group>
          </ProFormRadio.Group>

          <ProFormSwitch
            width="xs"
            name="isEnableProxy"
            label={l("rc.ai.isEnableProxy")}
            checkedChildren={l("button.enable")}
            unCheckedChildren={l("button.disable")}
          />
          <ProFormSwitch
            width="xs"
            name="isAtAll"
            label={l("rc.ai.isAtAll")}
            checkedChildren={l("button.enable")}
            unCheckedChildren={l("button.disable")}
          />
          <ProFormSwitch
            width="xs"
            name="enabled"
            label={l("global.table.isEnable")}
            checkedChildren={l("button.enable")}
            unCheckedChildren={l("button.disable")}
          />
        </ProForm.Group>

        {/* if Enable Proxy this group do render */}
        <ProForm.Group>
          {vals.isEnableProxy && <>
            <ProFormText
              width="md"
              name="proxy"
              label={l("rc.ai.proxy")}
              rules={[{required: true, message: l("rc.ai.proxyPleaseHolder")}]}
              placeholder={l("rc.ai.proxyPleaseHolder")}
            />

            <ProFormDigit
              width="md"
              name="port"
              label={l("rc.ai.port")}
              rules={[{required: true, message: l("rc.ai.portPleaseHolder")}]}
              placeholder={l("rc.ai.portPleaseHolder")}
            />
            <ProFormText
              width="md"
              name="user"
              label={l("rc.ai.user")}
              rules={[{required: true, message: l("rc.ai.userPleaseHolder")}]}
              placeholder={l("rc.ai.userPleaseHolder")}
            />
            <ProFormText.Password
              width={"md"}
              name="password"
              label={l("rc.ai.password")}
              rules={[{required: true, message: l("rc.ai.passwordPleaseHolder")}]}
              placeholder={l("rc.ai.passwordPleaseHolder")}
            />
          </>
          }
        </ProForm.Group>

        {/* if not Enable At All this group do render */}
        <ProForm.Group>
          {!vals.isAtAll &&
            <>
              <ProFormTextArea
                width="md"
                name="atMobiles"
                label={l("rc.ai.atMobiles")}
                rules={[{required: true, message: l("rc.ai.atMobilesPleaseHolder")}]}
                placeholder={l("rc.ai.atMobilesPleaseHolder")}
              />
            </>
          }
        </ProForm.Group>
      </>
    );
  };

  const renderFooter = () => {
    return [
      <Button key={"AlertCancel"} onClick={() => handleModalVisible(false)}>{l("button.cancel")}</Button>,
      <Button key={"AlertTest"} type="primary" onClick={() => sendTestForm()}>{l("button.test")}</Button>,
      <Button key={"AlertFinish"} type="primary" onClick={() => submitForm()}>{l("button.finish")}</Button>,
    ];
  };

  return (
    <Space>
      <ModalForm
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
          {renderDingTalkForm(getJSONData(formVals))}
        </ProForm>
      </ModalForm>
    </Space>
  );
};

export default DingTalk;
