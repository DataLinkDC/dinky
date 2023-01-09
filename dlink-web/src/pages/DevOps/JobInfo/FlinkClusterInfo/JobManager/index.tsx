import {Descriptions, Empty, Tabs} from 'antd';
import CodeShow from "@/components/Common/CodeShow";
import {getJobManagerInfo} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import {JobManagerConfiguration} from "@/pages/DevOps/data";

const {TabPane} = Tabs;

const JobManagerInfo = (props: any) => {
  const {job} = props;
  const [jobManager, setJobManager] = useState<JobManagerConfiguration>();

  const refreshJobManagerInfo = () => {
    const res = getJobManagerInfo(job?.history?.jobManagerAddress);
    res.then((result) => {
      setJobManager(result.datas);
    });
  }

  useEffect(() => {
    refreshJobManagerInfo();
  }, []);

  const getMetricsConfigForm = () => {
    let formList = [];
    let tempData = jobManager?.metrics;
    for (let key in tempData) {
      formList.push(
        <Descriptions.Item label={key}>
          {tempData[key]}
        </Descriptions.Item>
      )
    }
    return formList
  }

  const getJobManagerConfigForm = () => {
    let formList = [];
    let tempData = jobManager?.jobManagerConfig;
    for (let key in tempData) {
      formList.push(
        <Descriptions.Item label={key}>
          {tempData[key]}
        </Descriptions.Item>
      )
    }
    return formList
  }

  return (
    <>
      <Tabs defaultActiveKey="metrics" size="small" tabPosition="top" style={{
        border: "1px solid #f0f0f0",
      }}>
        <TabPane tab={<span>&nbsp; Metrics &nbsp;</span>} key="metrics">
          <Descriptions bordered size="small" column={1}>
            {getMetricsConfigForm()}
          </Descriptions>
        </TabPane>

        <TabPane tab={<span>&nbsp; Configuration &nbsp;</span>} key="configuration">
          <Descriptions bordered size="small" column={1}>
            {getJobManagerConfigForm()}
          </Descriptions>
        </TabPane>

        <TabPane tab={<span>&nbsp; Logs &nbsp;</span>} key="logs">
          {(jobManager?.jobManagerLog) ? <CodeShow code={jobManager?.jobManagerLog} language='java' height='500px'/>
            : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
          }
        </TabPane>

        <TabPane tab={<span>&nbsp; Stdout &nbsp;</span>} key="stdout">
          {(jobManager?.jobManagerStdout) ?
            <CodeShow code={jobManager?.jobManagerStdout} language='java' height='500px'/>
            : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
          }
        </TabPane>
      </Tabs>
    </>)
};

export default JobManagerInfo;
