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
import type {RunTimeLayoutConfig} from "@umijs/max";
import {history} from "@umijs/max";
import defaultSettings from "../config/defaultSettings";
import Settings from "../config/defaultSettings";
import {errorConfig} from "./requestErrorConfig";
import {currentUser as queryCurrentUser} from "./services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import {THEME} from "@/types/Public/data";
import {UnAccessible} from "@/pages/Other/403";
import {l} from "@/utils/intl";

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
  const fetchUserInfo = async () =>
    queryCurrentUser().then((result) => {
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
    }, (error) => {
      history.push(loginPath);
      return undefined;
    });

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
    headerTitleRender: () => {
      // 重新对 title 的设置进行设置
      Settings.title = l('layouts.userLayout.title');
      // 重新对 logo 的设置进行设置 由于 logo 是一个组件，所以需要重新渲染, 重新渲染的时候，会重新执行一次 layout
      return <>
        <img height={50} width={50} src={Settings.logo}/>
        <span style={{marginLeft: 10, color: 'white'}}>{Settings.title}</span>
      </>;
    },
    rightContentRender: () => <RightContent/>,
    footerRender: () => <Footer/>,
    siderWidth: 180,
    waterMarkProps: {
      content: initialState?.currentUser?.user.username + " " + new Date().toLocaleString(),
      fontColor: theme === THEME.light || undefined ? "rgba(0, 0, 0, 0.15)" : "rgba(255, 255, 255, 0.15)",
    },
    isChildrenLayout: true,
    onPageChange: () => {
      const {location} = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    // 自定义 403 页面
    unAccessible: <UnAccessible/>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      if (initialState?.loading) return <PageLoading/>;
      return (
        <>
          {children}
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
