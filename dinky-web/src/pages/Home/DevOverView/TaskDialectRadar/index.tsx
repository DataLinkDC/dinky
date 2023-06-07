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
import {Radar} from '@ant-design/plots';

const TaskDialectRadar = () => {
  // 数据更新于 2021.01.09
  const data = [
    {
      name: 'FlinkSQL',
      star: 10371,
    },
    {
      name: 'FlinkJar',
      star: 7380,
    },
    {
      name: 'K8SApplication',
      star: 7414,
    },
    {
      name: 'Doris',
      star: 2140,
    },
    {
      name: 'FlinkSQLEnv',
      star: 660,
    },
    {
      name: 'Java',
      star: 885,
    },
    {
      name: 'Mysql',
      star: 1626,
    },
  ];
  const config = {
    data: data.map((d) => ({...d, star: Math.sqrt(d.star)})),
    xField: 'name',
    yField: 'star',
    appendPadding: [0, 10, 0, 10],
    meta: {
      star: {
        alias: 'star 数量',
        min: 0,
        nice: true,
        formatter: (v) => Number(v).toFixed(2),
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
