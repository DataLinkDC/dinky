/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import {Col, Form, Row, Select} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import React, {useEffect} from "react";
import {l} from "@/utils/intl";

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

  const {current, column, onChange: handleChange, dispatch} = props;
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue(current.console.chart);
  }, [current.console.chart]);


  const onValuesChange = (change: any, all: any) => {
    let config: PieChartConfig = {
      angleField: all.angleField ? all.angleField : column[0],
      colorField: all.colorField ? all.colorField : column.length > 1 ? column[1] : column[0],
      label: {
        type: 'inner',
        offset: '-30%',
        content: ({percent}) => `${(percent * 100).toFixed(0)}%`,
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
              label={l('chart.angle')} className={styles.form_item} name="angleField"
            >
              {column && column.length > 0 ? (
                <Select allowClear showSearch
                        defaultValue={column[0]} value={column[0]}>
                  {getColumnOptions()}
                </Select>) : (<Select allowClear showSearch>
                {column && getColumnOptions()}
              </Select>)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label={l('chart.color')} className={styles.form_item} name="colorField"
            >
              {column && column.length > 1 ? (
                <Select allowClear showSearch
                        defaultValue={column[1]} value={column[1]}>
                  {getColumnOptions()}
                </Select>) : (<Select allowClear showSearch>
                {column && getColumnOptions()}
              </Select>)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  result: Studio.result,
}))(PieChartSetting);
