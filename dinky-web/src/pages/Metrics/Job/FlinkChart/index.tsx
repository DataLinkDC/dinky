import {Button, Col, Radio, Typography} from "antd";
import {Line} from "@ant-design/charts";
import {ProCard, StatisticCard} from "@ant-design/pro-components";
import React, {useEffect, useState} from "react";
import {getData} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";
import {ChartData, JobMetrics, MetricsLayout} from "@/pages/Metrics/Job/data";
import {l} from "@/utils/intl";
import {renderMetricsChartTitle} from "@/pages/Metrics/Job/function";


type FlinkChartProps = {
    job: JobMetrics
    data?: String
}


const FlinkChart: React.FC<FlinkChartProps> = (props) => {
    const {data, job} = props;

    const [chartProps, setChartProps] = useState({
        chartSize: "25%",
        chartType: "Chart",
        titleWidth: "50%"
    });

    const [data2, setData2] = useState<ChartData[]>([]);
    const getJobMetrics = async () => {
        const url = API_CONSTANTS.FLINK_PROXY + "/" + job.url + '/jobs/' + job.flinkJobId + '/vertices/' + job.subTaskId + '/metrics' + '?get=' + encodeURIComponent(job.metricsId);
        const json = await getData(url);
        json[0].time = new Date()
        return json[0] as ChartData
    }

    const asyncFetch = () => {
        getJobMetrics().then(res => {
            data2.push(res)
            setData2(data2)
        })
    };
    useEffect(() => {
        const interval = setInterval(() => {
            asyncFetch()
        }, 1000);
        return () => {
            clearInterval(interval);
        };
    }, [])
    const config = {
        animation: false,
        data: data2,
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
                        chartSize: e.target.value,
                        titleWidth: e.target.value == '25%' ? '50%' : '100%'
                    }))
                }}
            >
                <Radio.Button value={'50%'}>Big</Radio.Button>
                <Radio.Button value={'25%'}>Small</Radio.Button>
            </Radio.Group>
        </>
    }
    const saveThisLayout = () => {
        const saveLayout: MetricsLayout = {
            layout_name: "", position: "",
            job_id: job.flinkJobId,
            metrics: job.metricsId, show_size: chartProps.chartSize
            , show_type: chartProps.chartType, task_id: job.taskId
            , subTask: job.subTaskId,
            flink_url: job.url
        }
    }

    /**
     * render chart type radio
     * @returns {[JSX.Element]}
     */
    const renderChartNumericRadio = () => [
        <>
            <Radio.Group
                size="small"
                buttonStyle="solid"
                value={chartProps.chartType}
                onChange={(e) => {
                    setChartProps((prevState) => ({
                        ...prevState,
                        chartType: e.target.value
                    }))
                }}
                style={{textAlign: "left"}}
            >
                <Radio.Button value="Chart">Chart</Radio.Button>
                <Radio.Button value="Numeric">Numeric</Radio.Button>
            </Radio.Group>

            {
                data === undefined ?
                    <Button htmlType="button" style={{textAlign: "right"}}>{l('button.submit')}</Button>
                    :
                    <Button htmlType="button" style={{textAlign: "right"}}>{l('button.delete')}</Button>
            }

        </>
    ]


    /**
     * render
     */
    return <>
        <Col span={chartProps.chartSize == '25%' ? 6 : 12}>
            <ProCard
                bodyStyle={{textAlign: 'center'}}
                colSpan={chartProps.chartSize} bordered
                title={renderMetricsChartTitle(job.metricsId)}
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
                        <StatisticCard
                            statistic={{
                                value: data2[data2.length - 1].value,
                            }}
                        />
                    </StatisticCard.Group>
                }
            </ProCard>
            <ProCard/>
        </Col>
    </>
}
export default FlinkChart;
