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

import {ProCard, StatisticCard} from '@ant-design/pro-components';
import {Badge} from 'antd';
import RcResizeObserver from 'rc-resize-observer';
import React, {useState} from 'react';
import LoadScoreGauge from "@/pages/Home/JobOverView/LoadScoreGauge";
import JobStatusPie from "@/pages/Home/JobOverView/JobStatusPie";
import JobErrorView from "@/pages/Home/JobOverView/JobErrorView";
import JobRunView from "@/pages/Home/JobOverView/JobRunView";
import JobFinishedView from "@/pages/Home/JobOverView/JobFinishedView";
import JobRecoveryView from "@/pages/Home/JobOverView/JobRecoveryView";
import {l} from "@/utils/intl";

const JobOverView: React.FC = () => {
  const [split, setSplit] = useState<'vertical' | 'horizontal' | undefined>('vertical');

  return (
    <RcResizeObserver
      key="resize-observer"
      onResize={(offset) => {
        setSplit(offset.width < 596 ? 'horizontal' : 'vertical');
      }}
    >
      <ProCard
        title={<><Badge status="processing"/>{l('home.job.metrics')}</>}
        headerBordered
        bordered
        size="small"
        split={split}
      >
        <ProCard split={split}>
          <ProCard split="horizontal">
              <ProCard bodyStyle={{padding: '0 12px'}}>
                <JobRunView/>
              </ProCard>
              <ProCard bodyStyle={{padding: '0 12px'}}>
                <JobFinishedView/>
              </ProCard>
              <ProCard bodyStyle={{padding: '0 12px'}}>
                <JobRecoveryView/>
            </ProCard>
          </ProCard>
          <ProCard title={l('home.job.running.status')}>
            <JobStatusPie/>
          </ProCard>
        </ProCard>
        <ProCard split={split}>
          <ProCard title={l('home.server.load')}>
            <LoadScoreGauge/>
          </ProCard>
          <ProCard split="horizontal">
              <JobErrorView/>
          </ProCard>
        </ProCard>
      </ProCard>
    </RcResizeObserver>
  );
}

export default JobOverView
