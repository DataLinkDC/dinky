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
import { Datum } from '@antv/g2plot';
import { AreaOptions as G2plotConfig } from '@antv/g2plot/lib/plots/area/types';
import React from 'react';

type HeapProps = {
  data: JVMMetric[];
  max: number;
  chartConfig: G2plotConfig;
};
type Heap = {
  time: Date;
  value: string | number;
};
const Heap: React.FC<HeapProps> = (props) => {
  const { data, max, chartConfig } = props;
  const dataList: Heap[] = data.map((x) => {
    return {
      time: x.time,
      value: parseInt(String(x.jvm.heapUsed / (1024 * 1024)))
    };
  });

  const config: AreaConfig = {
    ...chartConfig,
    data: dataList,
    yAxis: {
      min: 0,
      max: max
    },
    tooltip: {
      formatter: (datum: Datum) => {
        return { name: 'Heap Memory', value: datum.value + ' MB' };
      }
    }
  };

  return <Area {...config} />;
};

export default Heap;
