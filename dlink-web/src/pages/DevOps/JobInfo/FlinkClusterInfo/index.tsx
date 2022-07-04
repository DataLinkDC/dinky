import JobManagerInfo from "@/pages/DevOps/JobInfo/FlinkClusterInfo/JobManager";
import TaskManagerInfo from "@/pages/DevOps/JobInfo/FlinkClusterInfo/TaskManager";
import {Tabs} from "antd";

const {TabPane} = Tabs;

const FlinkClusterInfo = (props: any) => {
  const {job} = props;

  return (
    <>
      {
        <Tabs defaultActiveKey="JobManagerInfo" size="small" tabPosition="top" style={{border: "1px solid #f0f0f0"}}>
          <TabPane tab={<span>Job Manager</span>} key="JobManagerInfo">
            <JobManagerInfo job={job}/>
          </TabPane>
          <TabPane tab={<span>Task Managers</span>} key="TaskManagerInfo">
            <TaskManagerInfo job={job}/>
          </TabPane>
        </Tabs>
      }
    </>
  );
};

export default FlinkClusterInfo;
