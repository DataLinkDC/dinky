import { ChartData } from '@/pages/Metrics/Job/data';
import { Line } from '@ant-design/charts';
import { ExpandOutlined } from '@ant-design/icons';
import { ProCard, StatisticCard } from '@ant-design/pro-components';
import { Col, Modal, Radio } from 'antd';
import Paragraph from 'antd/es/typography/Paragraph';
import { useState } from 'react';

type FlinkChartProps = {
  title: string;
  data?: ChartData[];
  chartType: string;
  chartSize: string;
};

const FlinkChart = (props: FlinkChartProps) => {
  const { data, title, chartType = 'Chart', chartSize = '25%' } = props;

  const [chartProps, setChartProps] = useState({
    chartType: chartType,
    chartSize: chartSize,
    titleWidth: '100%'
  });

  const config = {
    animation: false,
    data: data ?? [],
    xField: 'time',
    yField: 'value',
    xAxis: {
      type: 'time',
      mask: 'HH:mm:ss'
    }
  };

  const [isModalOpen, setIsModalOpen] = useState(false);

  /**
   * render chart type radio
   */
  const renderChartNumericRadio = () => [
    <Radio.Group
      size='small'
      buttonStyle='solid'
      value={chartProps.chartType}
      onChange={(e) => {
        setChartProps((prevState) => ({ ...prevState, chartType: e.target.value }));
      }}
      style={{ textAlign: 'left', paddingLeft: '5%' }}
    >
      <Radio.Button value='Chart'>Chart</Radio.Button>
      <Radio.Button value='Numeric'>Numeric</Radio.Button>
    </Radio.Group>
  ];

  const renderMetricsChartTitle = (metricsId: string, titleWidth: string | number) => {
    return (
      <>
        <Paragraph style={{ width: titleWidth }} code ellipsis={{ tooltip: true }}>
          {metricsId}
        </Paragraph>
      </>
    );
  };

  return (
    <>
      <Col span={chartProps.chartSize == '25%' ? 6 : 12}>
        <ProCard
          bodyStyle={{ textAlign: 'center' }}
          colSpan={chartProps.chartSize}
          bordered
          title={renderMetricsChartTitle(title, chartProps.titleWidth)}
          extra={<ExpandOutlined onClick={() => setIsModalOpen(true)} />}
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
      </Col>

      <Modal
        title={title}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null}
        width={'100vh'}
      >
        <Line {...config} />
      </Modal>
    </>
  );
};

export default FlinkChart;
