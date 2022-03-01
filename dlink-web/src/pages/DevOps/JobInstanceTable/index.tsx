import {Tag} from 'antd';
import {
  CheckCircleOutlined,
  SyncOutlined, CloseCircleOutlined, MinusCircleOutlined, ClockCircleOutlined, DownOutlined
} from "@ant-design/icons";
import {queryData} from "@/components/Common/crud";
import React, {useState} from "react";
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from "@ant-design/pro-table";
import {JobInstanceTableListItem} from "@/pages/DevOps/data";

const url = '/api/jobInstance';
const JobInstanceTable = (props: any) => {

  const {status, dispatch} = props;

  const getColumns = () => {
    const columns: ProColumns<JobInstanceTableListItem>[]  = [{
      title: "作业名",
      dataIndex: "name",
      sorter: true,
    },{
      title: "运行模式",
      dataIndex: "type",
      sorter: true,
    },{
      title: "集群实例",
      dataIndex: "clusterAlias",
      sorter: true,
    },{
      title: "作业ID",
      dataIndex: "jid",
      key: "jid",
    }, {
      title: "状态",
      dataIndex: "status",
      sorter: true,
      render: (_, row) => {
        return (
          <>
            {(row.status == 'FINISHED') ?
              (<Tag icon={<CheckCircleOutlined />} color="success">
                FINISHED
              </Tag>) :
              (row.status == 'RUNNING') ?
                (<Tag icon={<SyncOutlined spin />} color="processing">
                  RUNNING
                </Tag>) :
                (row.status == 'FAILED') ?
                  (<Tag icon={<CloseCircleOutlined />} color="error">
                    FAILED
                  </Tag>) :
                  (row.status == 'CANCELED') ?
                    (<Tag icon={<MinusCircleOutlined />} color="default">
                      CANCELED
                    </Tag>) :
                    (row.status == 'INITIALIZING') ?
                      (<Tag icon={<ClockCircleOutlined />} color="default">
                        INITIALIZING
                      </Tag>) :(row.status == 'RESTARTING') ?
                        (<Tag icon={<ClockCircleOutlined />} color="default">
                          RESTARTING
                        </Tag>) :
                        (<Tag color="default">
                          UNKNOWEN
                        </Tag>)
            }</>)
          ;
      }
    }, {
      title: "开始时间",
      dataIndex: "createTime",
      sorter: true,
      valueType: 'dateTime',
    }, {
      title: "更新时间",
      dataIndex: "updateTime",
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true
    }, {
      title: "结束时间",
      dataIndex: "finishTime",
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true
    }, {
      title: "耗时",
      dataIndex: "duration",
      sorter: true,
      valueType: 'second',
    },];
    return columns;
  };

  return (
    <><ProTable
      request={(params, sorter, filter) => queryData(url, {...params,status, sorter: {id: 'descend'}, filter})}
      columns={getColumns()}
      size="small"
      search={false}
      toolBarRender={false}
      pagination={{
        pageSize: 5,
      }}
    />
    </>
  );
};

export default JobInstanceTable;
