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


import {connect, useIntl} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {Button, Col, Form, Input, Row, Select, Tooltip} from "antd";
import {MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect} from "react";
import {JarStateType} from "@/pages/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";
import {RUN_MODE} from "@/components/Studio/conf";
import {AlertStateType} from "@/pages/AlertInstance/model";

const {Option} = Select;

const StudioKubernetesConfig = (props: any) => {

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);

  const {current, form, dispatch, tabs, group, toolHeight} = props;


  const getGroupOptions = () => {
    const itemList = [<Option key={0} value={0} label='禁用'>
      禁用
    </Option>];
    for (const item of group) {
      itemList.push(<Option key={item.id} value={item.id} label={item.name}>
        {item.name}
      </Option>)
    }
    return itemList;
  };

  useEffect(() => {
    //Force set type k8s
    current.task.type = RUN_MODE.KUBERNETES_APPLICATION
    form.setFieldsValue(current.task);
  }, [current.task]);


  const onValuesChange = (change: any, all: any) => {
    const newTabs = tabs;
    for (let i = 0; i < newTabs.panes.length; i++) {
      if (newTabs.panes[i].key === newTabs.activeKey) {
        for (const key in change) {
          newTabs.panes[i].task[key] = all[key];
        }
        break;
      }
    }
    dispatch({
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
          <Form.Item
            label="SavePoint策略" className={styles.form_item} name="savePointStrategy"
            tooltip='指定 SavePoint策略，默认为禁用'
          >
            <Select defaultValue={0}>
              <Option value={0}>禁用</Option>
              <Option value={1}>最近一次</Option>
              <Option value={2}>最早一次</Option>
              <Option value={3}>指定一次</Option>
            </Select>
          </Form.Item>
          {current.task.savePointStrategy === 3 ?
            (<Form.Item
              label="SavePointPath" className={styles.form_item} name="savePointPath"
              tooltip='从SavePointPath恢复Flink任务'
            >
              <Input placeholder="hdfs://..."/>
            </Form.Item>) : ''
          }
          <Row>
            <Col span={24}>
              <Form.Item label="报警组" tooltip={`选择报警组`} name="alertGroupId"
                         className={styles.form_item}>
                <Select
                  style={{width: '100%'}}
                  placeholder="选择报警组"
                  optionLabelProp="label"
                  defaultValue={0}
                >
                  {getGroupOptions()}
                </Select>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio, Jar, Alert}: { Studio: StateType, Jar: JarStateType, Alert: AlertStateType }) => ({
  sessionCluster: Studio.sessionCluster,
  clusterConfiguration: Studio.clusterConfiguration,
  current: Studio.current,
  tabs: Studio.tabs,
  session: Studio.session,
  currentSession: Studio.currentSession,
  toolHeight: Studio.toolHeight,
  jars: Jar.jars,
  env: Studio.env,
  group: Alert.group,
}))(StudioKubernetesConfig);
