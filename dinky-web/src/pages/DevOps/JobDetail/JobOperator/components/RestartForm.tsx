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
import useHookRequest from '@/hooks/useHookRequest';
import { restartTask } from '@/pages/DataStudio/HeaderContainer/service';
import { isStatusDone } from '@/pages/DevOps/function';
import { Jobs } from '@/types/DevOps/data';
import { parseByteStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { Alert, Button, Descriptions, DescriptionsProps, Divider, Spin, Typography } from 'antd';

const { Paragraph } = Typography;

const RestartForm = (props: { lastCheckpoint: any; instance: Jobs.JobInstance }) => {
  const { lastCheckpoint, instance } = props;

  const { data, loading, run } = useHookRequest(restartTask, {
    defaultParams: [instance?.taskId, '', l('devops.jobinfo.ck.recovery')],
    manual: true
  });

  const buildDesc = (ckp: any, title: string) => {
    if (!ckp) {
      return (
        <>
          <Alert message={`Not Found ${title}`} type='warning' />
          <br />
        </>
      );
    }
    const items: DescriptionsProps['items'] = [
      {
        key: 'ckp_id',
        label: 'ID',
        children: ckp.id
      },
      {
        key: 'ckp_state_size',
        label: l('devops.jobinfo.ck.state_size'),
        children: parseByteStr(ckp.state_size)
      },
      {
        key: 'latest_ack_timestamp',
        label: l('devops.jobinfo.ck.latest_ack_timestamp'),
        children: new Date(ckp.latest_ack_timestamp).toLocaleString('zh-cn')
      },
      {
        key: 'ckp_path',
        label: l('devops.jobinfo.ck.external_path'),
        span: 4,
        children: <EllipsisMiddle maxCount={60} children={ckp.external_path} copyable={false} />
      }
    ];
    return (
      <>
        <Descriptions
          title={title}
          size={'small'}
          bordered
          items={items}
          extra={
            <Button
              type={'link'}
              onClick={() =>
                run(instance?.taskId, ckp.external_path, l('devops.jobinfo.ck.recovery'))
              }
            >
              重启后此处恢复
            </Button>
          }
        />
        <br />
      </>
    );
  };

  return (
    <ModalForm
      loading={loading}
      title={l('devops.jobinfo.restart')}
      trigger={<Button type={'primary'}>{l('devops.jobinfo.restart')}</Button>}
      submitter={false}
    >
      <Spin spinning={loading}>
        {instance?.status && !isStatusDone(instance?.status) ? (
          <>
            <Divider orientation={'left'} style={{ margin: '8px 0' }}>
              {l('devops.jobinfo.smart_restart')}
            </Divider>
            <Paragraph>
              <blockquote>{l('devops.jobinfo.smart_restart.help')}</blockquote>
            </Paragraph>
            <Button
              type={'link'}
              size={'small'}
              onClick={() => run(instance?.taskId, '', l('devops.jobinfo.ck.recovery'))}
            >
              {l('devops.jobinfo.restart.auto.savepoint')}
            </Button>
          </>
        ) : (
          <Alert message={l('devops.jobinfo.restart.cannot.auto.savepoint')} type={'warning'} />
        )}
        <Divider orientation={'left'} style={{ margin: '8px 0' }}>
          {l('devops.jobinfo.restart.from.savepoint')}
        </Divider>
        <Paragraph>
          <blockquote>{l('devops.jobinfo.restart.from.savepoint.help')}</blockquote>
        </Paragraph>
        {buildDesc(lastCheckpoint?.completed, 'Last Checkpoint')}
        {buildDesc(lastCheckpoint?.savepoint, 'Last Savepoint')}
      </Spin>
    </ModalForm>
  );
};
export default RestartForm;
