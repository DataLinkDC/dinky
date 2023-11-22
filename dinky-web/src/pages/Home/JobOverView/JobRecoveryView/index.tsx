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
import { RingProgress, RingProgressConfig } from '@ant-design/plots';
import { StatisticCard } from '@ant-design/pro-components';
import { Space } from 'antd';
import React from 'react';

const { Statistic } = StatisticCard;

const JobRecoveryView: React.FC = () => {
  const config: RingProgressConfig = {
    height: 80,
    width: 80,
    autoFit: false,
    percent: 0.7,
    color: ['#5B8FF9', '#E8EDF3']
  };

  return (
    <StatisticCard
      chartPlacement='right'
      statistic={{
        title: l('home.job.recovery'),
        value: 2,
        suffix: l('global.item'),
        formatter: (value) => <CountFormatter value={Number(value)} />,
        description: (
          <Space>
            <Statistic title={l('home.job.online')} value='5 ' suffix={l('global.item')} />
            <Statistic title={l('home.job.recovery.rate')} value='40 %' />
          </Space>
        )
      }}
      chart={
        <div className={styles['tiny-charts']}>
          <RingProgress {...config} />
        </div>
      }
    />
  );
};

export default JobRecoveryView;
