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

import Footer from '@/components/Footer';
import RightContent from '@/components/RightContent';
import { AccessContextProvider } from '@/hooks/useAccess';
import { UnAccessible } from '@/pages/Other/403';
import { API_CONSTANTS } from '@/services/endpoints';
import { SysMenu } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { PageLoading, Settings as LayoutSettings } from '@ant-design/pro-components';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { history } from '@umijs/max';
import { JSX } from 'react';
import { Reducer, StoreEnhancer } from 'redux';
import { persistReducer, persistStore } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import { Navigate } from 'umi';
import { default as defaultSettings, default as Settings } from '../config/defaultSettings';
import { FullScreenProvider } from './hooks/useEditor';
import { errorConfig } from './requestErrorConfig';
import { getDataByParamsReturnResult } from './services/BusinessCrud';
import { API } from './services/data';

// const isDev = process.env.NODE_ENV === "development";
const loginPath = API_CONSTANTS.LOGIN_PATH;

const whiteList = ['/user', loginPath];

let extraRoutes: SysMenu[] = [];
let rendered = false;

/**
 * 初始化路由鉴权
 * @param param0
 */
export function patchRoutes({ routes }: any) {
  Object.keys(routes).forEach((key) => {
    let route = routes[key];
    if (!whiteList.includes(route.path)) {
      routes[key] = { ...route, access: 'canAuth' };
    }
  });
}

const queryUserInfo = async () => {
  return getDataByParamsReturnResult(API_CONSTANTS.CURRENT_USER).then((result) => {
    const { user, roleList, tenantList, currentTenant, menuList, saTokenInfo } = result.data;
    const currentUser: API.CurrentUser = {
      user: {
        ...user,
        avatar: user.avatar ?? './icons/user_avatar.png'
      },
      roleList: roleList,
      tenantList: tenantList,
      currentTenant: currentTenant,
      menuList: menuList,
      tokenInfo: saTokenInfo
    };
    return currentUser;
  });
};

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.CurrentUser;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.CurrentUser | undefined>;
}> {
  const fetchUserInfo = async () => {
    try {
      return await queryUserInfo();
    } catch (error) {
      history.push(loginPath);
    }
    return undefined;
  };

  // 如果不是登录页面，执行
  const { location } = history;
  if (location.pathname !== loginPath) {
    const currentUser = await fetchUserInfo();
    if (currentUser?.menuList) {
      extraRoutes = currentUser?.menuList;
    }

    return {
      fetchUserInfo,
      currentUser,
      settings: defaultSettings as Partial<LayoutSettings>
    };
  }
  return {
    fetchUserInfo,
    settings: defaultSettings as Partial<LayoutSettings>
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState }) => {
  // @ts-ignore
  const fullscreen = initialState?.fullscreen;

  const defaultSettings = {
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    // 自定义 403 页面
    unAccessible: <UnAccessible />,
    // 增加一个 loading 的状态
    childrenRender: (children: any) => {
      return initialState?.loading ? (
        <PageLoading />
      ) : (
        <AccessContextProvider currentUser={initialState?.currentUser}>
          {/* @ts-ignore */}
          <FullScreenProvider key={location.pathname}>{children}</FullScreenProvider>
        </AccessContextProvider>
      );
    }
  };

  if (fullscreen) {
    return {
      ...initialState?.settings,
      siderWidth: 0,
      ...defaultSettings,
      layout: 'side'
    };
  }
  return {
    headerTitleRender: () => {
      // 重新对 title 的设置进行设置
      Settings.title = l('layouts.userLayout.title');
      // 重新对 logo 的设置进行设置 由于 logo 是一个组件，所以需要重新渲染, 重新渲染的时候，会重新执行一次 layout
      return (
        <>
          <img height={50} width={50} src={Settings.logo} alt={'logo'} />
          <span style={{ marginLeft: 10, color: 'white' }}>{Settings.title}</span>
        </>
      );
    },
    rightContentRender: () => <RightContent />,
    footerRender: () => <Footer />,
    siderWidth: 180,
    /*waterMarkProps: {
      content: initialState?.currentUser?.user.username + ' ' + new Date().toLocaleString(),
      fontColor:
        theme === THEME.light || undefined ? 'rgba(0, 0, 0, 0.15)' : 'rgba(255, 255, 255, 0.15)'
    },*/
    isChildrenLayout: true,
    ...defaultSettings,
    ...initialState?.settings
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  ...errorConfig,
  // 修改为相对请求路径, 避免请求路径出现错误 会自动拼接为完整请求路径
  baseURL: API_CONSTANTS.BASE_URL
};

// 这个是redux-persist 的配置
const persistConfig = {
  key: 'root', // 自动框架生产的根目录id 是root。不变
  storage // 这个是选择用什么存储，session 还是 storage
};

const persistEnhancer: StoreEnhancer = (next) => (reducer: Reducer<any, any>) => {
  const store = next(persistReducer(persistConfig, reducer));
  const persist = persistStore(store);
  return { ...store, persist };
};

export const dva = {
  config: {
    extraEnhancers: [persistEnhancer]
  }
};

/***
 * 动态修改默认跳转路由
 */
const patch = (oldRoutes: any, routes: SysMenu[]) => {
  oldRoutes[1].routes = oldRoutes[1]?.routes?.map(
    (route: { routes: { path: any; element: JSX.Element }[]; path: string }) => {
      if (route.routes?.length) {
        const redirect = routes?.filter((r) => r.path.startsWith(route.path));
        if (redirect.length) {
          route.routes.shift();
          route.routes.unshift({
            path: route.path,
            element: <Navigate to={redirect[0].path} />
          });
        }
      }
      return route;
    }
  );
};

/***
 * 动态修改路由
 */
export function patchClientRoutes({ routes }: { routes: SysMenu[] }) {
  // 根据 extraRoutes 对 routes 做一些修改
  if (extraRoutes.length) {
    patch(routes, extraRoutes);
  }
}

/***
 * 路由切换并只加载首次
 */
export function onRouteChange({
  location,
  clientRoutes,
  routes,
  action
}: {
  location: any;
  clientRoutes: any;
  routes: any;
  action: any;
}) {
  if (location.pathname !== loginPath && !rendered) {
    const filterMenus = (menus: SysMenu[]) => {
      return menus?.filter((menu) => menu.type !== 'F');
    };
    extraRoutes = filterMenus(extraRoutes);
    patchClientRoutes({ routes: clientRoutes });
    rendered = true;
  }
}
