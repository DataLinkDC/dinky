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

import StatusTag from '@/components/JobTags/StatusTag';
import { l } from '@/utils/intl';
import { Card, Col, Row, Statistic, Typography } from 'antd';

const { Text, Paragraph } = Typography;

const DagDataNode = (props: any) => {
  const { node } = props;
  const data: any = node?.getData();

  // 渲染 ratio
  function renderRatio(ratio: number, reverse: boolean) {
    if (ratio === undefined || ratio === null || isNaN(ratio)) ratio = 0;
    if (!reverse) {
      return (
        <>
          <Statistic
            value={ratio * 100}
            suffix={'%'}
            prefix={' '}
            precision={0}
            valueStyle={{
              color:
                ratio > 0.75
                  ? '#cf1322'
                  : ratio > 0.5
                  ? '#d46b08'
                  : ratio > 0.25
                  ? '#d4b106'
                  : '#3f8600',
              fontSize: 10
            }}
          />
        </>
      );
    }
    return (
      <>
        <Statistic
          value={ratio * 100}
          suffix={'%'}
          prefix={' '}
          precision={0}
          valueStyle={{
            color:
              ratio > 0.75
                ? '#3f8600'
                : ratio > 0.5
                ? '#d4b106'
                : ratio > 0.25
                ? '#d46b08'
                : '#cf1322',
            fontSize: 10
          }}
        />
      </>
    );
  }

  const backpressure = data.backpressure;
  return (
    <Card
      style={{ width: '250px', padding: 0, margin: 0, height: 140 }}
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
        <Col flex='35%'>
          <Text style={{ display: 'inline-flex', alignItems: 'center' }} type='secondary'>
            {' '}
            {l('devops.baseinfo.busy')}:
            {renderRatio(
              backpressure && backpressure.subtasks ? backpressure.subtasks[0]?.busyRatio : 0,
              false
            )}
          </Text>
        </Col>
        <Col flex='auto'>
          <Text type='secondary' ellipsis>
            {l('devops.baseinfo.backpressure')}:
            <StatusTag
              status={backpressure ? backpressure.status : 0}
              bordered={false}
              animation={false}
            />
          </Text>
        </Col>
      </Row>

      <Row>
        <Col flex='35%'>
          <Text style={{ display: 'inline-flex', alignItems: 'center' }} type='secondary'>
            {l('devops.baseinfo.idle')}:
            {renderRatio(
              backpressure && backpressure.subtasks ? backpressure.subtasks[0]?.idleRatio : 0,
              true
            )}
          </Text>
        </Col>
        <Col flex='auto'>
          <Text type='secondary'>
            {l('devops.baseinfo.status')}:
            <StatusTag status={data.status} bordered={false} animation={false} />
          </Text>
        </Col>
      </Row>
    </Card>
  );
};
export default DagDataNode;
