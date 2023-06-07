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

import {Space} from 'antd';
import RootLogs from "@/pages/SettingCenter/SystemLogs/TagInfo/RootLogs";
import LogList from "@/pages/SettingCenter/SystemLogs/TagInfo/LogList";
import React, {useState} from 'react';
import {ProCard} from '@ant-design/pro-components';
import {LogSvg} from '@/components/Icons/CodeLanguageIcon';


const TagInfo = () => {

  const [activeKey, setActiveKey] = useState('logs');

  // tab list
  const tabList = [
    {
      key: 'logs',
      label: <Space><LogSvg/>{'Root Logs'}</Space>,
      children: <RootLogs/>,
    },
    {
      key: 'logList',
      label: <Space><LogSvg/>{'Log List'}</Space>,
      children:  <LogList/>,
    },
  ];

  // tab props
  const restTabProps = {
    activeKey: activeKey,
    type: 'card',
    animated: true,
    onChange: (key: string) => setActiveKey(key),
    items: tabList,
  };

  /**
   * render
   */
  return <>
    <ProCard ghost className={'schemaTree'} size="small" bordered tabs={{...restTabProps}}/>
  </>;
};

export default TagInfo;
