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

import JobLifeCycleTag from '@/components/JobTags/JobLifeCycleTag';
import StatusTag from '@/components/JobTags/StatusTag';
import { DevopContext } from '@/pages/DevOps';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import JobHistoryList from '@/pages/DevOps/JobList/components/JobHistoryList/JobHistoryList';
import { queryList } from '@/services/api';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { parseMilliSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { ClockCircleTwoTone, EyeTwoTone, RedoOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProCard, ProTable } from '@ant-design/pro-components';
import { Button, Radio, Table } from 'antd';
import { useContext, useEffect, useRef, useState } from 'react';
import { history } from 'umi';

const JobList = () => {
  const tableRef = useRef<ActionType>();
  const { statusFilter, setStatusFilter } = useContext<any>(DevopContext);
  const [stepFilter, setStepFilter] = useState<number | undefined>();
  const [taskFilter, setTaskFilter] = useState<string | undefined>();

  const jobListColumns: ProColumns<Jobs.JobInstance>[] = [
    {
      title: l('devops.jobinfo.config.taskId'),
      dataIndex: 'taskId',
      width: '6%',
      valueType: 'indexBorder',
      fixed: 'left'
    },
    {
      title: l('global.table.jobname'),
      dataIndex: 'name'
    },
    {
      title: l('global.table.lifecycle'),
      dataIndex: 'step',
      render: (_: any, row: { step: number }) => <JobLifeCycleTag status={row.step} />
    },
    {
      title: l('global.table.runmode'),
      dataIndex: 'type',
      hideInSearch: true
    },
    {
      title: l('devops.jobinfo.config.JobId'),
      dataIndex: 'jid',
      width: '20%',
      copyable: true
    },
    {
      title: l('global.table.createTime'),
      hideInSearch: true,
      dataIndex: 'createTime'
    },
    {
      title: l('global.table.useTime'),
      hideInSearch: true,
      render: (_: any, row: Jobs.JobInstance) => parseMilliSecondStr(row.duration)
    },
    {
      title: l('global.table.status'),
      dataIndex: 'status',
      render: (_: any, row: Jobs.JobInstance) => <StatusTag status={row.status} />
    },
    Table.EXPAND_COLUMN,
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '5%',
      fixed: 'right',
      render: (text: any, record: Jobs.JobInstance) => [
        <Button
          className={'options-button'}
          key={`${record.id}_history`}
          title={l('devops.joblist.detail')}
          icon={<EyeTwoTone />}
          onClick={() => history.push(`/devops/job-detail?id=${record.id}`)}
        />
      ]
    }
  ];

  useEffect(() => {
    setInterval(() => tableRef.current?.reload(false), 5 * 1000);
  }, []);

  return (
    <ProCard boxShadow={true} style={{ height: 'calc(100vh - 250px)' }}>
      <ProTable<Jobs.JobInstance>
        {...PROTABLE_OPTIONS_PUBLIC}
        search={false}
        loading={{ delay: 1000 }}
        rowKey={(record) => record.jid}
        columns={jobListColumns}
        params={{ isHistory: false, status: statusFilter, step: stepFilter, name: taskFilter }}
        actionRef={tableRef}
        toolbar={{
          settings: false,
          search: { onSearch: (value: string) => setTaskFilter(value) },
          filter: (
            <>
              <Radio.Group defaultValue={undefined} onChange={(e) => setStepFilter(e.target.value)}>
                <Radio.Button value={undefined} defaultChecked={true}>
                  {l('global.table.lifecycle.all')}
                </Radio.Button>
                <Radio.Button value={JOB_LIFE_CYCLE.PUBLISH}>
                  {l('global.table.lifecycle.publish')}
                </Radio.Button>
                <Radio.Button value={JOB_LIFE_CYCLE.DEVELOP}>
                  {l('global.table.lifecycle.dev')}
                </Radio.Button>
              </Radio.Group>
            </>
          ),
          actions: [<Button icon={<RedoOutlined />} onClick={() => tableRef.current?.reload()} />]
        }}
        request={async (params, sorter, filter: any) =>
          queryList(API_CONSTANTS.GET_JOB_LIST, {
            ...params,
            sorter,
            filter
          })
        }
        expandable={{
          expandedRowRender: (record) => <JobHistoryList taskId={record.taskId} key={record.jid} />,
          expandIcon: ({ expanded, onExpand, record }) => (
            <Button
              className={'options-button'}
              key={`${record.id}_history`}
              onClick={(e) => onExpand(record, e)}
              title={l('devops.joblist.history')}
              icon={<ClockCircleTwoTone twoToneColor={expanded ? '#52c41a' : '#4096ff'} />}
            />
          )
        }}
      />
    </ProCard>
  );
};
export default JobList;
