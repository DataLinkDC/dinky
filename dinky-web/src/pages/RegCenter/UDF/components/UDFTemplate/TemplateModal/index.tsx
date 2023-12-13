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
import TemplateProFrom from '@/pages/RegCenter/UDF/components/UDFTemplate/TemplateModal/TemplateProFrom';
import { GitProject, UDFTemplate } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { Button, Form } from 'antd';
import React, { useEffect } from 'react';

type TemplateModalProps = {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: Partial<UDFTemplate>) => void;
  values: Partial<UDFTemplate>;
};

const TemplateModal: React.FC<TemplateModalProps> = (props) => {
  const { visible, onCancel, onSubmit, values } = props;
  const [submitting, setSubmitting] = React.useState<boolean>(false);

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
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    form.setFieldsValue(values);
  }, [visible, values, form]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    onCancel();
    setSubmitting(false);
    formContext.resetForm();
  };
  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setSubmitting(true);
    await onSubmit({ ...values, ...fieldsValue });
    onCancel();
  };

  /**
   * render footer
   * @returns {[JSX.Element, JSX.Element]}
   */
  const renderFooter = () => {
    return [
      <Button key={'cancel'} onClick={() => handleCancel()}>
        {l('button.cancel')}
      </Button>,
      <Button
        key={'finish'}
        loading={submitting}
        type='primary'
        htmlType={'submit'}
        autoFocus
        onClick={() => submitForm()}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  return (
    <>
      <ModalForm<GitProject>
        title={values.id ? l('rc.template.modify') : l('rc.template.create')}
        open={visible}
        form={form}
        submitter={{ render: () => [...renderFooter()] }}
        initialValues={values}
      >
        <TemplateProFrom form={form} values={values} />
      </ModalForm>
    </>
  );
};

export default TemplateModal;
