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

import { LogSvg } from '@/components/Icons/CodeLanguageIcon';
import { AuthorizedObject, useAccess } from '@/hooks/useAccess';
import LogList from '@/pages/SettingCenter/SystemLogs/TagInfo/LogList';
import RootLogs from '@/pages/SettingCenter/SystemLogs/TagInfo/RootLogs';
import { PermissionConstants } from '@/types/Public/constants';
import { ProCard } from '@ant-design/pro-components';
import { Space } from 'antd';
import { useEffect, useState } from 'react';

const TagInfo = () => {
  const [activeKey, setActiveKey] = useState('logs');
  const [tags, setTags] = useState([]);
  const access = useAccess();

  // tab list
  const tabList = [
    {
      key: 'logs',
      label: (
        <Space>
          <LogSvg />
          Root Logs
        </Space>
      ),
      children: <RootLogs />,
      path: PermissionConstants.SYSTEM_SETTING_INFO_ROOT_LOG
    },
    {
      key: 'logList',
      label: (
        <Space>
          <LogSvg />
          Log List
        </Space>
      ),
      children: <LogList />,
      path: PermissionConstants.SYSTEM_SETTING_INFO_LOG_LIST
    }
  ];

  useEffect(() => {
    const filterResultTags = tabList.filter(
      (menu) =>
        !!!menu.path || !!AuthorizedObject({ path: menu.path, children: menu.label, access })
    );
    setTags(filterResultTags as []); // set tags
    setActiveKey(filterResultTags[0]?.key ?? 'logs'); // set default active key
  }, []);

  /**
   * render
   */
  return (
    <>
      <ProCard
        ghost
        className={'schemaTree'}
        size='small'
        bordered
        tabs={{
          size: 'small',
          activeKey: activeKey,
          type: 'card',
          animated: true,
          onChange: (key: string) => setActiveKey(key),
          items: tags
        }}
      />
    </>
  );
};

export default TagInfo;
