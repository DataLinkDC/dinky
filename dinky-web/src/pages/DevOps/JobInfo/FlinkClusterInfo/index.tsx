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
