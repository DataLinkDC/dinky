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

import { useModel } from '@@/exports';
import React, { useEffect, useState } from 'react';
import { Avatar, Card, Descriptions, Space, Tag } from 'antd';
import { getCurrentDateStr, getRandomGreeting } from '@/pages/Home/util';
import { API } from '@/services/data';
import { UserBaseInfo } from '@/types/AuthCenter/data';

const WorkHeader = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const [curTime, setCurTime] = useState(getCurrentDateStr());
  const [dayTip, setDayTip] = useState('');
  const { roleList, tenantList, currentTenant, user } =
    initialState?.currentUser as API.CurrentUser;

  useEffect(() => {
    const loop = setInterval(() => {
      setCurTime(getCurrentDateStr());
    }, 1000);
    setDayTip(getRandomGreeting(user?.nickname ?? user?.username));
    return () => {
      clearInterval(loop);
    };
  }, []);

  const renderRoleTagList = (items: UserBaseInfo.Role[]) => {
    return items?.map((item: UserBaseInfo.Role) => {
      return (
        <Descriptions.Item key={item.id}>
          <Tag color={'purple'} key={item.id}>
            {item.roleCode}
          </Tag>
        </Descriptions.Item>
      );
    });
  };
  const renderTenantTagList = (items: UserBaseInfo.Tenant[]) => {
    return items?.map((item: UserBaseInfo.Tenant) => {
      return (
        <Descriptions.Item key={item.id}>
          <Tag color={'orange'} key={item.id}>
            {item.tenantCode}
          </Tag>
        </Descriptions.Item>
      );
    });
  };

  return (
    <div style={{ position: 'relative' }}>
      <Card bordered={false}>
        <Space
          style={{
            fontSize: 20,
            marginBottom: 20
          }}
        >
          <Avatar size={60} src={user?.avatar} />
          {dayTip}
        </Space>
        <div>
          {user.worknum && <Tag color='magenta'>{user.worknum}</Tag>}
          {renderRoleTagList(roleList || [])}
          {renderTenantTagList(tenantList || [])}
        </div>
      </Card>
      <div
        style={{
          position: 'absolute',
          top: 20,
          right: 20,
          fontSize: 18
        }}
      >
        {curTime}
      </div>
    </div>
  );
};

export default WorkHeader;
