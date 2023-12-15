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

import { cancelTask } from '@/pages/DataStudio/HeaderContainer/service';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { isStatusDone } from '@/pages/DevOps/function';
import { getData, postAll } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { EllipsisOutlined, RedoOutlined } from '@ant-design/icons';
import { Button, Dropdown, message, Modal, Space } from 'antd';

const operatorType = {
  RESTART_JOB: 'restart',
  CANCEL_JOB: 'canceljob',
  SAVEPOINT_CANCEL: 'cancel',
  SAVEPOINT_TRIGGER: 'trigger',
  SAVEPOINT_STOP: 'stop'
};
export type OperatorType = {
  jobDetail: Jobs.JobInfoDetail;
  refesh: (isForce: boolean) => void;
};
const JobOperator = (props: OperatorType) => {
  const { jobDetail, refesh } = props;
  const webUri = `/api/flink/${jobDetail?.history?.jobManagerAddress}/#/job/running/${jobDetail?.instance?.jid}/overview`;

  const handleJobOperator = (key: string) => {
    Modal.confirm({
      title: l('devops.jobinfo.job.key', '', { key: key }),
      content: l('devops.jobinfo.job.keyConfirm', '', { key: key }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        if (key == operatorType.CANCEL_JOB) {
          postAll(API_CONSTANTS.CANCEL_JOB, {
            clusterId: jobDetail?.clusterInstance?.id,
            jobId: jobDetail?.instance?.jid
          });
        } else if (key == operatorType.RESTART_JOB) {
          getData(API_CONSTANTS.RESTART_TASK, {
            id: jobDetail?.instance?.taskId,
            isOnLine: jobDetail?.instance?.step == JOB_LIFE_CYCLE.PUBLISH
          });
        } else {
          cancelTask('', jobDetail?.instance?.taskId);
        }
        message.success(l('devops.jobinfo.job.key.success', '', { key: key }));
      }
    });
  };

  return (
    <Space>
      <Button icon={<RedoOutlined />} onClick={() => refesh(true)} />

      <Button key='flinkwebui' href={webUri} target={'_blank'}>
        FlinkWebUI
      </Button>
      <Button
        key='autorestart'
        type='primary'
        onClick={() => handleJobOperator(operatorType.RESTART_JOB)}
      >
        {jobDetail?.instance?.step == 5
          ? l('devops.jobinfo.reonline')
          : l('devops.jobinfo.restart')}
      </Button>

      {isStatusDone(jobDetail?.instance?.status as string) ? (
        <></>
      ) : (
        <>
          <Button
            key='autostop'
            type='primary'
            onClick={() => handleJobOperator(operatorType.SAVEPOINT_STOP)}
            danger
          >
            {jobDetail?.instance?.step == 5
              ? l('devops.jobinfo.offline')
              : l('devops.jobinfo.smart_stop')}
          </Button>
          <Dropdown
            key='dropdown'
            trigger={['click']}
            menu={{
              onClick: (e) => handleJobOperator(e.key as string),
              items: [
                {
                  key: operatorType.SAVEPOINT_TRIGGER,
                  label: l('devops.jobinfo.savepoint.trigger')
                },
                {
                  key: operatorType.SAVEPOINT_STOP,
                  label: l('devops.jobinfo.savepoint.stop')
                },
                {
                  key: operatorType.SAVEPOINT_CANCEL,
                  label: l('devops.jobinfo.savepoint.cancel')
                }
              ]
            }}
          >
            <Button key='4' style={{ padding: '0 8px' }}>
              <EllipsisOutlined />
            </Button>
          </Dropdown>
        </>
      )}
    </Space>
  );
};
export default JobOperator;
