import {Col, Radio} from "antd";
import {Line} from "@ant-design/charts";
import {ProCard, StatisticCard} from "@ant-design/pro-components";
import React, {forwardRef, Ref, useEffect, useImperativeHandle, useRef, useState} from "react";
import {ChartData, JobMetrics} from "@/pages/Metrics/Job/data";
import Paragraph from "antd/es/typography/Paragraph";
import {useRequest} from "@@/exports";

type FlinkChartProps = {
  title: string
  data?: ChartData[]
  chartType: string
  chartSize: string
  onChangeJobState: (chartSize: string, chartType: string) => void
  requestParams?: any
  autoResfeh?: boolean
}

const FlinkChart = (props: FlinkChartProps) => {

  const {
    data,
    title,
    chartType="Chart",
    chartSize="25%",
    onChangeJobState,
    requestParams,
    autoResfeh
  } = props;

  const [chartProps, setChartProps] = useState({
    chartType: chartType,
    chartSize: chartSize,
    titleWidth: "100%"
  });



  const config = {
    animation: false,
    data: data ?? [],
    xField: 'time',
    yField: 'value',
    xAxis: {
      type: 'time',
      mask: 'HH:mm:ss',
    },
  };

  /**
   * render chart size radio
   * @returns {JSX.Element}
   */
  const renderSizeChangeGroup = () => {
    return <>
      <Radio.Group
        className={'radio-group-chart'}
        size="small"
        buttonStyle="solid"
        value={chartProps.chartSize}
        onChange={(e) => {
          setChartProps((prevState) => ({
            ...prevState,
            titleWidth: e.target.value == '25%' ? '50%' : '100%',
            chartSize: e.target.value
          }))
          onChangeJobState(e.target.value, chartProps.chartType)

        }}
        style={{paddingRight: '5%', paddingTop: '2%'}}
      >
        <Radio.Button value={'50%'}>Big</Radio.Button>
        <Radio.Button value={'25%'}>Small</Radio.Button>
      </Radio.Group>
    </>
  }

  /**
   * render chart type radio
   * @returns {[JSX.Element]}
   */
  const renderChartNumericRadio = (): [JSX.Element] => [
      <Radio.Group
        size="small"
        buttonStyle="solid"
        value={chartProps.chartType}
        onChange={(e) => {
          setChartProps((prevState) => ({
            ...prevState,
            chartType: e.target.value
          }))
          onChangeJobState(chartProps.chartSize, e.target.value)

        }}
        style={{textAlign: "left", paddingLeft: '5%'}}
      >
        <Radio.Button value="Chart">Chart</Radio.Button>
        <Radio.Button value="Numeric">Numeric</Radio.Button>
      </Radio.Group>
  ]

  const renderMetricsChartTitle = (metricsId: string, titleWidth: string | number) => {
    return <>
      <Paragraph
        style={{width: titleWidth}}
        code ellipsis={{tooltip: true}}>
        {metricsId}
      </Paragraph>
    </>
  }
  /**
   * render
   */
  return <>
    <Col span={chartProps.chartSize == '25%' ? 6 : 12}>
      <ProCard
        bodyStyle={{textAlign: 'center'}}
        colSpan={chartProps.chartSize} bordered
        title={renderMetricsChartTitle(title, chartProps.titleWidth)}
        extra={renderSizeChangeGroup()}
        actions={renderChartNumericRadio()}
        style={{height: 240}}
      >
        {chartProps.chartType == "Chart" ? <Line {...config} /> :

          <StatisticCard.Group style={{
            minHeight: '100%',
            minWidth: '100%',
            display: "flex",
            justifyContent: "center",
            alignItems: "center"
          }}>
            {/*<StatisticCard statistic={{value: data?data[-1].value:0}}/>*/}
            {/*{data}*/}
          </StatisticCard.Group>
        }
      </ProCard>
      <ProCard/>
    </Col>
  </>
}

export default forwardRef(FlinkChart);
