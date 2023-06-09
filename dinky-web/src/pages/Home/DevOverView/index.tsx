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

import {ProCard} from '@ant-design/pro-components';
import {Badge, Col, Row} from 'antd';
import RcResizeObserver from 'rc-resize-observer';
import React, {useState} from 'react';
import DevHeatmap from "@/pages/Home/DevOverView/DevHeatmap";
import TaskDialectRadar from "@/pages/Home/DevOverView/TaskDialectRadar";
import ResourceView from "@/pages/Home/DevOverView/ResourceView";

const DevOverView: React.FC = () => {
  const [split, setSplit] = useState<'vertical' | 'horizontal' | undefined>('vertical');

  return (
    <RcResizeObserver
      key="resize-observer"
      onResize={(offset) => {
        setSplit(offset.width < 596 ? 'horizontal' : 'vertical');
      }}
    >
      <ProCard
        title={<><Badge status="processing"/> 数据开发</>}
        headerBordered
        bordered
        size="small"
        split={split}
      >
        <Row>
          <Col span={12}>
            <DevHeatmap/>
          </Col>
          <Col span={6}>
            <TaskDialectRadar/>
          </Col>
          <Col span={6}>
            <ResourceView/>
          </Col>
        </Row>
      </ProCard>
    </RcResizeObserver>
  );
}

export default DevOverView
