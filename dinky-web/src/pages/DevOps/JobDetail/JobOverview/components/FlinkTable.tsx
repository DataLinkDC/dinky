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

import StatusTag from '@/components/JobTags/StatusTag';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { parseByteStr, parseMilliSecondStr, parseNumStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { ProCard, ProColumns, ProTable } from '@ant-design/pro-components';
import { Typography } from 'antd';

const { Link } = Typography;

export type VerticesTableListItem = {
  name: string;
  status: string;
  metrics: any;
  parallelism: number;
  startTime?: number;
  duration?: number;
  endTime?: number;
  tasks: any;
};

/**
 * Renders the JobConfigTab component.
 *
 * @param {JobProps} props - The component props containing the job detail.
 * @returns {JSX.Element} - The rendered JobConfigTab component.
 */
const FlinkTable = (props: JobProps): JSX.Element => {
  const { jobDetail } = props;

  const columns: ProColumns<VerticesTableListItem>[] = [
    {
      title: l('devops.baseinfo.name'),
      dataIndex: 'name',
      ellipsis: true,
      width: '20%',
      render: (dom, entity) => {
        return <Link>{entity.name}</Link>;
      }
    },
    {
      title: l('devops.baseinfo.status'),
      dataIndex: 'status',
      sorter: true,
      width: '8%',
      render: (dom, entity) => {
        return <StatusTag status={entity.status} />;
      }
    },
    {
      title: l('devops.baseinfo.readbytes'),
      width: '7%',
      render: (dom, entity) => {
        return parseByteStr(entity.metrics['read-bytes']);
      }
    },
    {
      title: l('devops.baseinfo.readrecords'),
      width: '7%',
      render: (dom, entity) => {
        return parseNumStr(entity.metrics['read-records']);
      }
    },
    {
      title: l('devops.baseinfo.writebytes'),
      width: '7%',
      render: (dom, entity) => {
        return parseByteStr(entity.metrics['write-bytes']);
      }
    },
    {
      title: l('devops.baseinfo.writerecords'),
      width: '7%',
      render: (dom, entity) => {
        return parseNumStr(entity.metrics['write-records']);
      }
    },
    {
      title: l('devops.baseinfo.parallelism'),
      sorter: true,
      width: '7%',
      dataIndex: 'parallelism'
    },
    {
      title: l('global.table.startTime'),
      dataIndex: 'startTime',
      valueType: 'dateTime'
    },
    {
      title: l('global.table.endTime'),
      dataIndex: 'endTime',
      valueType: 'dateTime',
      render: (dom, entity) => {
        return entity.endTime === -1 ? '-' : entity.endTime;
      }
    },
    {
      title: l('global.table.useTime'),
      render: (dom, entity) => {
        return parseMilliSecondStr(entity.duration);
      }
    }
    // {
    //   title: l('devops.baseinfo.tasks'),
    //   render: (dom, entity) => {
    //     return <StatusCounts statusCounts={entity.tasks}/>;
    //   },
    // },
  ];

  return (
    <>
      <ProCard>
        <ProTable
          defaultSize={'small'}
          columns={columns}
          style={{ width: '100%', height: '30vh' }}
          dataSource={jobDetail?.jobDataDto?.job?.vertices}
          rowKey='name'
          pagination={{
            defaultPageSize: 10,
            showSizeChanger: true,
            hideOnSinglePage: true
          }}
          toolBarRender={false}
          search={false}
          size='small'
        />
      </ProCard>
    </>
  );
};

export default FlinkTable;
