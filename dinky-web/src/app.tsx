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

import Footer from "@/components/Footer";
import RightContent from "@/components/RightContent";
import {PageLoading, Settings as LayoutSettings} from "@ant-design/pro-components";
// import {SettingDrawer} from "@ant-design/pro-components";
import type {RunTimeLayoutConfig} from "@umijs/max";
import {history} from "@umijs/max";
import defaultSettings from "../config/defaultSettings";
import {errorConfig} from "./requestErrorConfig";
import {currentUser as queryCurrentUser} from "./services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import {THEME} from "@/types/Public/data";
import {UnAccessible} from "@/pages/Other/403";

// const isDev = process.env.NODE_ENV === "development";
const loginPath = API_CONSTANTS.LOGIN_PATH;

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
      const result = await queryCurrentUser();
      const user = result.datas.user;
      const currentUser: API.CurrentUser = {
        user: {
          id: user.id,
          username: user.username,
          password: user.password,
          nickname: user.nickname,
          worknum: user.worknum,
          avatar: user.avatar ? user.avatar : "/icons/user_avatar.png",
          mobile: user.mobile,
          enabled: user.enabled,
          isDelete: user.isDelete,
          createTime: user.createTime,
          updateTime: user.updateTime,
          isAdmin: user.isAdmin,
        },
        roleList: result.datas.roleList,
        tenantList: result.datas.tenantList,
        currentTenant: result.datas.currentTenant,
      };
      return currentUser;
    } catch (error) {
      history.push(loginPath);
    }
    return undefined;
  };
  // 如果不是登录页面，执行
  const {location} = history;
  if (location.pathname !== loginPath) {
    const currentUser = await fetchUserInfo();
    return {
      fetchUserInfo,
      currentUser,
      settings: defaultSettings as Partial<LayoutSettings>,
    };
  }
  return {
    fetchUserInfo,
    settings: defaultSettings as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({initialState, setInitialState}) => {

  const theme = localStorage.getItem("navTheme");

  return {
    rightContentRender: () => <RightContent />,
    footerRender: () => <Footer />,
    siderWidth: 180,
    waterMarkProps: {
      content: initialState?.currentUser?.user.username + " " + new Date().toLocaleString(),
      fontColor: theme === THEME.light|| undefined ? "rgba(0, 0, 0, 0.15)" : "rgba(255, 255, 255, 0.15)",
    },
    isChildrenLayout: true,
    onPageChange: () => {
      const {location} = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    menuHeaderRender: undefined,
    // 自定义 403 页面
    unAccessible:<UnAccessible/>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          {/*{isDev && <SettingDrawer*/}
          {/*  disableUrlParams*/}
          {/*  enableDarkTheme*/}
          {/*  settings={initialState?.settings}*/}
          {/*  onSettingChange={(settings) => {*/}
          {/*    setInitialState((preInitialState) => ({*/}
          {/*      ...preInitialState,*/}
          {/*      settings,*/}
          {/*    }));*/}
          {/*  }}*/}
          {/*/>*/}
          {/*}*/}
        </>
      );
    },
    ...initialState?.settings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  ...errorConfig,
};
