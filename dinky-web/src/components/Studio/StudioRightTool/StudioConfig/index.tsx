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
            <Tooltip title={l('component.minimize')}>
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
                label={l('pages.datastudio.label.execConfig.preview.result')} className={styles.form_item} name="useResult" valuePropName="checked"
                tooltip={{title: l('pages.datastudio.label.execConfig.preview.result.tip'), icon: <InfoCircleOutlined/>}}
              >
                <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={l('pages.datastudio.label.execConfig.changelog')} className={styles.form_item} name="useChangeLog" valuePropName="checked"
                tooltip={{
                  title: l('pages.datastudio.label.execConfig.changelog.tip'),
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
                label={l('pages.datastudio.label.execConfig.maxrow')} className={styles.form_item} name="maxRowNum"
                tooltip={l('pages.datastudio.label.execConfig.maxrow.tip')}
              >
                <InputNumber min={1} max={9999} defaultValue={100}/>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={l('pages.datastudio.label.execConfig.autostop')} className={styles.form_item} name="useAutoCancel" valuePropName="checked"
                tooltip={{title: l('pages.datastudio.label.execConfig.autostop.tip'), icon: <InfoCircleOutlined/>}}
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
