import {Descriptions, Empty, Tabs} from 'antd';
import CodeShow from "@/components/Common/CodeShow";

const {TabPane} = Tabs;

const ClusterConfiguration = (props: any) => {
  const {} = props;
  const {job} = props;

  const getMetricsConfigForm =() => {
    let formList = [];
    let tempData = job?.jobManagerConfiguration?.metrics;
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
    let tempData = job?.jobManagerConfiguration?.jobManagerConfig;
    for (let key in tempData) {
      formList.push(
        <Descriptions.Item label={key}>
          {tempData[key]}
        </Descriptions.Item>
      )
    }
    return formList
  }

  return (<>
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
        {(job?.jobManagerConfiguration?.jobManagerLog === ""|| job?.jobManagerConfig?.jobManagerLog === null) ?
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
          : <CodeShow code={job?.jobManagerConfiguration?.jobManagerLog} language='java' height='500px'/>
        }
      </TabPane>

      <TabPane tab={<span>&nbsp; Stdout &nbsp;</span>} key="stdout">
        {(job?.jobManagerConfiguration?.jobManagerStdout === ""|| job?.jobManagerConfig?.jobManagerStdout === null) ?
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
          : <CodeShow code={job?.jobManagerConfiguration?.jobManagerStdout} language='java' height='500px'/>
        }
      </TabPane>
    </Tabs>
  </>)
};

export default ClusterConfiguration;
