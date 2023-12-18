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

import FlinkChart from '@/components/Flink/FlinkChart';
import useHookRequest from '@/hooks/useHookRequest';
import { SseData } from '@/models/Sse';
import { SSE_TOPIC } from '@/pages/DevOps/constants';
import { JobMetricsItem, MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import { getMetricsData } from '@/pages/DevOps/JobDetail/srvice';
import { ChartData } from '@/pages/Metrics/Job/data';
import { MetricsDataType } from '@/pages/Metrics/Server/data';
import { Jobs } from '@/types/DevOps/data';
import { Empty, Row, Spin } from 'antd';
import { useEffect, useState } from 'react';
import { useModel } from 'umi';

export type JobChartProps = {
  jobDetail: Jobs.JobInfoDetail;
  metricsList: JobMetricsItem[];
  timeRange: MetricsTimeFilter;
};
const JobChart = (props: JobChartProps) => {
  const { jobDetail, metricsList, timeRange } = props;

  const [chartDatas, setChartDatas] = useState<Record<string, ChartData[]>>({});

  const { subscribeTopic } = useModel('Sse', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));

  const { loading } = useHookRequest(getMetricsData, {
    defaultParams: [timeRange, jobDetail.instance.jid],
    refreshDeps: [timeRange, metricsList],
    onSuccess: (result) => {
      const chData = {};
      (result as MetricsDataType[]).forEach((d) => dataProcess(chData, d));
    }
  });

  const dataProcess = (chData: Record<string, ChartData[]>, data: MetricsDataType) => {
    const verticesMap = data.content as Record<string, Record<string, string>>;
    Object.keys(verticesMap).forEach((verticeId) =>
      Object.keys(verticesMap[verticeId]).forEach((mertics) => {
        const key = `${verticeId}-${mertics}`;
        const value = {
          time: data.heartTime,
          value: verticesMap[verticeId][mertics]
        };
        if (!(key in chData)) {
          chData[key] = [];
        }
        chData[key].push(value);
      })
    );
    setChartDatas(chData);
  };

  useEffect(() => {
    if (timeRange.isReal) {
      const topic = `${SSE_TOPIC.METRICS}/${jobDetail.instance.jid}`;
      return subscribeTopic([topic], (data: SseData) => {
        dataProcess(chartDatas, data.data);
      });
    }
  }, [timeRange, metricsList]);

  const renderMetricsCardList = (
    metricsList: JobMetricsItem[],
    chartData: Record<string, ChartData[]>
  ) => {
    if (metricsList && metricsList.length > 0) {
      return metricsList?.map((metricsItem) => {
        const key = `${metricsItem.vertices}-${metricsItem.metrics}`;
        return (
          <FlinkChart
            key={key}
            chartSize={metricsItem.showSize}
            chartType={metricsItem.showType}
            title={metricsItem.metrics}
            data={chartData[key] ?? []}
          />
        );
      });
    }
    return <Empty className={'code-content-empty'} />;
  };
  return (
    <Spin spinning={loading} delay={500}>
      <Row gutter={[8, 16]}>{renderMetricsCardList(metricsList ?? {}, chartDatas)}</Row>
    </Spin>
  );
};

export default JobChart;
