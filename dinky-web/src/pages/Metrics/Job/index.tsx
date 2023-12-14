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
import { ChartData, JobMetrics, MetricsLayout, SubTask, Task } from '@/pages/Metrics/Job/data';
import {
  buildMetricsList,
  buildRunningJobList,
  buildSubTaskList
} from '@/pages/Metrics/Job/function';
import { getFlinkRunTask, saveFlinkMetrics } from '@/pages/Metrics/Job/service';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { ProCard, ProFormSelect } from '@ant-design/pro-components';
import { Button, Input, Row } from 'antd';
import { useEffect, useState } from 'react';

const getJobMetrics = async (job: JobMetrics) => {
  const url =
    API_CONSTANTS.FLINK_PROXY +
    '/' +
    job.url +
    '/jobs/' +
    job.flinkJobId +
    '/vertices/' +
    job.subTaskId +
    '/metrics' +
    '?get=' +
    encodeURIComponent(job.metrics);
  const json = await getData(url);
  json[0].time = new Date();
  return json[0] as ChartData;
};
const Job = () => {
  const [metricsData, setMetricsData] = useState({
    url: '',
    jid: '',
    flinkName: '',
    selectTaskId: 0,
    selectSubTask: '',
    selectMetrics: [] as string[]
  });

  const [subTaskList, setSubTaskList] = useState<SubTask[]>([]);
  const [metrics, setMetrics] = useState<string[]>([]);
  const [taskData, setTaskData] = useState<Task[]>([]);
  const [jobMetricsList, setJobMetricsList] = useState<JobMetrics[]>([]);
  const [chartData, setChartData] = useState<Record<string, ChartData[]>>({});
  const [layoutName, setLayoutName] = useState<string>('');
  const [timers, setTimers] = useState<Record<string, NodeJS.Timer>>({});

  useEffect(() => {
    getFlinkRunTask().then((res) => {
      setTaskData(res.data);
    });
  }, []);

  useEffect(() => {
    Object.keys(timers)
      .filter((x) => !jobMetricsList.map((x) => x.metrics).includes(x))
      // @ts-ignore
      .forEach((x) => clearInterval(timers[x]));
  }, [jobMetricsList]);

  /**
   * query flink job detail
   * @param {number} id
   * @returns {Promise<any>}
   */
  const getFlinkTaskDetail = async (id: number) => {
    return await getData(API_CONSTANTS.REFRESH_JOB_DETAIL, { id: id });
  };

  /**
   * query flink job sub task
   * @param {string} url
   * @param {string} jid
   * @returns {Promise<[]>}
   */
  const getFlinkJobSubTask = async (url: string, jid: string) => {
    const flinkJobVertices = await getData(API_CONSTANTS.FLINK_PROXY + '/' + url + '/jobs/' + jid);
    return flinkJobVertices.vertices as SubTask[];
  };

  /**
   * query flink job metrics list
   * @param {string} url
   * @param {string} jid
   * @param subTask
   * @returns {Promise<string[]>}
   */
  const getFlinkJobMetrics = async (url: string, jid: string, subTask: string) => {
    const flinkJobMetrics = await getData(
      API_CONSTANTS.FLINK_PROXY + '/' + url + '/jobs/' + jid + '/vertices/' + subTask + '/metrics'
    );
    return (flinkJobMetrics as any[]).map((x) => x.id as string);
  };

  /**
   * 1 level , change  running job
   * @returns {Promise<void>}
   * @param taskId
   */
  const handleRunningJobChange = async (taskId: number) => {
    // query data of flink running job
    const taskDetail = await getFlinkTaskDetail(taskId);
    // 解构出 flink job url , job name , job id
    const {
      cluster: { hosts: url },
      instance: { name: flinkJobName, jid: flinkJobId }
    } = taskDetail.data;
    setMetricsData((prevState) => ({
      ...prevState,
      url: url,
      flinkName: flinkJobName,
      jid: flinkJobId,
      selectTaskId: taskId
    }));
    const subTasks = await getFlinkJobSubTask(url, flinkJobId);
    setSubTaskList(subTasks);
  };

  /**
   * 2 level , change subtask
   * @returns {Promise<void>}
   * @param subTaskName
   */
  const handleSubTaskChange = async (subTaskName: string) => {
    setMetricsData((prevState) => ({
      ...prevState,
      selectSubTask: subTaskName
    }));
    const jobMetricsDataList = await getFlinkJobMetrics(
      metricsData.url,
      metricsData.jid,
      subTaskName
    );
    setMetrics(jobMetricsDataList.sort());
  };

  /**
   * 3 level , change metrics list
   * @returns {Promise<void>}
   * @param selectList
   */
  const handleMetricsChange = async (selectList: string[]) => {
    setMetricsData((prevState) => ({
      ...prevState,
      selectMetrics: selectList
    }));

    const d: JobMetrics[] = selectList.map((item) => {
      return {
        taskId: metricsData.selectTaskId,
        url: metricsData.url,
        flinkJobId: metricsData.jid,
        jobName: metricsData.flinkName,
        subTaskId: metricsData.selectSubTask,
        metrics: item,
        layoutName: layoutName,
        title: item,
        showSize: '25%',
        showType: 'Chart'
      };
    });
    d.forEach((j) => {
      const data: ChartData[] = [];
      chartData[j.taskId + j.subTaskId + j.metrics] = data;
      setChartData(chartData);
      timers[j.metrics] = setInterval(() => {
        getJobMetrics(j).then((res) => {
          data.push(res);
        });
      }, 1000);
      setTimers(timers);
    });
    setJobMetricsList(d);
  };
  /**
   * render metrics card list
   * @param {JobMetrics[]} metricsList
   * @returns {JSX.Element}
   */
  const renderMetricsCardList = (metricsList: JobMetrics[]) => {
    return (
      <>
        <Row gutter={[8, 16]}>
          {metricsList.map((j) => {
            return (
              <FlinkChart
                key={j.taskId + j.subTaskId + j.metrics}
                chartSize={j.showSize}
                chartType={j.showType}
                onChangeJobState={(chartSize, chartType) => {
                  j.showSize = chartSize;
                  j.showType = chartType;
                }}
                data={chartData[j.taskId + j.subTaskId + j.metrics]}
                title={j.metrics}
              />
            );
          })}
        </Row>
      </>
    );
  };

  return (
    <>
      <ProCard
        ghost
        hoverable
        bordered
        headerBordered
        gutter={[0, 8]}
        title={
          <Input
            placeholder='Flink Job Metrics'
            onChange={(e) => setLayoutName(e.target.value)}
            style={{ width: '100vh' }}
          />
        }
        extra={
          <Button
            size='middle'
            onClick={() => {
              const saveThisLayout = () => {
                const metricsLayouts: MetricsLayout[] = jobMetricsList.map((job, index) => {
                  return {
                    layoutName: layoutName,
                    position: index,
                    metrics: job.metrics,
                    showSize: job.showSize,
                    showType: job.showType,
                    taskId: job.taskId,
                    vertices: job.subTaskId,
                    title: job.metrics
                  } as MetricsLayout;
                });
                saveFlinkMetrics(metricsLayouts).then((res) => {
                  if (res.success) {
                    setSubTaskList([]);
                    setMetrics([]);
                    setJobMetricsList([]);
                  }
                });
              };
              saveThisLayout();
            }}
          >
            {l('button.submit')}
          </Button>
        }
      >
        <ProFormSelect
          name='job'
          label={l('metrics.flink.job.name')}
          placeholder={l('metrics.flink.job.placeholder')}
          options={buildRunningJobList(taskData)}
          fieldProps={{ onChange: (value) => handleRunningJobChange(value as number) }}
        />
        {metricsData.selectTaskId !== 0 && (
          <ProFormSelect
            name='vertices'
            label={l('metrics.flink.subTask')}
            placeholder={l('metrics.flink.subTask.placeholder')}
            options={buildSubTaskList(subTaskList)}
            fieldProps={{ onChange: (value) => handleSubTaskChange(value as string) }}
          />
        )}
        {metricsData.selectSubTask !== '' && (
          <ProFormSelect
            name='metrics'
            label={l('metrics.flink.metrics.name')}
            placeholder={l('metrics.flink.metrics.placeholder')}
            options={buildMetricsList(metrics)}
            mode='multiple'
            fieldProps={{ onChange: (value) => handleMetricsChange(value as string[]) }}
          />
        )}
        {/* render metrics list */}
        {jobMetricsList.length > 0 && renderMetricsCardList(jobMetricsList)}
      </ProCard>
    </>
  );
};

export default Job;
