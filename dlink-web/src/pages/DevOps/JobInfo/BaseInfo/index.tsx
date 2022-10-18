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


import {Descriptions, Typography} from 'antd';
import StatusCounts from "@/components/Common/StatusCounts";
import ProTable, {ProColumns} from '@ant-design/pro-table';
import {VerticesTableListItem} from "@/pages/DevOps/data";
import JobStatus from "@/components/Common/JobStatus";
import {parseByteStr, parseMilliSecondStr, parseNumStr, parseSecondStr} from "@/components/Common/function";
import {useIntl} from "umi";

const {Text} = Typography;

const BaseInfo = (props: any) => {

  const {job} = props;
  const l = (key: string, defaultMsg?: string) => useIntl().formatMessage({id: key, defaultMessage: defaultMsg})


  const columns: ProColumns<VerticesTableListItem>[] = [
    {
      title: '名称',
      dataIndex: 'name',
      render: (dom, entity) => {
        return <Text style={{width: 500}} ellipsis={{tooltip: entity.name}}>{entity.name}</Text>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      sorter: true,
      render: (dom, entity) => {
        return <JobStatus status={entity.status}/>;
      },
    },
    {
      title: '接收字节',
      render: (dom, entity) => {
        return parseByteStr(entity.metrics['read-bytes']);
      },
    },
    {
      title: '接收记录',
      render: (dom, entity) => {
        return parseNumStr(entity.metrics['read-records']);
      },
    },
    {
      title: '发送字节',
      render: (dom, entity) => {
        return parseByteStr(entity.metrics['write-bytes']);
      },
    },
    {
      title: '发送记录',
      render: (dom, entity) => {
        return parseNumStr(entity.metrics['write-records']);
      },
    },
    {
      title: '并行度',
      sorter: true,
      dataIndex: 'parallelism',
    },
    {
      title: '开始时间',
      dataIndex: 'start-time',
      valueType: 'dateTime',
    },
    {
      title: '耗时',
      render: (dom, entity) => {
        return parseMilliSecondStr(entity.duration);
      },
    },
    {
      title: '结束时间',
      dataIndex: 'end-time',
      valueType: 'dateTime',
    },
    {
      title: '算子',
      render: (dom, entity) => {
        return <StatusCounts statusCounts={entity.tasks}/>;
      },
    },
  ];

  return (<>
    <Descriptions bordered size="small">
      <Descriptions.Item label="作业状态">
        {job?.jobHistory?.job ? <StatusCounts statusCounts={job?.jobHistory?.job['status-counts']}/> : undefined}
      </Descriptions.Item>
      <Descriptions.Item
        label="重启次数">{job?.instance?.failedRestartCount ? job?.instance?.failedRestartCount : 0}</Descriptions.Item>
      <Descriptions.Item label="耗时">{parseSecondStr(job?.instance?.duration)}</Descriptions.Item>
      <Descriptions.Item label="启动时间">{job?.instance?.createTime}</Descriptions.Item>
      <Descriptions.Item label="更新时间">{job?.instance?.updateTime}</Descriptions.Item>
      <Descriptions.Item label="完成时间">{job?.instance?.finishTime}</Descriptions.Item>
    </Descriptions>
    {job?.jobHistory?.job ?
      <ProTable
        columns={columns}
        style={{width: '100%'}}
        dataSource={job?.jobHistory?.job.vertices}
        rowKey="name"
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
        }}
        toolBarRender={false}
        search={false}
        size="small"
      /> : undefined}
  </>)
};

export default BaseInfo;
