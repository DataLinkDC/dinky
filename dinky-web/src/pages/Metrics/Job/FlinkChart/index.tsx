import {Radio, Typography} from "antd";
import {Line} from "@ant-design/charts";
import {ProCard, StatisticCard} from "@ant-design/pro-components";
import React, {useEffect, useState} from "react";

const {Paragraph, Text} = Typography;


type ButtonSize = {
    name: string
    value: string
}
type FlinkChart = {
    url?: String;
    metricsId?: String
    data?: String
}

const FlinkChart: React.FC<FlinkChart> = (props) => {
    const {data, url, metricsId} = props;

    const [chartProps, setChartProps] = useState({
        chartSize: "25%",
        chartType: "Chart",
        titleWidth: "50%"
    });
    const [data2, setData2] = useState<[]>([]);

    const asyncFetch = () => {
        fetch(url + '?get=' + metricsId)
            .then((response) => response.json())
            .then((json) => {
                json[0].time = new Date()
                setData2(json)
            })
            .catch((error) => {
                console.log('fetch data failed', error);
            });
    };
    useEffect(() => {

        asyncFetch()
    }, [])
    const config = {
        data: data2,
        xField: 'time',
        yField: 'value',
        xAxis: {
            type: 'time',
            mask: 'HH:mm:ss',
        },
    };

    const renderSizeChangeGroup = () => {
        return <>
            <Radio.Group className={'radio-group-chart'}
                // options={[{label: 'Small', value: '25%'}, {label: 'Big', value: '50%'}]}
                         size="small"
                         buttonStyle="solid"
                         value={chartProps.chartSize}
                         onChange={(e) => {
                             setChartProps((prevState) => ({
                                 ...prevState,
                                 chartSize: e.target.value,
                                 titleWidth: e.target.value == '25%' ? '50%' : '95%'
                             }))
                         }}
            >
                <Radio.Button value={'25%'}>Small</Radio.Button>
                <Radio.Button value={'50%'}>Big</Radio.Button>
            </Radio.Group>
        </>
    }

    const renderChartNumericRadio = () => {
        return <>
            <Radio.Group size="small" buttonStyle="solid" value={chartProps.chartType}
                         onChange={(e) => {
                             setChartProps((prevState) => ({
                                 ...prevState,
                                 chartType: e.target.value
                             }))
                         }}>
                <Radio.Button value="Chart">Chart</Radio.Button>
                <Radio.Button value="Numeric">Numeric</Radio.Button>
            </Radio.Group>
        </>
    }

    return <>

        <ProCard wrap split={'vertical'} gutter={8}>

            <ProCard
                colSpan={chartProps.chartSize} bordered
                title={<Paragraph style={{width: chartProps.titleWidth}} code
                                  ellipsis={{tooltip: true}}>{metricsId} </Paragraph>}
                extra={renderSizeChangeGroup()}
            >
                {chartProps.chartType == "Chart" ? <Line {...config} /> :
                    <StatisticCard
                        style={{minHeight: '100%'}}
                        statistic={{
                            value: 1000,
                        }}
                    />
                }
                {renderChartNumericRadio()}
            </ProCard>
        </ProCard>


    </>
}
export default FlinkChart;
