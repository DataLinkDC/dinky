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


import { Tabs} from 'antd';
import {Jobs} from "@/types/DevOps/data";
import {ProCard} from "@ant-design/pro-components";
import CkDesc from "@/pages/DevOps/JobDetail/CheckPointsTab/components/CkDesc";
import CheckpointTable from "@/pages/DevOps/JobDetail/CheckPointsTab/components/CheckpointTable";
import SavepointTable from "@/pages/DevOps/JobDetail/CheckPointsTab/components/SavePointTable";
import {JobProps} from "@/pages/DevOps/JobDetail/data";

const {TabPane} = Tabs;

const CheckPoints = (props: JobProps) => {

  const {jobDetail} = props;

  return (
    <>
      <ProCard>
        <CkDesc jobDetail={jobDetail}/>
      </ProCard>
      <br/>
      <ProCard>
        <Tabs defaultActiveKey="history" size="small">
          <TabPane tab={"History"} key="history">
            <CheckpointTable jobDetail={jobDetail}/>
          </TabPane>
          <TabPane tab={"SavePoint"} key="savepoint">
            <SavepointTable jobDetail={jobDetail} />
          </TabPane>
        </Tabs>
      </ProCard>
    </>)
};
export default CheckPoints;
