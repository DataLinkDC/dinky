import {Tabs, Empty} from 'antd';
import CodeShow from "@/components/Common/CodeShow";

const {TabPane} = Tabs;

const Exception = (props: any) => {

  const {job} = props;

  return (<>
    <Tabs defaultActiveKey="RootException" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0"
    }}>
      <TabPane tab={<span>Root Exception</span>} key="RootException">
        <CodeShow code={job.jobHistory?.exceptions['root-exception'] as string} language='java' height='500px'/>
      </TabPane>
      <TabPane tab={<span>Exception History</span>} key="ExceptionHistory">
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      </TabPane>
    </Tabs>
  </>)
};

export default Exception;
