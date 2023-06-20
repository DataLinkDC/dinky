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


import {
  ProCard,
} from "@ant-design/pro-components";
import CPU from "@/pages/Metrics/Server/CPU";
import Heap from "@/pages/Metrics/Server/Heap";
import Thread from "@/pages/Metrics/Server/Thread";
import {CPUIcon, HeapIcon, OutHeapIcon, ThreadIcon} from "@/components/Icons/MetricsIcon";
import {Space} from "antd";
import React, {useEffect, useState} from "react";
import GlobalFilter from "@/pages/Metrics/Server/GlobalFilter";
import {
  getSubMinTime
} from "@/pages/Metrics/Server/function";
import {queryDataByParams} from "@/services/BusinessCrud";
import {MetricsDataType} from "@/pages/Metrics/Server/data";
import {API_CONSTANTS} from "@/services/constants";
import NonHeap from "@/pages/Metrics/Server/OutHeap";
import {getSseData} from "@/services/api";
import {AreaConfig} from "@ant-design/plots";
import {Datum} from "@antv/g2plot";
import {BaseConfig} from "@ant-design/plots/es/interface";
import dataList from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLConsole/DataList";
import {AreaOptions as G2plotConfig} from "@antv/g2plot/lib/plots/area/types";
import {c} from "@umijs/utils/compiled/tar";

export const imgStyle = {
  display: 'block',
  width: 24,
  height: 24,
};

type DataRecord = {
  cpuLastValue: number;
  heapMax: number;
  heapLastValue: number;
  nonHeapMax: number;
  nonHeapLastValue: number;
  threadPeakCount: number;
  threadCount: number;
}
type ServerProp = {
  chartConfig: G2plotConfig;
}


const Server: React.FC<ServerProp> = (props) => {
  const {chartConfig}=props
  const commonConfig: G2plotConfig = {
    ...chartConfig,
    yField: 'value',
    xField: 'time',
    xAxis: {
      type: 'time',
      mask: 'HH:mm:ss',
    }
  }

  const [currentTime, setCurrentTime] = useState(new Date());
  const [dateRange, setDateRange] = useState<string>('60s');
  const [startTime, setStartTime] = useState(getSubMinTime(currentTime, 1));
  const [endTime, setEndTime] = useState(new Date());
  const [data, setData] = useState<MetricsDataType[]>([]);
  const [custom, setCustom] = useState<boolean>(false);


  const [dataRecord, setDataRecord] = useState<DataRecord>({
    heapLastValue: 0, cpuLastValue: 0, heapMax: 0
    , threadCount: 0, threadPeakCount: 0
    , nonHeapMax: 0, nonHeapLastValue: 0
  });


  let eventSource: EventSource;
  const getLastData = (data: MetricsDataType[]) => {
    eventSource = getSseData(API_CONSTANTS.MONITOR_GET_LAST_DATA + "?lastTime=" + endTime.getTime());
    eventSource.onmessage = e => {
      let result = JSON.parse(e.data);
      if (result.model!=="local"){
        return
      }
      result.content = JSON.parse(result.content)
      data.push(result)
      setData(data)
      setDataRecord({
        cpuLastValue: result.content.jvm.cpuUsed.toFixed(2),
        heapMax: Number((result.content.jvm.heapMax / (1024 * 1024)).toFixed(0)),
        heapLastValue: Number((result.content.jvm.heapUsed / (1024 * 1024)).toFixed(0)),
        nonHeapMax: Number((result.content.jvm.nonHeapMax / (1024 * 1024)).toFixed(0)),
        nonHeapLastValue: Number((result.content.jvm.nonHeapUsed / (1024 * 1024)).toFixed(0)),
        threadPeakCount: result.content.jvm.threadPeakCount,
        threadCount: result.content.jvm.threadCount,
      })
    }

  }
  const getInitData = () => {
    queryDataByParams(API_CONSTANTS.MONITOR_GET_SYSTEM_DATA, {
      startTime: startTime.getTime(),
      endTime: endTime.getTime()
    })
      .then(res => {
        (res as any[]).forEach(x=>x.content = JSON.parse(x.content))
        res =(res as MetricsDataType[]).filter(x=>x.model=="local")
        if (!custom) {
          getLastData(res)
        } else {
          setData(res)
        }
      })
  }

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    getInitData()

    return () => {
      clearInterval(timer);
      eventSource?.close()
    };

  }, [startTime]);

  const handleDateRadioChange = (e: any) => {
    const dateKey = e.target.value;
    setDateRange(dateKey)
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

  const handleRangeChange = (dates: any) => {
    setDateRange('custom')
    setStartTime(new Date(dates[0]))
    setEndTime(new Date(dates[1]))
    setCustom(true)
  }


  const extraDataBuilder = (data: DataRecord) => {
    return {
      cpuLastValue: data.cpuLastValue + "%",
      heapLastValue: data.heapLastValue + " / " + data.heapMax + " MB",
      nonHeapLastValue: data.nonHeapLastValue + " / " + data.nonHeapMax + " MB",
      threadCount: data.threadCount + " / " + data.threadPeakCount
    }
  }


  return <>
    <ProCard size={'small'} colSpan={'100%'} bordered>
      <GlobalFilter custom={custom} dateRange={dateRange} endTime={endTime} startTime={startTime}
                    handleDateRadioChange={handleDateRadioChange} handleRangeChange={handleRangeChange}/>
    </ProCard>
    <ProCard bordered split={'vertical'}>
      <ProCard title={<Space><CPUIcon style={imgStyle}/>CPU</Space>}
               extra={extraDataBuilder(dataRecord).cpuLastValue}>
        <CPU data={data} chartConfig={commonConfig}/>
      </ProCard>
      <ProCard title={<Space><HeapIcon style={imgStyle}/>Heap</Space>}
               extra={extraDataBuilder(dataRecord).heapLastValue}>
        <Heap data={data} max={dataRecord.heapMax} chartConfig={commonConfig}/>
      </ProCard>
      <ProCard title={<Space><ThreadIcon style={imgStyle}/>Thread</Space>}
               extra={extraDataBuilder(dataRecord).threadCount}>
        <Thread data={data} chartConfig={commonConfig}/>
      </ProCard>
      <ProCard title={<Space><OutHeapIcon style={imgStyle}/>Out Heap</Space>}
               extra={extraDataBuilder(dataRecord).nonHeapLastValue}>
        <NonHeap data={data} max={dataRecord.nonHeapMax} chartConfig={commonConfig}/>
      </ProCard>
    </ProCard>
  </>
}

export default Server;
