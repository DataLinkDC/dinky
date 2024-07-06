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

import { Radar } from '@ant-design/plots';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import { Link, useRequest } from '@umijs/max';
import { Avatar, Button, Card, Col, List, Row, Space, Statistic, Tag } from 'antd';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import React, { FC } from 'react';
import { useModel } from '@@/exports';
import { getRandomGreeting } from '@/pages/Home/util';
import useHookRequest from '@/hooks/useHookRequest';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { StatusCountOverView } from '@/types/Home/data';
import StatisticsCard from '@/pages/DevOps/JobList/components/Overview/StatisticsCard';
import { l } from '@/utils/intl';
import {
  AllJobIcons,
  ErrorIcons,
  RunningIcons,
  UnknownIcons
} from '@/components/Icons/DevopsIcons';
import { ThunderboltTwoTone } from '@ant-design/icons';
import OSMetrics from '@/pages/Home/components/OSMetrics/OSMetrics';
import MyWorker from '@/pages/Home/components/MyWorker/MyWorker';
import FastLink from '@/pages/Home/components/FastLink/FastLink';
import AlertHistoryList from '@/pages/DevOps/JobDetail/AlertHistory/components/AlertHistoryList';
import BatchStreamProportion from '@/pages/Home/components/BatchStreamProportion';
import WorkHeader from '@/pages/Home/components/WorkerHeader/WorkHeader';

dayjs.extend(relativeTime);

const Workplace: FC = () => {
  const { data } = useHookRequest(getData, { defaultParams: [API_CONSTANTS.GET_STATUS_COUNT] });
  const statusCount = data as StatusCountOverView;

  const a: any = {};

  const ExtraContent: FC<Record<string, any>> = () => {
    return (
      <ProCard layout='center' ghost>
        <StatisticsCard
          title={l('devops.joblist.status.running')}
          value={statusCount?.running}
          icon={<RunningIcons size={50} />}
        />
        <StatisticsCard
          title={l('devops.joblist.status.failed')}
          value={statusCount?.failed}
          icon={<ErrorIcons size={50} />}
        />
        <StatisticsCard
          title={l('devops.joblist.status.unknown')}
          value={statusCount?.unknown}
          icon={<UnknownIcons size={50} />}
        />
      </ProCard>
    );
  };

  return (
    <PageContainer style={{ padding: 10 }}>
      <Row gutter={24}>
        <Col xl={16} lg={24} md={24} sm={24} xs={24}>
          <WorkHeader />
          <br />
          <MyWorker />
          <Card
            bodyStyle={{
              padding: 0,
              height: 100
            }}
            bordered={false}
            title={l('devops.jobinfo.config.JobAlert')}
          >
            <AlertHistoryList jobDetail={a} />
          </Card>
        </Col>
        <Col xl={8} lg={24} md={24} sm={24} xs={24}>
          <Card
            // size={"small"}
            title={l('home.fast.link')}
            bordered={false}
            bodyStyle={{
              padding: 0
            }}
          >
            <FastLink />
          </Card>
          <br />
          <BatchStreamProportion />
          <br />
          <ExtraContent />
          <br />
          <OSMetrics />
        </Col>
      </Row>
    </PageContainer>
  );
};
export default Workplace;
