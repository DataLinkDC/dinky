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

import {
  LogoutOutlined,
  TeamOutlined,
  UserSwitchOutlined
} from '@ant-design/icons';
import { setAlpha } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { history, useModel } from '@umijs/max';
import {Avatar, Modal, Spin} from 'antd';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import {chooseTenantSubmit, outLogin} from "@/services/BusinessCrud";
import {l} from "@/utils/intl";
import {setTenantStorageAndCookie} from "@/utils/function";
import {ErrorNotification, SuccessNotification} from "@/utils/messages";
import {
  ItemType,
} from "rc-menu/es/interface";



const Name = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};

  const nameClassName = useEmotionCss(({ token }) => {
    return {
      width: 'auto',
      height: '48px',
      overflow: 'hidden',
      lineHeight: '48px',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      [`@media only screen and (max-width: ${token.screenMD}px)`]: {
        display: 'none',
      },
    };
  });

  return <span className={`${nameClassName} anticon`}>{currentUser?.user.username}</span>;
};

const AvatarLogo = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};

  const avatarClassName = useEmotionCss(({ token }) => {
    return {
      marginRight: '8px',
      color: token.colorPrimary,
      verticalAlign: 'top',
      background: setAlpha(token.colorBgContainer, 0.85),
      [`@media only screen and (max-width: ${token.screenMD}px)`]: {
        margin: 0,
      },
    };
  });

  return (
    <Avatar size="small" className={avatarClassName} src={currentUser?.user.avatar} alt="avatar" />
  );
};

const AvatarDropdown = () => {
  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await outLogin();
    const { search, pathname } = window.location;
    const urlParams = new URL(window.location.href).searchParams;
    /** 此方法会跳转到 redirect 参数所在的位置 */
    const redirect = urlParams.get('redirect');
    // Note: There may be security issues, please note
    if (window.location.pathname !== '/user/login' && !redirect) {
      history.replace({
        pathname: '/user/login',
        search: stringify({
          redirect: pathname + search,
        }),
      });
    }
  };

  const actionClassName = useEmotionCss(({ token }) => {
    return {
      display: 'flex',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      alignItems: 'center',
      padding: '0 8px',
      cursor: 'pointer',
      color: 'white',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });
  const { initialState, setInitialState } = useModel('@@initialState');

  const loginOutHandler = useCallback(
    async (event: MenuInfo) => {
      const { key } = event;
        flushSync(() => {
            setInitialState((s) => ({ ...s, currentUser: undefined }));
        });
        await loginOut();
        return;
    },
    [setInitialState],
  );

  const loading = (
    <span className={actionClassName}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }
  const { currentUser } = initialState;
  if (!currentUser || !currentUser.user.username) {
    return loading;
  }


  /**
   *
   * @param option
   */
  const tenantHandleChange = (option:any) => {
    const tenantCode = option.domEvent.target.innerText;
    const tenantId = option.key as number;
    Modal.confirm({
      title: l("menu.account.checkTenant"),
      content: l("menu.account.checkTenantConfirm", "", {tenantCode}),
      okText: l("button.confirm"),
      cancelText: l("button.cancel"),
      onOk: async () => {
        const result = await chooseTenantSubmit({tenantId});
        setTenantStorageAndCookie(tenantId);
        if (result.code === 0) {
          SuccessNotification(result.msg);
        } else {
          ErrorNotification(result.msg);
        }
        // trigger global refresh, such as reload page
        window.location.reload();
      },
    });
  };
  const renderTenantList = () => {
    let chooseTenantList: ItemType[] = [];
    currentUser.tenantList?.map((item) => {
     return chooseTenantList.push({
        key: item.id,
        label: item.tenantCode,
        disabled: item.id === currentUser.currentTenant?.id,
        onClick: (e) => tenantHandleChange(e),
      })
    })
    return chooseTenantList;
  }


  const menuItems = [
      {
        key: 'center',
        icon: <TeamOutlined />,
        label: l('menu.account.center'),
        onClick: () => history.push('/account/center'),
      },
      {
        type: 'divider' as const,
      },
      {
        key: 'switching',
        icon: <UserSwitchOutlined />,
        label: l('menu.account.checkTenant'),
        children:  renderTenantList(),
      },
      {
        type: 'divider' as const,
      },
      {
        key: 'logout',
        icon: <LogoutOutlined />,
        label: l('menu.account.logout'),
        onClick: loginOutHandler,
      },
  ];

  return (
    <HeaderDropdown
      menu={{
        selectedKeys: [],
        items: menuItems,
      }}
    >
      <span className={actionClassName}>
        <AvatarLogo />
        <Name />
      </span>
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
