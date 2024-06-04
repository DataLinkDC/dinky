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

import { JVMMetric } from '@/pages/Metrics/Server/data';
import { Area, AreaConfig } from '@ant-design/plots';
import React from 'react';
import { Chart } from '@ant-design/plots/es/interface';

type CpuProps = {
  data: JVMMetric[];
  chartConfig: Chart;
};
type Cpu = {
  time: Date;
  value: string | number;
};
const CPU: React.FC<CpuProps> = (props) => {
  const { data, chartConfig } = props;

  const dataList: Cpu[] = data.map((x) => {
    return { time: x.time, value: Number(x.jvm.cpuUsed.toFixed(2)) };
  });

  const config: AreaConfig = {
    ...chartConfig,
    data: dataList,
    axis: {
      y: {
        min: 0,
        max: 100
      }
    },
    tooltip: {
      name: 'Cpu Used',
      channel: 'y',
      valueFormatter: (num: Number) => {
        return num + ' %';
      }
    }
  };

  return <Area {...config} />;
};

export default CPU;
