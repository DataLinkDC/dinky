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

import { DATE_RANGE_OPTIONS } from '@/pages/Metrics/Server/constants';
import { l } from '@/utils/intl';
import { LightFilter, ProFormDateTimeRangePicker, ProFormRadio } from '@ant-design/pro-components';
import React from 'react';

type GlobalFilterProps = {
  custom: boolean;
  dateRange: string;
  startTime: any;
  endTime: any;
  handleDateRadioChange: (e: any) => void;
  handleRangeChange: (e: any) => void;
};
const GlobalFilter: React.FC<GlobalFilterProps> = (props) => {
  const { custom, dateRange, startTime, endTime, handleDateRadioChange, handleRangeChange } = props;

  return (
    <>
      <LightFilter bordered size={'small'}>
        <ProFormRadio.Group
          name='dateRange'
          radioType='radio'
          initialValue={dateRange}
          fieldProps={{ onChange: handleDateRadioChange }}
          options={DATE_RANGE_OPTIONS(custom)}
        />
        {dateRange == 'custom' && (
          <ProFormDateTimeRangePicker
            name='datetimeRanger'
            label={l('metrics.filter.custom.range')}
            allowClear={false}
            initialValue={[startTime, endTime]}
            fieldProps={{
              onChange: handleRangeChange,
              style: { width: '100%' },
              value: [startTime, endTime]
            }}
          />
        )}
      </LightFilter>
    </>
  );
};

export default GlobalFilter;
