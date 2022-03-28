import {Button, Tag, Row, Col, Form, Select, Empty, Switch} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {FireOutlined, SearchOutlined, RedoOutlined, InfoCircleOutlined} from '@ant-design/icons';
import {useEffect, useState} from "react";
import React from "react";

const {Option} = Select;

export type PieChartConfig = {
  angleField: string,
  colorField: string,
  label: {},
  interactions: [],
};

export type PieChartProps = {
  onChange: (values: Partial<PieChartConfig>) => void;
  data: [];
  column: [];
};

const PieChartSetting: React.FC<PieChartProps> = (props) => {

  const {current,column,onChange: handleChange,dispatch} = props;
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue(current.console.chart);
  }, [current.console.chart]);


  const onValuesChange = (change: any, all: any) => {
    let config: PieChartConfig = {
      angleField: all.angleField?all.angleField:column[0],
      colorField: all.colorField?all.colorField:column.length>1?column[1]:column[0],
      label: {
        type: 'inner',
        offset: '-30%',
        content: ({ percent }) => `${(percent * 100).toFixed(0)}%`,
        style: {
          fontSize: 14,
          textAlign: 'center',
        },
      },
      interactions: [
        {
          type: 'element-active',
        },
      ],
    };
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
              label="弧轴" className={styles.form_item} name="angleField"
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
              label="颜色" className={styles.form_item} name="colorField"
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
      </Form>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  result: Studio.result,
}))(PieChartSetting);
