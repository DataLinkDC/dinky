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
import OutHeap from "@/pages/Metrics/Server/OutHeap";
import Thread from "@/pages/Metrics/Server/Thread";
import {CPUIcon, HeapIcon, OutHeapIcon, ThreadIcon} from "@/components/Icons/MetricsIcon";
import {Space} from "antd";
import {useEffect, useState} from "react";
import GlobalFilter from "@/pages/Metrics/Server/GlobalFilter";
import {
    getDayOfMonth,
    getSubDateTime,
    getSubMinTime
} from "@/pages/Metrics/Server/function";
import {getDataByParams, queryDataByParams} from "@/services/BusinessCrud";
import Metrics from "@/pages/Metrics";
import {MetricsDataType} from "@/pages/Metrics/Server/data";
import {API_CONSTANTS, METHOD_CONSTANTS} from "@/services/constants";
import proxy from "../../../../config/proxy";
import NonHeap from "@/pages/Metrics/Server/OutHeap";
import {getSseData} from "@/services/api";
import {request} from "@@/exports";
import {extend} from "umi-request";

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

const Server = () => {
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
    const getLastData =  (data: MetricsDataType[]) => {
        eventSource = getSseData(API_CONSTANTS.MONITOR_GET_LAST_DATA + "?lastTime=" + endTime.getTime() );
        eventSource.onmessage = e => {
            let result = JSON.parse(e.data);
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
        queryDataByParams(API_CONSTANTS.MONITOR_GET_SYSTEM_DATA, {startTime: startTime.getTime(), endTime: endTime.getTime()})
            .then(res => {
                (res as any[]).map(x => x.content = JSON.parse(x.content))
                if (!custom) {
                    getLastData(res)
                }else {
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


    return <>
        <ProCard colSpan={'100%'} bordered>
            <GlobalFilter custom={custom} dateRange={dateRange} endTime={endTime} startTime={startTime}
                          handleDateRadioChange={handleDateRadioChange} handleRangeChange={handleRangeChange}/>
        </ProCard>
        <ProCard bordered size={'small'} split={'vertical'}>
                <ProCard title={<Space><CPUIcon style={imgStyle}/>CPU</Space>} extra={dataRecord?.cpuLastValue + "%"}>
                    <CPU data={data}/>
                </ProCard>
                <ProCard title={<Space><HeapIcon style={imgStyle}/>Heap</Space>}
                         extra={dataRecord?.heapLastValue + " / " + dataRecord?.heapMax + " MB"}>
                    <Heap data={data} max={dataRecord?.heapMax}/>
                </ProCard>
                <ProCard title={<Space><ThreadIcon style={imgStyle}/>Thread</Space>}
                         extra={dataRecord?.threadCount + " / " + dataRecord?.threadPeakCount}>
                    <Thread data={data}/>
                </ProCard>
                <ProCard title={<Space><OutHeapIcon style={imgStyle}/>Out Heap</Space>}
                         extra={dataRecord?.nonHeapLastValue + " / " + dataRecord?.nonHeapMax + " MB"}>
                    <NonHeap data={data} max={dataRecord?.nonHeapMax}/>
                </ProCard>
        </ProCard>
    </>
}

export default Server;
