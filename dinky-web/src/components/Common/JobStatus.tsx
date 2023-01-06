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


import {Tag} from 'antd';
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  MinusCircleOutlined,
  QuestionCircleOutlined,
  SyncOutlined
} from "@ant-design/icons";
import {l} from "@/utils/intl";

export type JobStatusFormProps = {
  status: string | undefined;
};

export const JOB_STATUS = {
  FINISHED: 'FINISHED',
  RUNNING: 'RUNNING',
  FAILED: 'FAILED',
  CANCELED: 'CANCELED',
  INITIALIZING: 'INITIALIZING',
  RESTARTING: 'RESTARTING',
  CREATED: 'CREATED',
  FAILING: 'FAILING',
  SUSPENDED: 'SUSPENDED',
  CANCELLING: 'CANCELLING',
  UNKNOWN: 'UNKNOWN',
};

export function isStatusDone(type: string) {
  if (!type) {
    return true;
  }
  switch (type) {
    case JOB_STATUS.FAILED:
    case JOB_STATUS.CANCELED:
    case JOB_STATUS.FINISHED:
    case JOB_STATUS.UNKNOWN:
      return true;
    default:
      return false;
  }
};

const JobStatus = (props: JobStatusFormProps) => {

  const {status} = props;

  return (<>
    {(status === 'FINISHED') ?
      (<Tag icon={<CheckCircleOutlined/>} color="blue">
        {l('pages.devops.jobstatus.FINISHED')}
      </Tag>) : (status === 'RUNNING') ?
        (<Tag icon={<SyncOutlined spin/>} color="green">
          {l('pages.devops.jobstatus.RUNNING')}
        </Tag>) : (status === 'FAILED') ?
          (<Tag icon={<CloseCircleOutlined/>} color="error">
            {l('pages.devops.jobstatus.FAILED')}
          </Tag>) : (status === 'CANCELED') ?
            (<Tag icon={<MinusCircleOutlined/>} color="orange">
              {l('pages.devops.jobstatus.CANCELED')}
            </Tag>) : (status === 'INITIALIZING') ?
              (<Tag icon={<ClockCircleOutlined/>} color="default">
                {l('pages.devops.jobstatus.INITIALIZING')}
              </Tag>) : (status === 'RESTARTING') ?
                (<Tag icon={<ClockCircleOutlined/>} color="default">
                  {l('pages.devops.jobstatus.RESTARTING')}
                </Tag>) : (status === 'CREATED') ?
                  (<Tag icon={<ClockCircleOutlined/>} color="default">
                    {l('pages.devops.jobstatus.CREATED')}
                  </Tag>) :
                  (<Tag icon={<QuestionCircleOutlined/>} color="default">
                    {l('pages.devops.jobstatus.UNKNOWN')}
                  </Tag>)
    }
  </>)
};

export default JobStatus;
