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

import ConsoleContent from '@/pages/DataStudio/BottomContainer/Console/ConsoleContent';
import { StateType, TabsItemType } from '@/pages/DataStudio/model';
import { connect } from '@@/exports';
import { Tabs } from 'antd';
import React, { useEffect } from 'react';

const Console: React.FC = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;
  useEffect(() => {}, []);

  const tabItems = panes.map((item: TabsItemType) => ({
    key: item.key,
    label: <span style={{ paddingLeft: '5px' }}>{item.label}</span>,
    children: <ConsoleContent tab={item} />
  }));

  return (
    <Tabs activeKey={activeKey} size={'small'} items={tabItems} tabBarStyle={{ display: 'none' }} />
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console
}))(Console);
