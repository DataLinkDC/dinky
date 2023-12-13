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

import { Button, Form } from 'antd';
import React, { useEffect, useState } from 'react';

import { FormContextValue } from '@/components/Context/FormContext';
import InstanceForm from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm';
import { NORMAL_MODAL_OPTIONS } from '@/services/constants';
import { Alert } from '@/types/RegCenter/data.d';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';

/**
 * update form props
 */
type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<Alert.AlertInstance>) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  onTest: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance>;
  loading: boolean;
};

const AlertTypeChoose: React.FC<UpdateFormProps> = (props) => {
  /**
   * extract props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleCancelVisible,
    onTest: handleTest,
    modalVisible,
    values,
    loading
  } = props;

  const [formValues, setFormValues] = useState<Partial<Alert.AlertInstance>>(values);

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
   * cancel choose
   */
  const handleCancel = () => {
    handleCancelVisible();
    formContext.resetForm();
  };

  /**
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    form.setFieldsValue(formValues);
  }, [modalVisible, formValues, form]);

  const testSend = async () => {
    const validateFields = await form.validateFields();
    handleTest({ ...formValues, ...validateFields });
  };

  const submit = async () => {
    const validateFields = await form.validateFields();
    handleSubmit({ ...formValues, ...validateFields });
    handleCancel();
  };

  const renderFooter = () => {
    return [
      <Button key={'AlertCancel'} onClick={handleCancel}>
        {l('button.cancel')}
      </Button>,
      <Button key={'AlertTest'} type='primary' loading={loading} onClick={testSend}>
        {l('button.test')}
      </Button>,
      <Button
        key={'AlertFinish'}
        type='primary'
        htmlType={'submit'}
        autoFocus
        loading={loading}
        onClick={submit}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  return (
    <>
      <ModalForm<Alert.AlertInstance>
        {...NORMAL_MODAL_OPTIONS}
        title={formValues?.id ? l('rc.ai.modify') : l('rc.ai.create')}
        open={modalVisible}
        form={form}
        initialValues={formValues}
        onValuesChange={(changedValues, allValues) =>
          setFormValues((prevState) => ({ ...prevState, ...allValues, ...changedValues }))
        }
        modalProps={{ onCancel: handleCancel, ...NORMAL_MODAL_OPTIONS }}
        submitter={{ render: () => [...renderFooter()] }}
        syncToInitialValues
      >
        <InstanceForm form={form} values={formValues} />
      </ModalForm>
    </>
  );
};

export default AlertTypeChoose;
