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


import React, {useCallback, useRef} from 'react';
import {
  LogoutOutlined,
  SafetyOutlined,
  SecurityScanOutlined,
  SettingOutlined,
  UserSwitchOutlined
} from '@ant-design/icons';
import {Avatar, Menu, message, Modal, Spin} from 'antd';
import {history, useModel} from 'umi';
import {stringify} from 'querystring';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import {outLogin} from '@/services/ant-design-pro/api';
import {ActionType} from "@ant-design/pro-table";
import {postAll} from "@/components/Common/crud";

export type GlobalHeaderRightProps = {
  menu?: boolean;
};

/**
 * 退出登录，并且将当前的 url 保存
 */
const loginOut = async () => {
  await outLogin();
  const {query = {}, pathname} = history.location;
  const {redirect} = query;
  // Note: There may be security issues, please note
  if (window.location.pathname !== '/user/login' && !redirect) {
    history.replace({
      pathname: '/user/login',
      search: stringify({
        redirect: pathname,
      }),
    });
  }
};


const requestUrl = '/api/tenant/switchTenant';


const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({menu}) => {
  const {initialState, setInitialState} = useModel('@@initialState');
  const actionRef = useRef<ActionType>();

  const onMenuClick = useCallback(
    (event: {
      key: React.Key;
      keyPath: React.Key[];
      item: React.ReactInstance;
      domEvent: React.MouseEvent<HTMLElement>;
    }) => {
      const {key} = event;
      if (key === 'logout' && initialState) {
        setInitialState({...initialState, currentUser: undefined});
        loginOut();
        return;
      }
      // history.push(`/account/${key}`);
    },
    [initialState, setInitialState],
  );

  const loading = (
    <span className={`${styles.action} ${styles.account}`}>
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

  const {currentUser} = initialState;

  if (!currentUser || !currentUser.username) {
    return loading;
  }

  const getChooseTenantListForm = () => {
    let chooseTenantList: JSX.Element[] = [];
    currentUser.tenantList?.map((item) => {
      chooseTenantList.push(
        <>
          <Menu.Item
            // If the current key (tenant id) is equal to the tenant the current user chooses to log in, this item is not optional
            disabled={item.id === currentUser.currentTenant?.id}
            key={item.id}
            title={item.tenantCode}
            icon={<SecurityScanOutlined/>}
            onClick={(e) => {
              console.log(e)
              // get choose tenant title
              let title = e.domEvent.target.textContent;
              // get choose tenantId
              let tenantInfoId = e.key;
              Modal.confirm({
                title: '切换租户',
                content: '确定切换【' + title + '】租户吗?',
                okText: '确认',
                cancelText: '取消',
                onOk: async () => {
                  // 目前先直接退出登录 重新选择租户登录
                  loginOut();
                  // todo 切换租户需要将租户id 传入后端 以及本地存储中
                  // const {code, msg} = await postAll(requestUrl, {tenantId: tenantInfoId});
                  // localStorage.clear() // clear local storage
                  // localStorage.setItem('dlink-tenantId',tenantInfoId) // set tenant to localStorage
                  // code == 0 ? message.success(msg) : message.error(msg);
                  // todo
                  //  1.切换租户后 需要重新调用 /api/current接口获取用户的信息  (目前此接口从cookie直接取数 ,达不到预期效果)
                  //  2.同步刷新所有页面 获取该租户id下的数据
                  //actionRef.current?.reload()
                  // actionRef.current?.reloadAndRest?.();
                }
              });
            }}
          >
            {item.tenantCode}
          </Menu.Item>
        </>
      )
    })
    return <>
      <Menu.SubMenu
        key="chooseTenantList"
        title={"切换租户"}
        icon={<UserSwitchOutlined/>}
      >
        {chooseTenantList}
      </Menu.SubMenu>
    </>;
  }


  const menuHeaderDropdown = (
    <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick}>
      {menu && (
        getChooseTenantListForm()
      )}
      {menu && (
        <Menu.Item key="personSettings" disabled>
          <SettingOutlined/>
          个人设置
        </Menu.Item>
      )}
      {menu && (
        <Menu.Item key="changePassWord" disabled>
          <SafetyOutlined/>
          修改密码
        </Menu.Item>
      )}
      {menu && <Menu.Divider/>}
      <Menu.Item key="logout">
        <LogoutOutlined/>
        退出登录
      </Menu.Item>
    </Menu>
  );
  return (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar size="small" className={styles.avatar} src={currentUser.avatar} alt="avatar"/>
        <span className={`${styles.name} anticon`}>{currentUser.username}</span>
      </span>
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
