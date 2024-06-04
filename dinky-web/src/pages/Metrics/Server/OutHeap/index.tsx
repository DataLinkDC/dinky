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

type NonHeapProps = {
  data: JVMMetric[];
  max: number;
  chartConfig: Chart;
};
type NonHeap = {
  time: Date;
  value: number;
};
const NonHeap: React.FC<NonHeapProps> = (props) => {
  const { data, max, chartConfig } = props;
  const dataList: NonHeap[] = data.map((x) => {
    return {
      time: x.time,
      value: parseInt(String(x.jvm.nonHeapMax / (1024 * 1024)))
    };
  });

  const config: AreaConfig = {
    ...chartConfig,
    data: dataList,
    axis: {
      y: {
        min: 0,
        max: max
      }
    },
    tooltip: {
      name: 'NonHeap Memory',
      channel: 'y',
      valueFormat: (datum: Number) => {
        return datum + ' MB';
      }
    }
  };

  return <Area {...config} />;
};

export default NonHeap;
