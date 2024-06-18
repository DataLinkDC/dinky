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

import { Layout } from 'react-grid-layout';
import { ChartData } from '@/pages/Metrics/JobMetricsList/data';

export type LayoutData = {
  data: LayoutChartData[];
  title: string;
} & Layout;
export type DashboardData = {
  id: number;
  name: string;
  remark: string;
  chartTheme: string;
  data?: LayoutData[];
};

export const deleteKeyFromRecord = <T extends Record<string, any>, K extends keyof T>(
  obj: T,
  key: K
): Omit<T, K> => {
  const newObj = { ...obj };
  delete newObj[key];
  return newObj as Omit<T, K>;
};

export const EchartsTheme = [
  'chalk',
  'dark',
  'essos',
  'infographic',
  'macarons',
  'purple-passion',
  'roma',
  'shine',
  'vintage',
  'walden',
  'westeros',
  'wonderland'
];

export interface LayoutChartData {
  type: 'Line' | 'Area' | 'Bar' | 'Statistic' | string;
  name: string;
  id: number | string;
  data?: ChartData[];
}

export const EchartsOptions = (data: LayoutChartData[], title?: string) => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    legend: {
      data: data.map((x) => x.name),
      top: '30px'
    },
    // ECharts 配置项
    title: {
      text: title,
      left: 'center'
    },
    xAxis: {
      type: 'time'
    },
    yAxis: {
      type: 'value'
    },
    series: data.map((item) => {
      return {
        data: item.data?.map((x) => [x.time, x.value]),
        type: item.type === 'Area' ? 'line' : item.type.toLowerCase(),
        areaStyle: item.type === 'Area' ? {} : undefined,
        name: item.name,
        radius: ['40%', '55%'],
        center: ['50%', '55%'],
        avoidLabelOverlap: true
      };
    })
  };
};

export const getRandomData = (num: number) => {
  let base = +new Date(1988, 9, 3);
  let oneDay = 24 * 3600 * 1000;
  let data = [[base, Math.random() * 300]];
  for (let i = 1; i < num; i++) {
    let now = new Date((base += oneDay));
    data.push([+now, Math.round((Math.random() - 0.5) * 20 + data[i - 1][1])]);
  }
  return data.map((x) => {
    return {
      time: x[0],
      value: x[1]
    };
  });
};
