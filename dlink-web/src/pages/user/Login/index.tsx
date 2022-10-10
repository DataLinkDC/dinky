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

import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {Button, message, Modal} from 'antd';
import React, {useEffect, useState} from 'react';
import ProForm, {ProFormCheckbox, ProFormText} from '@ant-design/pro-form';
import {FormattedMessage, history, Link, SelectLang, useIntl, useModel} from 'umi';
import Footer from '@/components/Footer';
import {login} from '@/services/ant-design-pro/api';
import {CheckCard} from '@ant-design/pro-components';

import styles from './index.less';
import {getData} from "@/components/Common/crud";
import {TenantTableListItem} from "@/pages/ResourceCenter/data.d";


/** 此方法会跳转到 redirect 参数所在的位置 */
const goto = () => {
  if (!history) return;
  setTimeout(() => {
    const {query} = history.location;
    const {redirect} = query as { redirect: string };
    history.push(redirect || '/');
  }, 10);
};

const Login: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);
  const [userLoginState, setUserLoginState] = useState<API.LoginResult>({});
  const [userParamsState, setUserParamsState] = useState<API.LoginParams>({});
  const [type, setType] = useState<string>('password');
  const {initialState, setInitialState} = useModel('@@initialState');
  const [isLogin, setIsLogin] = useState<boolean>(true);
  const [chooseTenant, setChooseTenant] = useState<boolean>(false);
  const [tenantId, setTenantId] = useState<number>(0);
  const [tenant, setTenant] = useState<TenantTableListItem[]>([]);
  const intl = useIntl();

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      setInitialState({
        ...initialState,
        currentUser: userInfo,
      });
    }
  };


  useEffect(() => {
    // 调用接口
    const {username} = userParamsState
    if (!username) {
      return
    }
    getData("/api/geTenants", {username}).then(result => {
      setTenant(result?.datas);
    })
  }, [
    userParamsState?.username
  ])


  const handleSubmit = async (values: API.LoginParams) => {
    if (!isLogin) {
      return;
    }
    setIsLogin(false);
    setTimeout(() => {
      setIsLogin(true)
    }, 200);
    setSubmitting(true);
    try {
      // 登录
      const msg = await login({...values, type});
      if (msg.code === 0 && msg.datas != undefined) {
        const defaultloginSuccessMessage = intl.formatMessage({
          id: 'pages.login.success',
          defaultMessage: '登录成功！',
        });
        message.success(defaultloginSuccessMessage);
        await fetchUserInfo();
        goto();
        return;
      } else {
        const defaultloginFailureMessage = intl.formatMessage({
          id: msg.msg,
          defaultMessage: msg.msg,
        });
        message.error(defaultloginFailureMessage);
      }
      // 如果失败去设置用户错误信息
      setUserLoginState(msg.datas);
    } catch (error) {
      const defaultloginFailureMessage = intl.formatMessage({
        id: 'pages.login.failure',
        defaultMessage: '登录失败，请重试！',
      });

      message.error(defaultloginFailureMessage);
    }
    setSubmitting(false);
  };
  //const {code } = userLoginState;


  const handleShowTenant = (item: API.LoginParams) => {

    return <>
      <Modal title="请选择租户" visible={chooseTenant} destroyOnClose={true} width={"60%"}
             onCancel={() => {
               setChooseTenant(false);
             }}
             footer={[
               <Button key="back" onClick={() => {
                 setChooseTenant(false);
               }}>
                 关闭
               </Button>,
               <Button disabled={tenantId === 0} type="primary" key="submit" loading={submitting}
                       onClick={async () => {
                         userParamsState.tenantId = tenantId;
                         localStorage.setItem("dlink-tenantId", tenantId.toString());
                         await handleSubmit(userParamsState);
                       }}>
                 确定
               </Button>
             ]}>
        <CheckCard.Group
          multiple={false}
          onChange={(value) => {
            setTenantId(value as number)
            userParamsState.tenantId = tenantId;
          }}
        >
          {tenant?.map((item: any) => {
            return <>
              <CheckCard onChange={(check)=>{

              }}
                size={"default"}
                key={item?.id}
                avatar="https://gw.alipayobjects.com/zos/bmw-prod/f601048d-61c2-44d0-bf57-ca1afe7fd92e.svg"
                title={item?.tenantCode}
                value={item?.id}
                description={item?.note}
              />
            </>
          })}
        </CheckCard.Group>
      </Modal>
    </>
  }


  return (
    <div className={styles.container}>
      <div className={styles.lang}>{SelectLang && <SelectLang/>}</div>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            <Link to="/">
              <img alt="logo" className={styles.logo} src="/dinky.svg"/>
              <span className={styles.title}>Dinky</span>
            </Link>
          </div>
          <div className={styles.desc}>
            {intl.formatMessage({id: 'pages.layouts.userLayout.title'})}
          </div>
        </div>

        <div className={styles.main}>
          <ProForm
            initialValues={{
              autoLogin: true,
            }}
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({
                  id: 'pages.login.submit',
                  defaultMessage: '登录',
                }),
              },
              render: (_, dom) => dom.pop(),
              submitButtonProps: {
                loading: submitting,
                size: 'large',
                style: {
                  width: '100%',
                },
                htmlType: 'submit',
              },
            }}
            onFinish={async (values) => {
              values.grant_type = 'password';
              setUserLoginState(values);
              setUserParamsState(values);
              setChooseTenant(true)
            }}
          >
            {type === 'password' && (
              <>
                <ProFormText
                  name="username"
                  fieldProps={{
                    size: 'large',
                    prefix: <UserOutlined className={styles.prefixIcon}/>,
                  }}
                  placeholder={intl.formatMessage({
                    id: 'pages.login.username.placeholder',
                    defaultMessage: '用户名:',
                  })}
                  rules={[
                    {
                      required: true,
                      message: (
                        <FormattedMessage
                          id="pages.login.username.required"
                          defaultMessage="请输入用户名!"
                        />
                      ),
                    },
                  ]}
                />
                <ProFormText.Password
                  name="password"
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={styles.prefixIcon}/>,
                  }}
                  placeholder={intl.formatMessage({
                    id: 'pages.login.password.placeholder',
                    defaultMessage: '密码:',
                  })}
                  rules={[
                    {
                      required: true,
                      message: (
                        <FormattedMessage
                          id="pages.login.password.required"
                          defaultMessage="请输入密码！"
                        />
                      ),
                    },
                  ]}
                />
              </>
            )}


            <div
              style={{
                marginBottom: 24,
              }}
            >
              <ProFormCheckbox noStyle name="autoLogin">
                <FormattedMessage id="pages.login.rememberMe" defaultMessage="自动登录"/>
              </ProFormCheckbox>
              <a
                style={{
                  float: 'right',
                }}
              >
                <FormattedMessage id="pages.login.forgotPassword" defaultMessage="忘记密码"/>
              </a>
            </div>
          </ProForm>
        </div>
      </div>
      <Footer/>
      {handleShowTenant(userParamsState)}
    </div>

  );
};

export default Login;
