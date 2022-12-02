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
import {Button, Col, Form, Input, InputNumber, message, Row, Select, Space, Tag, Tooltip, Upload} from "antd";
import {
  InfoCircleOutlined,
  MinusCircleOutlined,
  MinusSquareOutlined,
  PlusOutlined,
  UploadOutlined
} from "@ant-design/icons";
import styles from "./index.less";
import React, {useEffect, useState} from "react";
import {JarStateType} from "@/pages/RegistrationCenter/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";
import {RUN_MODE} from "@/components/Studio/conf";
import {CODE} from "@/components/Common/crud";
import {
  getHadoopConfigPathFromClusterConfigurationsById
} from "@/pages/RegistrationCenter/ClusterManage/ClusterConfiguration/function";
import {l} from "@/utils/intl";

const {Option} = Select;

const StudioJarSetting = (props: any) => {

  const {clusterConfiguration, current, form, dispatch, tabs, jars, toolHeight} = props;
  const [hadoopConfigPath, setHadoopConfigPath] = useState<string | undefined>(undefined);
  const [jarPath, setJarPath] = useState<string | undefined>(undefined);

  const getClusterConfigurationOptions = () => {
    const itemList = [];
    for (const item of clusterConfiguration) {
      const tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias === "" ? item.name : item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const getJarOptions = () => {
    const itemList = [];
    for (const item of jars) {
      const tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias === "" ? item.name : item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  useEffect(() => {
    form.setFieldsValue(current.task);
    setHadoopConfigPath(getHadoopConfigPathFromClusterConfigurationsById(current.task.clusterConfigurationId, clusterConfiguration));
    for (let i in jars) {
      if (jars[i].id == current.task.jarId) {
        setJarPath(jars[i].path);
        break;
      }
    }
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
    let clusterConfigurationId = all['clusterConfigurationId'];
    let jarId = all['jarId'];
    setHadoopConfigPath(getHadoopConfigPathFromClusterConfigurationsById(clusterConfigurationId, clusterConfiguration));
    for (let i in jars) {
      if (jars[i].id == jarId) {
        setJarPath(jars[i].path);
        break;
      }
    }
  };

  const getUploadHdfsProps = () => {
    let dir = '';
    if (jarPath) {
      if (jarPath.indexOf('.jar') > -1) {
        dir = jarPath.substring(0, jarPath.lastIndexOf('/'));
      } else {
        dir = jarPath;
      }
    }
    return {
      name: 'files',
      action: '/api/fileUpload/hdfs',
      headers: {
        authorization: 'authorization-text',
      },
      data: {
        dir,
        hadoopConfigPath
      },
      showUploadList: true,
      onChange(info) {
        if (info.file.status === 'done') {
          if (info.file.response.code == CODE.SUCCESS) {
            message.success(info.file.response.msg);
          } else {
            message.warn(info.file.response.msg);
          }
        } else if (info.file.status === 'error') {
          message.error(`${info.file.name}` + l('app.request.upload.failed'));
        }
      },
    }
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
            label="执行模式" className={styles.form_item} name="type"
            tooltip='指定 Flink 任务的执行模式，默认为 Local'
          >
            <Select defaultValue={RUN_MODE.YARN_APPLICATION} value={RUN_MODE.YARN_APPLICATION}>
              <Option value={RUN_MODE.YARN_APPLICATION}>Yarn Application</Option>
            </Select>
          </Form.Item>
          <Row>
            <Col span={24}>
              <Form.Item label="Flink集群配置" tooltip={`选择Flink集群配置进行 ${current.task.type} 模式的远程提交任务`}
                         name="clusterConfigurationId"
                         className={styles.form_item}>
                <Select
                  style={{width: '100%'}}
                  placeholder="选择Flink集群配置"
                  optionLabelProp="label"
                >
                  {getClusterConfigurationOptions()}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item label={<>可执行 Jar </>}
                     tooltip={`选择可执行 Jar 进行 ${current.task.type} 模式的远程提交 Jar 任务。当该参数项存在值时，将只提交可执行 Jar.`}
                     className={styles.form_item}>
            <Form.Item name="jarId" noStyle>
              <Select
                style={{width: '80%'}}
                placeholder="选择可执行Jar，非必填"
                allowClear
                optionLabelProp="label"
              >
                {getJarOptions()}
              </Select>
            </Form.Item>
            <Upload {...getUploadHdfsProps()} multiple>
              <UploadOutlined/>
            </Upload>
          </Form.Item>
          <Row>
            <Col span={12}>
              <Form.Item label="CheckPoint" tooltip="设置Flink任务的检查点步长，0 代表不启用" name="checkPoint"
                         className={styles.form_item}>
                <InputNumber min={0} max={999999} defaultValue={0}/>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Parallelism" className={styles.form_item} name="parallelism"
                tooltip="设置Flink任务的并行度，最小为 1"
              >
                <InputNumber min={1} max={9999} defaultValue={1}/>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="SavePoint策略" className={styles.form_item} name="savePointStrategy"
            tooltip='指定 SavePoint策略，默认为禁用'
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
              label="SavePointPath" className={styles.form_item} name="savePointPath"
              tooltip='从SavePointPath恢复Flink任务'
            >
              <Input placeholder="hdfs://..."/>
            </Form.Item>) : ''
          }
          <Form.Item
            label="其他配置" className={styles.form_item}
            tooltip={{title: '其他配置项，将被应用于执行环境，如 pipeline.name', icon: <InfoCircleOutlined/>}}
          >

            <Form.List name="config"
            >
              {(fields, {add, remove}) => (
                <>
                  {fields.map(({key, name, fieldKey, ...restField}) => (
                    <Space key={key} style={{display: 'flex'}} align="baseline">
                      <Form.Item
                        {...restField}
                        name={[name, 'key']}
                        style={{marginBottom: '5px'}}
                      >
                        <Input placeholder="参数"/>
                      </Form.Item>
                      <Form.Item
                        {...restField}
                        name={[name, 'value']}
                        style={{marginBottom: '5px'}}
                      >
                        <Input placeholder="值"/>
                      </Form.Item>
                      <MinusCircleOutlined onClick={() => remove(name)}/>
                    </Space>
                  ))}
                  <Form.Item>
                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                      添加配置项
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
          </Form.Item>
        </Form>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio, Jar}: { Studio: StateType, Jar: JarStateType }) => ({
  sessionCluster: Studio.sessionCluster,
  clusterConfiguration: Studio.clusterConfiguration,
  current: Studio.current,
  tabs: Studio.tabs,
  session: Studio.session,
  currentSession: Studio.currentSession,
  toolHeight: Studio.toolHeight,
  jars: Jar.jars,
  env: Studio.env,
}))(StudioJarSetting);
