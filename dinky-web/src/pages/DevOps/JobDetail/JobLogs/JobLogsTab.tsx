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

import { JobProps } from '@/pages/DevOps/JobDetail/data';
import ExceptionTab from '@/pages/DevOps/JobDetail/JobLogs/components/ExceptionTab';
import JobManagerLogsTab from '@/pages/DevOps/JobDetail/JobLogs/components/JobManagerLogsTab';
import TaskManagerLogsTab from '@/pages/DevOps/JobDetail/JobLogs/components/TaskManagerLogsTab';
import { ProCard } from '@ant-design/pro-components';
import { useState } from 'react';

const JobLogsTab = (props: JobProps) => {
  const { jobDetail } = props;

  const [activeKey, setActiveKey] = useState('Exception');

  const tabsItems = [
    {
      label: 'Exception',
      key: 'Exception',
      children: <ExceptionTab jobDetail={jobDetail} />
    },

    {
      label: 'JobManager',
      key: 'JobManager',
      children: <JobManagerLogsTab jobDetail={jobDetail} />
    },
    {
      label: 'TaskManager',
      key: 'TaskManager',
      children: <TaskManagerLogsTab jobDetail={jobDetail} />
    }
  ];

  return (
    <>
      <ProCard
        tabs={{
          size: 'small',
          tabPosition: 'left',
          type: 'card',
          activeKey: activeKey,
          onChange: setActiveKey,
          items: tabsItems
        }}
      />
    </>
  );
};

export default JobLogsTab;
