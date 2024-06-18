import ReactECharts from "echarts-for-react";
import {Flex, Statistic} from "antd";
import React from "react";

type ChartShowProps = {
  show?: boolean
  type: 'Line' | 'Area' | 'Bar' | 'Statistic' | string;
  chartTheme: string,
  chartOptions: Record<string, any>,
  title?: string,
  value?: string | number,
  fontSize?: number,

}
export default (props: ChartShowProps) => {
  const {show = true, type, chartOptions, chartTheme, title, value, fontSize} = props
  return <>
    {
      show && (
        type !== 'Statistic' ? (
          <ReactECharts
            option={chartOptions}
            notMerge={true}
            lazyUpdate={true}
            theme={chartTheme}
            style={{height: '100%', width: '100%', zIndex: 99}}
          />
        ) : (
          <Flex justify={'center'} align={'center'} style={{width: 'inherit',height:"inherit"}}>
            <Statistic
              title={title}
              value={value}
              valueStyle={{fontSize: fontSize}}
            />
          </Flex>
        )
      )
    }
  </>
}
