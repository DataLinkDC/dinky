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

import { chooseTenantSubmit, login } from '@/services/api';
import { setTenantStorageAndCookie } from '@/services/function';
import { l } from '@/utils/intl';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { CheckCard, LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Helmet, history, SelectLang, useModel } from '@umijs/max';
import { Button, message, Modal } from 'antd';
import React, { useState } from 'react';
import { flushSync } from 'react-dom';
import Settings from '../../../../config/defaultSettings';

/** 此方法会跳转到 redirect 参数所在的位置 */
const gotoRedirectUrl = () => {
  if (!history) return;
  setTimeout(() => {
    const urlParams = new URL(window.location.href).searchParams;
    history.push(urlParams.get('redirect') || '/');
  }, 10);
};

const Lang = () => {
  const langClassName = useEmotionCss(({ token }) => {
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

const Login: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);
  const { initialState, setInitialState } = useModel('@@initialState');
  const [tenantVisible, handleTenantVisible] = useState<boolean>(false);
  const [checkDisabled, setCheckDisabled] = useState<boolean>(true);
  const [tenant, setTenant] = useState<UserBaseInfo.Tenant[]>([]);
  const [tenantIdParams, setTenantIdParams] = useState<number>();

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

  const handleChooseTenant = async (chooseTenantResult: API.Result) => {
    if (chooseTenantResult.code === 0) {
      message.success(
        l('pages.login.chooseTenantSuccess', '', {
          msg: chooseTenantResult.msg,
          tenantCode: chooseTenantResult.datas.tenantCode,
        }),
      );

      /**
       * After the selection is complete, refresh all user information
       */
      await fetchUserInfo();

      /**
       * Redirect to home page
       */
      gotoRedirectUrl();
    } else {
      message.error(l('pages.login.chooseTenantFailed'));
      return;
    }
  };

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      // login
      const result = await login({ ...values });
      if (result.code === 0) {
        message.success(l('pages.login.result', '', { msg: result.msg, time: result.time }));
        /**
         * After successful login, set the tenant list
         */
        const tenantList: UserBaseInfo.Tenant[] = result.datas.tenantList;
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
          setTenantIdParams(tenantList[0].id as number);
          setTenantStorageAndCookie(tenantList[0].id as number);
          const chooseTenantResult: API.Result = await chooseTenantSubmit({
            tenantId: tenantList[0].id as number,
          });
          await handleChooseTenant(chooseTenantResult);
        }
        return;
      } else {
        /**
         * If it fails to set the user error message
         */
        message.error(l('pages.login.result', '', { msg: result.msg, time: result.time }));
      }
    } catch (error) {
      message.error(l('pages.login.error', '', { msg: error }));
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
                setSubmitting(true);
                const result = await chooseTenantSubmit({ tenantId: tenantIdParams as number });
                await handleChooseTenant(result);
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
                setTenantStorageAndCookie(value as number);
                setTenantIdParams(value as number);
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
    <div className={containerClassName}>
      <Helmet>
        <title>
          {l('menu.login')}- {Settings.title}
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
                  message: l('pages.login.username.required'),
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
      <Footer />
      {handleShowTenant()}
    </div>
  );
};

export default Login;
