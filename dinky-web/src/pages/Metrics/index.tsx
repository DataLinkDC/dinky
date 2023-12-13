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
import useHookRequest from '@/hooks/useHookRequest';
import { MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import JobMetricsList from '@/pages/Metrics/JobMetricsList';
import Server from '@/pages/Metrics/Server';
import { getAllConfig } from '@/pages/Metrics/service';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import { useState } from 'react';

export default () => {
  const [timeRange, setTimeRange] = useState<MetricsTimeFilter>({
    startTime: new Date().getTime() - 2 * 60 * 1000,
    endTime: new Date().getTime(),
    isReal: true
  });

  const showServer = useHookRequest(getAllConfig, {
    defaultParams: [],
    onSuccess: (res: any) => {
      for (const config of res.metrics) {
        if (config.key === 'sys.metrics.settings.sys.enable') {
          return config.value;
        }
      }
      return false;
    }
  });

  const onTimeSelectChange = (filter: MetricsTimeFilter) => {
    setTimeRange(filter);
  };

  return (
    <div>
      <PageContainer
        fixedHeader={true}
        //todo 后面title改为下拉列表，用户自定义选择展示哪些layout，而不是全部展示
        loading={showServer.loading}
        header={{ extra: [<MetricsFilter onTimeSelect={onTimeSelectChange} />] }}
        content={
          <>
            {showServer.data && (
              <ProCard collapsible title={'Dinky Server'} ghost>
                <Server timeRange={timeRange} />
              </ProCard>
            )}
            {/*<Job />*/}
            <JobMetricsList timeRange={timeRange} />
          </>
        }
      ></PageContainer>
    </div>
  );
};
