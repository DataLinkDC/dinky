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
import { TinyArea, TinyAreaConfig } from '@ant-design/plots';
import { StatisticCard } from '@ant-design/pro-components';
import React from 'react';

const { Statistic } = StatisticCard;

const JobRunView: React.FC = () => {
  const data = [
    264, 417, 438, 887, 309, 397, 550, 575, 563, 430, 525, 592, 492, 467, 513, 546, 983, 340, 539,
    243, 226, 192
  ];
  const config: TinyAreaConfig = {
    height: 80,
    width: 220,
    autoFit: false,
    data,
    smooth: true,
    areaStyle: {
      fill: '#d6e3fd'
    }
  };

  return (
    <StatisticCard
      chartPlacement='right'
      statistic={{
        title: l('home.job.running'),
        value: 20,
        suffix: l('global.item'),
        formatter: (value) => <CountFormatter value={Number(value)} />,
        description: <Statistic title='已守护' value='520 天' />
      }}
      chart={
        <div className={styles['tiny-charts']}>
          <TinyArea {...config} />
        </div>
      }
    />
  );
};

export default JobRunView;
