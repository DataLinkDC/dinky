import {useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import JobManagerConfiguration from "@/pages/DevOps/JobInfo/ClusterConfiguration/JobManager";
import TaskManagerConfigurationForm from "@/pages/DevOps/JobInfo/ClusterConfiguration/TaskManager";
import {JOB_STATUS} from "@/components/Common/JobStatus";
import {Empty} from "antd";

const ClusterConfiguration = (props: any) => {
  const {job} = props;
  const [tabKey, setTabKey] = useState<string>('jobmanager');
  return (
    <>
      {job?.instance?.status === JOB_STATUS.RUNNING ?
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
          <ProCard>
            {tabKey === 'jobmanager' ? <JobManagerConfiguration job={job}/> : undefined}
            {tabKey === 'taskmanager' ? <TaskManagerConfigurationForm job={job}/> : undefined}
          </ProCard>
        </PageContainer> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      }

    </>
  );

};

export default ClusterConfiguration;
