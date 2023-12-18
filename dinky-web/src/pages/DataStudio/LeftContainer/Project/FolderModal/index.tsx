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
import FolderForm from '@/pages/DataStudio/LeftContainer/Project/FolderModal/FolderForm';
import { Catalogue } from '@/types/Studio/data';
import { ModalForm } from '@ant-design/pro-components';
import { Form } from 'antd';
import React, { useEffect } from 'react';

type JobModalProps = {
  onCancel: () => void;
  onSubmit: (values: Catalogue) => void;
  modalVisible: boolean;
  title: React.ReactNode;
  values: Partial<Catalogue>;
};
const FolderModal: React.FC<JobModalProps> = (props) => {
  const { onCancel, onSubmit, modalVisible, title, values } = props;

  const [form] = Form.useForm<Catalogue>();
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
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    if (modalVisible) form.resetFields();
    form.setFieldsValue(values);
  }, [open, values, form]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    onCancel();
    formContext.resetForm();
  };

  /**
   * submit form
   */
  const submitForm = async (formData: Catalogue) => {
    await form.validateFields();
    const newValue = form.getFieldsValue();
    onSubmit({ ...values, name: newValue.name } as Catalogue);
  };

  return (
    <>
      <ModalForm<Catalogue>
        title={title}
        form={form}
        width={'30%'}
        initialValues={values}
        open={modalVisible}
        layout={'horizontal'}
        autoFocusFirstInput
        modalProps={{
          destroyOnClose: true,
          maskClosable: false,
          okButtonProps: {
            htmlType: 'submit',
            autoFocus: true
          },
          onCancel: handleCancel
        }}
        onFinish={async (values) => submitForm(values)}
      >
        <FolderForm />
      </ModalForm>
    </>
  );
};

export default FolderModal;
