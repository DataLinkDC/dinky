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

import React, { useEffect, useState } from 'react';
import { Layout, Responsive, WidthProvider } from 'react-grid-layout';
import './index.less';
import {
  Button,
  Col,
  Empty,
  Flex,
  Input,
  Row,
  Segmented,
  Space,
  Spin,
  Switch,
  Tooltip
} from 'antd';
import {
  AreaChartOutlined,
  BackwardOutlined,
  BarChartOutlined,
  CheckOutlined,
  CloseOutlined,
  EditOutlined,
  FieldNumberOutlined,
  LineChartOutlined,
  PlusOutlined,
  SettingOutlined
} from '@ant-design/icons';
import _ from 'lodash';
import * as echarts from 'echarts';
import { ProCard } from '@ant-design/pro-components';
import { history, useLocation } from '@@/exports';
import useHookRequest from '@/hooks/useHookRequest';
import { addOrUpdate, getDataDetailById } from '@/pages/Dashboard/service';
import Edit from '@/pages/Dashboard/DashboardLayout/Edit';
import {
  deleteKeyFromRecord,
  EchartsOptions,
  LayoutChartData,
  LayoutData
} from '@/pages/Dashboard/data';
import { ChartData } from '@/pages/Metrics/JobMetricsList/data';
import ChartShow from '@/pages/Dashboard/DashboardLayout/ChartShow';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { queryDataByParams } from '@/services/BusinessCrud';
import { PermissionConstants } from '@/types/Public/constants';
import { Authorized } from '@/hooks/useAccess';
import { getUrlParam } from '@/utils/function';
import MetricsFilter from '@/components/Flink/MetricsFilter/MetricsFilter';
import { MetricsTimeFilter } from '@/pages/DevOps/JobDetail/data';

const ResponsiveReactGridLayout = WidthProvider(Responsive);

type ChartComponentData = {
  title: string;
  chartData: LayoutChartData[];
};

