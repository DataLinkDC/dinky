import { TagJobLifeCycle } from '@/pages/DevOps/function';
import CheckPoints from '@/pages/DevOps/JobDetail/CheckPointsTab';
import JobLogsTab from '@/pages/DevOps/JobDetail/JobLogs/JobLogsTab';
import JobOperator from '@/pages/DevOps/JobDetail/JobOperator/JobOperator';
import JobConfigTab from '@/pages/DevOps/JobDetail/JobOverview/JobOverview';
import JobVersionTab from '@/pages/DevOps/JobDetail/JobVersion/JobVersionTab';
import JobOperatorGraph from '@/pages/Home/JobOverView/JobOperatorGraph';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { ClusterOutlined, FireOutlined, RocketOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useRequest } from '@umijs/max';
import { Tag } from 'antd';
import type { FC } from 'react';
import { useState } from 'react';
import { useLocation } from 'umi';

/**
 * Enum defining different operators for the JobDetail component.
 */
const OperatorEnum = {
  JOB_BASE_INFO: 'job_base_info',
  JOB_LOGS: 'job_logs',
  JOB_VERSION: 'job_version',
  JOB_CHECKPOINTS: 'job_checkpoints',
  JOB_ALERT: 'job_alert',
  JOB_MONITOR: 'job_monitor',
  JOB_LINEAGE: 'job_lineage',
  JOB_GRAPH: 'job_graph'
};

/**
 * Renders the JobDetail component.
 *
 * @param {any} props - The component props.
 * @returns {JSX.Element} - The rendered JobDetail component.
 */
const JobDetail: FC = (props: any) => {
  // Get the URL parameters
  const params = useLocation();
  const id = params.search.split('=')[1];

  // Set the initial tab key state
  const [tabKey, setTabKey] = useState<string>(OperatorEnum.JOB_BASE_INFO);

  // Fetch the job detail data
  const { data } = useRequest(
    {
      url: API_CONSTANTS.GET_JOB_DETAIL,
      params: { id: id }
    },
    {
      cacheKey: 'data-detail',
      pollingInterval: 1000
    }
  );

  // Extract the job info detail from the fetched data
  const jobInfoDetail: Jobs.JobInfoDetail = data;

  // Define the components for each job operator
  const JobOperatorItems = {
    [OperatorEnum.JOB_BASE_INFO]: <JobConfigTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_LOGS]: <JobLogsTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_VERSION]: <JobVersionTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_CHECKPOINTS]: <CheckPoints jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_MONITOR]: <CheckPoints jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_LINEAGE]: <CheckPoints jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_ALERT]: <CheckPoints jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_GRAPH]: <JobOperatorGraph jobDetail={jobInfoDetail} />
  };

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
      key: OperatorEnum.JOB_MONITOR
    },
    {
      tab: l('devops.jobinfo.config.JobLineage'),
      key: OperatorEnum.JOB_LINEAGE
    },
    { tab: l('devops.jobinfo.config.JobAlert'), key: OperatorEnum.JOB_ALERT },
    {
      tab: l('devops.jobinfo.config.OperatorGraph'),
      key: OperatorEnum.JOB_GRAPH
    }
  ];

  return (
    <PageContainer
      title={jobInfoDetail?.instance?.name}
      subTitle={TagJobLifeCycle(jobInfoDetail?.instance?.step)}
      ghost={false}
      extra={<JobOperator jobDetail={jobInfoDetail} />}
      onBack={() => window.history.back()}
      breadcrumb={{}}
      tabList={JobOperatorTabs}
      onTabChange={(key) => setTabKey(key)}
      tags={[
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

export default JobDetail;
