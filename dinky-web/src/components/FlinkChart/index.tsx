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

import { ChartData } from '@/pages/Metrics/Job/data';
import { differenceDays } from '@/utils/function';
import { Line } from '@ant-design/charts';
import { ExpandOutlined } from '@ant-design/icons';
import { ProCard, StatisticCard } from '@ant-design/pro-components';
import { Col, Modal, Radio } from 'antd';
import Paragraph from 'antd/es/typography/Paragraph';
import { useState } from 'react';

type extraRenderType = 'size' | 'agrandir';
type FlinkChartProps = {
  title: string;
  data?: ChartData[];
  chartType?: string;
  chartSize?: string;
  extraType?: extraRenderType;
  onChangeJobState?: (chartSize: string, chartType: string) => void;
  chartOptions?: any;
};

const FlinkChart = (props: FlinkChartProps) => {
  const {
    data = [],
    title,
    chartType = 'Chart',
    chartSize = '25%',
    extraType = 'agrandir',
    chartOptions = {},
    onChangeJobState = () => {}
  } = props;

  const [chartProps, setChartProps] = useState({
    chartType: chartType,
    chartSize: chartSize,
    titleWidth: '100%',
    extraType: extraType
  });

  const getLineTimeMask = (charData: ChartData[]) => {
    if (!charData || charData.length <= 1) {
      return 'HH:mm:ss';
    }
    const t1 = charData[charData.length - 1].time;
    const t2 = charData[0].time;
    const duration = Math.abs(differenceDays(t1, t2));
    if (duration <= 0) {
      return 'HH:mm:ss';
    } else if (duration >= 1 && duration < 7) {
      return 'MM-DD HH:mm';
    } else {
      return 'MM-DD';
    }
  };

  const config = {
    animation: false,
    data: data,
    smooth: true,
    xField: 'time',
    yField: 'value',
    xAxis: {
      type: 'time',
      mask: getLineTimeMask(data),
      tickCount: 40
    },
    ...chartOptions
  };

  const [isModalOpen, setIsModalOpen] = useState(false);

  /**
   * render chart type radio
   */
  const renderChartNumericRadio = () => [
    <Radio.Group
      key={'chartRadioKey'}
      size='small'
      buttonStyle='solid'
      value={chartProps.chartType}
      onChange={(e) => {
        setChartProps((prevState) => ({ ...prevState, chartType: e.target.value }));
        onChangeJobState(chartProps.chartSize, e.target.value);
      }}
      style={{ textAlign: 'left', paddingLeft: '5%' }}
    >
      <Radio.Button value='Chart'>Chart</Radio.Button>
      <Radio.Button value='Numeric'>Numeric</Radio.Button>
    </Radio.Group>
  ];

  const renderMetricsChartTitle = (metricsId: string, titleWidth: string | number) => {
    return (
      <Paragraph style={{ width: titleWidth }} code ellipsis={{ tooltip: true }}>
        {metricsId}
      </Paragraph>
    );
  };

  /**
   * render chart size radio
   * @returns {JSX.Element}
   */
  const renderChartExtra = () => {
    if (chartProps.extraType == 'size') {
      return (
        <Radio.Group
          className={'radio-group-chart'}
          size='small'
          buttonStyle='solid'
          value={chartProps.chartSize}
          onChange={(e) => {
            setChartProps((prevState) => ({
              ...prevState,
              titleWidth: e.target.value == '25%' ? '50%' : '100%',
              chartSize: e.target.value
            }));
            onChangeJobState(e.target.value, chartProps.chartType);
          }}
          style={{ paddingRight: '5%', paddingTop: '2%' }}
        >
          <Radio.Button value={'50%'}>Big</Radio.Button>
          <Radio.Button value={'25%'}>Small</Radio.Button>
        </Radio.Group>
      );
    } else {
      return <ExpandOutlined onClick={() => setIsModalOpen(true)} />;
    }
  };

  return (
    <Col span={chartProps.chartSize == '25%' ? 6 : 12}>
      <ProCard
        bodyStyle={{ textAlign: 'center' }}
        colSpan={chartProps.chartSize}
        bordered
        title={renderMetricsChartTitle(title, chartProps.titleWidth)}
        extra={renderChartExtra()}
        actions={renderChartNumericRadio()}
        style={{ height: 240 }}
      >
        {chartProps.chartType == 'Chart' ? (
          <Line {...config} />
        ) : (
          <StatisticCard.Group
            style={{
              minHeight: '100%',
              minWidth: '100%',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center'
            }}
          >
            <StatisticCard statistic={{ value: data ? data[data.length - 1]?.value : 0 }} />
          </StatisticCard.Group>
        )}
      </ProCard>

      <Modal
        title={title}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null}
        width={'100%'}
      >
        <Line {...config} forceFit={false} height={700} />
      </Modal>
    </Col>
  );
};

export default FlinkChart;
