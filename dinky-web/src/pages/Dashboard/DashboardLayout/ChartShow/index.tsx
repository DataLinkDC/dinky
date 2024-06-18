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

import ReactECharts from 'echarts-for-react';
import { Flex, Statistic } from 'antd';
import React from 'react';

type ChartShowProps = {
  show?: boolean;
  type: 'Line' | 'Area' | 'Bar' | 'Statistic' | string;
  chartTheme: string;
  chartOptions: Record<string, any>;
  title?: string;
  value?: string | number;
  fontSize?: number;
};
export default (props: ChartShowProps) => {
  const { show = true, type, chartOptions, chartTheme, title, value, fontSize } = props;
  return (
    <>
      {show &&
        (type !== 'Statistic' ? (
          <ReactECharts
            option={chartOptions}
            notMerge={true}
            lazyUpdate={true}
            theme={chartTheme}
            style={{ height: '100%', width: '100%', zIndex: 99 }}
          />
        ) : (
          <Flex justify={'center'} align={'center'} style={{ width: 'inherit', height: 'inherit' }}>
            <Statistic title={title} value={value} valueStyle={{ fontSize: fontSize }} />
          </Flex>
        ))}
    </>
  );
};
