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

import { MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import { getSubMinTime } from '@/pages/Metrics/Server/function';
import { l } from '@/utils/intl';
import { DatePicker, Radio } from 'antd';
const { RangePicker } = DatePicker;

type TimeSelectProps = {
  onTimeSelect: (filter: MetricsTimeFilter) => void;
};

const MetricsFilter = (props: TimeSelectProps) => {
  const { onTimeSelect } = props;

  const onTimeSelectChange = (isReal: boolean, startTime: number, endTime?: number) =>
    onTimeSelect({
      startTime: startTime,
      endTime: endTime,
      isReal: isReal
    });

  const onTimeRadioChange = (e: any) => {
    const dateKey = e.target.value;
    let filter: MetricsTimeFilter = {
      startTime: new Date().getTime(),
      endTime: new Date().getTime(),
      isReal: false
    };
    switch (dateKey) {
      case 'real':
        return onTimeSelectChange(true, getSubMinTime(new Date(), 1).getTime());
      case '1h':
        return onTimeSelectChange(false, getSubMinTime(new Date(), 60).getTime());
      case '24h':
        return onTimeSelectChange(false, getSubMinTime(new Date(), 1440).getTime());
      case '7d':
        return onTimeSelectChange(false, getSubMinTime(new Date(), 10080).getTime());
    }
    onTimeSelect(filter);
  };

  return (
    <>
      <Radio.Group defaultValue={'real'} onChange={(v) => onTimeRadioChange(v)}>
        <Radio.Button value='real'>{l('metrics.filter.real')}</Radio.Button>
        <Radio.Button value='1h'>{l('metrics.filter.1hour')}</Radio.Button>
        <Radio.Button value='24h'>{l('metrics.filter.1day')}</Radio.Button>
        <Radio.Button value='7d'>{l('metrics.filter.1week')}</Radio.Button>
      </Radio.Group>
      <RangePicker
        showTime
        onChange={(_, time) =>
          onTimeSelectChange(false, new Date(time[0]).getTime(), new Date(time[1]).getTime())
        }
      />
    </>
  );
};

export default MetricsFilter;
