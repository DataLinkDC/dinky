import JobLifeCycleTag from '@/components/JobTags/JobLifeCycleTag';
import StatusTag from '@/components/JobTags/StatusTag';
import { JOB_STATUS_FILTER, LIFECYCLE_FILTER } from '@/pages/DevOps/function';
import JobHistoryList from '@/pages/DevOps/JobList/components/JobHistoryList';
import { queryList } from '@/services/api';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { parseMilliSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { ClockCircleTwoTone, EyeTwoTone } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { Button, Table } from 'antd';
import { useEffect, useRef } from 'react';
import { history } from 'umi';

const JobList = () => {
  const tableRef = useRef<ActionType>();

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
      filters: LIFECYCLE_FILTER(),
      hideInSearch: true,
      filterMultiple: false,
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
      filters: JOB_STATUS_FILTER(),
      filterMultiple: false,
      hideInSearch: true,
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
    <ProTable<Jobs.JobInstance>
      {...PROTABLE_OPTIONS_PUBLIC}
      rowKey={(record) => record.jid}
      columns={jobListColumns}
      params={{ isHistory: false }}
      actionRef={tableRef}
      headerTitle={l('devops.joblist.joblist')}
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
  );
};
export default JobList;
