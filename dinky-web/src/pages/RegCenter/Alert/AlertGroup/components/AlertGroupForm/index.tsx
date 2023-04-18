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


import React, {useState} from "react";
import {Button, Form, Tag} from "antd";
import {l} from "@/utils/intl";
import {Alert} from "@/types/RegCenter/data";
import {buildFormData, getFormData} from "@/pages/RegCenter/Alert/AlertGroup/function";
import {connect} from "@umijs/max";
import {
  ModalForm,
  ProForm,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";
import {MODAL_FORM_STYLE, SWITCH_OPTIONS} from "@/services/constants";
import {AlertStateType} from "@/pages/RegCenter/Alert/AlertInstance/model";

/**
 * alert group props
 */
type AlertGroupFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<Alert.AlertGroup>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertGroup>;
  instance: Alert.AlertInstance[];
};


const AlertGroupForm: React.FC<AlertGroupFormProps> = (props) => {
  /**
   * state
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertGroup>>({
    id: props.values.id,
    name: props.values.name,
    alertInstanceIds: props.values.alertInstanceIds,
    note: props.values.note,
    enabled: props.values.enabled ? props.values.enabled : true,
  });

  /**
   * extract props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    instance,
  } = props;

  /**
   * build alert instance select options
   */
  const buildAlertInstanceSelect = () => {
    const itemList = [];
    for (const item of instance) {
      const tag = (<><Tag color="processing">{item.type}</Tag>{item.name}</>);
      itemList.push({
          label: tag,
          value: item.id.toString(),
        }
      );
    }
    return itemList;
  };


  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildFormData(formVals, fieldsValue));
    handleSubmit(buildFormData(formVals, fieldsValue));
  };

  /**
   * render alert group form
   */
  const renderAlertGroupForm = () => {
    return (
      <>
        <ProFormText
          name="name"
          label={l("rc.ag.name")}
          rules={[{required: true, message: l("rc.ag.inputName")}]}
          placeholder={l("rc.ag.inputName")}
        />

        <ProFormSelect
          name="alertInstanceIds"
          label={l("rc.ag.alertInstanceIds")}
          rules={[{required: true, message: l("rc.ag.chooseAlertInstanceIds")}]}
          mode="multiple"
          options={buildAlertInstanceSelect()}
        />

        <ProFormTextArea
          name="note"
          label={l("global.table.note")}
          placeholder={l("global.table.notePlaceholder")}
        />

        <ProFormSwitch
          name="enabled"
          label={l("global.table.isEnable")}
          {...SWITCH_OPTIONS()}
        />
      </>
    );
  };


  /**
   * render footer button
   */
  const renderFooter = () => {
    return [
      <Button key={"GroupCancel"} onClick={() => handleModalVisible(false)}>{l("button.cancel")}</Button>,
      <Button key={"GroupFinish"} type="primary" onClick={() => submitForm()}>{l("button.finish")}</Button>,
    ];
  };

  /**
   * render
   */
  return (
    <ModalForm<Alert.AlertGroup>
      title={formVals.id ? l("rc.ag.modify") : l("rc.ag.create")}
      open={modalVisible}
      {...MODAL_FORM_STYLE}
      submitter={{render: () => [...renderFooter()]}}
    >
      <ProForm
        form={form}
        initialValues={getFormData(formVals)}
        submitter={false}
      >
        {renderAlertGroupForm()}
      </ProForm>
    </ModalForm>
  );
};

export default connect(({Alert}: { Alert: AlertStateType }) => ({
  instance: Alert.instance,
}))(AlertGroupForm);
