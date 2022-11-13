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
import {history, Link, SelectLang, useModel} from 'umi';
import Footer from '@/components/Footer';
import {login} from '@/services/ant-design-pro/api';
import {CheckCard} from '@ant-design/pro-components';
import styles from './index.less';
import {getData} from "@/components/Common/crud";
import {TenantTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {l} from "@/utils/intl";
import cookies from "js-cookie";
import {setLocale} from "@@/plugin-locale/localeExports";


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
  const [tenantId, setTenantId] = useState<number>();
  const [tenant, setTenant] = useState<TenantTableListItem[]>([]);

  const [checkDisabled, setCheckDisabled] = useState<boolean>(true);


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
        message.success(l('pages.login.success'));
        await fetchUserInfo();
        goto();
        return;
      } else {
        message.error(l(msg.msg, msg.msg));
      }
      // 如果失败去设置用户错误信息
      setUserLoginState(msg.datas);
    } catch (error) {
      message.error(l('pages.login.failure'));
    }
    setSubmitting(false);
  };
  //const {code } = userLoginState;


  const handleShowTenant = () => {

    return <>
      <Modal title={l('pages.login.chooseTenant')} visible={chooseTenant} destroyOnClose={true}
             width={"60%"}
             onCancel={() => {
               setChooseTenant(false);
             }}
             footer={[
               <Button key="back" onClick={() => {
                 setChooseTenant(false);
               }}>
                 {l('button.close')}
               </Button>,
               <Button disabled={checkDisabled} type="primary" key="submit" loading={submitting}
                       onClick={async () => {
                         userParamsState.tenantId = tenantId;
                         localStorage.setItem("dlink-tenantId", tenantId.toString());
                         await handleSubmit(userParamsState);
                       }}>
                 {l('button.confirm')}
               </Button>
             ]}>
        <CheckCard.Group
          multiple={false}
          onChange={(value) => {
            if (value) {
              setCheckDisabled(false)
              setTenantId(value as number)
              userParamsState.tenantId = value as number;
            } else {
              setCheckDisabled(true)
            }
          }}
        >
          {tenant?.map((item: any) => {
            return <>
              <CheckCard
                size={"default"}
                key={item?.id}
                avatar="/icons/tenant_default.svg"
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
      <div className={styles.lang}>{SelectLang && <SelectLang onItemClick={(e) => {
        let language = e.key.toString()
        cookies.set('language', language, {path: '/'})
        setLocale(language)
      }}/>}</div>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            <Link to="/">
              <img alt="logo" className={styles.logo} src="/dinky.svg"/>
              <span className={styles.title}>Dinky</span>
            </Link>
          </div>
          <div className={styles.desc}>
            {l('pages.layouts.userLayout.title')}
          </div>
        </div>

        <div className={styles.main}>
          <ProForm
            initialValues={{
              autoLogin: true,
            }}
            submitter={{
              searchConfig: {
                submitText: l('pages.login.submit'),
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
                  placeholder={l('pages.login.username.placeholder')}
                  rules={[
                    {
                      required: true,
                      message: l('pages.login.username.required'),
                    },
                  ]}
                />
                <ProFormText.Password
                  name="password"
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={styles.prefixIcon}/>,
                  }}
                  placeholder={l('pages.login.password.placeholder')}
                  rules={[
                    {
                      required: true,
                      message: l('pages.login.password.required'),
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
                {l('pages.login.rememberMe')}
              </ProFormCheckbox>
              <a
                style={{
                  float: 'right',
                }}
              >
                {l('pages.login.forgotPassword')}
              </a>
            </div>
          </ProForm>
        </div>
      </div>
      <Footer/>
      {handleShowTenant()}
    </div>

  );
};

export default Login;
