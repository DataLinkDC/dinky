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
import {Button, Col, Form, Input, InputNumber, Row, Select, Tag, Tooltip} from "antd";
import {MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import React, {useEffect} from "react";
import {JarStateType} from "@/pages/RegistrationCenter/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";
import {RUN_MODE} from "@/components/Studio/conf";
import {AlertStateType} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/model";
import {l} from "@/utils/intl";

const {Option} = Select;

const StudioKubernetesConfig = (props: any) => {
  const {
    sessionCluster,
    clusterConfiguration,
    current,
    form,
    dispatch,
    tabs,
    currentSession,
    env,
    group,
    toolHeight
  } = props;

  const getClusterConfigurationOptions = () => {
    const itemList = [];
    for (const item of clusterConfiguration) {
      const tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias === "" ? item.name : item.alias}</>);
      //opeartor mode can not have normal application config
      if (current.task.type == 'kubernetes-application-operator' && item.type == 'FlinkKubernetesOperator'){
        itemList.push(<Option key={item.id} value={item.id} label={tag}>{tag}</Option>)
      }else if (current.task.type != 'kubernetes-application-operator'  && item.type != 'FlinkKubernetesOperator'){
        //if not operator mode , add it normal
        itemList.push(<Option key={item.id} value={item.id} label={tag}>{tag}</Option>)
      }
    }
    return itemList;
  };


  const getGroupOptions = () => {
    const itemList = [<Option key={0} value={0} label={l('button.disable')}>
      {l('button.disable')}
    </Option>];
    for (const item of group) {
      itemList.push(<Option key={item.id} value={item.id} label={item.name}>
        {item.name}
      </Option>)
    }
    return itemList;
  };

  useEffect(() => {
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
          <Form.Item
            label= {l('global.table.execmode')} className={styles.form_item} name="type"
            tooltip={l('pages.datastudio.label.jobConfig.execmode.k8s.tip')}
          >
            <Select defaultValue={RUN_MODE.KUBERNETES_APPLICATION} value={RUN_MODE.KUBERNETES_APPLICATION}>
              <Option value={RUN_MODE.KUBERNETES_APPLICATION}>Kubernetes Application Native</Option>
            </Select>
          </Form.Item>

          <Row>
            <Col span={24}>
              <Form.Item label={l('pages.datastudio.label.jobConfig.clusterConfig')}
                         tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                           type: current.task.type
                         })}
                         name="clusterConfigurationId"
                         className={styles.form_item}>
                <Select
                  style={{width: '100%'}}
                  placeholder={l('pages.datastudio.label.jobConfig.clusterConfig.tip2')}
                  optionLabelProp="label"
                >
                  {getClusterConfigurationOptions()}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row>
            <Col span={12}>
              <Form.Item
                label={l('pages.datastudio.label.jobConfig.parallelism')} className={styles.form_item}
                name="parallelism"
                tooltip={l('pages.datastudio.label.jobConfig.parallelism.tip')}
              >
                <InputNumber min={1} max={9999} defaultValue={1}/>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label={l('pages.datastudio.label.jobConfig.savePointStrategy')} className={styles.form_item}
            name="savePointStrategy"
            tooltip={l('pages.datastudio.label.jobConfig.savePointStrategy.tip')}
          >
            <Select defaultValue={0}>
              <Option value={0}>{l('global.savepoint.strategy.disabled')}</Option>
              <Option value={1}>{l('global.savepoint.strategy.latest')}</Option>
              <Option value={2}>{l('global.savepoint.strategy.earliest')}</Option>
              <Option value={3}>{l('global.savepoint.strategy.custom')}</Option>
            </Select>
          </Form.Item>
          {current.task.savePointStrategy === 3 ?
            (<Form.Item
              label={l('pages.datastudio.label.jobConfig.savePointpath')} className={styles.form_item}
              name="savePointPath"
              tooltip={l('pages.datastudio.label.jobConfig.savePointpath.tip1')}
            >
              <Input placeholder={l('pages.datastudio.label.jobConfig.savePointpath.tip2')}/>
            </Form.Item>) : ''
          }
          <Row>
            <Col span={24}>
              <Form.Item label={l('pages.datastudio.label.jobConfig.alertGroup')}  name="alertGroupId"
                         className={styles.form_item}>
                <Select
                  style={{width: '100%'}}
                  placeholder={l('pages.datastudio.label.jobConfig.alertGroup.tip')}
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
