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


import React, {useEffect, useState} from "react";
import Server from "@/pages/Metrics/Server";
import {PageContainer, ProCard} from "@ant-design/pro-components";
import Job from "./Job";
import {AreaOptions as G2plotConfig} from "@antv/g2plot/lib/plots/area/types";
import {Button, Input, Row} from "antd";
import {getMetricsLayout} from "@/pages/Metrics/service";
import {ChartData, MetricsLayout} from "@/pages/Metrics/Job/data";
import FlinkChart from "@/pages/Metrics/Job/FlinkChart";
import {FlinkMetricsData, JvmDataRecord, JVMMetric, MetricsDataType} from "@/pages/Metrics/Server/data";
import {getSseData} from "@/services/api";
import { API_CONSTANTS } from '@/services/endpoints';
import {queryDataByParams} from "@/services/BusinessCrud";
import GlobalFilter from "@/pages/Metrics/Server/GlobalFilter";
import {getSubMinTime} from "@/pages/Metrics/Server/function";
import {JobProps} from "@/pages/DevOps/JobDetail/data";
import JobChart from "./Job";
import MonitorFilter from "@/pages/DevOps/JobDetail/JobMonitor/MonitorConfig";
import {connect} from "umi";
import {MonitorType} from "@/pages/DevOps/JobDetail/JobMonitor/model";
import {StateType} from "@/pages/DataStudio/model";

const JobMonitor = (props: any) => {

  const {jobDetail, dispatch, monitors} = props
  const layoutName = `${jobDetail.instance.name}-${jobDetail.instance.taskId}`


  const [layoutData, setLayoutData] = useState<Record<string, MetricsLayout[]>>();
  const [jvmData] = useState<JVMMetric[]>([]);
  const [flinkMetricsData] = useState<FlinkMetricsData[]>([]);

  const [chartDataList, setChartDataList] = useState<Record<string, ChartData[]>>({});
  const [eventSource, setEventSource] = useState<EventSource>();

  const [endTime, setEndTime] = useState(new Date());
  const [custom, setCustom] = useState<boolean>(false);
  const [currentTime, setCurrentTime] = useState(new Date());
  const [startTime, setStartTime] = useState(getSubMinTime(currentTime, 1));
  const [loading, setLoading] = useState<boolean>(true)

  // const getInitData = () => {
  //   queryDataByParams(API_CONSTANTS.MONITOR_GET_SYSTEM_DATA, {
  //     startTime: startTime.getTime(),
  //     endTime: endTime.getTime()
  //   }).then(res => {
  //     jvmData.length = 0;
  //     flinkMetricsData.length = 0;
  //     (res as MetricsDataType[]).forEach(d => dataProcess(d))
  //   })
  //   queryDataByParams(API_CONSTANTS.SYSTEM_GET_ALL_CONFIG).then(res => {
  //     for (const config of res.metrics) {
  //       if (config.key === "metrics.settings.sys.enable") {
  //         setLoading(false)
  //         break
  //       }
  //     }
  //   })
  // }


  /**
   * Data processing
   * @param data
   */
  const dataProcess = (data: MetricsDataType) => {
    if (data.model != "flink") {
      return
    }
    const fd = data.content as FlinkMetricsData;
    const verticesMap = fd.verticesAndMetricsMap;
    Object.keys(verticesMap).forEach(verticeId => {
      Object.keys(verticesMap[verticeId]).forEach(mertics => {
        const key = `${verticeId}-${mertics}`
        // chartRefs[key].current.addData();
        dispatch({
          type: 'monitors/updateChartDatas',
          payload: {
            key: key,
            data: {
              time: data.heartTime,
              value: verticesMap[verticeId][mertics]
            }
          }
        })
      })
    })
    // console.log(monitors.chartDataList)
  }


  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    // getInitData()

    return () => {
      clearInterval(timer);
      eventSource?.close()
    };

  }, [startTime]);

  useEffect(() => {
    const layoutName = `${jobDetail.instance.name}-${jobDetail.instance.taskId}`
    const url = `${API_CONSTANTS.MONITOR_GET_LAST_DATA}?lastTime=${endTime.getTime()}&layoutName=${layoutName}`
    setEventSource(getSseData(url))
    dispatch({
      type: 'monitors/queryMonitorLayout',
      payload: {layoutName: layoutName}
    })
  }, [endTime])


  useEffect(() => {
    // !custom &&
    eventSource && (eventSource.onmessage = e => {
      let result = JSON.parse(e.data);
      dataProcess(result)
    })
  }, [eventSource, custom])

  const handleRangeChange = (dates: any) => {
    setStartTime(new Date(dates[0]))
    setEndTime(new Date(dates[1]))
    setCustom(true)
  }
  const handleDateRadioChange = (e: any) => {
    const dateKey = e.target.value;
    switch (dateKey) {
      case '60s':
        setStartTime(getSubMinTime(currentTime, 1))
        setEndTime(currentTime)
        break;
      case '5min':
        setStartTime(getSubMinTime(currentTime, 5))
        setEndTime(currentTime)
        break;
      case '10min':
        setStartTime(getSubMinTime(currentTime, 10))
        setEndTime(currentTime)
        break;
      case '1h':
        setStartTime(getSubMinTime(currentTime, 60))
        setEndTime(currentTime)
        break;
      case '2h':
        setStartTime(getSubMinTime(currentTime, 2 * 60))
        setEndTime(currentTime)
        break;
      case '5h':
        setStartTime(getSubMinTime(currentTime, 5 * 60))
        setEndTime(currentTime)
        break;
    }
    setCustom(false)
  }


  return (
    <>
      {/*<>123456========={monitors.user}</>*/}

      <MonitorFilter endTime={endTime} startTime={startTime} jobDetail={jobDetail}
                     handleDateRadioChange={handleDateRadioChange} handleRangeChange={handleRangeChange}/>
      <JobChart jobDetail={jobDetail}/>
    </>);
}


export default connect(({monitors}: { monitors: MonitorType }) => ({
  monitors,
}))(JobMonitor);
