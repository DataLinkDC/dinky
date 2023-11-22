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

import CountFormatter from '@/components/CountFormatter';
import styles from '@/global.less';
import { l } from '@/utils/intl';
import { TinyColumn, TinyColumnConfig } from '@ant-design/plots';
import { StatisticCard } from '@ant-design/pro-components';
import React from 'react';

const { Statistic } = StatisticCard;

const JobFinishedView: React.FC = () => {
  const data = [274, 337, 81, 497, 666, 219, 269];
  const config: TinyColumnConfig = {
    height: 80,
    width: 220,
    autoFit: false,
    data,
    tooltip: {
      customContent: function (x: any, data: { data: { y: number } }[]) {
        return `NO.${x}: ${data[0]?.data?.y.toFixed(2)}`;
      }
    }
  };

  return (
    <StatisticCard
      chartPlacement='right'
      statistic={{
        title: l('home.job.finished'),
        value: 123,
        suffix: l('global.bout'),
        formatter: (value) => <CountFormatter value={Number(value)} />,
        description: <Statistic title={l('home.job.running.dayonday')} value='3.57 %' trend='up' />
      }}
      chart={
        <div className={styles['tiny-charts']}>
          <TinyColumn {...config} />
        </div>
      }
    />
  );
};

export default JobFinishedView;
