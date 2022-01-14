import {Button, Tag,Row, Col,Form,Select, Empty} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {FireOutlined, SearchOutlined,RedoOutlined} from '@ant-design/icons';
import {useState} from "react";
import React from "react";

const {Option} = Select;

export type LineChartConfig = {
  xField: string,
  yField: string,
  seriesField?: string,
  xAxis?: {
    type?: string,
  },
};

export type LineChartProps = {
  onChange: (values: Partial<LineChartConfig>) => void;
  data: [];
  column: [];
};

const LineChartSetting: React.FC<LineChartProps> = (props) => {

  const {data,column,onChange: handleChange,dispatch} = props;

  const onValuesChange = (change: any, all: any) => {
    handleChange(all);
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
        className={styles.form_setting}
        onValuesChange={onValuesChange}
      >
        <Row>
          <Col span={12}>
            <Form.Item
              label="x 轴" className={styles.form_item} name="xField"
            >
              {column&&column.length > 0 ? (
                  <Select defaultValue={column[0]} value={column[0]}>
                    {getColumnOptions()}
                  </Select>):(<Select >
                {column&&getColumnOptions()}
                  </Select>)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label="y 轴" className={styles.form_item} name="yField"
            >
              {column&&column.length > 0 ? (
                <Select defaultValue={column[0]} value={column[0]}>
                  {getColumnOptions()}
                </Select>):(<Select >
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
                <Select defaultValue={column[0]} value={column[0]}>
                  {getColumnOptions()}
                </Select>):(<Select >
                {column&&getColumnOptions()}
              </Select>)}
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
