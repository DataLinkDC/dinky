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
import GlobalVarForm from '@/pages/RegCenter/GlobalVar/components/GlobalVarModal/GlobalVarForm';
import { NORMAL_MODAL_OPTIONS } from '@/services/constants';
import { GlobalVar } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { Form, Modal } from 'antd';
import React, { useEffect } from 'react';

type GlobalVarModalProps = {
  onSubmit: (values: Partial<GlobalVar>) => void;
  onCancel: () => void;
  modalVisible: boolean;
  values: Partial<GlobalVar>;
};
const GlobalVarModal: React.FC<GlobalVarModalProps> = (props) => {
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
  const { onSubmit: handleSubmit, onCancel: handleModalVisible, modalVisible, values } = props;

  /**
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    form.setFieldsValue(values);
  }, [modalVisible, values, form]);

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
    handleSubmit({ ...values, ...fieldsValue });
    handleCancel();
  };

  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={values.id ? l('rc.gv.modify') : l('rc.gv.create')}
      open={modalVisible}
      okButtonProps={{ htmlType: 'submit', autoFocus: true }}
      onOk={() => submitForm()}
      onCancel={() => handleModalVisible()}
    >
      <GlobalVarForm form={form} values={values} />
    </Modal>
  );
};

export default GlobalVarModal;
