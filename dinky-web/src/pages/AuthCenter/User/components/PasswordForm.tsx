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


import React, { useState } from 'react';
import { Form, Modal } from 'antd';
import { l } from "@/utils/intl";
import { ProForm, ProFormText } from '@ant-design/pro-components';
import {FORM_LAYOUT_PUBLIC, NORMAL_MODAL_OPTIONS} from "@/services/constants";

export type PasswordFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserBaseInfo.ChangePasswordParams>) => void;
  modalVisible: boolean;
  values: Partial<UserBaseInfo.ChangePasswordParams>;
};



const PasswordForm: React.FC<PasswordFormProps> = (props) => {

  /**
   * status
   */
  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<UserBaseInfo.ChangePasswordParams>>({
    username: props.values.username,
  });

  /**
   * from props take params
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({ ...formVals, ...fieldsValue });
    handleSubmit({ ...formVals, ...fieldsValue });
    handleModalVisible(false)
    form.resetFields();
  };

  /**
   * render changePassword form
   */
  const ChangePasswordFormRender = () => {
    return (
      <>
        <ProFormText.Password
          width="md"
          name="password"
          hasFeedback
          label={l('user.UserOldPassword')}
          placeholder={l('user.UserEnterOldPassword')}
          rules={[{ required: true, message: l('user.UserEnterOldPassword') }]}
        />
        <ProFormText.Password
          width="md"
          name="newPassword"
          hasFeedback
          label={l('user.UserNewPassword')}
          placeholder={l('user.UserEnterNewPassword')}
          rules={[{ required: true, message: l('user.UserEnterNewPassword') }]}
        />
        <ProFormText.Password
          width="md"
          name="newPassword"
          hasFeedback
          dependencies={['newPassword']}
          label={l('user.UserRepeatNewPassword')}
          placeholder={l('user.UserRepeatNewPassword')}
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

        />
      </>
    );
  };

  /**
   * render
   */
  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={l('button.changePassword')}
      open={modalVisible}
      onCancel={() => {
        handleModalVisible(false)
        form.resetFields();
      }}
      onOk={() => submitForm()}
    >
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        initialValues={formVals}
        layout={"horizontal"}
        submitter={false}
      >
        {ChangePasswordFormRender()}
      </ProForm >
    </Modal>
  );
};

export default PasswordForm;
