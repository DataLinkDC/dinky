import {Tag} from 'antd';
import { history } from 'umi';
import {
  CheckCircleOutlined,
  SyncOutlined, CloseCircleOutlined, MinusCircleOutlined, ClockCircleOutlined, DownOutlined
} from "@ant-design/icons";
import {queryData} from "@/components/Common/crud";
import React, {useState} from "react";
import type { ProColumns } from '@ant-design/pro-table';
import ProTable from "@ant-design/pro-table";
import {JobInstanceTableListItem} from "@/pages/DevOps/data";
import moment from 'moment';
import {RUN_MODE} from "@/components/Studio/conf";

const url = '/api/jobInstance';
const JobInstanceTable = (props: any) => {

  const {status, activeKey, dispatch} = props;
  const [time, setTime] = useState(() => Date.now());

  const getColumns = () => {
    const columns: ProColumns<JobInstanceTableListItem>[]  = [{
      title: "作业名",
      dataIndex: "name",
      sorter: true,
    },{
      title: "运行模式",
      dataIndex: "type",
      sorter: true,
      valueType: 'radio',
      valueEnum: {
        '': {text: '全部', status: 'ALL'},
        'local': {
          text: RUN_MODE.LOCAL,
          status: RUN_MODE.LOCAL,
        },
        'standalone': {
          text: RUN_MODE.STANDALONE,
          status: RUN_MODE.STANDALONE,
        },
        'yarn-session': {
          text: RUN_MODE.YARN_SESSION,
          status: RUN_MODE.YARN_SESSION,
        },
        'yarn-per-job': {
          text: RUN_MODE.YARN_PER_JOB,
          status: RUN_MODE.YARN_PER_JOB,
        },
        'yarn-application': {
          text: RUN_MODE.YARN_APPLICATION,
          status: RUN_MODE.YARN_APPLICATION,
        },
        'kubernetes-session': {
          text: RUN_MODE.KUBERNETES_SESSION,
          status: RUN_MODE.KUBERNETES_SESSION,
        },
        'kubernetes-application': {
          text: RUN_MODE.KUBERNETES_APPLICATION,
          status: RUN_MODE.KUBERNETES_APPLICATION,
        },
      },
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
      hideInSearch: true,
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
      hideInSearch: true,
    }, {
      title: "更新时间",
      dataIndex: "updateTime",
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true,
      hideInSearch: true,
    }, {
      title: "结束时间",
      dataIndex: "finishTime",
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true,
      hideInSearch: true,
    }, {
      title: "耗时",
      dataIndex: "duration",
      sorter: true,
      valueType: 'second',
      hideInSearch: true,
    },];
    return columns;
  };

  return (
    <><ProTable
      request={(params, sorter, filter) => {
        setTime(Date.now());
        return queryData(url, {...params,status, sorter: {id: 'descend'}, filter});
      }}
      columns={getColumns()}
      size="small"
      search={{
        filterType: 'light',
      }}
      headerTitle={`上次更新时间：${moment(time).format('HH:mm:ss')}`}
      polling={status==activeKey?3000:undefined}
      pagination={{
        pageSize: 5,
      }}
      onRow={ record => {
        return {
          onClick: event => {
            history.push({
              pathname: '/job',
              query: {
                id: record.id,
              },
            });
          },
        };
      }}
    />
    </>
  );
};

export default JobInstanceTable;
