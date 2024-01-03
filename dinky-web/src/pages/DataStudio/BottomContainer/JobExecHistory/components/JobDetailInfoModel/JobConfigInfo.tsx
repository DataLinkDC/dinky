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

import { JobExecutionHistory } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { FireOutlined } from '@ant-design/icons';
import ProDescriptions from '@ant-design/pro-descriptions';

import { Tag } from 'antd';
import React from 'react';

type JobDetailInfoModelProps = {
  row: JobExecutionHistory | undefined;
};

export const JobConfigInfo: React.FC<JobDetailInfoModelProps> = (props) => {
  const { row } = props;

  return (
    <>
      <ProDescriptions
        bordered
        size={'small'}
        column={2}
        title={l('pages.datastudio.label.history.execConfig')}
      >
        <ProDescriptions.Item label='JobId'>
          <Tag color='blue' key={row?.jobId}>
            <FireOutlined /> {row?.jobId}
          </Tag>
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.jobName')}>
          {row?.configJson?.jobName}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('global.table.runmode')}>
          {row?.configJson?.useRemote
            ? l('global.table.runmode.remote')
            : l('global.table.runmode.local')}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.taskType')}>
          {row?.configJson?.type}
        </ProDescriptions.Item>

        <ProDescriptions.Item label={l('pages.datastudio.label.history.clusterType')}>
          {row?.clusterId
            ? l('pages.datastudio.label.history.clusterInstance')
            : row?.clusterConfigurationId
            ? l('pages.datastudio.label.history.clusterConfig')
            : l('pages.datastudio.label.history.local')}
        </ProDescriptions.Item>

        <ProDescriptions.Item
          label={
            row?.clusterId
              ? l('pages.datastudio.label.history.clusterId')
              : l('pages.datastudio.label.history.clusterConfigurationId')
          }
        >
          {row?.clusterId ? row?.clusterId : row?.clusterConfigurationId ?? 'None'}
        </ProDescriptions.Item>

        <ProDescriptions.Item label={l('pages.datastudio.label.history.clusterName')}>
          {row?.clusterName}
        </ProDescriptions.Item>

        <ProDescriptions.Item label={l('pages.datastudio.label.history.changelog')}>
          {row?.configJson?.useChangeLog ? l('button.enable') : l('button.disable')}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.maxRows')}>
          {row?.configJson?.maxRowNum}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.autoStop')}>
          {row?.configJson?.useAutoCancel ? l('button.enable') : l('button.disable')}
        </ProDescriptions.Item>
        <ProDescriptions.Item label='JobManagerAddress'>
          {row?.jobManagerAddress}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.jobId')}>
          {row?.configJson?.taskId}
        </ProDescriptions.Item>

        <ProDescriptions.Item label={l('pages.datastudio.label.history.fragment')}>
          {row?.configJson?.fragment ? l('button.enable') : l('button.disable')}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.statementSet')}>
          {row?.configJson?.statementSet ? l('button.enable') : l('button.disable')}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.parallelism')}>
          {row?.configJson?.parallelism}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.checkpoint')}>
          {row?.configJson?.checkpoint}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.savePointStrategy')}>
          {row?.configJson?.savepointStrategy}
        </ProDescriptions.Item>
        <ProDescriptions.Item label={l('pages.datastudio.label.history.savePointPath')}>
          {row?.configJson?.savePointPath}
        </ProDescriptions.Item>
      </ProDescriptions>
    </>
  );
};
