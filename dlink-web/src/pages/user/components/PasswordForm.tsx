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


import React, {useState} from 'react';
import {Form, Button, Input, Modal} from 'antd';
import {PasswordItem} from "@/pages/user/data";
import { useIntl, Link, history, FormattedMessage, SelectLang} from 'umi';

export type PasswordFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<PasswordItem>) => void;
  modalVisible: boolean;
  values: Partial<PasswordItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const PasswordForm: React.FC<PasswordFormProps> = (props) => {

  const intl = useIntl();

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<PasswordItem>>({
    username: props.values.username,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


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
          label= {intl.formatMessage({id: 'pages.user.UserOldPassword', defaultMessage: '旧密码',})}
          hasFeedback
          rules={[{required: true, message: intl.formatMessage({id: 'pages.user.UserEnterOldPassword', defaultMessage: '请输入旧密码！',})}]}>
          <Input.Password  placeholder={intl.formatMessage({id: 'pages.user.UserEnterOldPassword', defaultMessage: '请输入旧密码！',})}/>
        </Form.Item>
        <Form.Item
          name="newPassword"
          label={intl.formatMessage({id: 'pages.user.UserNewPassword', defaultMessage: '新密码',})}
          hasFeedback
          rules={[{required: true, message: intl.formatMessage({id: 'pages.user.UserEnterNewPassword', defaultMessage: '请输入新密码',})}]}>
          <Input.Password  placeholder={intl.formatMessage({id: 'pages.user.UserEnterNewPassword', defaultMessage: '请输入新密码',})}/>
        </Form.Item>
        <Form.Item
        name="newPasswordCheck"
        label={intl.formatMessage({id: 'pages.user.UserRepeatNewPassword', defaultMessage: '重复新密码',})}
        hasFeedback
        dependencies={['newPassword']}
        rules={[
          {
            required: true,
            message: intl.formatMessage({id: 'pages.user.UserNewPasswordNotMatch', defaultMessage: '重复密码不一致',}),
          },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue('newPassword') === value) {
                return Promise.resolve();
              }
              return Promise.reject(new Error(intl.formatMessage({id: 'pages.user.UserNewPasswordNotMatch', defaultMessage: '重复密码不一致',})));
            },
          }),
        ]}>
        <Input.Password placeholder={intl.formatMessage({id: 'pages.user.UserEnterRepeatNewPassword', defaultMessage: '请重复输入新密码',})}/>
      </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{intl.formatMessage({id: 'pages.user.UserCancel', defaultMessage: '取消',})}</Button>
        <Button type="primary" onClick={() => submitForm()}>
          {intl.formatMessage({id: 'pages.user.UserComplete', defaultMessage: '完成',})}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={intl.formatMessage({id: 'pages.user.UserUpdatePassword', defaultMessage: '修改密码',})}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={formVals}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default PasswordForm;
