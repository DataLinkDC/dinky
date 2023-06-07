/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {Cluster} from '@/types/RegCenter/data';
import {Button, Form} from 'antd';
import {MODAL_FORM_OPTIONS, NORMAL_MODAL_OPTIONS} from '@/services/constants';
import React, {useEffect} from 'react';
import {ModalForm} from '@ant-design/pro-components';
import {l} from '@/utils/intl';
import {FormContextValue} from '@/components/Context/FormContext';
import InstanceForm from './InstanceForm';

type InstanceModalProps = {
  visible: boolean;
  onClose: () => void;
  value: Partial<Cluster.Instance>;
  onSubmit: (values: Partial<Cluster.Instance>) => void;
}
const InstanceModal: React.FC<InstanceModalProps> = (props) => {

  const {visible, onClose, onSubmit, value} = props;

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

  const [submitting, setSubmitting] = React.useState<boolean>(false);


  /**
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    form.setFieldsValue(value);
  }, [visible, value, form]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    onClose();
    setSubmitting(false);
    formContext.resetForm();
  };
  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setSubmitting(true);
    await onSubmit({...value, ...fieldsValue});
    handleCancel();
  };


  /**
   * render footer
   * @returns {[JSX.Element, JSX.Element]}
   */
  const renderFooter = () => {
    return [
      <Button key={'cancel'} onClick={() => handleCancel()}>{l('button.cancel')}</Button>,
      <Button key={'finish'} loading={submitting} type="primary"
              onClick={() => submitForm()}>{l('button.finish')}</Button>,
    ];
  };


  return <>
    <ModalForm
      {...MODAL_FORM_OPTIONS}
      open={visible}
      modalProps={{onCancel: handleCancel}}
      title={value.id ? l('rc.ci.modify') : l('rc.ci.create')}
      submitter={{render: () => [...renderFooter()]}}
      initialValues={value}
    >
      <InstanceForm form={form} value={value}/>
    </ModalForm>
  </>;
};

export default InstanceModal;
