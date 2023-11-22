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

import { SubTask, Task } from '@/pages/Metrics/Job/data';
import { l } from '@/utils/intl';
import { SyncOutlined } from '@ant-design/icons';
import { Tag, Typography } from 'antd';

const { Paragraph } = Typography;

/**
 * build flink job metrics list  ( 3 level)
 * @returns {{label: JSX.Element, value: string, key: string}[]}
 * @param subTasks
 */
export const buildMetricsList = (subTasks: string[]) =>
  subTasks.map((subTask) => {
    // build label
    let label = (
      <div className={'tag-content'}>
        <Tag icon={<SyncOutlined spin />} color={'processing'}>
          {l('metrics.flink.metrics.name')}: {subTask}
        </Tag>
      </div>
    );

    return {
      key: subTask,
      label: label,
      value: subTask
    };
  });

/**
 * build flink job vertices list ( 2 level)
 * @returns {{children: {label: JSX.Element, value: string, key: string}[], label: JSX.Element, value: string, key: string}[]}
 * @param subTaskList
 */
export const buildSubTaskList = (subTaskList: SubTask[]) =>
  subTaskList.map((subTask) => {
    // build label
    let label = (
      <div className={'tag-content'}>
        <Tag icon={<SyncOutlined spin />} color={'success'}>
          {subTask.name}
        </Tag>
      </div>
    );

    return {
      key: subTask.name,
      label: label,
      value: subTask.id
    };
  });

/**
 * build running job list ( 1 level)
 * @param {Task[]} metrics
 * @returns {{label: JSX.Element, value: number, key: string}[]}
 */
export const buildRunningJobList = (metrics: Task[]) =>
  metrics.map((item) => {
    // build label
    let label = (
      <div className={'tag-content'}>
        <Tag color={'processing'}>
          {l('metrics.flink.jobId')}: {item.jid}
        </Tag>
        <Tag color={'success'}>
          {l('metrics.flink.taskId')}: {item.id}
        </Tag>
        <Tag color={'success'}>
          {l('metrics.flink.job.name')}: {item.name}
        </Tag>
      </div>
    );

    return {
      key: item.name,
      label: label,
      value: item.id
    };
  });

/**
 * render metrics chart title
 * @param {string} metricsId
 * @returns {JSX.Element}
 */
export const renderMetricsChartTitle = (metricsId: string, titleWidth: string | number) => {
  return (
    <>
      <Paragraph style={{ width: titleWidth }} code ellipsis={{ tooltip: true }}>
        {metricsId}
      </Paragraph>
    </>
  );
};
