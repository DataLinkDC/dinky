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
  ProForm, ProFormDigit,
  ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";
import {MODAL_FORM_STYLE} from "@/services/constants";

/**
 * props of AlertInstanceForm
 */
export type AlertInstanceFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  onTest: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
};


const WeChat: React.FC<AlertInstanceFormProps> = (props) => {

  /**
   * status of form
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertInstance>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.WECHAT,
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
   * send test message
   */
  const sendTestMsg = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleTest(buildJSONData(formVals, fieldsValue));
  };

  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals, fieldsValue));
    handleSubmit(buildJSONData(formVals, fieldsValue));

  };

  /**
   * render form
   * @param vals
   */
  const renderWeChatForm = (vals: any) => {
    return (
      <>
        <Divider>{l("rc.ai.wechat")}</Divider>
        <ProForm.Group>
          <ProFormText
            width="md"
            name="name"
            label={l("rc.ai.name")}
            rules={[{required: true, message: l("rc.ai.namePleaseHolder")}]}
            placeholder={l("rc.ai.namePleaseHolder")}
          />

          <ProFormRadio.Group
            name="msgtype"
            label={l("rc.ai.msgtype")}
            rules={[{required: true, message: l("rc.ai.msgtypePleaseHolder")}]}
          >
            <Radio.Group>
              <Radio value="markdown">{l("rc.ai.markdown")}</Radio>
              <Radio value="text">{l("rc.ai.text")}</Radio>
            </Radio.Group>
          </ProFormRadio.Group>

          <ProFormRadio.Group
            name="sendType"
            validateTrigger={["onChange", "onBlur"]}
            label={l("rc.ai.sendType")}
            rules={[{required: true, message: l("rc.ai.sendTypePleaseHolder")}]}
          >
            <Radio.Group>
              <Radio value="app">{l("rc.ai.sendType.app")}</Radio>
              <Radio value="wechat">{l("rc.ai.sendType.wechat")}</Radio>
            </Radio.Group>
          </ProFormRadio.Group>

          <ProFormSwitch
            name="enabled"
            label={l("global.table.isEnable")}
            checkedChildren={l("button.enable")}
            unCheckedChildren={l("button.disable")}
          />
          {(vals.sendType === "wechat") &&
            // if sendType is wechat render this switch group
            <ProFormSwitch
              name="isAtAll"
              label={l("rc.ai.isAtAll")}
              checkedChildren={l("button.enable")}
              unCheckedChildren={l("button.disable")}
            />
          }
        </ProForm.Group>

        <ProForm.Group>
        {(vals.sendType === "wechat") ?
          // if sendType is wechat
          <>
            <ProFormTextArea
              width="md"
              allowClear
              name="webhook"
              label={l("rc.ai.webhook")}
              rules={[{required: true, message: l("rc.ai.webhookPleaseHolder")}]}
              placeholder={l("rc.ai.webhookPleaseHolder")}
            />
            <ProFormText
              width="md"
              name="keyword"
              label={l("rc.ai.keyword")}
              placeholder={l("rc.ai.keywordPleaseHolder")}
            />

            {/* if not Enable At All this group do render */}
            <ProForm.Group>
              {!vals.isAtAll &&
                <>
                  <ProFormTextArea
                    width="md"
                    name="users"
                    label={l('rc.ai.atUsers')}
                    rules={[{required: true, message: l('rc.ai.atUsersPleaseHolder')}]}
                    placeholder={l("rc.ai.atUsersPleaseHolder")}
                  />
                </>
              }
            </ProForm.Group>
          </>
          :
          // if sendType is app
          <>
            <ProFormText
              width="sm"
              name="corpId"
              label={l("rc.ai.corpId")}
              rules={[{required: true, message: l("rc.ai.corpIdPleaseHolder")}]}
              placeholder={l("rc.ai.corpIdPleaseHolder")}
            />
            <ProFormText.Password
              width="sm"
              name="secret"
              label={l("rc.ai.secret")}
              rules={[{required: true, message: l("rc.ai.secretPleaseHolder")}]}
              placeholder={l("rc.ai.secretPleaseHolder")}
            />
            <ProFormDigit
              width="sm"
              name="agentId"
              label={l("rc.ai.agentId")}
              rules={[{required: true, message: l("rc.ai.agentIdPleaseHolder")}]}
              placeholder={l("rc.ai.agentIdPleaseHolder")}
            />
            <ProFormTextArea
              width="xl"
              name="users"
              label={l("rc.ai.user")}
              rules={[{required: true, message: l("rc.ai.userPleaseHolder")}]}
              placeholder={l("rc.ai.userPleaseHolder")}
            />
          </>
        }
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

  /**
   * render
   */
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
          {renderWeChatForm(getJSONData(formVals))}
        </ProForm>
      </ModalForm>
    </Space>
  );
};

export default WeChat;
