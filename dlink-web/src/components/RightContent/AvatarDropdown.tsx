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
import {GroupOutlined, LogoutOutlined, UsergroupAddOutlined} from '@ant-design/icons';
import {Avatar, Menu, Modal, Spin} from 'antd';
import {history, useModel} from 'umi';
import {stringify} from 'querystring';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import {outLogin} from '@/services/ant-design-pro/api';
import {ActionType} from "@ant-design/pro-table";

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
      } else if (key === 'chooseTenant') {
        //TODO:
        // 1.先刷新当前用户所有的租户  因为如果在角色管理中重新赋予角色 会触发增删角色(角色关联租户)
        // 2.选择租户回调 选择租户 直接使用 modal
        Modal.confirm({
          title: '切换租户',
          content: '确定切换【' + 11 + '】租户吗?',
          okText: '确认',
          cancelText: '取消',
          onOk: async () => {
            // TODO: handle
            // await handleRemove(url,[currentItem]);
            actionRef.current?.reloadAndRest?.();
          }
        });
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
    console.log(currentUser.roleDTOList)
    currentUser.roleDTOList?.map((item) => {
      chooseTenantList.push(
        <>
          <Menu.Item
            key={item.tenant?.id}
            title={item.tenant?.tenantCode}
            icon={<UsergroupAddOutlined/>}
            defaultValue={item.tenant?.id}
          >
            {item.tenant?.tenantCode}
          </Menu.Item>
        </>
      )
    })
    return <>
      <Menu.SubMenu
        key="chooseTenantList"
        title={"切换租户"}
        icon={<GroupOutlined/>}
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
      {/*{menu && (*/}
      {/*  <Menu.Item key="settings">*/}
      {/*    <SettingOutlined/>*/}
      {/*    个人设置*/}
      {/*  </Menu.Item>*/}
      {/*)}*/}
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
