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

import StatusTag from '@/components/JobTags/StatusTag';
import { Card, Col, Row, Tag, Typography } from 'antd';

const { Text, Paragraph } = Typography;

const DagDataNode = (props: any) => {
  const { node } = props;
  const data: any = node?.getData();

  return (
    <Card
      style={{ width: '250px', padding: 0 }}
      bordered={false}
      size={'small'}
      type={'inner'}
      hoverable={true}
      title={data.name}
      extra={<Text keyboard>{data.parallelism}</Text>}
    >
      <Paragraph ellipsis={{ tooltip: data.description }}>
        <blockquote style={{ margin: 0 }}>
          <Text type='secondary'>{data.description}</Text>
        </blockquote>
      </Paragraph>

      <Row>
        <Col span={15}>
          <Text type='secondary'>BackPressure：</Text>
          <Tag bordered={false} color='success'>
            OK
          </Tag>
        </Col>
        <Col span={8}>
          <Text type='secondary'>Busy：97%</Text>
        </Col>
      </Row>
      <Row>
        <Col span={15}>
          <Text type='secondary'>Status：</Text>
          <StatusTag status={data.status} bordered={false} animation={false} />
        </Col>
        <Col span={8}>
          <Text type='secondary'>Idle：50%</Text>
        </Col>
      </Row>
    </Card>
  );
};
export default DagDataNode;
