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
import {JOB_STATUS} from "@/services/constants";

export type JobStatusFormProps = {
  status: string | undefined;
};

const JobStatus = (props: JobStatusFormProps) => {

  const {status} = props;

  const getStatusRender = (status:string|undefined) => {
    switch (status) {
      case JOB_STATUS.RUNNING:
        return <Tag icon={<SyncOutlined spin/>} color="green">RUNNING</Tag>
      case JOB_STATUS.FINISHED:
        return <Tag icon={<CheckCircleOutlined/>} color="blue">FINISHED</Tag>
      case JOB_STATUS.CANCELED:
        return <Tag icon={<MinusCircleOutlined/>} color="orange">CANCELED</Tag>
      case JOB_STATUS.INITIALIZING:
        return <Tag icon={<ClockCircleOutlined/>} color="default">INITIALIZING</Tag>
      case JOB_STATUS.RESTARTING:
        return <Tag icon={<ClockCircleOutlined/>} color="default">RESTARTING</Tag>
      case JOB_STATUS.CREATED:
        return <Tag icon={<ClockCircleOutlined/>} color="default">CREATED</Tag>
      case JOB_STATUS.UNKNOWN:
        return <Tag icon={<QuestionCircleOutlined/>} color="default">UNKNOWN</Tag>
      default:
        return <Tag icon={<CloseCircleOutlined/>} color="error">FAILED</Tag>;
    }
  }

  return (<>{getStatusRender(status)}</>)
};

export default JobStatus;
