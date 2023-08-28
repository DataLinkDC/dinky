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

import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { getMetricsData } from '@/pages/DevOps/JobDetail/JobMetrics/service';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { ChartData } from '@/pages/Metrics/Job/data';
import { FlinkMetricsData, MetricsDataType } from '@/pages/Metrics/Server/data';
import { getSseData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { connect } from '@@/exports';
import {Row, Spin} from 'antd';
import { useEffect, useState } from 'react';
import FlinkChart from "@/components/FlinkChart";

const JobChart = (props: any) => {
  const {jobDetail, metricsTarget, layoutName, timeRange } = props;

  const [eventSource, setEventSource] = useState<EventSource>();
  const [chartDatas, setChartDatas] = useState<Record<string, ChartData[]>>({});
  const [loading, setLoading] = useState<boolean>(false);

  const sseUrl = `${
    API_CONSTANTS.MONITOR_GET_LAST_DATA
  }?lastTime=${new Date().getTime()}&layoutName=${layoutName}`;

  const dataProcess = (chData: Record<string, ChartData[]>, data: MetricsDataType) => {
    if (data.model == 'local') {
      return;
    }
    const fd = data.content as FlinkMetricsData;
    const verticesMap = fd.verticesAndMetricsMap;
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
    setLoading(true);
    getMetricsData({
      startTime: timeRange.startTime,
      endTime: timeRange.endTime,
      taskIds: jobDetail.instance.taskId
    }).then((result) => {
      setLoading(false);
      const chData = {};
      (result as MetricsDataType[]).forEach((d) => dataProcess(chData, d));
      if (!timeRange.isReal) {
        eventSource?.close();
        setEventSource(undefined);
      } else {
        if (!eventSource){
          setEventSource(getSseData(sseUrl))
        }
      }
    });
  }, [timeRange]);

  useEffect(() => {
    if (eventSource && timeRange.isReal){
      eventSource.onmessage = (e) => {
        let result = JSON.parse(e.data);
        dataProcess(chartDatas, result);
      }
    }
  }, [eventSource]);

  const renderMetricsCardList = (
    metricsList: Record<string, JobMetricsItem[]>,
    chartData: Record<string, ChartData[]>
  ) => {
    let datas: JobMetricsItem[] = [];
    for (let [key, value] of Object.entries(metricsList)) {
      datas = [...datas, ...value];
    }
    return datas?.map((metricsItem) => {
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
  };
  return <Spin spinning={loading}>
    <Row gutter={[8, 16]}>{renderMetricsCardList(metricsTarget ?? {}, chartDatas)}</Row>
  </Spin>;
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail,
  metricsTarget: Devops.metrics.jobMetricsTarget,
  layoutName: Devops.metrics.layoutName
}))(JobChart);
