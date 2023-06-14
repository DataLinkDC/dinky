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
import {PageLoading, ProCard, ProFormSelect, StatisticCard} from "@ant-design/pro-components";
import {Radio, Space, Tag} from "antd";
import {OutHeapIcon} from "@/components/Icons/MetricsIcon";
import NonHeap from "@/pages/Metrics/Server/OutHeap";
import {imgStyle} from "@/pages/Metrics/Server";
import {Line} from "@ant-design/charts";
import FlinkChart from "./FlinkChart";


type JobMetrics = {
    taskId: number
    flinkJobId: string
    jobName: string
    url: string
}


const templateData: JobMetrics[] = [
    {
        taskId: 1,
        url: "",
        flinkJobId: '6327183eghjshajdkahsjdhasjkdhksjageqyw',
        jobName: 'job1',
    },
    {
        taskId: 2,
        url: "http://10.8.16.157:8282/",
        flinkJobId: '6906a29cdfbdaf41430ece27bdc265e8',
        jobName: 'udf_test_1',
    }
]



const Job = () => {

    const [data, setData] = useState<JobMetrics[]>(templateData);
    const [selectTaskId, setSelectTaskId] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(false);

    // useEffect(() => {
    //     getDataByParams(API_CONSTANTS.JOB_METRICS, {taskId: selectTaskId}).then((res) => {
    //         setData(res.data);
    //     });
    // }, []);


    const buildSelectDataOptions = (metrics: JobMetrics[]) => metrics.map((item) => {

        let label = <div style={{alignItems: 'center', alignContent: 'center'}}>
            <Tag color={'processing'}>Flink JobId: {item.flinkJobId}</Tag>
            <Tag color={'success'}>TaskId: {item.taskId}</Tag>
            <Tag color={'success'}>TaskName: {item.jobName}</Tag>
        </div>;

        return {
            key: item.taskId,
            label: label,
            value: item.taskId,
        };
    })

    const handleSelectChange = (value: number) => {
        setLoading(true);
        setSelectTaskId(value);
        setTimeout(() => {
            setLoading(false);
        }, 2000);
    };

  return <>
        <ProFormSelect
            name="job"
            label="Job"
            placeholder={'Select a job'}
            options={buildSelectDataOptions(data)}
            fieldProps={{
                onChange: (value) => handleSelectChange(value)
            }}
        />
        <>
            {
                (selectTaskId !== 0 && selectTaskId !== undefined) &&
                <>
                    {loading ? <PageLoading/> : <>
                        <ProCard direction="column" ghost gutter={[0, 8]}>
                            <FlinkChart url={templateData[1].url} metricsId={"0.Source__TableSourceScan(table=[[default_catalog__default_database__sourceTable]].numRecordsOut"} ></FlinkChart>
                        </ProCard>
                    </>}
                </>
            }
        </>
    </>
}

export default Job;
