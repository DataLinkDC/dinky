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

import CodeShow from '@/components/CustomEditor/CodeShow';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { ProCard } from '@ant-design/pro-components';
import { Card, Col, List, Row, Typography } from 'antd';
import moment from 'moment';
import { useState } from 'react';

const { Paragraph, Text } = Typography;

type Exceptions = {
  location: string;
  taskName: string;
  timestamp: string;
  stacktrace: string;
  exceptionName: string;
};

const ExceptionTab = (props: JobProps) => {
  const { jobDetail } = props;
  const [currentLog, setCurrentLog] = useState<Exceptions>({
    exceptionName: '',
    location: '',
    stacktrace: '',
    taskName: '',
    timestamp: ''
  });

  const renderLogTab = () => {
    let logs = [];
    const rte = jobDetail?.jobDataDto?.exceptions['root-exception'];
    logs.push({
      taskName: 'RootException',
      stacktrace: rte,
      exceptionName: rte
    });
    logs.push(...jobDetail.jobDataDto?.exceptions['exceptionHistory']['entries']);
    return (
      <Row>
        <Col span={3}>
          <div id='scrollableDiv'>
            <List
              size={'small'}
              header={'Exception List'}
              dataSource={logs}
              renderItem={(item: Exceptions) => (
                <List.Item onClick={() => setCurrentLog(item)}>
                  <Paragraph ellipsis={true}>{item.taskName}</Paragraph>
                </List.Item>
              )}
            />
          </div>
        </Col>
        <Col span={21}>
          <Card
            title={currentLog.taskName}
            bordered={false}
            extra={
              <Paragraph>
                {moment(currentLog.timestamp).format('YYYY-MM-DD HH:mm:ss.SSS')}
              </Paragraph>
            }
          >
            <Paragraph ellipsis={{ rows: 1, expandable: false }}>
              <blockquote>{currentLog.exceptionName}</blockquote>
            </Paragraph>
            <CodeShow
              code={currentLog.stacktrace ? currentLog.stacktrace : 'No Exception'}
              height={'calc(100vh - 350px)'}
              showFloatButton
            />
          </Card>
        </Col>
      </Row>
    );
  };

  return <ProCard bodyStyle={{ height: '100%', overflow: 'auto' }}>{renderLogTab()}</ProCard>;
};

export default ExceptionTab;
