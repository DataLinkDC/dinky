/**
 * @Author: kevin
 * @Title: index.tsx
 * @Date: 2023-02-10 16:45:06
 * @Description:
 */

import Footer from '@/components/Footer';

import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { CheckCard, LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Helmet, history, SelectLang, useModel } from '@umijs/max';
import { Alert, Button, message, Modal } from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, { useEffect, useState } from 'react';
import { flushSync } from 'react-dom';
import { login, getTenants } from '@/services/api';
import { l } from '@/utils/intl';
import cookies from 'js-cookie';

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
  const [userLoginState, setUserLoginState] = useState<API.LoginResult>({});
  const { initialState, setInitialState } = useModel('@@initialState');
  const [chooseTenant, setChooseTenant] = useState<boolean>(false);
  const [checkDisabled, setCheckDisabled] = useState<boolean>(true);
  const [tenant, setTenant] = useState<API.TenantListItem[]>([]);

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

  /**
   * 获取租户
   *
   */
  const getTenant = async () => {
    const res = await getTenants({ username: 'admin' });
    console.log('res', res?.datas);
    if ((res?.datas?.length || 0) > 1) {
      setTenant(res.datas || []);
      setChooseTenant(true);
    }
  };

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      // 登录
      const msg = await login({ ...values, type: 'password', tenantId: 1 });
      if (msg.code === 0) {
        // message.success(l('pages.login.success'));
        // 选择租户
        await fetchUserInfo();
        await getTenant();
        return;
      }
      // 如果失败去设置用户错误信息
      setUserLoginState(msg);
    } catch (error) {
      message.error(l('pages.login.failure'));
    }
  };

  const setTenantCookie = (tenantId: number) => {
    localStorage.setItem('dlink-tenantId', tenantId.toString()); // 放入本地存储中 request2请求时会放入header
    cookies.set('tenantId', tenantId.toString(), { path: '/' }); // 放入cookie中
  };

  const handleShowTenant = () => {
    return (
      <>
        <Modal
          title={l('pages.login.chooseTenant')}
          open={chooseTenant}
          destroyOnClose={true}
          width={'60%'}
          onCancel={() => {
            setChooseTenant(false);
          }}
          footer={[
            <Button
              key="back"
              onClick={() => {
                setChooseTenant(false);
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
                // await handleSubmit(userParamsState);
                setChooseTenant(false);
                const urlParams = new URL(window.location.href).searchParams;
                history.push(urlParams.get('redirect') || '/');
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
                // userParamsState.tenantId = value as number; // 将租户id给后端入参
                setTenantCookie(value as number);
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
