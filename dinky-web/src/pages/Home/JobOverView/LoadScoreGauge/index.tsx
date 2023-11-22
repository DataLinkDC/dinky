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

import { l } from '@/utils/intl';
import { Gauge, GaugeConfig } from '@ant-design/plots';

const LoadScoreGauge = () => {
  const ticks = [0, 1 / 3, 2 / 3, 1];
  const color = ['#30BF78', '#FAAD14', '#F4664A'];

  const config: GaugeConfig = {
    padding: 15,
    percent: 0.25,
    type: 'meter',
    innerRadius: 0.75,
    range: {
      ticks,
      color
    },
    indicator: {
      pointer: {
        style: {
          stroke: '#D0D0D0'
        }
      },
      pin: {
        style: {
          stroke: '#D0D0D0'
        }
      }
    },
    statistic: {
      title: {
        formatter: ({ percent }) => {
          if (percent < ticks[1]) {
            return l('home.server.load.excellent');
          }

          if (percent < ticks[2]) {
            return l('home.server.load.good');
          }
          return l('home.server.load.bad');
        },
        style: ({ percent }) => {
          return {
            fontSize: '24px',
            lineHeight: 1,
            color: percent < ticks[1] ? color[0] : percent < ticks[2] ? color[1] : color[2]
          };
        }
      }
    }
  };
  return (
    <div style={{ height: '35vh' }}>
      <Gauge {...config} />
    </div>
  );
};

export default LoadScoreGauge;
