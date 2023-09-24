/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import React from "react";
import {Jobs} from "@/types/DevOps/data";
import {Card, Col, Row, Tag, Typography} from "antd";
import StatusTag from "@/components/JobTags/StatusTag";

const { Text } = Typography;

const DagNode = (props: any) => {
  const {node} = props
  const vertices:Jobs.JobVertices = node?.getData()

  return (
    <Card
      style={{width: "250px"}}
      bordered={false}
      size={"small"}
      type={"inner"}
      hoverable={true}
      title={vertices.name}
      extra={<Text keyboard>{vertices.parallelism}</Text>}
    >
      <Row>
        <Col span={15}>
          <Text type="secondary">BackPressure：</Text>
          <Tag bordered={false} color="success">
            OK
          </Tag>
        </Col>
        <Col span={8}>
          <Text type="secondary">Busy：97%</Text>
        </Col>
      </Row>
      <Row>
        <Col span={15}>
          <Text type="secondary">Status：</Text>
          <StatusTag  status={vertices.status} bordered={false} animation={false}/>
        </Col>
        <Col span={8}>
          <Text type="secondary">Idle：50%</Text>
        </Col>
      </Row>
    </Card>
  )
};
export default DagNode;
