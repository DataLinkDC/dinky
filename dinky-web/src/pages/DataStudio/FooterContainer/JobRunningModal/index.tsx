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

import { JobRunningMsgType } from '@/pages/DataStudio/model';
import { StopTwoTone } from '@ant-design/icons';
import { Col, Modal, Progress, Row, Space } from 'antd';
import React from 'react';

type JobRunningModalProps = {
  visible: boolean;
  onCancel: () => void;
  onOk: () => void;
  value: JobRunningMsgType;
};
const JobRunningModal: React.FC<JobRunningModalProps> = (props) => {
  const { visible, onCancel, onOk, value } = props;

  return (
    <Modal
      title={value?.taskId ? `Job ${value?.jobName} is running` : 'No job running'}
      open={visible}
      onOk={onOk}
      width={'50%'}
      centered
      onCancel={onCancel}
      footer={null}
    >
      <Space direction='vertical' style={{ width: '100%' }}>
        <Row gutter={[12, 12]}>
          <Col span={23}>
            <Progress
              showInfo
              strokeLinecap={'round'}
              percent={
                value?.taskId
                  ? value?.jobState === 'RUNNING'
                    ? 50
                    : value?.jobState === 'FINISHED'
                    ? 100
                    : 0
                  : 0
              }
              status='active'
              strokeColor={{ from: '#108ee9', to: '#87d068' }}
            />
          </Col>
          <Col span={1}>
            <StopTwoTone />
          </Col>
        </Row>
      </Space>
    </Modal>
  );
};

export default JobRunningModal;
