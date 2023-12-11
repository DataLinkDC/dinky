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

import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { parseByteStr, parseMilliSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  MinusCircleOutlined,
  SyncOutlined
} from '@ant-design/icons';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import { Button, message, Modal, Tag } from 'antd';
import { useRef } from 'react';

export type CheckPointsDetailInfo = {
  id: number;
  status: string;
  checkpoint_type: string;
  end_to_end_duration: number;
  external_path: string;
  latest_ack_timestamp: number;
  state_size: number;
  trigger_timestamp: number;
};

const CheckpointTable = (props: JobProps) => {
  const { jobDetail } = props;

  const actionRef = useRef<ActionType>();

  const checkpoints = jobDetail?.jobDataDto?.checkpoints;

  // const restartFromCheckpoint = useRequest((id: number, isOnLine: boolean, savePointPath: string) => (
  //     {
  //       url: API_CONSTANTS.RESTART_TASK_FROM_CHECKPOINT,
  //       params: {id: id, isOnLine: isOnLine, savePointPath: savePointPath}
  //     }
  //   ),
  //   {manual: true});

  function recoveryCheckPoint(row: CheckPointsDetailInfo) {
    Modal.confirm({
      title: l('devops.jobinfo.ck.recovery'),
      content: l('devops.jobinfo.ck.recoveryConfirm', '', {
        path: row.external_path
      }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        const param = {
          id: jobDetail?.instance?.taskId,
          isOnLine: jobDetail?.instance?.step == JOB_LIFE_CYCLE.PUBLISH,
          savePointPath: row.external_path
        };
        const result = await getData(API_CONSTANTS.RESTART_TASK_FROM_CHECKPOINT, param);
        if (result.code == 0) {
          message.success(l('devops.jobinfo.ck.recovery.success'));
        } else {
          message.error(l('devops.jobinfo.ck.recovery.failed'));
        }
      }
    });
  }

  const columns: ProColumns<CheckPointsDetailInfo>[] = [
    {
      title: l('devops.jobinfo.ck.id'),
      align: 'center',
      dataIndex: 'id'
    },
    {
      title: l('devops.jobinfo.ck.status'),
      align: 'center',
      copyable: true,
      render: (dom, entity) => {
        if (entity.status === 'COMPLETED') {
          return (
            <Tag icon={<CheckCircleOutlined />} color='success'>
              {entity.status}
            </Tag>
          );
        }
        if (entity.status === 'IN_PROGRESS') {
          return (
            <Tag icon={<SyncOutlined spin />} color='processing'>
              {entity.status}
            </Tag>
          );
        }
        if (entity.status === 'FAILED') {
          return (
            <Tag icon={<CloseCircleOutlined />} color='error'>
              {entity.status}
            </Tag>
          );
        }
        return (
          <Tag icon={<MinusCircleOutlined />} color='default'>
            {entity.status}
          </Tag>
        );
      }
    },
    {
      title: l('devops.jobinfo.ck.duration'),
      align: 'center',
      copyable: true,
      render: (_, entity) => parseMilliSecondStr(entity.end_to_end_duration)
    },
    {
      title: l('devops.jobinfo.ck.checkpoint_type'),
      align: 'center',
      dataIndex: 'checkpoint_type'
    },
    {
      title: l('devops.jobinfo.ck.external_path'),
      align: 'center',
      copyable: true,
      dataIndex: 'external_path'
    },
    {
      title: l('devops.jobinfo.ck.latest_ack_timestamp'),
      align: 'center',
      dataIndex: 'latest_ack_timestamp',
      valueType: 'dateTime'
    },
    {
      title: l('devops.jobinfo.ck.state_size'),
      align: 'center',
      render: (dom, entity) => parseByteStr(entity.state_size)
    },
    {
      title: l('devops.jobinfo.ck.trigger_timestamp'),
      align: 'center',
      valueType: 'dateTime',
      dataIndex: 'trigger_timestamp'
    },
    {
      title: l('global.table.operate'),
      align: 'center',
      render: (dom, entity) => {
        return (
          <>
            {entity.status === 'COMPLETED' ? (
              <Button onClick={() => recoveryCheckPoint(entity)}>
                {l('devops.jobinfo.ck.recovery.recoveryTo')}
              </Button>
            ) : undefined}
          </>
        );
      }
    }
  ];

  return (
    <ProTable<CheckPointsDetailInfo>
      columns={columns}
      style={{ width: '100%', height: 'calc(100vh - 450px)' }}
      dataSource={checkpoints?.history}
      onDataSourceChange={() => actionRef.current?.reload()}
      actionRef={actionRef}
      rowKey='id'
      pagination={{
        defaultPageSize: 10,
        showSizeChanger: true
      }}
      toolBarRender={false}
      dateFormatter='string'
      search={false}
      size='small'
    />
  );
};

export default CheckpointTable;
