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

import { Authorized } from '@/hooks/useAccess';
import DevOverView from '@/pages/Home/DevOverView';
import JobOverView from '@/pages/Home/JobOverView';
import { PageContainer } from '@ant-design/pro-components';
import { Col, Row } from 'antd';

export default () => {
  return (
    <PageContainer title={false} style={{ height: 'calc(100% - 300px)' }}>
      <Authorized path='/home/jobOverView'>
        <Row style={{ marginTop: '5px', marginBottom: '10px', height: 'calc(100% - 50%)' }}>
          <Col span={24}>
            <JobOverView />
          </Col>
        </Row>
      </Authorized>
      <Authorized path='/home/devOverView'>
        <Row style={{ marginTop: '5px', marginBottom: '10px', height: 'calc(100% - 50%)' }}>
          <Col span={24}>
            <DevOverView />
          </Col>
        </Row>
      </Authorized>
    </PageContainer>
  );
};
