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

import { MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import MetricsFilter from '@/pages/DevOps/JobDetail/JobMetrics/MetricsFilter/MetricsFilter';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { useEffect, useState } from 'react';
import { connect } from 'umi';
import JobChart from './JobChart/JobChart';

const JobMetrics = (props: any) => {
  const { layoutName, dispatch } = props;

  const [timeRange, setTimeRange] = useState<MetricsTimeFilter>({
    startTime: new Date().getTime() - 60000,
    endTime: new Date().getTime(),
    isReal: true
  });

  useEffect(() => {
    dispatch({
      type: 'Devops/queryMetricsTarget',
      payload: { layoutName: layoutName }
    });
  }, []);

  const onTimeSelectChange = (filter: MetricsTimeFilter) => {
    setTimeRange(filter);
  };

  return (
    <>
      <MetricsFilter onTimeSelect={onTimeSelectChange} />
      <JobChart timeRange={timeRange} />
    </>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail,
  layoutName: Devops.metrics.layoutName
}))(JobMetrics);
