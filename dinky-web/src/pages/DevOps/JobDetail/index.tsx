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

import JobLifeCycleTag from '@/components/JobTags/JobLifeCycleTag';
import StatusTag from '@/components/JobTags/StatusTag';
import AlertHistory from '@/pages/DevOps/JobDetail/AlertHistory';
import CheckPoints from '@/pages/DevOps/JobDetail/CheckPointsTab';
import JobLineage from '@/pages/DevOps/JobDetail/JobLineage';
import JobLogsTab from '@/pages/DevOps/JobDetail/JobLogs/JobLogsTab';
import JobMetrics from '@/pages/DevOps/JobDetail/JobMetrics';
import JobOperator from '@/pages/DevOps/JobDetail/JobOperator/JobOperator';
import JobConfigTab from '@/pages/DevOps/JobDetail/JobOverview/JobOverview';
import JobVersionTab from '@/pages/DevOps/JobDetail/JobVersion/JobVersionTab';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { ClusterOutlined, FireOutlined, RocketOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useRequest } from '@umijs/max';
import { Tag } from 'antd';
import { useState } from 'react';
import { connect, useLocation } from 'umi';

/**
 * Enum defining different operators for the JobDetail component.
 */
const OperatorEnum = {
  JOB_BASE_INFO: 'job_base_info',
  JOB_LOGS: 'job_logs',
  JOB_VERSION: 'job_version',
  JOB_CHECKPOINTS: 'job_checkpoints',
  JOB_ALERT: 'job_alert',
  JOB_METRICS: 'job_monitor',
  JOB_LINEAGE: 'job_lineage'
};

/**
 * Renders the JobDetail component.
 *
 * @param {any} props - The component props.
 * @returns {JSX.Element} - The rendered JobDetail component.
 */
const JobDetail = (props: any) => {
  const { dispatch, jobInfoDetail } = props;

  const params = useLocation();
  const id = params.search.split('=')[1];

  const [tabKey, setTabKey] = useState<string>(OperatorEnum.JOB_BASE_INFO);

  // Define the components for each job operator
  const JobOperatorItems = {
    [OperatorEnum.JOB_BASE_INFO]: <JobConfigTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_LOGS]: <JobLogsTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_VERSION]: <JobVersionTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_CHECKPOINTS]: <CheckPoints jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_METRICS]: <JobMetrics />,
    [OperatorEnum.JOB_LINEAGE]: <JobLineage />,
    [OperatorEnum.JOB_ALERT]: <AlertHistory jobDetail={jobInfoDetail} />
  };

  useRequest(
    {
      url: API_CONSTANTS.REFRESH_JOB_DETAIL,
      params: { id: id }
    },
    {
      cacheKey: 'data-detail',
      pollingInterval: 3000,
      onSuccess: (data: Jobs.JobInfoDetail, params) => {
        dispatch({
          type: 'Devops/setJobInfoDetail',
          jobDetail: data
        });
      }
    }
  );

  // Define the tabs config for job operators
  const JobOperatorTabs = [
    {
      tab: l('devops.jobinfo.config.JobInfo'),
      key: OperatorEnum.JOB_BASE_INFO
    },
    { tab: l('devops.jobinfo.config.JobLogs'), key: OperatorEnum.JOB_LOGS },
    {
      tab: l('devops.jobinfo.config.JobVersion'),
      key: OperatorEnum.JOB_VERSION
    },
    {
      tab: l('devops.jobinfo.config.JobCheckpoints'),
      key: OperatorEnum.JOB_CHECKPOINTS
    },
    {
      tab: l('devops.jobinfo.config.JobMonitor'),
      key: OperatorEnum.JOB_METRICS
    },
    {
      tab: l('devops.jobinfo.config.JobLineage'),
      key: OperatorEnum.JOB_LINEAGE
    },
    { tab: l('devops.jobinfo.config.JobAlert'), key: OperatorEnum.JOB_ALERT }
  ];

  return (
    <PageContainer
      title={jobInfoDetail?.instance?.name}
      subTitle={<JobLifeCycleTag status={jobInfoDetail?.instance?.step} />}
      ghost={false}
      extra={<JobOperator jobDetail={jobInfoDetail} />}
      onBack={() => window.history.back()}
      breadcrumb={{}}
      tabList={JobOperatorTabs}
      onTabChange={(key) => setTabKey(key)}
      tags={[
        <StatusTag status={jobInfoDetail?.instance?.status} />,
        <Tag key={'tg1'} color='blue'>
          <FireOutlined /> {jobInfoDetail?.instance?.jid}
        </Tag>,
        <Tag key={'tg2'} color='blue'>
          <RocketOutlined /> {jobInfoDetail?.history?.type}
        </Tag>,
        <Tag key={'tg3'} color='green'>
          <ClusterOutlined /> {jobInfoDetail?.cluster?.alias}
        </Tag>
      ]}
    >
      {JobOperatorItems[tabKey]}
    </PageContainer>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobInfoDetail: Devops.jobInfoDetail
}))(JobDetail);