// todo 当添加或更新时，需要刷新数据，其次还差值范围获取
export default () => {
  const location = useLocation();
  const layoutId = getUrlParam(location.search, 'layoutId');
  console.log('layoutId', layoutId, location);

  const { data, refresh, loading } = useHookRequest<any, any>(getDataDetailById, {
    defaultParams: [layoutId]
  });
  const chartTheme = data?.chartTheme ?? 'roma';

  const [openChange, setOpenChange] = useState(false);
  const [items, setItems] = useState<Layout[]>([]);
  const [chartData, setChartData] = useState<Record<string, ChartComponentData>>({});
  const [cData, setCData] = useState<Record<number | string, ChartData[]>>({});
  const [updateModel, setUpdateModel] = useState('');
  const [editIsUpdate, setEditIsUpdate] = useState(false);

  // date change
  const [timeRange, setTimeRange] = useState<MetricsTimeFilter>({
    startTime: new Date().getTime() - 60000,
    endTime: new Date().getTime(),
    isReal: true
  });
  const onTimeSelectChange = (filter: MetricsTimeFilter) => {
    setTimeRange(filter);
  };

  const onAddLayout = async (d: { title: string; layouts: LayoutChartData[] }) => {
    if (editIsUpdate) {
      chartData[updateModel] = {
        title: d.title,
        chartData: d.layouts
      };
    } else {
      setUpdateModel('');
      const i = items.length.toString();
      items.push({
        x: (items.length * 2) % (config.cols.lg || 6),
        y: Infinity,
        w: 2,
        h: 2,
        i: i
      });
      chartData[i] = {
        title: d.title,
        chartData: d.layouts
      };
    }
    setItems([...items]);
    setChartData({ ...chartData });
    setOpenChange(false);
  };

  // getChartData
  useEffect(() => {
    // if (!timeRange.isReal) {
    // todo real time
    const d = Object.values(chartData);
    if (d.length > 0) {
      queryDataByParams(API_CONSTANTS.GET_FLINK_DAT_BY_DASHBOARD, {
        startTime: timeRange.startTime,
        endTime: timeRange.endTime,
        flinkMetricsIdList: d
          .flatMap((x) => x.chartData)
          .map((x) => x.id)
          .join()
      }).then((d) => {
        setCData(d as any);
      });
    }
    // }
  }, [chartData, timeRange]);

  useEffect(() => {
    if (!data) {
      return;
    }
    const chartTheme = data.chartTheme;
    echarts.registerTheme(chartTheme, require(`../ChartTheme/${chartTheme}.json`)['theme']);
    const layout = JSON.parse(data.layouts);
    layout.forEach((item: LayoutData) => {
      chartData[item.i] = {
        title: item.title,
        chartData: item.data.map((x: any) => {
          return {
            type: x.type,
            name: x.name,
            id: x.id
            // data: getRandomData(5)
          } as LayoutChartData;
        })
      };
    });
    setChartData({ ...chartData });
    setItems(
      layout.map((x: any) => {
        return {
          ...x,
          minW: 1,
          minH: 1,
          maxW: 6,
          maxH: 3
        };
      })
    );
  }, [data]);

  const config = {
    className: 'layout',
    rowHeight: 200,
    cols: { lg: 6, md: 6, sm: 6, xs: 6, xxs: 6 }
  };
  const [isUpdate, setIsUpdate] = useState(false);
  const [isShowEditCard, setIsShowEditCard] = useState(false);
  const [state] = useState({
    currentBreakpoint: 'lg',
    compactType: 'vertical',
    mounted: false
  });

  const onRemoveItem = (i: string) => {
    setItems(_.reject(items, { i: i }));
    setChartData(deleteKeyFromRecord(chartData, i));
  };

  const generateDOM = () => {
    if (items.length === 0) {
      return <Empty description={l('dashboard.empty')} />;
    }
    return items.map((l, i) => {
      const chartDatum = chartData[l.i]?.chartData;
      chartDatum?.forEach((v) => {
        v.data = (cData ?? {})[v.id] ?? [];
      });

      const title: string = chartData[l.i].title;

      const chartOptions = EchartsOptions(chartDatum, isShowEditCard ? '' : title);

      const options = [
        { label: 'Line', value: 'Line', icon: <LineChartOutlined /> },
        { label: 'Area', value: 'Area', icon: <AreaChartOutlined /> },
        { label: 'Bar', value: 'Bar', icon: <BarChartOutlined /> }
      ];
      if (chartDatum.length < 2) {
        options.push({ label: 'Statistic', value: 'Statistic', icon: <FieldNumberOutlined /> });
      }

      return (
        <div
          key={i}
          data-grid={l}
          style={{
            paddingBottom: 10,
            display: 'flex',
            border: isUpdate ? '1px solid black' : ''
          }}
        >
          {isUpdate && (
            <Button
              style={{
                position: 'absolute',
                right: 10,
                top: 10,
                zIndex: 999
              }}
              type='primary'
              danger
              ghost
              icon={<CloseOutlined />}
              onClick={() => onRemoveItem(l.i)}
            />
          )}

          {isUpdate && isShowEditCard && (
            <ProCard
              title={
                <Input
                  defaultValue={title}
                  onChange={(value) => {
                    setChartData((v) => {
                      v[l.i].title = value.currentTarget.value;
                      return { ...v };
                    });
                  }}
                />
              }
              style={{
                flex: 1,
                zIndex: 1000
              }}
              extra={
                <Button
                  type={'text'}
                  icon={<SettingOutlined size={24} />}
                  onClick={() => {
                    setUpdateModel(l.i);
                    setEditIsUpdate(true);
                    setOpenChange(true);
                  }}
                />
              }
              bordered
              actions={[
                <Flex align={'center'} justify={'center'}>
                  <Segmented
                    defaultValue={chartDatum[0]?.type}
                    onChange={(value) => {
                      setChartData((v) => {
                        v[l.i].chartData.forEach((x) => {
                          x.type = value;
                        });
                        return { ...v };
                      });
                    }}
                    options={options}
                  />
                </Flex>
              ]}
            >
              {
                <ChartShow
                  chartTheme={chartTheme}
                  chartOptions={chartOptions}
                  value={chartDatum[0]?.data?.slice(-1)[0]?.value}
                  type={chartDatum[0]?.type}
                  fontSize={(l.h * config.rowHeight) / 4}
                />
              }
            </ProCard>
          )}
          {
            <ChartShow
              show={!isUpdate || !isShowEditCard}
              chartTheme={chartTheme}
              chartOptions={chartOptions}
              title={title}
              value={chartDatum[0]?.data?.slice(-1)[0]?.value}
              type={chartDatum[0]?.type}
              fontSize={(l.h * config.rowHeight) / 4}
            />
          }
        </div>
      );
    });
  };

  const handleBackClick = () => {
    // go back
    history.push(`/dashboard`);
  };

  return (
    <>
      <Flex wrap gap='small' justify={'space-between'}>
        <Button
          size={'middle'}
          icon={<BackwardOutlined />}
          type='primary'
          onClick={handleBackClick}
        >
          {l('button.back')}
        </Button>
        <div>
          {isUpdate && (
            <>
              <Tooltip title='add'>
                <Switch
                  checkedChildren='开启'
                  unCheckedChildren='关闭'
                  defaultValue={isShowEditCard}
                  onChange={setIsShowEditCard}
                />
              </Tooltip>
            </>
          )}
        </div>
        {isUpdate && (
          <Space size={10}>
            <Authorized key={`added_auth`} path={PermissionConstants.DASHBOARD_CHART_ADD}>
              <Tooltip title={l('button.add')}>
                <Button
                  type='primary'
                  ghost
                  icon={<PlusOutlined />}
                  onClick={() => {
                    setEditIsUpdate(false);
                    setOpenChange(true);
                  }}
                />
              </Tooltip>
            </Authorized>
            <Tooltip title={l('button.finish')}>
              <Button
                type='primary'
                ghost
                icon={<CheckOutlined />}
                onClick={async () => {
                  const layoutData = items.map((item) => {
                    return {
                      x: item.x,
                      y: item.y,
                      w: item.w,
                      h: item.h,
                      i: item.i,
                      title: chartData[item.i].title,
                      data: [
                        ...chartData[item.i].chartData.map((v) => deleteKeyFromRecord(v, 'data'))
                      ]
                    };
                  });
                  await addOrUpdate({
                    ...data,
                    layouts: JSON.stringify(layoutData)
                  });
                  setIsShowEditCard(false);
                  setIsUpdate(false);
                }}
              />
            </Tooltip>

            <Tooltip title={l('button.cancel')}>
              <Button
                type='primary'
                danger
                ghost
                icon={<CloseOutlined />}
                onClick={async () => {
                  setIsUpdate(false);
                  setIsShowEditCard(false);
                  await refresh();
                }}
              />
            </Tooltip>
          </Space>
        )}

        {!isUpdate && (
          <Authorized key={`added_auth`} path={PermissionConstants.DASHBOARD_CHART_EDIT}>
            <Row gutter={10}>
              <MetricsFilter onTimeSelect={onTimeSelectChange} />
              <Col>
                <Tooltip title={l('button.edit')}>
                  <Button
                    type='primary'
                    danger
                    ghost
                    icon={<EditOutlined />}
                    onClick={() => setIsUpdate(true)}
                  />
                </Tooltip>
              </Col>
            </Row>
          </Authorized>
        )}
      </Flex>

      {/*todo 当 resize改变高度或宽度时，rgl 会真的拉升或缩短宽高，导致echarts 没重新渲染，就会造成布局溢出情况*/}
      <Spin spinning={loading}>
        <ResponsiveReactGridLayout
          {...config}
          // layouts={{"lg":items}}
          // WidthProvider option
          measureBeforeMount={false}
          // I like to have it animate on mount. If you don't, delete `useCSSTransforms` (it's default `true`)
          // and set `measureBeforeMount={true}`.
          useCSSTransforms={state.mounted}
          // compactType={state.compactType}
          preventCollision={!state.compactType}
          isDroppable={isUpdate}
          isDraggable={isUpdate}
          isResizable={isUpdate}
          onLayoutChange={(currentLayout) => {
            setItems(currentLayout);
            const tempData = {} as Record<string, ChartComponentData>;
            const values = Object.values(chartData) as ChartComponentData[];
            for (let j = 0; j < values.length; j++) {
              tempData[j] = values[j];
            }
            setChartData(tempData);
          }}
          autoSize
        >
          {Object.values(chartData).length === items.length && generateDOM()}
        </ResponsiveReactGridLayout>
      </Spin>
      {openChange && (
        <Edit
          chartTheme={chartTheme}
          open={openChange}
          title={editIsUpdate ? l('dashboard.update') : l('dashboard.add')}
          onCancel={() => setOpenChange(false)}
          onOk={onAddLayout}
          defaultValue={
            editIsUpdate
              ? {
                  title: chartData[updateModel].title,
                  layouts: chartData[updateModel].chartData
                }
              : { title: '', layouts: [] }
          }
        />
      )}
    </>
  );
};
