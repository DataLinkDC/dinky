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
import {Tag} from "antd";
import FlinkChart from "./FlinkChart";
import {getData, queryList} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";


const templateData: JobMetrics[] = [
  {
    taskId: 1,
    url: "",
    flinkJobId: '6327183eghjshajdkahsjdhasjkdhksjageqyw',
    jobName: 'job1',
    verticesId: 'job1',
    metricsId: 'job1',
  },
  {
    taskId: 2,
    url: "http://10.8.16.157:8282/",
    flinkJobId: '6906a29cdfbdaf41430ece27bdc265e8',
    jobName: 'udf_test_1',
    verticesId: 'cbc357ccb763df2852fee8c4fc7d55f2',
    metricsId: '0.Source__TableSourceScan(table=[[default_catalog__default_database__sourceTable]].numRecordsOut',
  }
]


const Job = () => {

  const [url, setUrl] = useState<string>('');
  const [jid, setJid] = useState<string>('');
  const [flinkName, setFlinkName] = useState<string>('');
  const [selectTaskId, setSelectTaskId] = useState<number>(0);
  const [selectVertices, setSelectVertices] = useState<string>('');
  const [selectMetrics, setSelectMetrics] = useState<string[]>([]);
  const [vertices, setVertices] = useState<Vertices[]>([]);
  const [metrics, setMetrics] = useState<string[]>([]);
  const [taskData, setTaskData] = useState<Task[]>([]);
  const [data, setData] = useState<JobMetrics[]>([]);

  useEffect(() => {
    getFlinkRunTask().then(res => {
      setTaskData(res.data)
    })
  }, []);

  const getFlinkRunTask = () => {
    return queryList(API_CONSTANTS.GET_JOB_LIST, {
      filter: {},
      currentPage: 1,
      status: "RUNNING",
      sorter: {id: "descend"}
    })
  }
  const getFlinkTaskDetail = (id: number) => {
    return getData(API_CONSTANTS.GET_JOB_DETAIL, {
      id: id,
    })
  }
  const getFlinkJobVertices = async (url: string, jid: string) => {
    const data1 = await getData(API_CONSTANTS.FLINK_PROXY + "/" + url + "/jobs/" + jid);
    return data1.vertices as Vertices[]
  }
  const getFlinkJobMetrics = async (url: string, jid: string, vertices: string) => {
    const data1 = await getData(API_CONSTANTS.FLINK_PROXY + "/" + url + "/jobs/" + jid + "/vertices/" + vertices + "/metrics");
    return (data1 as any[]).map(x => x.id as string)
  }
  const buildSelectDataOptions = (metrics: Task[]) => metrics.map((item) => {

    let label = <div style={{alignItems: 'center', alignContent: 'center'}}>
      <Tag color={'processing'}>Flink JobId: {item.jid}</Tag>
      <Tag color={'success'}>TaskId: {item.id}</Tag>
      <Tag color={'success'}>TaskName: {item.name}</Tag>
    </div>;

    return {
      key: item.name,
      label: label,
      value: item.id,
    };
  })
  const buildSelectDataVerticesOptions = (vertices: Vertices[]) => vertices.map((item) => {

    let label = <div style={{alignItems: 'center', alignContent: 'center'}}>
      <Tag color={'success'}>TaskName: {item.name}</Tag>
    </div>;

    return {
      key: item.name,
      label: label,
      value: item.id,
    };
  })
  const buildSelectDataMetricsOptions = (vertices: string[]) => vertices.map((item) => {

    let label = <div style={{alignItems: 'center', alignContent: 'center'}}>
      <Tag color={'blue'}>TaskName: {item}</Tag>
    </div>;

    return {
      key: item,
      label: label,
      value: item,
    };
  })


  const handleSelectChange = async (value: number) => {
    const res = await getFlinkTaskDetail(value);
    setSelectTaskId(value)
    const url = res.datas.cluster.hosts;
    setUrl(url)
    setFlinkName(res.datas.instance.name)
    const jid1 = res.datas.instance.jid;
    setJid(jid1)
    const data = await getFlinkJobVertices(url, jid1);
    setVertices(data)
  };
  const handleSelectVerticesChange = async (value: string) => {
    setSelectVertices(value)
    const data = await getFlinkJobMetrics(url, jid, value);
    setMetrics(data)
  }
  const handleSelectMetricsChange = async (value: string[]) => {
    setSelectMetrics(value)
    setData(value.map(id => {
      return {
        taskId: selectTaskId,
        url: url,
        flinkJobId: jid,
        jobName: flinkName,
        verticesId: selectVertices,
        metricsId: id,
      }
    }))
  }

  return <>
    <ProCard ghost gutter={[0, 8]}>
      <ProFormSelect
        name="job"
        label="Job"
        placeholder={'Select a job'}
        options={buildSelectDataOptions(taskData)}
        fieldProps={{
          onChange: (value) => handleSelectChange(value)
        }}
      />
      {
        selectTaskId === 0 ? <></> :
          <ProFormSelect
            name="vertices"
            label="vertices"
            placeholder={'Select a vertices'}
            options={buildSelectDataVerticesOptions(vertices)}
            fieldProps={{
              onChange: (value) => handleSelectVerticesChange(value)
            }}
          />
      }
      {
        selectVertices === '' ? <></> :
          <ProFormSelect
            name="metrics"
            label="metrics"
            placeholder={'Select a metrics'}
            options={buildSelectDataMetricsOptions(metrics)}
            mode="multiple"
            fieldProps={{
              onChange: (value) => handleSelectMetricsChange(value)
            }}
          />
      }
    </ProCard>

    <ProCard
        split={'vertical'}
    >
      {
          (selectMetrics.length > 0) &&
          <>
            {data.map(j=> <FlinkChart job={j}></FlinkChart>
            )}
          </>
      }
    </ProCard>
  </>
}

export default Job;
