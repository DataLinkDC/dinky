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
import { Chart } from '@ant-design/plots/es/interface';
import React from 'react';

type ThreadProps = {
  data: JVMMetric[];
  chartConfig: Chart;
};
type Thread = {
  time: Date;
  value: string | number;
  name: string;
};
const Thread: React.FC<ThreadProps> = (props) => {
  const { data, chartConfig } = props;
  const dataList: Thread[] = data.map((x) => {
    return { time: x.time, value: x.jvm.threadPeakCount, name: 'Peak' };
  });
  const dataList2: Thread[] = data.map((x) => {
    return { time: x.time, value: x.jvm.threadCount, name: 'Count' };
  });
  const dataListAll = dataList.concat(dataList2);

  const config: AreaConfig = {
    ...chartConfig,
    data: dataListAll,
    colorField: 'name',
    shapeField: 'name',
    stack: false,
    tooltip: {}
  };

  return <Area {...config} />;
};

export default Thread;
