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

import useThemeValue from '@/hooks/useThemeValue';
import JobRunningModal from '@/pages/DataStudio/FooterContainer/JobRunningModal';
import { getCurrentTab } from '@/pages/DataStudio/function';
import { StateType, TabsPageType, VIEW } from '@/pages/DataStudio/model';
import { getSseData } from '@/services/api';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { Button, GlobalToken, Space } from 'antd';
import React, { useEffect, useState } from 'react';

export type FooterContainerProps = {
  token: GlobalToken;
};

type ButtonRoute = {
  text: React.ReactNode;
  title: string;
  onClick?: () => void;
  isShow?: (type?: TabsPageType, subType?: string, data?: any) => boolean;
};

const FooterContainer: React.FC<FooterContainerProps & StateType> = (props) => {
  const {
    footContainer: {
      memDetails,
      codeType,
      lineSeparator,
      codeEncoding,
      space,
      codePosition,
      jobRunningMsg
    },
    token,
    tabs
  } = props;

  const themeValue = useThemeValue();
  const [viewJobRunning, setViewJobRunning] = useState(false);
  const [memDetailInfo, setMemDetailInfo] = useState(memDetails);
  const currentTab = getCurrentTab(tabs.panes ?? [], tabs.activeKey);

  useEffect(() => {
    const eventSource = getSseData('/api/monitor/getJvmInfo');
    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data).data;
      setMemDetailInfo(
        Number(data['heapUsed'] / 1024 / 1024).toFixed(0) +
          '/' +
          Number(data['max'] / 1024 / 1024).toFixed(0) +
          'M'
      );
    };
    return () => {
      eventSource.close();
    };
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
      }),
      isShow: () => true
    },
    {
      text: codeType,
      title: l('pages.datastudio.footer.codeType') + codeType,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: lineSeparator,
      title: l('pages.datastudio.footer.lineSeparator') + lineSeparator,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: codeEncoding,
      title: l('pages.datastudio.footer.codeEncoding') + codeEncoding,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: 'Space: ' + space,
      title: 'Space: ' + space,
      isShow: (type) => TabsPageType.project === type
    },
    {
      text: codePosition[0] + ':' + codePosition[1],
      title: l('pages.datastudio.footer.codePosition', '', {
        Ln: codePosition[0],
        Col: codePosition[1]
      }),
      isShow: (type) => TabsPageType.project === type
    }
  ];

  /**
   * render footer right info
   */
  const renderFooterRightInfo = (routes: ButtonRoute[]) => {
    return routes
      .filter((x) => {
        if (x.isShow) {
          return x.isShow(currentTab?.type, currentTab?.subType, currentTab?.params);
        }
        return false;
      })
      .map((item, index) => (
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

  return (
    <>
      <div
        style={{
          backgroundColor: themeValue.footerColor,
          height: VIEW.footerHeight,
          width: '100%',
          display: 'flex',
          paddingInline: 10,
          position: 'fixed',
          bottom: 0,
          right: 0,
          left: 0
        }}
      >
        <Space style={{ direction: 'ltr', width: '30%%' }}>
          <Button size={'small'} type={'text'} block style={{ paddingInline: 4 }}>
            Welcome to Dinky !
          </Button>
        </Space>
        <Space onClick={() => setViewJobRunning(true)} style={{ direction: 'rtl', width: '30%' }}>
          {jobRunningMsg.jobName} - {jobRunningMsg.runningLog}
        </Space>
        <Space style={{ direction: 'rtl', width: '70%' }} size={4} direction={'horizontal'}>
          {renderFooterRightInfo(route)}
        </Space>
      </div>
      <JobRunningModal
        value={jobRunningMsg}
        //TODO 目前实现不了，禁掉
        visible={false}
        onCancel={() => setViewJobRunning(false)}
        onOk={() => setViewJobRunning(false)}
      />
    </>
  );
};
export default connect(({ Studio }: { Studio: StateType }) => ({
  footContainer: Studio.footContainer,
  tabs: Studio.tabs
}))(FooterContainer);
