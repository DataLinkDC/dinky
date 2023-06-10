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

export const imgStyle = {
    display: 'block',
    width: 24,
    height: 24,
};


const Server = () => {
    const [currentTime, setCurrentTime] = useState(new Date());
    const [dateRange, setDateRange] = useState<string>('today');
    const [startTime, setStartTime] = useState(new Date());
    const [endTime, setEndTime] = useState(new Date());

    useEffect(() => {
        const timer = setInterval(() => {
            setCurrentTime(new Date());
        }, 1000);
        return () => {
            clearInterval(timer);
        };
    }, []);


    const handleDateRadioChange = (e: any) => {
        const dateKey = e.target.value;
        setDateRange(dateKey)
        switch (dateKey) {
            case 'all':
                setStartTime(getSubMinTime(currentTime, 24 * 60 * 9999))
                setEndTime(currentTime)
                break;
            case 'auto':
                setStartTime(getSubMinTime(currentTime, 24 * 60 * 9999))
                setEndTime(currentTime)
                break;
            case '60s':
                setStartTime(getSubMinTime(currentTime, 1))
                setEndTime(currentTime)
                break;
            case '5min':
                console.log('5min')
                setStartTime(getSubMinTime(currentTime, 5))
                setEndTime(currentTime)
                break;
            case '10min':
                console.log('10min')
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
            case '12h':
                setStartTime(getSubMinTime(currentTime, 12 * 60))
                setEndTime(currentTime)
                break;
            case '24h':
                setStartTime(getSubMinTime(currentTime, 24 * 60))
                setEndTime(currentTime)
                break;
            case 'today':
                setStartTime(getSubDateTime(currentTime, 0, true))
                setEndTime(getSubDateTime(currentTime, 0, false))
                break;
            case 'yesterday':
                setStartTime(getSubDateTime(currentTime, 1, true))
                setEndTime(getSubDateTime(currentTime, 1, false))
                break;
            case 'yesterdaybefore':
                setStartTime(getSubDateTime(currentTime, 2, true))
                setEndTime(getSubDateTime(currentTime, 2, false))
                break;
            case 'last7days':
                setStartTime(getSubDateTime(currentTime, 7,true))
                setEndTime(getSubDateTime(currentTime, 0,false))
                break;
            case 'last15days':
                setStartTime(getSubDateTime(currentTime, 15,true))
                setEndTime(getSubDateTime(currentTime, 0,false))
                break;
            case 'monthly':
                setStartTime(getDayOfMonth(currentTime, true))
                setEndTime(getDayOfMonth(currentTime, false))
                break;
        }
    }

    const handleRangeChange = (e: any) => {
        e.map((item: any) => {
            setStartTime(item[0])
            setEndTime(item[1])
        })
    }


    return <>
        <ProCard style={{height: '4vh'}} ghost colSpan={'100%'}>
            <GlobalFilter dateRange={dateRange} endTime={endTime} startTime={startTime}
                          handleDateRadioChange={handleDateRadioChange} handleRangeChange={handleRangeChange}/>
        </ProCard>
        <ProCard style={{height: '88vh'}} ghost wrap size={'small'} split={'vertical'}>
            <ProCard style={{height: '20vh'}} split={'vertical'}>
                <ProCard title={<Space><CPUIcon style={imgStyle}/>CPU</Space>}>
                    <CPU/>
                </ProCard>
                <ProCard title={<Space><HeapIcon style={imgStyle}/>Heap</Space>}>
                    <Heap/>
                </ProCard>
                <ProCard title={<Space><ThreadIcon style={imgStyle}/>Thread</Space>}>
                    <Thread/>
                </ProCard>
                <ProCard title={<Space><OutHeapIcon style={imgStyle}/>Out Heap</Space>}>
                    <OutHeap/>
                </ProCard>
            </ProCard>
        </ProCard>
    </>
}

export default Server;