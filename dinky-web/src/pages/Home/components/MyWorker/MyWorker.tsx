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

import { Button, Card, Space, Typography } from 'antd';
import useHookRequest from '@/hooks/useHookRequest';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskInfo } from '@/types/Studio/data';
import { ProCard } from '@ant-design/pro-components';
import JobLifeCycleTag from '@/components/JobTags/JobLifeCycleTag';
import StatusTag from '@/components/JobTags/StatusTag';
import EllipsisMiddle from '@/components/Typography/EllipsisMiddle';
import { l } from '@/utils/intl';
import { history } from 'umi';
import { formatDateToYYYYMMDDHHMMSS } from '@/utils/function';
import { ErrorMessageAsync } from '@/utils/messages';
import { getTabIcon } from '@/pages/DataStudio/function';

const MyWorker = () => {
  const { loading, data } = useHookRequest<any, any>(getData, {
    defaultParams: [API_CONSTANTS.MY_TASK]
  });

  const renderTitle = (item: TaskInfo) => {
    return (
      <Space>
        {getTabIcon(item.dialect, 20)}
        <EllipsisMiddle copyable={false} maxCount={15}>
          {item.name}
        </EllipsisMiddle>
      </Space>
    );
  };

  return (
    <Card
      style={{
        marginBottom: 18,
        height: 'calc(100vh - 32%)'
      }}
      title={l('home.mywork')}
      bordered={false}
      extra={
        <Button type='link' onClick={() => history.push('/devops')}>
          {l('home.allwork')}
        </Button>
      }
      loading={loading}
      styles={{
        body: {
          padding: 0
        }
      }}
    >
      <Card
        style={{
          height: '60vh',
          overflowY: 'auto'
        }}
      >
        {data?.map((item: TaskInfo) => (
          <Card.Grid key={item.id} style={{ padding: 5 }}>
            <ProCard
              style={{ cursor: 'pointer' }}
              bordered={false}
              title={renderTitle(item)}
              extra={
                <Space>
                  <JobLifeCycleTag animation={false} bordered={false} status={item.step} />
                </Space>
              }
              onClick={async () => {
                if (!item.jobInstanceId) {
                  await ErrorMessageAsync(l('home.task.not.instance'), 2);
                  return;
                } else {
                  history.push('/devops/job-detail?id=' + item.jobInstanceId);
                }
              }}
            >
              <div style={{ marginBottom: 10 }}>{item.note ?? l('home.task.not.desc')}</div>
              <Space style={{ fontSize: 10 }}>
                <Typography.Text type='secondary'>
                  {l('home.task.update.at', '', {
                    time: formatDateToYYYYMMDDHHMMSS(item.updateTime)
                  })}
                </Typography.Text>
                <StatusTag animation={false} bordered={false} status={item.status} />
              </Space>
            </ProCard>
          </Card.Grid>
        ))}
      </Card>
    </Card>
  );
};
export default MyWorker;
