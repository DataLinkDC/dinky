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

import MetricsFilter from '@/components/Flink/MetricsFilter/MetricsFilter';
import { MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import JobMetricsList from '@/pages/Metrics/JobMetricsList';
import Server from '@/pages/Metrics/Server';
import { CONFIG_MODEL_ASYNC, SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { l } from '@/utils/intl';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import { connect } from '@umijs/max';
import { Divider, Result } from 'antd';
import React, { memo, useEffect, useState } from 'react';

const Metrics: React.FC<connect> = (props) => {
  const [timeRange, setTimeRange] = useState<MetricsTimeFilter>({
    startTime: new Date().getTime() - 2 * 60 * 1000,
    endTime: new Date().getTime(),
    isReal: true
  });
  const [loading, setLoading] = useState<boolean>(false);

  const { dispatch, enableMetricMonitor } = props;

  useEffect(() => {
    setLoading(true);
    dispatch({
      type: CONFIG_MODEL_ASYNC.queryMetricConfig,
      payload: SettingConfigKeyEnum.METRIC.toLowerCase()
    });
    setLoading(false);
  }, []);

  const onTimeSelectChange = (filter: MetricsTimeFilter) => {
    setTimeRange(filter);
  };

  return (
    <PageContainer
      fixedHeader={true}
      loading={loading}
      header={{ extra: [<MetricsFilter key={'filter'} onTimeSelect={onTimeSelectChange} />] }}
      content={
        <>
          {enableMetricMonitor ? (
            <>
              <ProCard collapsible title={'Dinky Server'} ghost bordered hoverable>
                <Server timeRange={timeRange} />
              </ProCard>
              <Divider />
              <JobMetricsList timeRange={timeRange} />
            </>
          ) : (
            <>
              <Result
                status={'warning'}
                title={<span className={'needWrap'}>{l('metrics.dinky.not.open')}</span>}
              />
            </>
          )}
        </>
      }
    />
  );
};

export default connect(({ SysConfig }: { SysConfig: SysConfigStateType }) => ({
  enableMetricMonitor: SysConfig.enableMetricMonitor
}))(memo(Metrics));
