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

import { StatusTagProps } from '@/components/JobTags/data';
import { BackPressureStatus, JOB_STATUS } from '@/pages/DevOps/constants';
import {
  AlertTwoTone,
  CheckCircleOutlined,
  ClockCircleOutlined,
  InfoCircleOutlined,
  MinusCircleOutlined,
  QuestionCircleOutlined,
  SyncOutlined
} from '@ant-design/icons';
import { Tag } from 'antd';

/**
 * Renders a tag for the job status based on the provided status.
 *
 * @returns {JSX.Element} - The tag representing the job status.
 * @param props
 */
const StatusTag = (props: StatusTagProps) => {
  const { status, animation = true, bordered = true } = props;

  const buildParam = () => {
    switch (status) {
      case JOB_STATUS.RUNNING:
        return {
          icon: <SyncOutlined spin />,
          color: 'success',
          text: 'RUNNING'
        };
      case JOB_STATUS.FINISHED:
        return {
          icon: <CheckCircleOutlined />,
          color: 'blue',
          text: 'FINISHED'
        };
      case JOB_STATUS.FAILED:
        return {
          icon: <AlertTwoTone twoToneColor={'red'} />,
          color: 'red',
          text: 'FAILED'
        };
      case JOB_STATUS.CANCELED:
        return {
          icon: <MinusCircleOutlined />,
          color: 'orange',
          text: 'CANCELED'
        };
      case JOB_STATUS.INITIALIZING:
        return {
          icon: <ClockCircleOutlined />,
          color: 'default',
          text: 'INITIALIZING'
        };
      case JOB_STATUS.RESTARTING:
        return {
          icon: <ClockCircleOutlined />,
          color: 'default',
          text: 'RESTARTING'
        };
      case JOB_STATUS.CREATED:
        return {
          icon: <ClockCircleOutlined />,
          color: 'default',
          text: 'CREATED'
        };
      case JOB_STATUS.RECONNECTING:
        return {
          icon: <SyncOutlined spin />,
          color: 'warning',
          text: 'RECONNECTING'
        };
      case JOB_STATUS.UNKNOWN:
        return {
          icon: <QuestionCircleOutlined />,
          color: 'default',
          text: 'UNKNOWN'
        };
      case BackPressureStatus.OK:
        return {
          icon: <QuestionCircleOutlined />,
          color: 'success',
          text: 'OK'
        };
      case BackPressureStatus.HIGH:
        return {
          icon: <InfoCircleOutlined twoToneColor={'red'} />,
          color: 'error',
          text: 'HIGH'
        };
      default:
        return {
          icon: <QuestionCircleOutlined />,
          color: 'default',
          text: 'UNKNOWN'
        };
    }
  };

  const param = buildParam();
  return (
    <Tag icon={animation ? param.icon : undefined} color={param.color} bordered={bordered}>
      {param.text}
    </Tag>
  );
};

export default StatusTag;
