import {PageContainer} from "@ant-design/pro-components";
import {TagJobLifeCycle} from "@/pages/DevOps/function";
import {Jobs} from '@/types/DevOps/data';
import {Tag} from "antd";
import {ClusterOutlined, FireOutlined, RocketOutlined} from "@ant-design/icons";
import {useLocation} from 'umi';
import {useRequest} from "@umijs/max";
import {API_CONSTANTS} from "@/services/constants";
import JobOperator from "@/pages/DevOps/JobDetail/components/JobOperator";
import type {FC} from 'react';
import {useState} from "react";
import JobConfigTab from "@/pages/DevOps/JobDetail/components/JobConfigTab";
import {l} from "@/utils/intl";

/**
 * Enum defining different operators for the JobDetail component.
 */
const OperatorEnum = {
  JOB_BASE_INFO:"job_base_info",
  JOB_OVERIVEW:"job_overivew",
  JOB_EXCEPTION:"job_exception",
}

/**
 * Renders the JobDetail component.
 *
 * @param {any} props - The component props.
 * @returns {JSX.Element} - The rendered JobDetail component.
 */
const JobDetail:FC = (props: any) => {
  // Get the URL parameters
  const params = useLocation();
  const id = params.search.split("=")[1];

  // Set the initial tab key state
  const [tabKey, setTabKey] = useState<string>(OperatorEnum.JOB_BASE_INFO);

  // Fetch the job detail data
  const { data } = useRequest({
    url: API_CONSTANTS.GET_JOB_DETAIL,
    params: { id: id },
  }, {
    cacheKey: 'data-detail',
    pollingInterval: 1000,
  });

  // Extract the job info detail from the fetched data
  const jobInfoDetail: Jobs.JobInfoDetail = data;

  // Define the components for each job operator
  const JobOperatorItems = {
    [OperatorEnum.JOB_BASE_INFO]: <JobConfigTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_OVERIVEW]: <JobConfigTab jobDetail={jobInfoDetail} />,
    [OperatorEnum.JOB_EXCEPTION]: <JobConfigTab jobDetail={jobInfoDetail} />,
  };

  // Define the tabs config for job operators
  const JobOperatorTabs = [
    { tab: l('devops.jobinfo.config.JobInfo'), key: OperatorEnum.JOB_BASE_INFO, },
    { tab: l('devops.jobinfo.config.JobOveriew'), key: OperatorEnum.JOB_OVERIVEW, },
    { tab: l('devops.jobinfo.config.JobException'), key: OperatorEnum.JOB_EXCEPTION, },
  ];

  return (
    <PageContainer
      title={jobInfoDetail?.instance?.name}
      subTitle={TagJobLifeCycle(jobInfoDetail?.instance?.step)}
      ghost={false}
      extra={<JobOperator jobDetail={jobInfoDetail}/>}
      onBack={()=> window.history.back()}
      breadcrumb={{}}
      tabList={JobOperatorTabs}
      onTabChange={(key) => setTabKey(key)}
      tags={[
        <Tag key={"tg1"} color="blue"><FireOutlined/> {jobInfoDetail?.instance?.jid}</Tag>,
        <Tag key={"tg2"} color="blue"><RocketOutlined/> {jobInfoDetail?.history?.type}</Tag>,
        <Tag key={"tg3"} color="green"><ClusterOutlined/> {jobInfoDetail?.cluster?.alias}</Tag>
      ]}
    >
      {JobOperatorItems[tabKey]}
    </PageContainer>
  )
}

export default JobDetail;
