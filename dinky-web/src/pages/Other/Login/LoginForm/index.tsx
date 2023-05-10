/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import Settings from "../../../../../config/defaultSettings";
import {l} from "@/utils/intl";
import {LoginFormPage, ProFormCheckbox, ProFormText} from "@ant-design/pro-components";
import {LockOutlined, UserOutlined} from "@ant-design/icons";

type LoginFormProps = {
  onSubmit: (values: any) => Promise<void>;
}

const LoginForm: React.FC<LoginFormProps> = (props) => {

  const {onSubmit} = props;


  const renderLoginForm = () => {

    return <>
      <ProFormText
        name="username"
        fieldProps={{
          size: "large",
          prefix: <UserOutlined/>,
        }}
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
          size: "large",
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
    </>
  }



  return <div
    style={{
      display: "flex",
      flex: "auto",
      padding: "0vh 5vh 30vh 2vh",
      justifyContent: 'center',
      alignItems: 'center',
      flexDirection: 'column',
    }}
  >
    <LoginFormPage
      title={<b>Dinky</b> as any}
      logo={Settings.logo}
      subTitle={l("layouts.userLayout.title")}
      backgroundImageUrl={"icons/bg.svg"}
      onFinish={onSubmit}
    >
      {renderLoginForm()}
    </LoginFormPage>
  </div>;
};

export default LoginForm;
