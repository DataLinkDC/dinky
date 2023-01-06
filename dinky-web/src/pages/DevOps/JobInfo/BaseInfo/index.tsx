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
import {l} from "@/utils/intl";

const {Text} = Typography;

const BaseInfo = (props: any) => {

  const {job} = props;

  const columns: ProColumns<VerticesTableListItem>[] = [
    {
      title: l('pages.devops.baseinfo.name'),
      dataIndex: 'name',
      render: (dom, entity) => {
        return <Text style={{width: 500}} ellipsis={{tooltip: entity.name}}>{entity.name}</Text>;
      },
    },
    {
      title: l('pages.devops.baseinfo.status'),
      dataIndex: 'status',
      sorter: true,
      render: (dom, entity) => {
        return <JobStatus status={entity.status}/>;
      },
    },
    {
      title: l('pages.devops.baseinfo.readbytes'),
      render: (dom, entity) => {
        return parseByteStr(entity.metrics['read-bytes']);
      },
    },
    {
      title: l('pages.devops.baseinfo.readrecords'),
      render: (dom, entity) => {
        return parseNumStr(entity.metrics['read-records']);
      },
    },
    {
      title: l('pages.devops.baseinfo.writebytes'),
      render: (dom, entity) => {
        return parseByteStr(entity.metrics['write-bytes']);
      },
    },
    {
      title: l('pages.devops.baseinfo.writerecords'),
      render: (dom, entity) => {
        return parseNumStr(entity.metrics['write-records']);
      },
    },
    {
      title: l('pages.devops.baseinfo.parallelism'),
      sorter: true,
      dataIndex: 'parallelism',
    },
    {
      title: l('global.table.startTime'),
      dataIndex: 'start-time',
      valueType: 'dateTime',
    },
    {
      title: l('global.table.useTime'),
      render: (dom, entity) => {
        return parseMilliSecondStr(entity.duration);
      },
    },
    {
      title:  l('global.table.endTime'),
      dataIndex: 'end-time',
      valueType: 'dateTime',
    },
    {
      title: l('pages.devops.baseinfo.tasks'),
      render: (dom, entity) => {
        return <StatusCounts statusCounts={entity.tasks}/>;
      },
    },
  ];

  return (<>
    <Descriptions bordered size="small">
      <Descriptions.Item label={l('pages.devops.jobinfo.overview')}>
        {job?.jobHistory?.job ? <StatusCounts statusCounts={job?.jobHistory?.job['status-counts']}/> : undefined}
      </Descriptions.Item>
      <Descriptions.Item
        label={l('pages.devops.baseinfo.restart_number')}>{job?.instance?.failedRestartCount ? job?.instance?.failedRestartCount : 0}</Descriptions.Item>
      <Descriptions.Item label={l('global.table.useTime')}>{parseSecondStr(job?.instance?.duration)}</Descriptions.Item>
      <Descriptions.Item label={l('global.table.startUpTime')}>{job?.instance?.createTime}</Descriptions.Item>
      <Descriptions.Item label={l('global.table.updateTime')}>{job?.instance?.updateTime}</Descriptions.Item>
      <Descriptions.Item label={l('global.table.finishTime')}>{job?.instance?.finishTime}</Descriptions.Item>
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
