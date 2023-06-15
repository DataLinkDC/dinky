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
import {ProCard, ProFormSelect} from "@ant-design/pro-components";
import {Row} from "antd";
import FlinkChart from "./FlinkChart";
import {getData, queryList} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";
import {l} from "@/utils/intl";
import {buildMetricsList, buildRunningJobList, buildSubTaskList} from "@/pages/Metrics/Job/function";
import {JobMetrics, SubTask, Task} from "@/pages/Metrics/Job/data";


const Job = () => {

    const [metricsData, setMetricsData] = useState({
        url: '',
        jid: '',
        flinkName: '',
        selectTaskId: 0,
        selectSubTask: '',
        selectMetrics: [] as string[],
    });

    const [subTaskList, setSubTaskList] = useState<SubTask[]>([]);
    const [metrics, setMetrics] = useState<string[]>([]);
    const [taskData, setTaskData] = useState<Task[]>([]);
    const [jobMetricsList, setJobMetricsList] = useState<JobMetrics[]>([]);

    useEffect(() => {
        getFlinkRunTask().then(res => {
            setTaskData(res.data)
        })
    }, []);

    /**
     * 获取 运行的 flink任务 列表
     * @returns {Promise<any>}
     */
    const getFlinkRunTask = () => {
        return queryList(API_CONSTANTS.GET_JOB_LIST, {
            filter: {},
            currentPage: 1,
            status: "RUNNING",
            sorter: {id: "descend"}
        })
    }
    /**
     * query flink job detail
     * @param {number} id
     * @returns {Promise<any>}
     */
    const getFlinkTaskDetail = async (id: number) => {
        return await getData(API_CONSTANTS.GET_JOB_DETAIL, {id: id,})
    }

    /**
     * query flink job sub task
     * @param {string} url
     * @param {string} jid
     * @returns {Promise<Vertices[]>}
     */
    const getFlinkJobSubTask = async (url: string, jid: string) => {
        const flinkJobVertices = await getData(API_CONSTANTS.FLINK_PROXY + "/" + url + "/jobs/" + jid);
        return flinkJobVertices.vertices as SubTask[]
    }

    /**
     * query flink job metrics list
     * @param {string} url
     * @param {string} jid
     * @param subTask
     * @returns {Promise<string[]>}
     */
    const getFlinkJobMetrics = async (url: string, jid: string, subTask: string) => {
        const flinkJobMetrics = await getData(API_CONSTANTS.FLINK_PROXY + "/" + url + "/jobs/" + jid + "/vertices/" + subTask + "/metrics");
        return (flinkJobMetrics as any[]).map(x => x.id as string)
    }


    /**
     * 1 level , change  running job
     * @returns {Promise<void>}
     * @param taskId
     */
    const handleRunningJobChange = async (taskId: number) => {
        // query data of flink running job
        const taskDetail = await getFlinkTaskDetail(taskId);
        // 解构出 flink job url , job name , job id
        const {cluster: {hosts: url}, instance: {name: flinkJobName, jid: flinkJobId}} = taskDetail.datas;
        setMetricsData((prevState) => ({
            ...prevState,
            url: url,
            flinkName: flinkJobName,
            jid: flinkJobId,
            selectTaskId: taskId,
        }))
        const subTasks = await getFlinkJobSubTask(url, flinkJobId);
        setSubTaskList(subTasks)
    };

    /**
     * 2 level , change subtask
     * @returns {Promise<void>}
     * @param subTaskName
     */
    const handleSubTaskChange = async (subTaskName: string) => {
        setMetricsData((prevState) => ({...prevState, selectSubTask: subTaskName}))
        const jobMetricsDataList = await getFlinkJobMetrics(metricsData.url, metricsData.jid, subTaskName);
        setMetrics(jobMetricsDataList.sort())
    }

    /**
     * 3 level , change metrics list
     * @returns {Promise<void>}
     * @param selectList
     */
    const handleMetricsChange = async (selectList: string[]) => {
        setMetricsData((prevState) => ({...prevState, selectMetrics: selectList}))
        setJobMetricsList(selectList.map(item => {
            return {
                taskId: metricsData.selectTaskId,
                url: metricsData.url,
                flinkJobId: metricsData.jid,
                jobName: metricsData.flinkName,
                subTaskId: metricsData.selectSubTask,
                metricsId: item,
            }
        }))
    }


    /**
     * render metrics card list
     * @param {JobMetrics[]} metricsList
     * @returns {JSX.Element}
     */
    const renderMetricsCardList = (metricsList: JobMetrics[]) => {
        return <>
            <Row gutter={[8, 16]}>
                {metricsList.map(j => <FlinkChart job={j}></FlinkChart>)}
            </Row>
        </>
    }


    return <>
        <ProCard>
            <ProFormSelect
                name="job"
                label={l('metrics.flink.job.name')}
                placeholder={l('metrics.flink.job.placeholder')}
                options={buildRunningJobList(taskData)}
                fieldProps={{onChange: (value) => handleRunningJobChange(value)}}
            />
            {
                metricsData.selectTaskId !== 0 &&
                <ProFormSelect
                    name="vertices"
                    label={l('metrics.flink.subTask')}
                    placeholder={l('metrics.flink.subTask.placeholder')}
                    options={buildSubTaskList(subTaskList)}
                    fieldProps={{onChange: (value) => handleSubTaskChange(value)}}
                />
            }
            {
                metricsData.selectSubTask !== '' &&
                <ProFormSelect
                    name="metrics"
                    label={l('metrics.flink.metrics.name')}
                    placeholder={l('metrics.flink.metrics.placeholder')}
                    options={buildMetricsList(metrics)}
                    mode="multiple"
                    fieldProps={{onChange: (value) => handleMetricsChange(value)}}
                />
            }
        </ProCard>
        {/* render metrics list */}
        {(metricsData.selectMetrics.length > 0) && renderMetricsCardList(jobMetricsList)}
    </>
}

export default Job;
