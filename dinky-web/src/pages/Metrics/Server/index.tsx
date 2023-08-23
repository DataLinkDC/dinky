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

import { CPUIcon, HeapIcon, OutHeapIcon, ThreadIcon } from '@/components/Icons/MetricsIcon';
import CPU from '@/pages/Metrics/Server/CPU';
import { JvmDataRecord, JVMMetric } from '@/pages/Metrics/Server/data';
import Heap from '@/pages/Metrics/Server/Heap';
import NonHeap from '@/pages/Metrics/Server/OutHeap';
import Thread from '@/pages/Metrics/Server/Thread';
import { ProCard } from '@ant-design/pro-components';
import { AreaOptions as G2plotConfig } from '@antv/g2plot/lib/plots/area/types';
import { Space } from 'antd';
import React from 'react';

export const imgStyle = {
  display: 'block',
  width: 24,
  height: 24
};

type ServerProp = {
  chartConfig: G2plotConfig;
  data: JVMMetric[];
};

const Server: React.FC<ServerProp> = (props) => {
  const { chartConfig, data } = props;
  const commonConfig: G2plotConfig = {
    ...chartConfig,
    yField: 'value',
    xField: 'time',
    xAxis: {
      type: 'time',
      mask: 'HH:mm:ss'
    }
  };
  const jvmMetric = data[data.length - 1];
  const showLastData: JvmDataRecord = jvmMetric
    ? {
        cpuLastValue: Number(jvmMetric.jvm.cpuUsed.toFixed(2)),
        heapMax: Number((jvmMetric.jvm.heapMax / (1024 * 1024)).toFixed(0)),
        heapLastValue: Number((jvmMetric.jvm.heapUsed / (1024 * 1024)).toFixed(0)),
        nonHeapMax: Number((jvmMetric.jvm.nonHeapMax / (1024 * 1024)).toFixed(0)),
        nonHeapLastValue: Number((jvmMetric.jvm.nonHeapUsed / (1024 * 1024)).toFixed(0)),
        threadPeakCount: jvmMetric.jvm.threadPeakCount,
        threadCount: jvmMetric.jvm.threadCount
      }
    : {
        cpuLastValue: 0,
        heapMax: 0,
        heapLastValue: 0,
        nonHeapMax: 0,
        nonHeapLastValue: 0,
        threadPeakCount: 0,
        threadCount: 0
      };

  const extraDataBuilder = (data: JvmDataRecord) => {
    return {
      cpuLastValue: data.cpuLastValue + '%',
      heapLastValue: data.heapLastValue + ' / ' + data.heapMax + ' MB',
      nonHeapLastValue: data.nonHeapLastValue + ' / ' + data.nonHeapMax + ' MB',
      threadCount: data.threadCount + ' / ' + data.threadPeakCount
    };
  };
  return (
    <>
      <ProCard bordered split={'vertical'}>
        <ProCard
          title={
            <Space>
              <CPUIcon style={imgStyle} />
              CPU
            </Space>
          }
          extra={extraDataBuilder(showLastData).cpuLastValue}
        >
          <CPU data={data} chartConfig={commonConfig} />
        </ProCard>
        <ProCard
          title={
            <Space>
              <HeapIcon style={imgStyle} />
              Heap
            </Space>
          }
          extra={extraDataBuilder(showLastData).heapLastValue}
        >
          <Heap data={data} max={showLastData.heapMax} chartConfig={commonConfig} />
        </ProCard>
        <ProCard
          title={
            <Space>
              <ThreadIcon style={imgStyle} />
              Thread
            </Space>
          }
          extra={extraDataBuilder(showLastData).threadCount}
        >
          <Thread data={data} chartConfig={commonConfig} />
        </ProCard>
        <ProCard
          title={
            <Space>
              <OutHeapIcon style={imgStyle} />
              Out Heap
            </Space>
          }
          extra={extraDataBuilder(showLastData).nonHeapLastValue}
        >
          <NonHeap data={data} max={showLastData.nonHeapMax} chartConfig={commonConfig} />
        </ProCard>
      </ProCard>
    </>
  );
};
export default Server;
