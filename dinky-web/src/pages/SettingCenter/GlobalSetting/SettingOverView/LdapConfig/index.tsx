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

import GeneralConfig from '@/pages/SettingCenter/GlobalSetting/SettingOverView/GeneralConfig';
import { LoadUser } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/LdapConfig/compontents/LoadUser';
import { TestLogin } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/LdapConfig/compontents/TestLogin';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { SuccessMessage } from '@/utils/messages';
import { ApiFilled } from '@ant-design/icons';
import { Space, Tag } from 'antd';
import React from 'react';

interface LdapConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
  auth: string;
}

export const LdapConfig = ({ data, onSave, auth }: LdapConfigProps) => {
  const [loading, setLoading] = React.useState(false);

  const testConnection = async () => {
    setLoading(true);
    const data = await queryDataByParams(API_CONSTANTS.LDAP_TEST_CONNECT);
    if (data) {
      SuccessMessage(l('sys.ldap.settings.testConnect.success', '', { count: data }));
    }
    setLoading(false);
  };

  const onSaveHandler = async (data: BaseConfigProperties) => {
    setLoading(true);
    await onSave(data);
    setLoading(false);
  };

  /**
   * render ldap test case toolbar
   */
  const renderToolBar = () => {
    return [
      <Space key={'ldapToolBar'}>
        <Tag icon={<ApiFilled />} color='#87d068' onClick={() => testConnection()}>
          {l('sys.ldap.settings.testConnect')}
        </Tag>
        <TestLogin />
        <LoadUser />
      </Space>
    ];
  };

  return (
    <>
      <GeneralConfig
        loading={loading}
        onSave={onSaveHandler}
        auth={auth}
        tag={
          <>
            <Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag>
          </>
        }
        data={data}
        toolBarRender={renderToolBar}
      />
    </>
  );
};
