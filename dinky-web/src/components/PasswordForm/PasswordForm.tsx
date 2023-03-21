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

import { l } from '@/utils/intl';
import { Button, Form, Input, Modal } from 'antd';
import React, { useState } from 'react';

export type PasswordFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserBaseInfo.ChangePasswordParams>) => void;
  modalVisible: boolean;
  values: Partial<UserBaseInfo.ChangePasswordParams>;
};

const formLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 13 },
};

const PasswordForm: React.FC<PasswordFormProps> = (props) => {
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<UserBaseInfo.ChangePasswordParams>>({
    username: props.values.username,
  });

  const { onSubmit: handleSubmit, onCancel: handleModalVisible, modalVisible } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({ ...formVals, ...fieldsValue });
    handleSubmit({ ...formVals, ...fieldsValue });
  };

  const renderContent = () => {
    return (
      <>
        <Form.Item
          name="password"
          label={l('user.UserOldPassword')}
          hasFeedback
          rules={[{ required: true, message: l('user.UserEnterOldPassword') }]}
        >
          <Input.Password placeholder={l('user.UserEnterOldPassword')} />
        </Form.Item>
        <Form.Item
          name="newPassword"
          label={l('user.UserNewPassword')}
          hasFeedback
          rules={[{ required: true, message: l('user.UserEnterNewPassword') }]}
        >
          <Input.Password placeholder={l('user.UserEnterNewPassword')} />
        </Form.Item>
        <Form.Item
          name="newPasswordCheck"
          label={l('user.UserRepeatNewPassword')}
          hasFeedback
          dependencies={['newPassword']}
          rules={[
            {
              required: true,
              message: l('user.UserNewPasswordNotMatch'),
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('newPassword') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error(l('user.UserNewPasswordNotMatch')));
              },
            }),
          ]}
        >
          <Input.Password placeholder={l('user.UserEnterRepeatNewPassword')} />
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}> {l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>
          {l('button.finish')}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={'40%'}
      bodyStyle={{ padding: '32px 40px 48px' }}
      destroyOnClose
      title={l('button.changePassword')}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form {...formLayout} form={form} initialValues={formVals}>
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default PasswordForm;
