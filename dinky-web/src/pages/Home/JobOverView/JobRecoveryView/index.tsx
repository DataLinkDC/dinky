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

import {StatisticCard} from '@ant-design/pro-components';
import React from 'react';
import {RingProgress} from '@ant-design/plots';
import {Space} from "antd";
import styles from '@/global.less';

const {Statistic} = StatisticCard;

const JobRecoveryView: React.FC = () => {

  const config = {
    height: 80,
    width: 80,
    autoFit: false,
    percent: 0.7,
    color: ['#5B8FF9', '#E8EDF3'],
  };

  return (
    <StatisticCard
      chartPlacement="right"
      statistic={{
        title: '今日恢复',
        value: 2,
        suffix: '个',
        description: (
          <Space>
            <Statistic
              title="今日上线"
              value='5 '
              suffix='个'
            />
            <Statistic
              title="恢复占比"
              value="40 %"
            />
          </Space>
        ),
      }}
      chart={
        <div className={styles['tiny-charts']}>
          <RingProgress {...config} />
        </div>
      }
    />
  );
}

export default JobRecoveryView
