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
import { JOB_STATUS } from '@/pages/DevOps/constants';
import { JobMetricsItem, JobProps, MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import { buildMetricsTarget } from '@/pages/DevOps/JobDetail/JobMetrics/function';
import MonitorConfigForm from '@/pages/DevOps/JobDetail/JobMetrics/MetricsForm/MetricsConfigForm';
import { getMetricsLayout, putMetricsLayout } from '@/pages/DevOps/JobDetail/service';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { l } from '@/utils/intl';
import { connect } from '@umijs/max';
import { Result, Space } from 'antd';
import { memo, useState } from 'react';
import JobChart from './JobChart/JobChart';

const JobMetrics = (props: JobProps & connect) => {
  const { jobDetail, dispatch, enableMetricMonitor } = props;
  const layoutName = `${jobDetail.instance.name}-${jobDetail.instance.taskId}`;

  const [timeRange, setTimeRange] = useState<MetricsTimeFilter>({
    startTime: new Date().getTime() - 60000,
    endTime: new Date().getTime(),
    isReal: true
  });

  const layoutData = useHookRequest(getMetricsLayout, {
    defaultParams: [layoutName],
    refreshDeps: [layoutName, timeRange, jobDetail.instance]
  });
  const saveLayout = useHookRequest(putMetricsLayout, {
    manual: true,
    defaultParams: [layoutName, []],
    refreshDeps: [layoutName]
  });

  const onTimeSelectChange = (filter: MetricsTimeFilter) => {
    setTimeRange(filter);
  };

  const onSelectMetricsChange = async (targetKeys: Record<string, JobMetricsItem[]>) => {
    let params: JobMetricsItem[] = [];
    Object.values(targetKeys).forEach((i) => params.push(...i));
    await saveLayout.run(layoutName, params);
    await layoutData.run(layoutName);
    await layoutData.refresh();
    return true;
  };

  return (
    <>
      {enableMetricMonitor ? (
        <>
          <Space style={{ marginBottom: 20 }}>
            <MetricsFilter onTimeSelect={onTimeSelectChange} />
            {jobDetail.instance.status == JOB_STATUS.RUNNING ? (
              <MonitorConfigForm
                onSelectChange={onSelectMetricsChange}
                jobDetail={jobDetail}
                initSelected={buildMetricsTarget(layoutData.data as JobMetricsItem[])}
              />
            ) : (
              <></>
            )}
          </Space>
          <JobChart
            metricsList={layoutData.data as JobMetricsItem[]}
            jobDetail={jobDetail}
            timeRange={timeRange}
          />
        </>
      ) : (
        <Result
          status={'warning'}
          title={<span className={'needWrap'}>{l('metrics.dinky.not.open')}</span>}
        />
      )}
    </>
  );
};

export default connect(({ SysConfig }: { SysConfig: SysConfigStateType }) => ({
  enableMetricMonitor: SysConfig.enableMetricMonitor
}))(memo(JobMetrics));
