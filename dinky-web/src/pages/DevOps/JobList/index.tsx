import type {ActionType, ProColumns} from '@ant-design/pro-components';
import {ProTable} from '@ant-design/pro-components';
import {Button, Table} from 'antd';
import React, {useEffect, useRef} from 'react';
import {Jobs} from '@/types/DevOps/data';
import JobHistoryList from '@/pages/DevOps/JobList/components/JobHistoryList';
import {l} from '@/utils/intl';
import {parseSecondStr} from '@/utils/function';
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from '@/services/constants';
import {queryList} from '@/services/api';
import {ClockCircleTwoTone, EyeTwoTone} from '@ant-design/icons';
import {JOB_STATUS_FILTER, LIFECYCLE_FILTER, TagJobLifeCycle, TagJobStatus} from '@/pages/DevOps/function';
import { history } from 'umi';

const JobList = () => {
  const tableRef = useRef<ActionType>();

  const jobListColumns: ProColumns<Jobs.JobInstance>[] = [
    {
      title: l('devops.jobinfo.config.taskId'),
      dataIndex: 'taskId',
    },
    {
      title: l('global.table.jobname'),
      dataIndex: 'name',
    },
    {
      title: l('global.table.lifecycle'),
      filters: LIFECYCLE_FILTER(),
      hideInSearch: true,
      filterMultiple: false,
      dataIndex: 'step',
      render: (_: any, row: { step: number; }) => TagJobLifeCycle(row.step)
    },
    {
      title: l('global.table.runmode'),
      dataIndex: 'type',
      hideInSearch: true
    },
    {
      title: l('devops.jobinfo.config.JobId'),
      dataIndex: 'jid',
      width: '15%',
    },
    {
      title: l('global.table.createTime'),
      hideInSearch: true,
      dataIndex: 'createTime',
    },
    {
      title: l('global.table.useTime'),
      hideInSearch: true,
      render: (_: any, row: Jobs.JobInstance) => parseSecondStr(row.duration)
    },
    {
      title: l('global.table.status'),
      filters: JOB_STATUS_FILTER(),
      filterMultiple: false,
      hideInSearch: true,
      dataIndex: 'status',
      render: (_: any, row: Jobs.JobInstance) => TagJobStatus(row.status)
    },
    Table.EXPAND_COLUMN,
    {
      title: l('global.table.operate'),
      valueType: 'option',
      render: (text: any, record: Jobs.JobInstance) => [
        <Button
          className={'options-button'}
          key={`${record.id}_history`}
          title={l('devops.joblist.detail')}
          icon={<EyeTwoTone/>}
          onClick={()=>history.push(`/devops/job-detail?id=${record.id}`)}
        />
      ],
    },
  ];

  useEffect(() => {
    setInterval(() => tableRef.current?.reload(false), 5 * 1000);
  }, []);

  return (
    <ProTable<Jobs.JobInstance>
      {...PROTABLE_OPTIONS_PUBLIC}
      rowKey={(record => record.jid)}
      columns={jobListColumns}
      params={{isHistory: false}}
      actionRef={tableRef}
      headerTitle={l('devops.joblist.joblist')}
      request={async (params, sorter, filter: any) => queryList(API_CONSTANTS.GET_JOB_LIST, {
        ...params,
        sorter,
        filter
      })}
      expandable={{
        expandedRowRender: (record) => <JobHistoryList taskId={record.taskId} key={record.jid}/>,
        expandIcon: ({expanded, onExpand, record}) =>
          (
            <Button
              className={'options-button'}
              key={`${record.id}_history`}
              onClick={e => onExpand(record, e)}
              title={l('devops.joblist.history')}
              icon={<ClockCircleTwoTone twoToneColor={expanded ? '#52c41a' : '#4096ff'}/>}
            />
          )
      }}
    />
  );
};
export default JobList;
