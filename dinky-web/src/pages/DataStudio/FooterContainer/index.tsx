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

import { l } from '@/utils/intl';
import { useModel } from '@@/exports';
import { Button, GlobalToken, Space } from 'antd';
import React, { useEffect, useState } from 'react';
import { SseData, Topic } from '@/models/UseWebSocketModel';
import { DataStudioState } from '@/pages/DataStudio/model';
import { formatDate } from '@/pages/DataStudio/FooterContainer/function';

type ButtonRoute = {
  text: React.ReactNode;
  title: string;
  onClick?: () => void;
};

export default (props: { token: GlobalToken; centerContent: DataStudioState['centerContent'] }) => {
  const { token, centerContent } = props;
  const [memDetailInfo, setMemDetailInfo] = useState('0/0M');
  const { subscribeTopic } = useModel('UseWebSocketModel', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));

  useEffect(() => {
    return subscribeTopic(Topic.JVM_INFO, null, (data: SseData) => {
      const respData = data.data['none-params'];
      setMemDetailInfo(
        Number(respData['heapUsed'] / 1024 / 1024).toFixed(0) +
          '/' +
          Number(respData['max'] / 1024 / 1024).toFixed(0) +
          'M'
      );
    });
  }, []);
  const route: ButtonRoute[] = [
    {
      text: (
        <span style={{ backgroundColor: token.colorBgContainer }}>
          <div
            style={{
              width:
                (1 -
                  parseInt(memDetailInfo.split('/')[0]) / parseInt(memDetailInfo.split('/')[1])) *
                  100 +
                '%',
              backgroundColor: token.colorFill
            }}
          >
            {memDetailInfo}
          </div>
        </span>
      ),
      title: l('pages.datastudio.footer.memDetails', '', {
        max: memDetailInfo.split('/')[1],
        used: memDetailInfo.split('/')[0]
      })
    }
  ];

  /**
   * render footer right info
   */
  const renderFooterRightInfo = (routes: ButtonRoute[]) => {
    return routes.map((item, index) => (
      <Button
        size={'small'}
        type={'text'}
        block
        style={{ paddingInline: 4 }}
        key={index}
        onClick={item.onClick}
        title={item.title}
      >
        {item.text}
      </Button>
    ));
  };
  const renderFooterLastUpdate = () => {
    const currentTab = centerContent?.tabs.find((item) => item.id === centerContent?.activeTab);
    if (currentTab && currentTab.tabType === 'task') {
      return (
        <div>
          {l('pages.datastudio.label.lastUpdateDes')}: {formatDate(currentTab.params.updateTime)}
        </div>
      );
    }
  };

  return (
    <>
      <div
        style={{
          backgroundColor: 'var(--footer-bg-color)',
          height: 25,
          width: '100%',
          display: 'flex',
          paddingInline: 10,
          position: 'fixed',
          bottom: 0,
          right: 0,
          left: 0,
          justifyContent: 'space-between'
        }}
      >
        <Space style={{ direction: 'ltr', width: '30%%' }}>
          <Button size={'small'} type={'text'} block style={{ paddingInline: 4 }}>
            Welcome to Dinky !
          </Button>
        </Space>
        <Space style={{ direction: 'rtl', width: '70%' }} size={4} direction={'horizontal'}>
          {renderFooterRightInfo(route)}
          {renderFooterLastUpdate()}
        </Space>
      </div>
    </>
  );
};
