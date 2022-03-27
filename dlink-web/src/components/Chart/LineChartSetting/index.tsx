import {Button, Tag, Row, Col, Form, Select, Empty, Switch} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {FireOutlined, SearchOutlined, RedoOutlined, InfoCircleOutlined} from '@ant-design/icons';
import {useEffect, useState} from "react";
import React from "react";

const {Option} = Select;

export type LineChartConfig = {
  padding: string,
  xField: string,
  yField: string,
  seriesField?: string,
  stepType?: string,
  xAxis?: {
    type?: string,
  },
  slider?: {},
};

export type LineChartProps = {
  onChange: (values: Partial<LineChartConfig>) => void;
  data: [];
  column: [];
};

const LineChartSetting: React.FC<LineChartProps> = (props) => {

  const {current,column,onChange: handleChange,dispatch} = props;
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue(current.console.chart);
  }, [current.console.chart]);


  const onValuesChange = (change: any, all: any) => {
    let config: LineChartConfig = {
      padding: 'auto',
      xField: all.xField?all.xField:column[0],
      yField: all.yField?all.yField:column.length>1?column[1]:column[0],
    };
    if(all.seriesField){
      config.seriesField = all.seriesField;
    }
    if(all.openStepType){
      config.stepType = 'hv';
    }
    if(all.openSlider){
      config.slider = {
        start: 0,
        end: 0.5,
      };
    }
    handleChange(config);
  };

  const getColumnOptions = () => {
    const itemList = [];
    for (const item of column) {
      itemList.push(<Option key={item} value={item} label={item}>
        {item}
      </Option>)
    }
    return itemList;
  };

  return (
    <>
      <Form
        form={form}
        className={styles.form_setting}
        onValuesChange={onValuesChange}
      >
        <Row>
          <Col span={12}>
            <Form.Item
              label="x 轴" className={styles.form_item} name="xField"
            >
              {column&&column.length > 0 ? (
                  <Select allowClear showSearch
                    defaultValue={column[0]} value={column[0]}>
                    {getColumnOptions()}
                  </Select>):(<Select allowClear showSearch>
                {column&&getColumnOptions()}
                  </Select>)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label="y 轴" className={styles.form_item} name="yField"
            >
              {column&&column.length > 1 ? (
                <Select allowClear showSearch
                  defaultValue={column[1]} value={column[1]}>
                  {getColumnOptions()}
                </Select>):(<Select allowClear showSearch>
                {column&&getColumnOptions()}
              </Select>)}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Form.Item
              label="分组字段" className={styles.form_item} name="seriesField"
            >
              {column&&column.length > 0 ? (
                <Select allowClear showSearch>
                  {getColumnOptions()}
                </Select>):(<Select allowClear showSearch>
                {column&&getColumnOptions()}
              </Select>)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label="缩略轴" className={styles.form_item} name="openSlider" valuePropName="checked"
            >
              <Switch checkedChildren="启用" unCheckedChildren="禁用"
              />
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Form.Item
              label="阶梯线" className={styles.form_item} name="openStepType" valuePropName="checked"
            >
              <Switch checkedChildren="启用" unCheckedChildren="禁用"
              />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  result: Studio.result,
}))(LineChartSetting);
