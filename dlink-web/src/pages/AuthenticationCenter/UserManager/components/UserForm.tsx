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
import {UserTableListItem} from "@/pages/AuthenticationCenter/data.d";
import { useIntl, Link, history, FormattedMessage, SelectLang} from 'umi';

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

  const intl = useIntl();

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

  const renderContent = (formVals: Partial<UserTableListItem>) => {
    return (
      <>
        <Form.Item
          name="username"
          label={intl.formatMessage({id: 'pages.user.UserName', defaultMessage: '用户名',})}
          rules={[{required: true, message: intl.formatMessage({id: 'pages.user.UserEnterUserName', defaultMessage: '请输入用户名',})}]}>
          <Input placeholder={intl.formatMessage({id: 'pages.user.UserEnterUniqueUserName', defaultMessage: "请输入唯一用户名",})}/>
        </Form.Item>
        <Form.Item
          name="nickname"
          label={intl.formatMessage({id: 'pages.user.UserNickName', defaultMessage: '昵称',})}
        >
          <Input placeholder={intl.formatMessage({id: 'pages.user.UserEnterNickName', defaultMessage: "请输入昵称",})}/>
        </Form.Item>
        <Form.Item
          name="worknum"
          label={intl.formatMessage({id: 'pages.user.UserJobNumber', defaultMessage: '工号',})}
        >
          <Input placeholder={intl.formatMessage({id: 'pages.user.UserEnterJobNumber', defaultMessage: "请输入工号",})}/>
        </Form.Item>
        <Form.Item
          name="mobile"
          label={intl.formatMessage({id: 'pages.user.UserPhoneNumber', defaultMessage: '手机号',})}
        >
          <Input placeholder={intl.formatMessage({id: 'pages.user.UserEnterPhoneNumber', defaultMessage: "请输入手机号",})}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label={intl.formatMessage({id: 'pages.user.UserIsUse', defaultMessage: '是否启用',})}>
          <Switch checkedChildren={intl.formatMessage({id: 'pages.user.UserInUse', defaultMessage: '启用',})} unCheckedChildren={intl.formatMessage({id: 'pages.user.UserNotUse', defaultMessage: '禁用',})}
                  defaultChecked={formVals.enabled}/>
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
      title={formVals.id?intl.formatMessage({id: 'pages.user.UserUpdateUser', defaultMessage: '维护用户',}):intl.formatMessage({id: 'pages.user.UserCreateUser', defaultMessage: '创建用户',})}
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
