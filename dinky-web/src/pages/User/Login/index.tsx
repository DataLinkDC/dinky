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

import Footer from '@/components/Footer';

import { LockOutlined, UserOutlined, } from '@ant-design/icons';
import { LoginForm, ProFormCheckbox, ProFormText, } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Helmet, history, SelectLang, useModel } from '@umijs/max';
import { Alert, message } from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, { useState } from 'react';
import { flushSync } from 'react-dom';
import { login } from "@/services/api";
import { l } from '@/utils/intl';

const Lang = () => {
  const langClassName = useEmotionCss(({token}) => {
    return {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });

  return (
    <div className={langClassName} data-lang>
      {SelectLang && <SelectLang />}
    </div>
  );
};

const LoginMessage: React.FC<{
  content: string;
}> = ({content}) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};

const Login: React.FC = () => {
  const [userLoginState, setUserLoginState] = useState<API.LoginResult>({});
  const {initialState, setInitialState} = useModel('@@initialState');

  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    };
  });

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      flushSync(() => {
        setInitialState((s) => ({
          ...s,
          currentUser: userInfo,
        }));
      });
    }
  };

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      // 登录
      const msg = await login({...values, type: 'password', tenantId: 1});
      console.log('msg',msg);
      if (msg.code === 0) {
        message.success(l('pages.login.success'));
        await fetchUserInfo();
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        return;
      }
      // 如果失败去设置用户错误信息
      setUserLoginState(msg);
    } catch (error) {
      message.error(l('pages.login.failure'));
    }
  };
  // const {code, type: loginType} = userLoginState;

  return (
    <div className={containerClassName}>
      <Helmet>
        <title>
          {l('menu.login')}
          - {Settings.title}
        </title>
      </Helmet>
      <Lang />
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src={Settings.logo} />}
          title="Dinky"
          subTitle={l('pages.layouts.userLayout.title')}
          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.LoginParams);
          }}
        >
          {/*<LoginMessage*/}
          {/*  content={l('pages.login.accountLogin.errorMessage')}*/}
          {/*/>*/}
          <>
            <ProFormText
              name="username"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined />,
              }}
              placeholder={l('pages.login.username.placeholder')}
              rules={[
                {
                  required: true,
                  message: l('pages.login.username.required')
                },
              ]}
            />
            <ProFormText.Password
              name="password"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined />,
              }}
              placeholder={l('pages.login.password.placeholder')}
              rules={[
                {
                  required: true,
                  message: l('pages.login.password.required')
                },
              ]}
            />
          </>
          <div
            style={{
              marginBottom: 24,
            }}
          >
            <ProFormCheckbox noStyle name="autoLogin">
              {l('pages.login.rememberMe')}
            </ProFormCheckbox>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
