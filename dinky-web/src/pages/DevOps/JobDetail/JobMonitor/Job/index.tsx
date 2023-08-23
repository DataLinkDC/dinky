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

import React, {useEffect, useRef, useState} from "react";
import {ProCard, ProFormSelect} from "@ant-design/pro-components";
import {Button, Input, Row} from "antd";
import FlinkChart from "./components/FlinkChart";
import {getData, getSseData} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";
import {l} from "@/utils/intl";
import {buildMetricsList, buildRunningJobList, buildSubTaskList} from "@/pages/Metrics/Job/function";
import {ChartData, MetricsLayout, SubTask, Task} from "@/pages/Metrics/Job/data";
import {getFlinkRunTask, saveFlinkMetrics} from "@/pages/Metrics/Job/service";
import {JobProps} from "@/pages/DevOps/JobDetail/data";
import jobDetail from "@/pages/DevOps/JobDetail";
import {connect, useRequest} from "@@/exports";
import {FlinkMetricsData, MetricsDataType} from "@/pages/Metrics/Server/data";
import {getSubMinTime} from "@/pages/Metrics/Server/function";
import {MonitorType} from "@/pages/DevOps/JobDetail/JobMonitor/model";

type JobMetrics = {
  id: number
  taskId: number
  vertices: string
  metrics: string
  title: string
  layoutName: string
  showType: string;
  showSize: string;
}

const JobChart = (props: any) => {

  const {jobDetail, monitors} = props
  const layoutName = `${jobDetail.instance.name}-${jobDetail.instance.taskId}`

  const jobManagerUrl = jobDetail?.cluster?.jobManagerHost
  const jobId = jobDetail?.jobHistory?.job?.jid

  const [custom, setCustom] = useState<boolean>(false);
  const [currentTime, setCurrentTime] = useState(new Date());
  const [startTime, setStartTime] = useState(getSubMinTime(currentTime, 1));
  const [chartData, setChartData] = useState<Record<string, ChartData[]>>({});
  const [endTime, setEndTime] = useState(new Date());

  const metrics = useRequest({
    url: API_CONSTANTS.GET_METRICS_LAYOUT_BY_NAME,
    params: {layoutName: layoutName},
  });


  /**
   * render metrics card list
   * @param {JobMetrics[]} metricsList
   * @returns {JSX.Element}
   */
  const renderMetricsCardList = (metricsList: JobMetrics[]) => {
    return (
      <Row gutter={[8, 16]}>
        {metricsList?.map(j => {
          const params = {
            url: API_CONSTANTS.GET_JOB_MERTICE_DATA,
            params: {address: jobManagerUrl, jobId: jobId, verticeId: j.vertices, metrics: j.metrics}
          }
          const key = `${j.vertices}-${j.metrics}`

          return <FlinkChart
            chartSize={j.showSize} chartType={j.showType} title={j.metrics}
            onChangeJobState={(chartSize, chartType) => {
              j.showSize = chartSize
              j.showType = chartType
            }}
            data={monitors?.chartDataList[key] ?? []}
            requestParams={params} autoResfeh={true}/>
        })}
      </Row>
    )
  }

  return <>
    {renderMetricsCardList(metrics.data)}
  </>
}

export default connect(({monitors}: { monitors: MonitorType }) => ({
  monitors,
}))(JobChart);
