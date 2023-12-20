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

import BatchStreamProportion from '@/pages/Home/DevOverView/BatchStreamProportion';
import DevHeatmap from '@/pages/Home/DevOverView/DevHeatmap';
import ResourceView from '@/pages/Home/DevOverView/ResourceView';
import TaskDialectRadar from '@/pages/Home/DevOverView/TaskDialectRadar';
import { l } from '@/utils/intl';
import { ProCard } from '@ant-design/pro-components';
import { Badge } from 'antd';
import React from 'react';

const noPadding = {
  paddingInline: '0',
  paddingBlock: '0',
  height: '100%'
};

const DevOverView: React.FC = () => {
  return (
    <ProCard
      style={{ height: '100%' }}
      title={
        <>
          <Badge status='processing' />
          {l('home.develop')}
        </>
      }
      headerBordered
      bordered
      size='small'
      split={'vertical'}
      bodyStyle={noPadding}
    >
      <ProCard split='vertical' bodyStyle={noPadding} style={{ height: '100%' }}>
        <ProCard
          title={l('home.job.development')}
          split='horizontal'
          colSpan={'40%'}
          bodyStyle={noPadding}
        >
          <ProCard>
            <BatchStreamProportion />
          </ProCard>
          <ProCard>
            <DevHeatmap />
          </ProCard>
        </ProCard>

        <ProCard title={l('home.job.onlineRate')} colSpan={'30%'} bodyStyle={noPadding}>
          <TaskDialectRadar />
        </ProCard>
        <ProCard title={l('home.develop.re')} bodyStyle={noPadding}>
          <ResourceView />
        </ProCard>
      </ProCard>
    </ProCard>
  );
};

export default DevOverView;
