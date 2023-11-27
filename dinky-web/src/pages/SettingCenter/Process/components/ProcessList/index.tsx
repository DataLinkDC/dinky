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

import {
  MatchProcessStatus,
  MatchProcessType
} from '@/pages/SettingCenter/Process/components/ProcessList/function';
import SubStepsTable from '@/pages/SettingCenter/Process/components/ProcessList/SubStepsTable';
import { getData } from '@/services/api';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Process } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { ProTable } from '@ant-design/pro-components';
import { ProColumns } from '@ant-design/pro-table';
import React from 'react';

const ProcessList: React.FC = () => {
  const processColumns: ProColumns<Process>[] = [
    {
      title: l('sys.process.id'),
      dataIndex: 'pid'
    },
    {
      title: l('sys.process.name'),
      dataIndex: 'name'
    },
    {
      title: l('sys.process.taskId'),
      dataIndex: 'taskId'
    },
    {
      title: l('sys.process.type'),
      dataIndex: 'type',
      render: (_, record) => {
        return MatchProcessType(record.type);
      }
    },
    {
      title: l('sys.process.status'),
      dataIndex: 'status',
      render: (_, record) => {
        return MatchProcessStatus(record.status);
      }
    },
    {
      title: l('sys.process.startTime'),
      dataIndex: 'startTime',
      valueType: 'dateTime'
    },
    {
      title: l('sys.process.endTime'),
      dataIndex: 'endTime',
      valueType: 'dateTime'
    },
    {
      title: l('sys.process.duration'),
      dataIndex: 'time'
    },
    {
      title: l('sys.process.operator'),
      dataIndex: 'userId'
    }
  ];

  return (
    <>
      <ProTable<Process>
        headerTitle={false}
        {...PROTABLE_OPTIONS_PUBLIC}
        rowKey='pid'
        size={'small'}
        search={false}
        columns={processColumns}
        request={() => getData(API_CONSTANTS.PROCESS_LIST, { active: false })}
        expandable={{
          expandRowByClick: true,
          expandedRowRender: (record) => <SubStepsTable steps={record.steps} />
        }}
      />
    </>
  );
};

export default ProcessList;
