import {useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import JobManagerConfiguration from "@/pages/DevOps/JobInfo/ClusterConfiguration/JobManager";
import TaskManagerConfigurationForm from "@/pages/DevOps/JobInfo/ClusterConfiguration/TaskManager";

const ClusterConfiguration = (props: any) => {
  const {job} = props;
  const [tabKey, setTabKey] = useState<string>('jobmanager');
  return (
    <PageContainer
      header={{title: undefined}}
      tabList={[
        {
          tab: 'Job Manager',
          key: 'jobmanager',
          closable: false,
        },
        {
          tab: 'Task Managers',
          key: 'taskmanager',
          closable: false,
        },
      ]}
      onTabChange={(key) => {
        setTabKey(key);
      }}
    >
      <ProCard  >
        {tabKey === 'jobmanager' ? <JobManagerConfiguration  job={job}/> : undefined}
        {tabKey === 'taskmanager' ? <TaskManagerConfigurationForm  job={job}/> : undefined}
      </ProCard>
    </PageContainer>
  );

};

export default ClusterConfiguration;
