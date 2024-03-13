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

import { FormContextValue } from '@/components/Context/FormContext';
import { ResourceInfo } from '@/types/RegCenter/data.d';
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Form } from 'antd';
import React, { useEffect } from 'react';

type ResourceModalProps = {
  title: string;
  visible: boolean;
  onClose: () => void;
  onOk: (value: Partial<ResourceInfo>) => void;
  formValues: Partial<ResourceInfo>;
};

const ResourceModal: React.FC<ResourceModalProps> = (props) => {
  /**
   * init form
   */
  const [form] = Form.useForm();
  /**
   * init form context
   */
  const formContext = React.useMemo<FormContextValue>(
    () => ({
      resetForm: () => form.resetFields() // 定义 resetForm 方法
    }),
    [form]
  );

  /**
   * init props
   */
  const { title, onOk: handleSubmit, onClose: handleModalVisible, visible, formValues } = props;

  /**
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    form.setFieldsValue(formValues);
  }, [visible, formValues, form]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    handleModalVisible();
    formContext.resetForm();
  };
  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    handleSubmit({ ...formValues, ...fieldsValue });
    handleCancel();
  };

  return (
    <>
      <ModalForm<ResourceInfo>
        title={title}
        modalProps={{
          destroyOnClose: true,
          onCancel: handleModalVisible,
          okButtonProps: { htmlType: 'submit', autoFocus: true }
        }}
        onFinish={submitForm}
        form={form}
        open={visible}
      >
        <ProFormText colProps={{ span: 24 }} required name='fileName' label='名称' />
        <ProFormTextArea colProps={{ span: 24 }} name='description' label='描述' />
      </ModalForm>
    </>
  );
};

export default ResourceModal;
