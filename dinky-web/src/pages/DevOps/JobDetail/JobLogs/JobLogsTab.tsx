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


import {Descriptions, Tabs, Tag, Typography} from 'antd';
import {Jobs} from "@/types/DevOps/data";
import {ProCard} from "@ant-design/pro-components";
import ExceptionTab from "@/pages/DevOps/JobDetail/JobLogs/components/ExceptionTab";
import JobManagerLogsTab from "@/pages/DevOps/JobDetail/JobLogs/components/JobManagerLogsTab";
import React from "react";
import TaskManagerLogsTab from "@/pages/DevOps/JobDetail/JobLogs/components/TaskManagerLogsTab";
import {JobProps} from "@/pages/DevOps/JobDetail/data";

const {Text, Paragraph} = Typography;

const JobLogsTab = (props: JobProps) => {

  const {jobDetail} = props;

  return <>
    {/*<ProCard>*/}
    <Tabs
      tabPosition={'left'}
      size={"small"}
      items={
        [
          {
            label: 'Exception',
            key: 'Exception',
            children: <ExceptionTab jobDetail={jobDetail}/>,
          },

          {
            label: 'JobManager',
            key: 'JobManager',
            children: <JobManagerLogsTab jobDetail={jobDetail}/>,
          },
          {
            label: 'TaskManager',
            key: 'TaskManager',
            children: <TaskManagerLogsTab jobDetail={jobDetail}/>,
          },
        ]
      }
    />
    {/*</ProCard>*/}
  </>
};

export default JobLogsTab;
