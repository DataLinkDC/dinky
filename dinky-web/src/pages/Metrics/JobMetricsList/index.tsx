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
import ListPagination from '@/components/Flink/ListPagination';
import useHookRequest from '@/hooks/useHookRequest';
import { SseData } from '@/models/Sse';
import { SSE_TOPIC } from '@/pages/DevOps/constants';
import { JobMetricsItem, MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';
import { getMetricsData } from '@/pages/DevOps/JobDetail/service';
import { ChartData } from '@/pages/Metrics/JobMetricsList/data';
import { MetricsDataType } from '@/pages/Metrics/Server/data';
import { getMetricsLayout } from '@/pages/Metrics/service';
import { handleRemoveById } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { useModel } from '@@/exports';
import { ProCard, ProFormSelect, ProFormText, QueryFilter } from '@ant-design/pro-components';
import { Empty, Spin } from 'antd';
import { useEffect, useState } from 'react';

export type MetricsProps = {
  timeRange: MetricsTimeFilter;
};

const JobMetricsList = (props: MetricsProps) => {
  const { timeRange } = props;

  const [chartDatas, setChartDatas] = useState<Record<string, ChartData[]>>({});
  const [jobIds, setJobIds] = useState<string>('');

  const { data, refresh: refreshMetricsLayout } = useHookRequest<any, any>(getMetricsLayout, {
    defaultParams: []
  });

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

  const { loading, refresh: refreshMetricsData } = useHookRequest<MetricsDataType[], any>(
    getMetricsData,
    {
      defaultParams: [timeRange, jobIds],
      refreshDeps: [timeRange, jobIds],
      onSuccess: (result: MetricsDataType[]) => setChartDatas(() => dataProcess({}, result))
    }
  );

  const { subscribeTopic } = useModel('Sse', (model: any) => ({
    subscribeTopic: model.subscribeTopic
  }));
  useEffect(() => {
    if (data != undefined) {
      const topics: string[] = [];
      const jids: string[] = [];
      data.forEach((item: any) => {
        if (item.flinkJobId != undefined) {
          topics.push(`${SSE_TOPIC.METRICS}/${item.flinkJobId}`);
          jids.push(item.flinkJobId);
        }
      });
      setJobIds(jids.join(','));
      if (timeRange.isReal) {
        return subscribeTopic(topics, (data: SseData) => {
          setChartDatas((prevState) => dataProcess(prevState, [data.data]));
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
                      await handleRemoveById(API_CONSTANTS.METRICS_LAYOUT_DELETE, lo.taskId, () =>
                        refreshMetricsLayout()
                      );
                    }}
                    description={
                      <span className={'needWrap'}>{l('metrics.flink.deleteConfirm')}</span>
                    }
                  />
                }
              >
                <ListPagination<JobMetricsItem, Filter>
                  data={lo.metrics}
                  layout={(data1) => renderFlinkChartGroup(lo.flinkJobId, data1)}
                  defaultPageSize={12}
                  filter={{
                    content: (data: JobMetricsItem[], setFilter) => {
                      return (
                        <QueryFilter<Filter>
                          labelWidth={'auto'}
                          span={8}
                          defaultCollapsed
                          split
                          onFinish={async (values) => setFilter(values)}
                          onReset={async () => {
                            // 清空筛选条件
                            setFilter({
                              vertices: '',
                              metrics: ''
                            });
                            await refreshMetricsData();
                            await refreshMetricsLayout();
                          }}
                        >
                          <ProFormSelect
                            name='vertices'
                            colProps={{ md: 12, xl: 8 }}
                            label={l('devops.jobinfo.metrics.vertices')}
                            valueEnum={[...new Set(data.map((item) => item.vertices))].reduce(
                              (accumulator, item) => {
                                accumulator[item] = item;
                                return accumulator;
                              },
                              {} as Record<string, string>
                            )}
                          />
                          <ProFormText
                            colProps={{ md: 12, xl: 8 }}
                            name='metrics'
                            label={l('devops.jobinfo.metrics.name')}
                          />
                        </QueryFilter>
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
