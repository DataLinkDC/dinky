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

import {
  AllJobIcons,
  BatchIcons,
  CancelIcons,
  ErrorIcons,
  FinishIcons,
  RestartIcons,
  RunningIcons,
  SteamIcons,
  UnknownIcons
} from '@/components/Icons/DevopsIcons';
import { DevopContext } from '@/pages/DevOps';
import { JOB_STATUS } from '@/pages/DevOps/constants';
import StatisticsCard from '@/pages/DevOps/JobList/components/Overview/StatisticsCard';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { StatusCountOverView } from '@/types/Home/data';
import { l } from '@/utils/intl';
import { ProCard } from '@ant-design/pro-components';
import { Button, Col, Row, Space } from 'antd';
import { useContext, useEffect, useState } from 'react';

const JobOverview = (props: any) => {
  const [statusCount, setStatusCount] = useState<StatusCountOverView>();
  const { statusFilter, setStatusFilter } = useContext<any>(DevopContext);

  useEffect(() => {
    getData(API_CONSTANTS.GET_STATUS_COUNT)
      .then((res) => {
        setStatusCount(res.data);
      })
      .catch(() => {});
  }, []);

  return (
    <Row gutter={[16, 8]}>
      <Col span={5}>
        <ProCard colSpan={'20%'} boxShadow={true}>
          <StatisticsCard
            title={l('devops.joblist.status.all')}
            value={statusCount?.all}
            icon={<AllJobIcons size={60} />}
            divider={false}
            atClick={() => {
              setStatusFilter(undefined);
            }}
            extra={
              <Space direction='vertical'>
                <Button type={'text'} icon={<BatchIcons size={20} />}>
                  {l('home.job.batch')}: {statusCount?.modelOverview?.batchJobCount}
                </Button>
                <Button type={'text'} icon={<SteamIcons size={20} />}>
                  {l('home.job.stream')}: {statusCount?.modelOverview?.streamingJobCount}
                </Button>
              </Space>
            }
          />
        </ProCard>
      </Col>
      <Col span={19}>
        <ProCard layout='center' boxShadow={true}>
          <StatisticsCard
            title={l('devops.joblist.status.running')}
            value={statusCount?.running}
            icon={<RunningIcons size={60} />}
            atClick={() => {
              setStatusFilter(JOB_STATUS.RUNNING);
            }}
          />
          <StatisticsCard
            title={l('devops.joblist.status.cancelled')}
            value={statusCount?.canceled}
            icon={<CancelIcons size={60} />}
            atClick={() => {
              setStatusFilter(JOB_STATUS.CANCELED);
            }}
          />
          <StatisticsCard
            title={l('devops.joblist.status.failed')}
            value={statusCount?.failed}
            icon={<ErrorIcons size={60} />}
            atClick={() => {
              setStatusFilter(JOB_STATUS.FAILED);
            }}
          />
          <StatisticsCard
            title={l('devops.joblist.status.restarting')}
            value={statusCount?.restarting}
            icon={<RestartIcons size={60} />}
            atClick={() => {
              setStatusFilter(JOB_STATUS.RESTARTING);
            }}
          />
          <StatisticsCard
            title={l('devops.joblist.status.finished')}
            value={statusCount?.finished}
            icon={<FinishIcons size={60} />}
            atClick={() => {
              setStatusFilter(JOB_STATUS.FINISHED);
            }}
          />
          <StatisticsCard
            title={l('devops.joblist.status.unknown')}
            value={statusCount?.unknown}
            icon={<UnknownIcons size={60} />}
            divider={false}
            atClick={() => {
              setStatusFilter(JOB_STATUS.UNKNOWN);
            }}
          />
        </ProCard>
      </Col>
    </Row>
  );
};
export default JobOverview;
