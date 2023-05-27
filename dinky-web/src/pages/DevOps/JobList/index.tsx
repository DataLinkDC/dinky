import type {ActionType, ProColumns} from '@ant-design/pro-components';
import {ProTable} from '@ant-design/pro-components';
import {Button, Table} from 'antd';
import React, {useEffect, useRef} from 'react';
import {Jobs} from "@/types/DevOps/data";
import JobHistoryItem from "@/pages/DevOps/JobList/compents/JobHistoryItem";
import {l} from "@/utils/intl";
import {parseSecondStr} from "@/utils/function";
import {API_CONSTANTS} from "@/services/constants";
import {queryList} from "@/services/api";
import {ClockCircleTwoTone, EyeTwoTone} from "@ant-design/icons";
import {JOB_STATUS_FILTER, LIFECYCLE_FILTER, TagJobLifeCycle, TagJobStatus} from "@/pages/DevOps/JobList/function";

const JobList = () => {
  const tableRef = useRef<ActionType>();

  const jobListColums: ProColumns<Jobs.JobInstanceTableListItem>[] = [
    {
      title: l('global.table.taskid'),
      dataIndex: "taskId",
    },
    {
      title: l('global.table.jobname'),
      dataIndex: "name",
    },
    {
      title: l('global.table.lifecycle'),
      filters: LIFECYCLE_FILTER(),
      hideInSearch:true,
      dataIndex: "step",
      render: (_, row) => TagJobLifeCycle(row.step)
    },
    {
      title: l('global.table.runmode'),
      dataIndex: "type",
      hideInSearch: true
    },
    {
      title: l('global.table.jobid'),
      dataIndex: "jid",
    },
    {
      title: l('global.table.createTime'),
      hideInSearch: true,
      dataIndex: "createTime",
    },
    {
      title: l('global.table.useTime'),
      hideInSearch: true,
      render: (_, row) => parseSecondStr(row.duration)
    },
    {
      title: l('global.table.status'),
      filters: JOB_STATUS_FILTER(),
      hideInSearch:true,
      dataIndex: "status",
      render: (_, row) => TagJobStatus(row.status)
    },
    Table.EXPAND_COLUMN,
    {
      title: l('global.table.operate'),
      valueType: 'option',
      render: (text, record) => [
        <Button
          className={'options-button'}
          key={`${record.id}_history`}
          title={l('devops.joblist.detail')}
          icon={<EyeTwoTone/>}
        />
      ],
    },
  ];

  useEffect(() => {
    setInterval(()=>tableRef.current?.reload(false), 1000);
  }, []);

  return (
    <ProTable<Jobs.JobInstanceTableListItem>
      rowKey={(record => record.jid)}
      columns={jobListColums}
      params={{isHistory: false}}
      actionRef={tableRef}
      headerTitle={l('devops.joblist.joblist')}
      request={async (params, sorter, filter: any) => {
        return queryList(API_CONSTANTS.GET_JOB_LIST, {
          ...params,
          sorter,
          filter
        })
      }}
      expandable={{
        expandedRowRender: (record) => <JobHistoryItem taskId={record.taskId} key={record.jid}/>,
        expandIcon: ({expanded, onExpand, record}) => {
          return (
            <Button
              className={'options-button'}
              key={`${record.id}_history`}
              onClick={e => onExpand(record, e)}
              title={l('devops.joblist.history')}
              icon={<ClockCircleTwoTone twoToneColor={expanded ? '#52c41a' : '#4096ff'}/>}
            />
          )
        }
      }}
    />
  );
}
export default JobList;
