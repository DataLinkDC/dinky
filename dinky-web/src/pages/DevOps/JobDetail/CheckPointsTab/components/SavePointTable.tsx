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

import EllipsisMiddle from '@/components/Typography/EllipsisMiddle';
import { recoveryCheckPoint } from '@/pages/DevOps/JobDetail/CheckPointsTab/components/functions';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { postAll } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { SavePoint } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import { Button } from 'antd';
import { useRef } from 'react';

const SavepointTable = (props: JobProps) => {
  const { jobDetail } = props;

  const actionRef = useRef<ActionType>();

  const columns: ProColumns<SavePoint>[] = [
    {
      title: l('devops.jobinfo.ck.id'),
      align: 'center',
      dataIndex: 'id',
      hideInTable: true
    },
    {
      title: l('devops.jobinfo.ck.taskid'),
      align: 'center',
      dataIndex: 'taskId',
      hideInTable: true
    },
    {
      title: l('devops.jobinfo.ck.name'),
      align: 'center',
      dataIndex: 'name'
    },
    {
      title: l('devops.jobinfo.ck.checkpoint_type'),
      align: 'center',
      dataIndex: 'type'
    },
    {
      title: l('devops.jobinfo.ck.external_path'),
      align: 'center',
      copyable: true,
      dataIndex: 'path',
      render: (_, entity) => <EllipsisMiddle maxCount={60} children={entity.path} />
    },
    {
      title: l('devops.jobinfo.ck.trigger_timestamp'),
      align: 'center',
      valueType: 'dateTime',
      dataIndex: 'createTime'
    },
    {
      title: l('global.table.operate'),
      align: 'center',
      render: (dom, entity) => {
        return (
          <Button onClick={() => recoveryCheckPoint(jobDetail?.instance?.taskId, entity.path)}>
            {l('devops.jobinfo.ck.recovery.recoveryTo')}
          </Button>
        );
      }
    }
  ];

  return (
    <ProTable<SavePoint>
      columns={columns}
      style={{ width: '100%', height: 'calc(100vh - 450px)' }}
      request={(params, sorter, filter) =>
        postAll(API_CONSTANTS.GET_SAVEPOINT_LIST, {
          ...params,
          sorter,
          filter
        })
      }
      params={{ taskId: jobDetail?.instance?.taskId }}
      actionRef={actionRef}
      toolBarRender={false}
      rowKey='id'
      pagination={{
        pageSize: 10
      }}
      search={false}
      size='small'
    />
  );
};

export default SavepointTable;
