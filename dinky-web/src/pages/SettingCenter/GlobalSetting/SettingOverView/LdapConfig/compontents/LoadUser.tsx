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

import { getData } from '@/services/api';
import { handleOption } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { l } from '@/utils/intl';
import { UserSwitchOutlined } from '@ant-design/icons';
import { ModalForm } from '@ant-design/pro-components';
import { Input, Table, Tag } from 'antd';
import { ColumnsType } from 'antd/es/table';
import { useState } from 'react';

const columns: ColumnsType<UserBaseInfo.User> = [
  {
    title: l('user.username'),
    dataIndex: 'username'
  },
  {
    title: l('user.nickname'),
    dataIndex: 'nickname'
  },
  {
    title: l('sys.ldap.settings.loadable'),
    dataIndex: 'enabled',
    render: (_, record) => (record.enabled ? l('rc.ai.isSimple.yes') : l('rc.ai.isSimple.no'))
  }
];

export const LoadUser = () => {
  const [loading, setLoading] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState<UserBaseInfo.User[]>([]);
  const [users, setUsers] = useState<UserBaseInfo.User[]>([]);

  const [keyword, setKeyword] = useState('');

  const fetchUserData = async () => {
    setLoading(true);
    const res = await getData(API_CONSTANTS.LDAP_LIST_USER);
    setUsers(res.data);
    setLoading(false);
  };

  const importUser = async () => {
    await handleOption(
      API_CONSTANTS.LDAP_IMPORT_USERS,
      l('sys.ldap.settings.loadUser'),
      selectedUsers
    );
    await fetchUserData();
    setSelectedUsers([]);
  };

  return (
    <>
      <ModalForm
        title={l('sys.ldap.settings.loadUser')}
        width={'50%'}
        submitter={{
          submitButtonProps: {
            disabled: selectedUsers.length === 0
          }
        }}
        onFinish={() => importUser()}
        trigger={
          <Tag icon={<UserSwitchOutlined />} color='#f50' onClick={() => fetchUserData()}>
            {l('sys.ldap.settings.loadUser')}
          </Tag>
        }
      >
        <Input.Search
          placeholder={l('sys.ldap.settings.keyword')}
          style={{ marginBottom: 8 }}
          enterButton
          loading={loading}
          onSearch={(value) => setKeyword(value)}
        />
        <Table<UserBaseInfo.User>
          loading={loading}
          size={'small'}
          columns={columns}
          rowSelection={{
            onChange: (_, rows) => setSelectedUsers(rows),
            getCheckboxProps: (record) => ({ disabled: !record.enabled })
          }}
          dataSource={users.filter(
            (user) =>
              user.username.indexOf(keyword) !== -1 || user.nickname?.indexOf(keyword) !== -1
          )}
          rowKey={'username'}
        />
      </ModalForm>
    </>
  );
};
