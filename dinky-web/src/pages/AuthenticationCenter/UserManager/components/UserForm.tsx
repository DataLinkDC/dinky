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
import {Button, Form, Input, Modal, Switch} from 'antd';
import {l} from "@/utils/intl";

export type UserFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserBaseInfo.User>) => void;
  modalVisible: boolean;
  values: Partial<UserBaseInfo.User>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const UserForm: React.FC<UserFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<UserBaseInfo.User>>({
    id: props.values.id,
    username: props.values.username,
    nickname: props.values.nickname,
    password: props.values.password,
    worknum: props.values.worknum,
    mobile: props.values.mobile,
    avatar: props.values.avatar,
    enabled: props.values.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals: Partial<UserBaseInfo.User>) => {
    return (
      <>
        <Form.Item
          name="username"
          label={l('user.UserName')}
          rules={[{
            required: true,
            message: l('user.UserEnterUserName')
          }]}>
          <Input placeholder={l('user.UserEnterUniqueUserName')}/>
        </Form.Item>
        <Form.Item
          name="nickname"
          label={l('user.UserNickName')}
        >
          <Input placeholder={l('user.UserEnterNickName')}/>
        </Form.Item>
        <Form.Item
          name="worknum"
          label={l('user.UserJobNumber')}
        >
          <Input
            placeholder={l('user.UserEnterJobNumber')}/>
        </Form.Item>
        <Form.Item
          name="mobile"
          label={l('user.UserPhoneNumber')}
        >
          <Input
            placeholder={l('user.UserEnterPhoneNumber')}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}>
          <Switch checkedChildren={l('button.enable')}
                  unCheckedChildren={l('button.disable')}
                  defaultChecked={formVals.enabled}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l('button.cancel')}</Button>
        <Button type="primary" onClick={() => submitForm()}>
          {l('button.finish')}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={"40%"}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id ? l('user.UserUpdateUser') : l('user.UserCreateUser')}
      open={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={formVals}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default UserForm;
