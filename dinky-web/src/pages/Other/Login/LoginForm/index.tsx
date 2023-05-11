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

import React, {useState} from "react";
import {l} from "@/utils/intl";
import {ProForm, ProFormCheckbox, ProFormText} from "@ant-design/pro-components";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import MainWithStyle from "./MainWithStyle";
import {SubmitterProps} from "@ant-design/pro-form/es/components";
import style from "../../../../global.less";
import FadeIn from "@/components/Animation/FadeIn";

type LoginFormProps = {
  onSubmit: (values: any) => Promise<void>;
}

const LoginForm: React.FC<LoginFormProps> = (props) => {

  const {onSubmit} = props;

  const [form] = ProForm.useForm();

  const [submitting, setSubmitting] = useState(false);


  const handleClickLogin = async () => {
    setSubmitting(true);
    await onSubmit({...form.getFieldsValue()});
    setSubmitting(false);
  };

  const renderLoginForm = () => {

    return <>
      <ProFormText
        name="username"
        width={"lg"}
        fieldProps={{
          prefix: <UserOutlined/>,
        }}
        required
        placeholder={l("login.username.placeholder")}
        rules={[
          {
            required: true,
            message: l("login.username.required"),
          },
        ]}
      />
      <ProFormText.Password
        name="password"
        fieldProps={{
          prefix: <LockOutlined/>,
        }}
        placeholder={l("login.password.placeholder")}
        rules={[
          {
            required: true,
            message: l("login.password.required"),
          },
        ]}
      />
      <ProFormCheckbox name="autoLogin">
        {l("login.rememberMe")}
      </ProFormCheckbox>
    </>;
  };


  const proFormSubmitter: SubmitterProps = {
    searchConfig: {submitText: l("menu.login")},
    resetButtonProps: false,
    submitButtonProps: {
      loading: submitting,
      size: "large",
      shape: "round",
      style: {width: "100%"}
    }
  };


  return <MainWithStyle>
    <FadeIn>
      <ProForm
        className={style.loginform}
        form={form}
        onFinish={handleClickLogin}
        initialValues={{autoLogin: true}}
        submitter={{...proFormSubmitter}}
      >
        {renderLoginForm()}
      </ProForm>
    </FadeIn>
  </MainWithStyle>;
};

export default LoginForm;
