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

import Pop from '@/components/Animation/Pop';
import { LogSvg } from '@/components/Icons/CodeLanguageIcon';
import { loginOut } from '@/components/RightContent/AvatarDropdown';
import PasswordForm from '@/pages/AuthCenter/User/components/PasswordModal/PasswordForm';
import BaseInfo from '@/pages/Other/PersonCenter/BaseInfo';
import LoginLogRecord from '@/pages/Other/PersonCenter/LoginLogRecord';
import OperationLogRecord from '@/pages/Other/PersonCenter/OperationLogRecord';
import { handleOption } from '@/services/BusinessCrud';
import { RESPONSE_CODE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { UserBaseInfo } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { SuccessMessage } from '@/utils/messages';
import { useModel } from '@@/exports';
import { SecurityScanTwoTone } from '@ant-design/icons';
import { PageContainer, PageLoading, ProCard } from '@ant-design/pro-components';
import { Descriptions, Divider, Form, Tag } from 'antd';
import { useEffect, useState } from 'react';

const PersonCenter = () => {
  const [form] = Form.useForm();

  const { initialState, setInitialState } = useModel('@@initialState');
  const [activeKey, setActiveKey] = useState('loginlog');
  const loading = <PageLoading />;

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      setInitialState((s) => ({
        ...s,
        currentUser: userInfo
      }));
    }
  };

  useEffect(() => {
    fetchUserInfo();
  }, [initialState?.currentUser]);

  if (!initialState) {
    return loading;
  }
  const { currentUser } = initialState;
  if (!currentUser || !currentUser.user.username) {
    return loading;
  }

  const { roleList, tenantList, currentTenant, user } = currentUser;

  /**
   * renderTenantTagList
   * @param {UserBaseInfo.Tenant[]} items
   * @returns {JSX.Element[] | undefined}
   */
  const renderTenantTagList = (items: UserBaseInfo.Tenant[]) => {
    return items?.map((item: UserBaseInfo.Tenant) => {
      return (
        <Descriptions.Item key={item.id}>
          <Tag color={'success'} key={item.id}>
            {item.tenantCode}
          </Tag>
        </Descriptions.Item>
      );
    });
  };

  /**
   * renderRoleTagList
   * @param {UserBaseInfo.Role[]} items
   * @returns {JSX.Element[] | undefined}
   */
  const renderRoleTagList = (items: UserBaseInfo.Role[]) => {
    return items?.map((item: UserBaseInfo.Role) => {
      return (
        <Descriptions.Item key={item.id}>
          <Tag color={'success'} key={item.id}>
            {item.roleCode}
          </Tag>
        </Descriptions.Item>
      );
    });
  };

  /**
   * handleSubmitPassWord
   * @param value
   * @returns {Promise<void>}
   */
  const handleSubmitPassWord = async (value: UserBaseInfo.ChangePasswordParams) => {
    const result = await handleOption(
      API_CONSTANTS.USER_MODIFY_PASSWORD,
      l('button.changePassword'),
      value
    );
    if (result && result.code === RESPONSE_CODE.SUCCESS) {
      form.resetFields();
      (await SuccessMessage(l('user.changePasswordSuccess'))) && (await loginOut());
    }
  };

  /**
   * tabList
   * @type {({children: JSX.Element, label: JSX.Element, key: string} | {children: JSX.Element, label: JSX.Element, key: string})[]}
   */
  const tabList = [
    {
      key: 'loginlog',
      label: (
        <>
          <LogSvg />
          {l('user.loginlog')}
        </>
      ),
      children: (
        <>
          <LoginLogRecord userId={user?.id} />
        </>
      )
    },
    {
      key: 'operation',
      label: (
        <>
          <LogSvg />
          {l('user.op')}
        </>
      ),
      children: (
        <>
          <OperationLogRecord userId={user?.id} />
        </>
      )
    },
    {
      key: 'changePassword',
      label: (
        <>
          <SecurityScanTwoTone />
          {l('button.changePassword')}
        </>
      ),
      children: (
        <PasswordForm form={form} renderSubmit values={user} onSubmit={handleSubmitPassWord} />
      )
    }
  ];

  /**
   * render
   */
  return (
    <Pop>
      <PageContainer title={false}>
        <ProCard ghost gutter={[16, 16]} hoverable loading={!loading && currentUser}>
          <ProCard
            style={{ height: '91vh', textAlign: 'center', overflowY: 'auto' }}
            colSpan='22%'
            hoverable
            bordered
          >
            <BaseInfo user={user} tenant={currentTenant} />
            <Divider orientation={'left'} plain>
              {l('user.tenant')}
            </Divider>
            <Descriptions size={'small'} column={3}>
              {renderTenantTagList(tenantList || [])}
            </Descriptions>
            <Divider plain></Divider>
            <Divider orientation={'left'} plain>
              {l('user.role')}
            </Divider>
            <Descriptions size={'small'} column={3}>
              {renderRoleTagList(roleList || [])}
            </Descriptions>
          </ProCard>

          <ProCard
            hoverable
            bordered
            style={{ height: '91vh', textAlign: 'center', overflowY: 'auto' }}
            tabs={{
              activeKey: activeKey,
              type: 'card',
              animated: true,
              onChange: (key: string) => setActiveKey(key),
              items: tabList
            }}
          />
        </ProCard>
      </PageContainer>
    </Pop>
  );
};

export default PersonCenter;
