/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
import StatusTag from '@/components/JobTags/StatusTag';
import { queryList } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { parseSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { useRef } from 'react';

type HistoryProps = {
  taskId: number;
};

const JobHistoryList = (props: HistoryProps) => {
  const { taskId } = props;

  const actionRef = useRef<ActionType>();

  const jobListColumns: ProColumns<Jobs.JobInstance>[] = [
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime'
    },
    {
      title: l('global.table.endTime'),
      dataIndex: 'finishTime',
      valueType: 'dateTime'
    },
    {
      title: l('global.table.jobid'),
      dataIndex: 'jid',
      key: 'jid'
    },
    {
      title: l('global.table.status'),
      dataIndex: 'status',
      render: (_: any, row: { status: string | undefined }) => <StatusTag status={row.status} />
    },
    {
      title: l('global.table.useTime'),
      render: (_: any, row: { duration: number }) => parseSecondStr(row.duration)
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      render: () => [<a key={'history-detail'}>{l('devops.joblist.detail')}</a>]
    }
  ];

  return (
    <>
      <ProTable<Jobs.JobInstance>
        search={false}
        params={{ isHistory: true }}
        tableStyle={{
          overflowX: 'hidden',
          overflowY: 'hidden',
          margin: '10px'
        }}
        size={'small'}
        columns={jobListColumns}
        actionRef={actionRef}
        request={(params) =>
          queryList(API_CONSTANTS.GET_JOB_LIST, {
            ...params,
            filter: { task_id: [taskId] }
          })
        }
        rowKey={(record) => record.id}
        toolBarRender={false}
        pagination={{ showSizeChanger: false }}
      />
    </>
  );
};
export default JobHistoryList;
