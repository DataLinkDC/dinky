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

import CodeShow from '@/components/CustomEditor/CodeShow';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { API_CONSTANTS } from '@/services/endpoints';
import { useRequest } from '@@/exports';
import { ProCard } from '@ant-design/pro-components';
import { Spin } from 'antd';
import { useState } from 'react';

type ThreadDumpMessage = {
  stringifiedThreadInfo: string;
  threadName: string;
};

const JobManagerLogsTab = (props: JobProps) => {
  const { jobDetail } = props;
  const jmaddr = jobDetail?.history?.jobManagerAddress;

  const [activeKey, setActiveKey] = useState('LOG');

  const log = useRequest({
    url: API_CONSTANTS.GET_JOBMANAGER_LOG,
    params: { address: jmaddr }
  });

  const stdout = useRequest({
    url: API_CONSTANTS.GET_JOBMANAGER_STDOUT,
    params: { address: jmaddr }
  });

  const dump = useRequest({
    url: API_CONSTANTS.GET_JOBMANAGER_THREAD_DUMP,
    params: { address: jmaddr }
  });

  const getLog = (ur: any, language?: string) => {
    return (
      <Spin spinning={ur.loading}>
        <CodeShow
          showFloatButton
          language={language}
          code={ur.data ? ur.data : 'No Log'}
          height={parent.innerHeight - 300}
        />
      </Spin>
    );
  };

  const buildDumpLog = (ur: any) => {
    if (!ur.data) {
      return;
    } else {
      const threadInfos =
        JSON.parse(ur.data) && (JSON.parse(ur.data)['threadInfos'] as ThreadDumpMessage[]);
      if (!threadInfos) {
        return 'No Log';
      } else if (threadInfos && threadInfos.length === 0) {
        return 'No Thread Info';
      }
      return threadInfos.map((x: ThreadDumpMessage) => x.stringifiedThreadInfo).join('');
    }
  };

  const getDump = (ur: any, language?: string) => {
    return (
      <Spin spinning={ur.loading}>
        <CodeShow
          showFloatButton
          language={language}
          code={buildDumpLog(ur) ?? 'No Log'}
          height={'calc(100vh - 250px)'}
        />
      </Spin>
    );
  };

  return (
    <ProCard
      headerBordered
      bordered
      bodyStyle={{ height: parent.innerHeight, overflow: 'auto' }}
      tabs={{
        size: 'small',
        tabPosition: 'top',
        activeKey: activeKey,
        onChange: setActiveKey,
        items: [
          { label: 'Log', key: 'LOG', children: getLog(log, 'javalog') },
          { label: 'Std Out', key: 'STDOUT', children: getLog(stdout, 'javalog') },
          { label: 'Thread Dump', key: 'DUMP', children: getDump(dump, 'java') }
        ]
      }}
    />
  );
};

export default JobManagerLogsTab;
