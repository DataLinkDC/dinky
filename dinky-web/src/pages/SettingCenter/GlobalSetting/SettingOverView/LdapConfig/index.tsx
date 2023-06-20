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


import {BaseConfigProperties} from '@/types/SettingCenter/data';
import {ProCard} from '@ant-design/pro-components';
import GeneralConfig from '@/pages/SettingCenter/GlobalSetting/SettingOverView/GeneralConfig';
import {l} from '@/utils/intl';
import {Space, Tag} from 'antd';
import React from 'react';
import {
  ApiFilled,
} from "@ant-design/icons";
import {queryDataByParams} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import {SuccessMessage} from "@/utils/messages";
import {TestLogin} from "@/pages/SettingCenter/GlobalSetting/SettingOverView/LdapConfig/compontents/TestLogin";
import {LoadUser} from "@/pages/SettingCenter/GlobalSetting/SettingOverView/LdapConfig/compontents/LoadUser";

interface LdapConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
}

export const LdapConfig = ({data, onSave}: LdapConfigProps) => {

  const [loading, setLoading] = React.useState(false);

  const testConnection = async () => {
    setLoading(true);
    const datas = await queryDataByParams(API_CONSTANTS.LDAP_TEST_CONNECT);
    if (datas) {
      SuccessMessage(l("sys.ldap.settings.testConnect.success","",{count:datas}))
    }
    setLoading(false);
  }

  const onSaveHandler = (data: BaseConfigProperties) => {
    setLoading(true);
    onSave(data);
    setTimeout(() => {
      setLoading(false);
    }, 1000);
  };

  /**
   * render ldap test case toolbar
   */
  const renderToolBar = () => {
    return [
      <Space key={"ldapToolBar"}>
        <Tag icon={<ApiFilled/>} color="#87d068" onClick={() => testConnection()}>
          {l("sys.ldap.settings.testConnect")}
        </Tag>
        <TestLogin/>
        <LoadUser/>
      </Space>
    ];
  };

  return <>
    <ProCard
      title={l('sys.setting.ldap')}
      tooltip={l('sys.setting.ldap.tooltip')}
      size="small"
      headerBordered ghost collapsible
      defaultCollapsed={false}
    >
      <GeneralConfig
        loading={loading}
        onSave={onSaveHandler}
        tag={<><Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag></>}
        data={data}
        toolBarRender={renderToolBar}
      />
    </ProCard>
  </>;
};
