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


import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {Button, Col, Form, InputNumber, Row, Switch, Tooltip,} from "antd";
import {InfoCircleOutlined, MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {Scrollbars} from 'react-custom-scrollbars';
import {l} from "@/utils/intl";

const StudioConfig = (props: any) => {


  const {current, form, dispatch, tabs, toolHeight} = props;

  form.setFieldsValue(current.task);


  const onValuesChange = (change: any, all: any) => {
    let newTabs = tabs;
    for (let i = 0; i < newTabs.panes.length; i++) {
      if (newTabs.panes[i].key == newTabs.activeKey) {
        for (let key in change) {
          newTabs.panes[i].task[key] = change[key];
        }
        break;
      }
    }

    dispatch && dispatch({
      type: "Studio/saveTabs",
      payload: newTabs,
    });
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined/>}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        <Form
          form={form}
          layout="vertical"
          className={styles.form_setting}
          onValuesChange={onValuesChange}
        >
          <Row>
            <Col span={12}>
              <Form.Item
                label="预览结果" className={styles.form_item} name="useResult" valuePropName="checked"
                tooltip={{title: '开启预览结果，将同步运行并返回数据结果', icon: <InfoCircleOutlined/>}}
              >
                <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="打印流" className={styles.form_item} name="useChangeLog" valuePropName="checked"
                tooltip={{
                  title: '开启打印流，将同步运行并返回含有 op 信息的 ChangeLog，默认不开启且返回最终结果 Table',
                  icon: <InfoCircleOutlined/>
                }}
              >
                <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item
                label="最大行数" className={styles.form_item} name="maxRowNum"
                tooltip='预览数据的最大行数'
              >
                <InputNumber min={1} max={9999} defaultValue={100}/>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="自动停止" className={styles.form_item} name="useAutoCancel" valuePropName="checked"
                tooltip={{title: '开启自动停止，将在捕获最大行数记录后自动停止任务', icon: <InfoCircleOutlined/>}}
              >
                <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
  current: Studio.current,
  currentSession: Studio.currentSession,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
}))(StudioConfig);
