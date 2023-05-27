import type {ActionType, ProColumns} from '@ant-design/pro-components';
import {ProTable} from '@ant-design/pro-components';
import React, {useRef} from 'react';
import {Jobs} from "@/types/DevOps/data";
import {l} from "@/utils/intl";
import {API_CONSTANTS} from "@/services/constants";
import JobStatus from "@/pages/DevOps/JobList/compents/JobStatus";
import {parseSecondStr} from "@/utils/function";
import {queryList} from "@/services/api";


type HistoryProps = {
  taskId: number;
};

const JobHistoryItem = (props: HistoryProps) => {

  const {taskId} = props

  const actionRef = useRef<ActionType>();

  const jobListColums: ProColumns<Jobs.JobInstanceTableListItem>[] = [

    {
      title: l('global.table.createTime'),
      dataIndex: "createTime",
      valueType: 'dateTime',
    },
    {
      title: l('global.table.endTime'),
      dataIndex: "finishTime",
      valueType: 'dateTime',
    },
    {
      title: l('global.table.jobid'),
      dataIndex: "jid",
      key: "jid",
    },
    {
      title: l('global.table.status'),
      dataIndex: "status",
      render: (_, row) => {
        return (<JobStatus status={row.status}/>);
      }
    },
    {
      title: l('global.table.useTime'),
      render: (_, row) => {
        return parseSecondStr(row.duration)
      }
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      render: () => [
        <a key={"history-detail"}>{l('devops.joblist.detail')}</a>
      ],
    },
  ];

  return (
    <>
      <ProTable<Jobs.JobInstanceTableListItem>
        search={false}
        params={{isHistory: true}}
        tableStyle={{overflowX: "hidden", overflowY: "hidden", margin: "10px"}}
        size={"small"}
        columns={jobListColums}
        actionRef={actionRef}
        request={(params) => queryList(API_CONSTANTS.GET_JOB_LIST, {
            ...params,
            filter: {task_id: [taskId]}
          })
        }
        rowKey={(record) => record.id}
        toolBarRender={false}
        pagination={{showSizeChanger: false}}
      />
    </>

  );
}
export default JobHistoryItem;
