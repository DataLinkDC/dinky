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
import {Card, Col, List, Row, Skeleton, Tabs, Tag, Typography} from "antd";
import moment from "moment";
import CodeShow from "@/components/CustomEditor/CodeShow";
import React, {useState} from "react";
import {useRequest} from "@@/exports";
import {API_CONSTANTS} from "@/services/constants";
import {RocketOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {JobProps} from "@/pages/DevOps/JobDetail/data";

type Version = {
  id?: number,
  taskId?: number,
  name?: string,
  dialect?: string,
  type?: string,
  statement: string,
  versionId?: string,
  createTime?: string
  isLatest?: boolean
}

const JobVersionTab = (props: JobProps) => {

  const {jobDetail} = props;
  const latestVersion: Version = {
    type: jobDetail.history.type,
    statement: jobDetail.history.statement,
    createTime: jobDetail.history.startTime,
    versionId: "LATEST",
    isLatest: true
  };

  const [currentVersion, setCurrentVersion] = useState<Version>({statement: ""})

  const version_list = useRequest({
    url: API_CONSTANTS.GET_JOB_VERSION,
    params: {taskId: jobDetail.history.taskId},
  }, {
    onSuccess: (data: Version[], params) => {
      data.splice(0, 0, latestVersion)
    }
  });

  const renderVersionList = () => {
    return (
      <Row>
        <Col span={3}>
          <div id="scrollableDiv">
            <List
              size={"small"}
              header={l('devops.jobinfo.version.versionList')}
              dataSource={version_list.data}
              renderItem={(item: Version) => (<>
                  <List.Item onClick={() => {
                    setCurrentVersion(item)
                  }}>
                    <Skeleton avatar title={false} loading={version_list.loading} active>
                      <List.Item.Meta
                        title={<a>{!item.isLatest ? "V" + item.versionId :
                          <Tag key={"v-latest"} color="green">{l('devops.jobinfo.version.latestVersion')}</Tag>}</a>}
                        description={item.createTime}
                      />
                    </Skeleton>
                  </List.Item>
                </>
              )}
            >
            </List>
          </div>
        </Col>
        <Col span={21}>
          <Card title={"V" + currentVersion?.versionId} bordered={false}
                extra={<>
                  <Tag key={"v-type"} color="blue">{currentVersion?.type}</Tag>
                  <Tag key={"v-dialect"} color="yellow">{currentVersion?.dialect}</Tag>
                </>}>
            <CodeShow
              code={currentVersion?.statement}
              height={500}
              language={"sql"}
            />
          </Card>
        </Col>
      </Row>
    );
  };


  return <>
    <ProCard>
      {renderVersionList()}
    </ProCard>
  </>
};

export default JobVersionTab;
