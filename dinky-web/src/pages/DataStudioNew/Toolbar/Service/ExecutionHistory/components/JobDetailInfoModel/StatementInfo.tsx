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
import { CustomEditorLanguage } from '@/components/CustomEditor/languages/constants';
import { JobExecutionHistory } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { FireOutlined } from '@ant-design/icons';
import ProDescriptions from '@ant-design/pro-descriptions';
import { Tag } from 'antd';
import React from 'react';

type StatementInfoProps = {
  row: JobExecutionHistory | undefined;
};

export const StatementInfo: React.FC<StatementInfoProps> = (props) => {
  const { row } = props;

  return (
    <>
      <ProDescriptions
        column={6}
        size={'middle'}
        title={l('pages.datastudio.label.history.statement')}
      >
        <ProDescriptions.Item label='Jobname'>
          <Tag color={row?.jobName ? 'blue' : 'red'} key={row?.jobName}>
            <FireOutlined /> {row?.jobName ?? l('global.job.status.failed-tip')}
          </Tag>
        </ProDescriptions.Item>
        <ProDescriptions.Item label='JobId'>
          <Tag color={row?.jobId ? 'blue' : 'red'} key={row?.jobId}>
            <FireOutlined /> {row?.jobId ?? l('global.job.status.failed-tip')}
          </Tag>
        </ProDescriptions.Item>
      </ProDescriptions>
      <CodeShow
        showFloatButton
        autoWrap={'on'}
        width={'100%'}
        height={'60vh'}
        language={CustomEditorLanguage.FlinkSQL}
        code={row?.statement ?? ''}
      />
    </>
  );
};
