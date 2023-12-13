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

import {
  buildAlertInstanceSelect,
  buildFormData,
  getFormData
} from '@/pages/RegCenter/Alert/AlertGroup/function';
import { AlertStateType } from '@/pages/RegCenter/Alert/AlertInstance/model';
import { MODAL_FORM_STYLE, SWITCH_OPTIONS } from '@/services/constants';
import { Alert } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import {
  ModalForm,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { connect } from '@umijs/max';
import { Button, Form } from 'antd';
import React, { useState } from 'react';

/**
 * alert group props
 */
type AlertGroupFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<Alert.AlertGroup>) => void;
  modalVisible: boolean;
  values: Partial<Alert.AlertGroup>;
};

const AlertGroupForm: React.FC<AlertGroupFormProps & connect> = (props) => {
  /**
   * extract props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    alertInstance,
    values
  } = props;
  /**
   * state
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<Alert.AlertGroup>>({ ...values });

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
          name='name'
          label={l('rc.ag.name')}
          rules={[{ required: true, message: l('rc.ag.inputName') }]}
          placeholder={l('rc.ag.inputName')}
        />

        <ProFormSelect
          name='alertInstanceIds'
          label={l('rc.ag.alertInstanceIds')}
          rules={[{ required: true, message: l('rc.ag.chooseAlertInstanceIds') }]}
          mode='multiple'
          options={buildAlertInstanceSelect(alertInstance ?? [])}
        />

        <ProFormTextArea
          name='note'
          label={l('global.table.note')}
          placeholder={l('global.table.notePlaceholder')}
        />

        <ProFormSwitch name='enabled' label={l('global.table.isEnable')} {...SWITCH_OPTIONS()} />
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
      <Button
        key={'GroupFinish'}
        type='primary'
        htmlType={'submit'}
        autoFocus
        onClick={() => submitForm()}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  /**
   * render
   */
  return (
    <ModalForm<Alert.AlertGroup>
      title={formVals.id ? l('rc.ag.modify') : l('rc.ag.create')}
      open={modalVisible}
      {...MODAL_FORM_STYLE}
      form={form}
      initialValues={getFormData(formVals)}
      submitter={{ render: () => [...renderFooter()] }}
      modalProps={{
        destroyOnClose: true,
        onCancel: () => handleModalVisible(false)
      }}
    >
      {renderAlertGroupForm()}
    </ModalForm>
  );
};

export default connect(({ Alert }: { Alert: AlertStateType }) => ({
  alertInstance: Alert.instance
}))(AlertGroupForm);
