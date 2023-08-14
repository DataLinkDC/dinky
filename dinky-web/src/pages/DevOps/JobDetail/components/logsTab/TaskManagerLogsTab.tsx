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


import {Jobs} from "@/types/DevOps/data";
import {ProCard} from "@ant-design/pro-components";
import {Card, Col, List, Row, Tabs, Typography} from "antd";
import moment from "moment";
import CodeShow from "@/components/CustomEditor/CodeShow";
import React, {useState} from "react";
import {useRequest} from "@@/exports";
import {API_CONSTANTS} from "@/services/constants";

const {Paragraph, Text} = Typography;

type JobProps = {
  jobDetail: Jobs.JobInfoDetail;
};

type Taskmanager = {
  containerId: string;
  containerPath: string;
  dataPort: string;
  slotsNumber: string;
  freeSlots: string;
  timeSinceLastHeartbeat: string;
}

const TaskManagerLogsTab = (props: JobProps) => {

  const {jobDetail} = props;
  const [currentTM, setCurrentTm] = useState<Taskmanager>({
    containerId: "1",
    containerPath: "",
    dataPort: "",
    freeSlots: "",
    slotsNumber: "",
    timeSinceLastHeartbeat: ""
  });

  const jmaddr = jobDetail?.history?.jobManagerAddress;

  const taskManagerList = useRequest({
    url: API_CONSTANTS.GET_TASKMANAGER_LIST,
    params: {address: jmaddr},
  });

  const tmLog = useRequest((cid)=>(
      {
        url: API_CONSTANTS.GET_TASKMANAGER_LOG,
        params: {address: jmaddr, containerId: cid}
      }
    ),
    {manual:true});

  const refeshLog = (tm: Taskmanager) => {
    // setLogRequestData();
    setCurrentTm(tm);
    tmLog.run(tm.containerId);
  }

  const renderLogTab = () => {
    return (
      <Row>
        <Col span={3}>
          <div id="scrollableDiv">
            <List
              size={"small"}
              header={'TaskManager列表'}
              dataSource={taskManagerList.data}
              renderItem={(item: Taskmanager) => (
                <List.Item onClick={() => refeshLog(item)}>
                  <Paragraph ellipsis={true}>{item.containerId}</Paragraph>
                </List.Item>
              )}
            />
          </div>
        </Col>
        <Col span={21}>
          <Card title={currentTM.containerId} bordered={false}
                extra={<Paragraph>{currentTM.containerPath}</Paragraph>}>
            <CodeShow
              code={tmLog.data}
              height={500}
            />
          </Card>
        </Col>
      </Row>
    );
  };


  return <>
    <ProCard>
      {renderLogTab()}
    </ProCard>
  </>
};

export default TaskManagerLogsTab;
