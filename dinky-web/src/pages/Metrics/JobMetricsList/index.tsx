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

import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import FlinkChart from '@/components/Flink/FlinkChart';
import useHookRequest from '@/hooks/useHookRequest';
import { JobMetricsItem, MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import { getMetricsData } from '@/pages/DevOps/JobDetail/srvice';
import { ChartData } from '@/pages/Metrics/JobMetricsList/data';
import { MetricsDataType } from '@/pages/Metrics/Server/data';
import { getMetricsLayout } from '@/pages/Metrics/service';
import { handleRemoveById } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { useModel } from '@@/exports';
import { ProCard, ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { Empty, Spin } from 'antd';
import React, { useEffect, useState } from 'react';
import ListPagination from '@/components/Flink/ListPagination';
import { SseData, Topic } from '@/models/UseWebSocketModel';

export type MetricsProps = {
  timeRange: MetricsTimeFilter;
};

const JobMetricsList = (props: MetricsProps) => {
  const { timeRange } = props;

  const [chartDatas, setChartDatas] = useState<Record<string, ChartData[]>>({});
  const [jobIds, setJobIds] = useState<string>('');

  const { data, refresh } = useHookRequest<any, any>(getMetricsLayout, { defaultParams: [] });

  const dataProcess = (sourceData: Record<string, ChartData[]>, datas: MetricsDataType[]) => {
    datas.forEach((item) => {
      const verticesMap = item.content as Record<string, Record<string, string>>;
      const flinkJobId = item.model;
      Object.keys(verticesMap).forEach((verticeId) =>
        Object.keys(verticesMap[verticeId]).forEach((mertics) => {
          const key = `${flinkJobId}-${verticeId}-${mertics}`;
          if (!(key in sourceData)) {
            sourceData[key] = [];
          }
          sourceData[key].push({
            time: item.heartTime,
            value: verticesMap[verticeId][mertics]
          });
        })
      );
    });
    return sourceData;
  };

  const { loading } = useHookRequest<MetricsDataType[], any>(getMetricsData, {
    defaultParams: [timeRange, jobIds],
    refreshDeps: [timeRange, jobIds],
    onSuccess: (result: MetricsDataType[]) => setChartDatas(() => dataProcess({}, result))
  });

  const { subscribeTopic } = useModel('UseWebSocketModel', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));
  useEffect(() => {
    if (data != undefined) {
      const params: string[] = [];
      const jids: string[] = [];
      data.forEach((item: any) => {
        if (item.flinkJobId != undefined) {
          params.push(item.flinkJobId);
          jids.push(item.flinkJobId);
        }
      });
      setJobIds(jids.join(','));
      if (timeRange.isReal) {
        return subscribeTopic(Topic.METRICS, params, (data: SseData) => {
          //todo ws 实时待优化实现
          // setChartDatas((prevState) => dataProcess(prevState, [data.data]));
        });
      }
    }
  }, [data]);

  const renderFlinkChartGroup = (flinkJobId: string, metricsList: JobMetricsItem[]) => {
    if (metricsList && metricsList?.length > 0) {
      return metricsList?.map((item) => {
        const key = `${flinkJobId}-${item.vertices}-${item.metrics}`;
        return (
          <FlinkChart
            key={key}
            chartSize={item.showSize}
            chartType={item.showType}
            title={item.metrics}
            data={chartDatas[key] ?? []}
          />
        );
      });
    }
    return [<Empty className={'code-content-empty'} />];
  };

  return (
    <>
      {data != undefined &&
        data.map((lo: any) => {
          return (
            <Spin spinning={loading} key={`spin-${lo.layoutName}`}>
              <ProCard
                key={lo.layoutName}
                bordered
                hoverable
                title={lo.layoutName}
                collapsible
                ghost
                gutter={[8, 16]}
                subTitle={
                  <PopconfirmDeleteBtn
                    onClick={async () => {
                      await handleRemoveById(API_CONSTANTS.METRICS_LAYOUT_DELETE, lo.taskId, () => {
                        refresh();
                      });
                    }}
                    description={
                      <span className={'needWrap'}>{l('metrics.flink.deleteConfirm')}</span>
                    }
                  />
                }
              >
                <ListPagination<JobMetricsItem, Filter>
                  data={lo.metrics}
                  layount={(data1) => renderFlinkChartGroup(lo.flinkJobId, data1)}
                  defaultPageSize={12}
                  filter={{
                    content: (data: JobMetricsItem[], setFilter) => {
                      return (
                        <ProForm<Filter>
                          layout={'horizontal'}
                          grid
                          rowProps={{
                            gutter: [16, 0]
                          }}
                          onFinish={async (values) => {
                            setFilter(values);
                          }}
                        >
                          <ProFormSelect
                            colProps={{ md: 12, xl: 8 }}
                            name='vertices'
                            label='边'
                            valueEnum={[...new Set(data.map((item) => item.vertices))].reduce(
                              (accumulator, item) => {
                                accumulator[item] = item;
                                return accumulator;
                              },
                              {} as Record<string, string>
                            )}
                          />
                          <ProFormText colProps={{ md: 12, xl: 8 }} name='metrics' label='节点名' />
                        </ProForm>
                      );
                    },
                    filter: (item: JobMetricsItem, filter: Filter) => {
                      let rule = true;
                      if (!isBlank(filter.vertices)) {
                        rule = rule && item.vertices.includes(filter.vertices);
                      }
                      if (!isBlank(filter.metrics)) {
                        rule = rule && item.metrics.includes(filter.metrics);
                      }
                      return rule;
                    }
                  }}
                />
              </ProCard>
            </Spin>
          );
        })}
      <br />
      <br />
    </>
  );
};
export type Filter = {
  vertices: string;
  metrics: string;
};
export const isBlank = (str: string) => {
  if (str) {
    return false;
  } else if (str == '') {
    return true;
  }
  return true;
};

export default JobMetricsList;
