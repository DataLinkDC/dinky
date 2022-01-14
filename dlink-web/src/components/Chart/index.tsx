import {Button, Tag,Row, Col,Form,Select, Empty} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {FireOutlined, SearchOutlined,RedoOutlined} from '@ant-design/icons';
import {CHART, isSql} from "@/components/Studio/conf";
import { Line } from '@ant-design/plots';
import {useEffect, useState} from "react";
import LineChartSetting from "./LineChartSetting";
import {showJobData} from "@/components/Studio/StudioEvent/DQL";


const {Option} = Select;

const Chart = (props:any) => {

  const {current,result,dispatch} = props;
  const [config, setConfig] = useState({});
  const [data, setData] = useState([]);
  const [column, setColumn] = useState([]);
  const [type, setType] = useState<string>(CHART.LINE);

  useEffect(() => {
    toBuild();
  }, [result,current.console.result]);

  const toBuild = () => {
    if(isSql(current.task.dialect)){
      setData(current.console.result.result.rowData);
      setColumn(current.console.result.result.columns);
    }else{
      setData(result.rowData);
      setColumn(result.columns);
    }
  };

  const toRebuild = () => {
    if(!isSql(current.task.diagnosticCodesToIgnore)){
      showJobData(current.console.result.jobId,dispatch);
    }
  };

  const onValuesChange = (change: any, all: any) => {
    if(change.type){
      setType(change.type);
    }
  };

  const renderChartSetting = () => {
    switch (type){
      case CHART.LINE:
        return <LineChartSetting data={data} column={column} onChange={(value) => {
          setConfig(value);
        }} />;
      default:
        return <LineChartSetting />;
    }
  };

  const renderChartContent = () => {
    if(column.length==0){
      return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />;
    }
    switch (type){
      case CHART.LINE:
        return <Line data={data} {...config} />;
      default:
        return <Line data={data} {...config} />;
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
            className={styles.form_setting}
            onValuesChange={onValuesChange}
          >
            <Row>
              <Col span={12}>
            <Form.Item
              label="图形类型" className={styles.form_item} name="type"
            >
              <Select defaultValue={CHART.LINE} value={CHART.LINE}>
                <Option value={CHART.LINE}>折线图</Option>
              </Select>
            </Form.Item>
              </Col>
              <Col span={12}>
                <Button type="primary" onClick={toRebuild} icon={<RedoOutlined />}>
                  重新渲染
                </Button>
              </Col>
            </Row>
          </Form>
            {renderChartSetting()}
        </Col>
      </Row>
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  result: Studio.result,
}))(Chart);
