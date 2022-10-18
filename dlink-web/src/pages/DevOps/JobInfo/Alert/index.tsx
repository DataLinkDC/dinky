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


import {Typography} from 'antd';
import ProTable, {ProColumns} from '@ant-design/pro-table';
import {queryData} from "@/components/Common/crud";
import {useIntl} from "umi";

const {Text} = Typography;
type AlertHistoryTableListItem = {
  title: string,
  content: string,
  status: number,
  log: string,
  createTime: string,
}


const Alert = (props: any) => {
  const l = (key: string, defaultMsg?: string) => useIntl().formatMessage({id: key, defaultMessage: defaultMsg})


  const url = '/api/alertGroup';
  const {job} = props;

  const columns: ProColumns<AlertHistoryTableListItem>[] = [
    {
      title: '标题',
      dataIndex: 'title',
      render: (dom, entity) => {
        return <Text style={{width: 200}} ellipsis={{tooltip: entity.title}}>{entity.title}</Text>;
      },
    },
    {
      title: '正文',
      dataIndex: 'content',
      render: (dom, entity) => {
        return <Text style={{width: 500}} ellipsis={{tooltip: entity.content}}>{entity.content}</Text>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      sorter: true,
      render: (dom, entity) => {
        return entity.status === 1 ? <Text type="success">成功</Text> : <Text type="danger">失败</Text>;
      },
    },
    {
      title: '日志',
      dataIndex: 'log',
      render: (dom, entity) => {
        return <Text style={{width: 500}} ellipsis={{tooltip: entity.log}}>{entity.log}</Text>;
      },
    },
    {
      title: '报警时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
  ];

  return (<>
    <ProTable
      columns={columns}
      style={{width: '100%'}}
      request={(params, sorter, filter) => queryData(url + '/history', {
        ...params,
        jobInstanceId: job.instance?.id,
        sorter,
        filter
      })}
      rowKey="name"
      pagination={{
        defaultPageSize: 10,
        showSizeChanger: true,
      }}
      toolBarRender={false}
      search={false}
      size="small"
    />
  </>)
};

export default Alert;
