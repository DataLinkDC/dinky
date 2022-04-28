import {Button, Tag,Row, Col,Form,Select, Empty} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {RedoOutlined} from '@ant-design/icons';
import {CHART, isSql} from "@/components/Studio/conf";
import { Line,Bar,Pie } from '@ant-design/plots';
import React, {useEffect, useState} from "react";
import LineChartSetting from "./LineChartSetting";
import BarChartSetting from "./BarChartSetting";
import PieChartSetting from "./PieChartSetting";
import {showJobData} from "@/components/Studio/StudioEvent/DQL";
import {Dispatch} from "@@/plugin-dva/connect";


const {Option} = Select;

const Chart = (props:any) => {

  const {current,result,height,dispatch} = props;
  const [config, setConfig] = useState(undefined);
  const [type, setType] = useState<string>(CHART.LINE);
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue(current.console.chart);
  }, [current.console.chart]);

  const toRebuild = () => {
    if(!isSql(current.task.dialect)){
      showJobData(current.key,current.console.result.jobId,dispatch);
    }
  };

  const onValuesChange = (change: any, all: any) => {
    if(change.type){
      setConfig(undefined);
      setType(change.type);
      props.saveChart({type:change.type});
    }
  };

  const renderChartSetting = () => {
    if(!current.console.chart||!current.console.result.result){
      return undefined;
    }
    switch (type){
      case CHART.LINE:
        return <LineChartSetting column={current.console.result.result.columns} onChange={(value) => {
          setConfig(value);
          props.saveChart({...value,type: current.console.chart.type});
        }} />;
        case CHART.BAR:
        return <BarChartSetting column={current.console.result.result.columns} onChange={(value) => {
          setConfig(value);
          props.saveChart({...value,type: current.console.chart.type});
        }} />;
        case CHART.PIE:
        return <PieChartSetting column={current.console.result.result.columns} onChange={(value) => {
          setConfig(value);
          props.saveChart({...value,type: current.console.chart.type});
        }} />;
      default:
        return <LineChartSetting column={current.console.result.result.columns} onChange={(value) => {
          setConfig(value);
          props.saveChart({...value,type: current.console.chart.type});
        }} />
    }
  };

  const renderChartContent = () => {
    if(!current.console.result.result||!current.console.result.result.columns){
      return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />;
    }
    switch (current.console.chart.type){
      case CHART.LINE:
        if(config){
          return <Line data={current.console.result.result.rowData} {...config} />;
        } else {
          return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />;
        }
      case CHART.BAR:
        if(config){
          return <Bar data={current.console.result.result.rowData} {...config} />;
        } else {
          return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />;
        }
      case CHART.PIE:
        if(config && config.angleField){
          return <Pie data={current.console.result.result.rowData} {...config} />;
        } else {
          return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />;
        }
      default:
        return <Line data={current.console.result.result.rowData} {...config} />;
    }
  };

  return (
    <div style={{width: '100%'}}>
      <Row>
        <Col span={16} style={{padding:'20px'}}>
          {renderChartContent()}
        </Col>
        <Col span={8}>
          <Form
            form={form}
            className={styles.form_setting}
            onValuesChange={onValuesChange}
          >
            <Row>
              <Col span={12}>
            <Form.Item
              label="图形类型" className={styles.form_item} name="type"
            >
              <Select defaultValue={CHART.LINE} value={CHART.LINE}>
                <Option value={CHART.LINE}>{CHART.LINE}</Option>
                <Option value={CHART.BAR}>{CHART.BAR}</Option>
                <Option value={CHART.PIE}>{CHART.PIE}</Option>
              </Select>
            </Form.Item>
              </Col>
              { !isSql(current.task.dialect) ? <Col span={12}>
                <Button type="primary" onClick={toRebuild} icon={<RedoOutlined />}>
                  刷新数据
                </Button>
              </Col>:undefined}
            </Row>
          </Form>
            {renderChartSetting()}
        </Col>
      </Row>
    </div>
  );
};

const mapDispatchToProps = (dispatch: Dispatch)=>({
  saveChart:(chart: any)=>dispatch({
    type: "Studio/saveChart",
    payload: chart,
  }),
})

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  result: Studio.result,
}),mapDispatchToProps)(Chart);
