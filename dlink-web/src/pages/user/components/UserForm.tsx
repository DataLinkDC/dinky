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
import {Form, Button, Input, Modal, Switch} from 'antd';
import {UserTableListItem} from "@/pages/user/data";

export type UserFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<UserTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const UserForm: React.FC<UserFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<UserTableListItem>>({
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
    setFormVals({ ...formVals, ...fieldsValue });
    handleSubmit({ ...formVals, ...fieldsValue });
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="username"
          label="用户名"
          rules={[{required: true, message: '请输入用户名！'}]}>
          <Input placeholder="请输入唯一用户名"/>
        </Form.Item>
        <Form.Item
          name="nickname"
          label="昵称"
        >
          <Input placeholder="请输入昵称"/>
        </Form.Item>
        <Form.Item
          name="worknum"
          label="工号"
        >
          <Input placeholder="请输入工号"/>
        </Form.Item>
        <Form.Item
          name="mobile"
          label="手机号"
        >
          <Input placeholder="请输入手机号"/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label="是否启用">
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>取消</Button>
        <Button type="primary" onClick={() => submitForm()}>
          完成
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id?"维护用户":"创建用户"}
      visible={modalVisible}
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
