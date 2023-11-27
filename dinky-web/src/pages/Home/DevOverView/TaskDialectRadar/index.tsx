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

import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { TaskDialectSummary } from '@/types/Home/data';
import { l } from '@/utils/intl';
import { Radar, RadarConfig } from '@ant-design/plots';
import { useEffect, useState } from 'react';

const TaskDialectRadar = () => {
  const [data, setData] = useState<TaskDialectSummary[]>([]);

  useEffect(() => {
    queryDataByParams(API_CONSTANTS.GET_JOB_TYPE_OVERVIEW).then((res) => {
      setData(res);
    });
  }, []);

  const config: RadarConfig = {
    data: data,
    xField: 'jobType',
    yField: 'rate',
    autoFit: true,
    appendPadding: [0, 10, 0, 10],
    meta: {
      rate: {
        alias: l('home.job.onlineRate'),
        min: 0,
        max: 100,
        nice: true,
        formatter: (v) => Number(v) + '%'
      }
    },
    yAxis: {
      label: false,
      grid: {
        alternateColor: 'rgba(0, 0, 0, 0.04)'
      }
    },
    // 开启辅助点
    point: {
      size: 2
    },
    area: {}
  };
  return (
    <div style={{ height: '35vh' }}>
      <Radar {...config} />
    </div>
  );
};

export default TaskDialectRadar;
