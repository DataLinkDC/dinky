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

import React from 'react';
import {Radar, RadarConfig} from '@ant-design/plots';
import {l} from "@/utils/intl";

type TaskDialectSummary = {
    type: string;
    count: number;
    rate: number;
}
const TaskDialectRadar = () => {
  // 数据更新于 2021.01.09
  const data : TaskDialectSummary[] = [
    {
      type: 'FlinkSQL',
      count: 10371,
      rate: 0.01,
    },
    {
      type: 'FlinkJar',
      count: 7380,
      rate: 0.01,
    },
    {
      type: 'K8SApplication',
      count: 7414,
      rate: 0.01,
    },
    {
      type: 'Doris',
      count: 2140,
      rate: 0.01,
    },
    {
      type: 'FlinkSQLEnv',
      count: 660,
      rate: 0.01,
    },
    {
      type: 'Java',
      count: 885,
      rate: 0.01,
    },
    {
      type: 'Mysql',
      count: 1626,
      rate: 0.01,
    },
  ];
  const config:RadarConfig = {
    data: data.map((d) => ({...d, count: Math.sqrt(d.count)})),
    xField: 'type',
    yField: 'count',
    autoFit: true,
    appendPadding: [0, 10, 0, 10],
    meta: {
      count: {
        alias: l('home.job.total'),
        min: 0,
        nice: true,
        formatter: (v) => Number(v),
      },
    },
    xAxis: {
      tickLine: null,
    },
    yAxis: {
      label: false,
      grid: {
        alternateColor: 'rgba(0, 0, 0, 0.04)',
      },
    },
    // 开启辅助点
    point: {
      size: 2,
    },
    area: {},
  };
  return <div style={{height: '35vh'}}><Radar {...config} /></div>;
};

export default TaskDialectRadar
