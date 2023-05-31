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


import React, {useEffect} from 'react';
import {Button, Form} from 'antd';

import {l} from '@/utils/intl';
import {Alert} from '@/types/RegCenter/data.d';
import {NORMAL_MODAL_OPTIONS} from '@/services/constants';
import {ModalForm} from '@ant-design/pro-components';
import {FormContextValue} from '@/components/Context/FormContext';
import InstanceForm from '@/pages/RegCenter/Alert/AlertInstance/components/AlertTypeChoose/InstanceForm';
import {buildJSONData, getJSONData} from '@/pages/RegCenter/Alert/AlertInstance/function';

/**
 * update form props
 */
type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<Alert.AlertInstance>) => void;
  onSubmit: (values: Partial<Alert.AlertInstance>) => void;
  onTest: (values: Partial<Alert.AlertInstance>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertInstance> ;
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
    values
  } = props;


  /**
   * init form
   */
  const [form] = Form.useForm();


  /**
   * init form context
   */
  const formContext = React.useMemo<FormContextValue>(() => ({
    resetForm: () => form.resetFields(), // 定义 resetForm 方法
  }), [form]);

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
    form.setFieldsValue(getJSONData(values));
  }, [modalVisible, values, form]);

  const testSend = async () => {
    const validateFields = await form.validateFields();
    const data = buildJSONData(values, validateFields);
    await handleTest(data);
  };

  const submit = async () => {
    const validateFields = await form.validateFields();
    const data = buildJSONData(values, validateFields);
    await handleSubmit(data);
    handleCancel();
  };


  const renderFooter = () => {
    return [
      <Button key={'AlertCancel'} onClick={handleCancel}>{l('button.cancel')}</Button>,
      <Button key={'AlertTest'} type="primary" onClick={testSend}>{l('button.test')}</Button>,
      <Button key={'AlertFinish'} type="primary" onClick={submit}>{l('button.finish')}</Button>,
    ];
  };

  return <>
    <ModalForm
      {...NORMAL_MODAL_OPTIONS}
      title={values?.id ? l('rc.ai.modify') : l('rc.ai.create')}
      open={modalVisible}
      form={form}
      modalProps={{onCancel: handleCancel,...NORMAL_MODAL_OPTIONS}}
      submitter={{render: () => [...renderFooter()]}}
    >
      <InstanceForm form={form} values={values}/>
    </ModalForm>
  </>;
};

export default AlertTypeChoose;
