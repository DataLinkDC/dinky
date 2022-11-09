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


import {Tabs} from "antd";
import VersionList from "@/pages/DevOps/JobInfo/Version/VersionList";
import VersionTimeLineList from "@/pages/DevOps/JobInfo/Version/VersionTimeLineList";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;
const TaskVersionInfo = (props: any) => {
  const {job} = props;


  return (<>
    <Tabs defaultActiveKey="overview" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0",
    }}>
      <TabPane tab={<span>&nbsp; 版本列表 &nbsp;</span>} key="versionlist">
        <VersionList job={job}/>
      </TabPane>

      <TabPane tab={<span>&nbsp; TimeLine &nbsp;</span>} key="timeline">
        <VersionTimeLineList job={job}/>
      </TabPane>

    </Tabs>
  </>)
};

export default TaskVersionInfo;
