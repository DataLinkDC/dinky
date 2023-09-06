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

import { buildFormData, getFormData } from '@/pages/RegCenter/Alert/AlertGroup/function';
import { l } from '@/utils/intl';
import {
  ModalForm,
  ProForm,
  ProFormText,
} from '@ant-design/pro-components';
import { Button, Form } from 'antd';
import React, { useState } from 'react';
import {Alert} from "@/types/RegCenter/data";
import {MODAL_FORM_STYLE} from "@/services/constants";
import CodeEdit from "@/components/CustomEditor/CodeEdit";

/**
 * alert group props
 */
type AlertTemplateFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Alert.AlertTemplate) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertTemplate>;
};

/**
 * code edit props
 */
const CodeEditProps = {
  height: '40vh',
  width: '100vh',
  lineNumbers: 'on',
  language: 'markdown',
  showFloatButton: false
};

const AlertTemplateForm: React.FC<AlertTemplateFormProps> = (props) => {
  /**
   * extract props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    values
  } = props;
  /**
   * state
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertTemplate>>({ ...values });
  const [codeValue, setCodeValue] = useState<string>(values.templateContent || '');


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
  const renderAlertTemplateForm = () => {
    return (
      <>
        <ProFormText
          name='name'
          label={l('rc.template.name')}
          rules={[{ required: true, message: l('rc.template.namePlaceholder') }]}
          placeholder={l('rc.template.name')}
        />

        <ProForm.Item
          name='templateContent'
          label={l('rc.template.templateCodeLabel', '', {
            language: "markdown"
          })}
          rules={[
            {
              required: true,
              message: l('rc.template.templateCodePlaceholder')
            }
          ]}
        >
          <CodeEdit
            {...CodeEditProps}
            code={codeValue}
            onChange={(value) => setCodeValue(value ?? '')}
            language={"markdown"}
          />
        </ProForm.Item>

      </>
    );
  };

  /**
   * render footer button
   */
  const renderFooter = () => {
    return [
      <Button key={'GroupCancel'} onClick={() => handleModalVisible(false)}>
        {l('button.cancel')}
      </Button>,
      <Button key={'GroupFinish'} type='primary' onClick={() => submitForm()}>
        {l('button.finish')}
      </Button>
    ];
  };

  /**
   * render
   */
  return (
    <ModalForm<Alert.AlertTemplate>
      title={formVals.id ? l('rc.alert.template.modify') : l('rc.alert.template.create')}
      open={modalVisible}
      {...MODAL_FORM_STYLE}
      submitter={{ render: () => [...renderFooter()] }}
    >
      <ProForm form={form} initialValues={getFormData(formVals)} submitter={false}>
        {renderAlertTemplateForm()}
      </ProForm>
    </ModalForm>
  );
};

export default AlertTemplateForm;
