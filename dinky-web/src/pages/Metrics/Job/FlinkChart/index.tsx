import {Radio} from "antd";
import {Line} from "@ant-design/charts";
import {ProCard, StatisticCard} from "@ant-design/pro-components";
import React, {useEffect, useState} from "react";
import {MetricsDataType} from "@/pages/Metrics/Server/data";
import Heap from "@/pages/Metrics/Server/Heap";


type ButtonSize = {
    name: string
    value: string
}
type FlinkChart = {
    url?:String;
    metricsId?:String
    data?:String
}

const sizeData: ButtonSize[] = [
    {
        name: "Small",
        value: "25%"
    },
    {
        name: "Big",
        value: "50%"
    }
]
const FlinkChart: React.FC<FlinkChart> = (props) => {
    const {data, url,metricsId} = props;

    const [chartSize, setChartSize] = useState("25%");
    const [chartType, setChartType] = useState("Chart");
    const [data2, setData2] = useState<[]>([]);

    const asyncFetch = () => {
        fetch(url+'?get='+metricsId)
            .then((response) => response.json())
            .then((json) => {
                json[0].time = new Date()
                setData2(json)
            })
            .catch((error) => {
                console.log('fetch data failed', error);
            });
    };
    useEffect(()=>{

        asyncFetch()
    },[])
    const config = {
        data: data2,
        xField: 'time',
        yField: 'value',
        xAxis: {
            type: 'time',
            mask: 'HH:mm:ss',
        },
    };

    return <>
        <ProCard
            title={metricsId?.substring(0, 50)}
            extra={<Radio.Group size="small" buttonStyle="solid" value={chartSize}
                                onChange={(e) => setChartSize(e.target.value)}>
                {
                    sizeData.map((d) => {
                        return (<Radio.Button value={d.value}>{d['name']}</Radio.Button>)
                    })
                }
            </Radio.Group>}
            ghost wrap colSpan={chartSize}
            tooltip={metricsId}
        >
            {chartType == "Chart" ? <Line {...config} /> :
                <StatisticCard
                    statistic={{
                        value: data2[0].value,
                    }}
                />
            }
            <Radio.Group size="small" buttonStyle="solid" value={chartType}
                         onChange={(e) => setChartType(e.target.value)}>
                <Radio.Button value="Chart">Chart</Radio.Button>
                <Radio.Button value="Numeric">Numeric</Radio.Button>
            </Radio.Group>
        </ProCard>
    </>
}
export default FlinkChart;
