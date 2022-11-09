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


import {Col, Form, Row, Select, Switch} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import React, {useEffect} from "react";
import {l} from "@/utils/intl";

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

  const {current, column, onChange: handleChange, dispatch} = props;
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue(current.console.chart);
  }, [current.console.chart]);


  const onValuesChange = (change: any, all: any) => {
    let config: LineChartConfig = {
      padding: 'auto',
      xField: all.xField ? all.xField : column[0],
      yField: all.yField ? all.yField : column.length > 1 ? column[1] : column[0],
    };
    if (all.seriesField) {
      config.seriesField = all.seriesField;
    }
    if (all.openStepType) {
      config.stepType = 'hv';
    }
    if (all.openSlider) {
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
              label={l('chart.xAxis')} className={styles.form_item} name="xField"
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
              label={l('chart.yAxis')} className={styles.form_item} name="yField"
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
        <Row>
          <Col span={12}>
            <Form.Item
              label={l('chart.xAxis')} className={styles.form_item} name="seriesField"
            >
              {column && column.length > 0 ? (
                <Select allowClear showSearch>
                  {getColumnOptions()}
                </Select>) : (<Select allowClear showSearch>
                {column && getColumnOptions()}
              </Select>)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label={l('chart.openSlider')} className={styles.form_item} name="openSlider" valuePropName="checked"
            >
              <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}/>

            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Form.Item
              label={l('chart.openStepType')} className={styles.form_item} name="openStepType" valuePropName="checked"
            >
              <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}/>

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
}))(LineChartSetting);
