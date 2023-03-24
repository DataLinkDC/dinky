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

import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Button, message, Modal} from 'antd';
import React, {useState} from 'react';
import {LoginForm, ProFormCheckbox, ProFormText} from '@ant-design/pro-form';
import {history, Link, SelectLang, useModel} from 'umi';
import Footer from '@/components/Footer';
import {chooseTenantSubmit, login} from '@/services/ant-design-pro/api';
import {CheckCard} from '@ant-design/pro-components';
import styles from './index.less';
import {l} from '@/utils/intl';
import cookies from 'js-cookie';
import {setLocale} from '@@/plugin-locale/localeExports';

/** 此方法会跳转到 redirect 参数所在的位置 */
const goto = () => {
  if (!history) return;
  setTimeout(() => {
    const { query } = history.location;
    const { redirect } = query as { redirect: string };
    history.push(redirect || '/');
  }, 10);
};

const Login: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);
  const {initialState, setInitialState} = useModel('@@initialState');
  const [tenantVisible, handleTenantVisible] = useState<boolean>(false);
  const [checkDisabled, setCheckDisabled] = useState<boolean>(true);
  const [tenant, setTenant] = useState<API.Tenant[]>([]);
  const [tenantIdParams, setTenantIdParams] = useState<number>();


  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    debugger
    if (userInfo) {
      setInitialState({
        ...initialState,
        currentUser: userInfo,
      });
    }
  };


  const setTenantCookie = (tenantId: number) => {
    localStorage.setItem('dlink-tenantId', tenantId.toString()); // 放入本地存储中 request2请求时会放入header
    cookies.set('tenantId', tenantId.toString(), {path: '/'}); // 放入cookie中
  };

  const handleChooseTenant = async (chooseTenantResult: API.Result) => {
    if (chooseTenantResult.code === 0) {
      message.success(l('pages.login.chooseTenantSuccess', '', {
        msg: chooseTenantResult.msg,
        tenantCode: chooseTenantResult.datas.tenantCode
      }));

      /**
       * After the selection is complete, refresh all user information
       */
      await fetchUserInfo();

      goto()

    } else {
      message.error(l('pages.login.chooseTenantFailed'))
      return;
    }
  }

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      // login
      const result = await login({...values});
      if (result.code === 0) {
        message.success(l('pages.login.result', '', {msg: result.msg, time: result.time}));
        /**
         * After successful login, set the tenant list
         */
        const tenantList: API.Tenant[] = result.datas.tenantList
        if (tenantList === null || tenantList.length === 0) {
          message.error('该用户未绑定租户');
          return;
        } else {
          setTenant(tenantList);
        }

        /**
         * Determine whether the current tenant list is multiple
         * 1. If there are multiple execution pop-up modals, the user selects a specific tenant to enter the system
         * 2. If it is a single, only use the unique tenant id to enter the system directly
         */

        if (tenantList && tenantList.length > 1) {
          handleTenantVisible(true);
        } else {
          setTenantIdParams(tenantList[0].id as number)
          setTenantCookie(tenantList[0].id as number);
          const chooseTenantResult: API.Result = await chooseTenantSubmit({tenantId: tenantList[0].id as number});
          await handleChooseTenant(chooseTenantResult)
        }
        return;
      } else {
        /**
         * If it fails to set the user error message
         */
        message.error(l('pages.login.result', '', {msg: result.msg, time: result.time}));
      }
    } catch (error) {
      message.error(l('pages.login.error', '', {msg: error}));
    }
  };


  const handleShowTenant = () => {
    return (
      <>
        <Modal
          title={l('pages.login.chooseTenant')}
          open={tenantVisible}
          destroyOnClose={true}
          width={'60%'}
          onCancel={() => {
            handleTenantVisible(false);
          }}
          footer={[
            <Button
              key="back"
              onClick={() => {
                handleTenantVisible(false);
              }}
            >
              {l('button.close')}
            </Button>,
            <Button
              disabled={checkDisabled}
              type="primary"
              key="submit"
              loading={submitting}
              onClick={async () => {
                setSubmitting(true)
                const result = await chooseTenantSubmit({tenantId: tenantIdParams as number});
                await handleChooseTenant(result)
                handleTenantVisible(false);
              }}
            >
              {l('button.confirm')}
            </Button>,
          ]}
        >
          <CheckCard.Group
            multiple={false}
            onChange={(value) => {
              if (value) {
                setCheckDisabled(false); // 如果没选择租户 ·确认按钮· 则禁用
                setTenantCookie(value as number);
                setTenantIdParams(value as number)
              } else {
                setCheckDisabled(true);
              }
            }}
          >
            {tenant?.map((item: any) => {
              return (
                <CheckCard
                  size={'default'}
                  key={item?.id}
                  avatar="/icons/tenant_default.svg"
                  title={item?.tenantCode}
                  value={item?.id}
                  description={item?.note}
                />
              );
            })}
          </CheckCard.Group>
        </Modal>
      </>
    );
  };

  return (
    <div className={styles.container}>
      <div className={styles.lang}>
        {SelectLang && (
          <SelectLang
            onItemClick={(e) => {
              let language = e.key.toString();
              if (language === undefined || language === '') {
                language = localStorage.getItem('umi_locale');
              }
              cookies.set('language', language, {path: '/'});
              setLocale(language);
            }}
          />
        )}
      </div>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            <Link to="/">
              <img alt="logo" className={styles.logo} src="/dinky.svg"/>
              <span className={styles.title}>Dinky</span>
            </Link>
          </div>
          <div className={styles.desc}>{l('pages.layouts.userLayout.title')}</div>
        </div>

        <div className={styles.main}>
          <LoginForm
            contentStyle={{
              minWidth: 280,
              maxWidth: '75vw',
            }}
            initialValues={{
              autoLogin: true,
            }}
            onFinish={async (values) => {
              await handleSubmit(values as API.LoginParams);
            }}
          >
            <>
              <ProFormText
                name="username"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined/>,
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
                  prefix: <LockOutlined/>,
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
      </div>
      <Footer/>
      {handleShowTenant()}
    </div>
  );
};

export default Login;
