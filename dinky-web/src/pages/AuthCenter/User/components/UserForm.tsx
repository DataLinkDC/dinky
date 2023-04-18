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


import React, {useState} from "react";
import {Form, Modal} from "antd";
import {l} from "@/utils/intl";
import {ProForm, ProFormSwitch, ProFormText} from "@ant-design/pro-components";
import {FORM_LAYOUT_PUBLIC, NORMAL_MODAL_OPTIONS, SWITCH_OPTIONS} from "@/services/constants";

export type UserFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<UserBaseInfo.User>) => void;
  modalVisible: boolean;
  values: Partial<UserBaseInfo.User>;
};


const UserForm: React.FC<UserFormProps> = (props) => {

  /**
   * status
   */
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
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };


  /**
   * user form render
   * @returns {JSX.Element}
   */
  const UserFormRender = () => {
    return (
      <>
        <ProFormText
          name="username"
          label={l("user.UserName")}
          placeholder={l("user.UserEnterUniqueUserName")}
          rules={[{
            required: true,
            message: l("user.UserEnterUserName")
          }]}
        />

        <ProFormText
          name="nickname"
          label={l("user.UserNickName")}
          placeholder={l("user.UserEnterNickName")}
          rules={[{
            required: true,
            message: l("user.UserEnterNickName")
          }]}
        />

        <ProFormText
          name="worknum"
          label={l("user.UserJobNumber")}
          placeholder={l("user.UserEnterJobNumber")}
        />

        <ProFormText
          name="mobile"
          label={l("user.UserPhoneNumber")}
          placeholder={l("user.UserEnterPhoneNumber")}
        />

        <ProFormSwitch
          name="enabled"
          {...SWITCH_OPTIONS()}
          label={l("global.table.isEnable")}
        />
      </>
    );
  };


  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={formVals.id ? l("user.UserUpdateUser") : l("user.UserCreateUser")}
      open={modalVisible}
      onCancel={() => handleModalVisible()}
      onOk={submitForm}
    >
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        initialValues={formVals}
        layout={"horizontal"}
        submitter={false}
      >
        {UserFormRender()}
      </ProForm>
    </Modal>
  );
};

export default UserForm;
