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
import {Badge, Button, Col, Form, Input, InputNumber, Row, Select, Space, Switch, Tag, Tooltip, Typography} from "antd";
import {
  InfoCircleOutlined,
  MinusCircleOutlined,
  MinusSquareOutlined,
  PaperClipOutlined,
  PlusOutlined
} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect} from "react";
import {showTables} from "@/components/Studio/StudioEvent/DDL";
import {JarStateType} from "@/pages/RegistrationCenter/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";
import {RUN_MODE} from "@/components/Studio/conf";
import {AlertStateType} from "@/pages/RegistrationCenter/AlertManage/AlertInstance/model";
import {l} from "@/utils/intl";

const {Option} = Select;
const {Text} = Typography;

const StudioSetting = (props: any) => {


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

  const getClusterOptions = () => {
    const itemList = [];
    for (const item of sessionCluster) {
      const tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias === "" ? item.name : item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

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

  const getEnvOptions = () => {
    const itemList = [<Option key={0} value={0} label='无'>
      无
    </Option>];
    for (const item of env) {
      const tag = (<>{item.enabled ? <Badge status="success"/> : <Badge status="error"/>}
        {item.fragment ? <PaperClipOutlined/> : undefined}{item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
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

  const onChangeClusterSession = () => {
    showTables(currentSession.session, dispatch);
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
            <Select defaultValue={RUN_MODE.LOCAL} value={RUN_MODE.LOCAL}>
              <Option value={RUN_MODE.LOCAL}>Local</Option>
              <Option value={RUN_MODE.STANDALONE}>Standalone</Option>
              <Option value={RUN_MODE.YARN_SESSION}>Yarn Session</Option>
              <Option value={RUN_MODE.YARN_PER_JOB}>Yarn Per-Job</Option>
              <Option value={RUN_MODE.YARN_APPLICATION}>Yarn Application</Option>
              <Option value={RUN_MODE.KUBERNETES_SESSION}>Kubernetes Session</Option>
              <Option value={RUN_MODE.KUBERNETES_APPLICATION}>Kubernetes Application</Option>
            </Select>
          </Form.Item>
          {(current.task.type === RUN_MODE.YARN_SESSION || current.task.type === RUN_MODE.KUBERNETES_SESSION || current.task.type === RUN_MODE.STANDALONE) ? (
            <Row>
              <Col span={24}>
                <Form.Item label="Flink集群" tooltip={`选择Flink集群进行 ${current.task.type} 模式的远程提交任务`}
                           name="clusterId"
                           className={styles.form_item}>
                  {
                    currentSession.session ?
                      (currentSession.sessionConfig && currentSession.sessionConfig.clusterId ?
                          (<><Badge status="success"/><Text
                            type="success">{currentSession.sessionConfig.clusterName}</Text></>)
                          : (<><Badge status="error"/><Text type="danger">本地模式</Text></>)
                      ) : (<Select
                        style={{width: '100%'}}
                        placeholder="选择Flink集群"
                        optionLabelProp="label"
                        onChange={onChangeClusterSession}
                      >
                        {getClusterOptions()}
                      </Select>)
                  }
                </Form.Item>
              </Col>
            </Row>) : undefined}
          {(current.task.type === RUN_MODE.YARN_PER_JOB || current.task.type === RUN_MODE.YARN_APPLICATION || current.task.type === RUN_MODE.KUBERNETES_APPLICATION) ? (
            <Row>
              <Col span={24}>
                <Form.Item label="Flink集群配置"
                           tooltip={`选择Flink集群配置进行 ${current.task.type} 模式的远程提交任务`}
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
            </Row>) : undefined}
          <Form.Item label="FlinkSQL 环境"
                     tooltip={`选择当前任务的 FlinkSQL 执行环境，会提前执行环境语句，默认无。`}
                     name="envId"
                     className={styles.form_item}>
            <Select
              style={{width: '100%'}}
              placeholder="选择 FlinkSQL 环境，非必填"
              allowClear
              optionLabelProp="label"
              defaultValue={0} value={0}
            >
              {getEnvOptions()}
            </Select>
          </Form.Item>
          <Row>
            <Col span={12}>
              <Form.Item
                label="任务并行度" className={styles.form_item} name="parallelism"
                tooltip="设置Flink任务的并行度，最小为 1"
              >
                <InputNumber min={1} max={9999} defaultValue={1}/>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Insert语句集" className={styles.form_item} name="statementSet" valuePropName="checked"
                tooltip={{
                  title: '【增强特性】 开启语句集机制，将把多个 Insert 语句合成一个 JobGraph 再进行提交，Select 语句无效',
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
                label="全局变量" className={styles.form_item} name="fragment" valuePropName="checked"
                tooltip={{
                  title: '【增强特性】 开启FlinkSql全局变量，使用“:=”进行定义（以“;”结束），“${}”进行调用',
                  icon: <InfoCircleOutlined/>
                }}
              >
                <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="批模式" className={styles.form_item} name="batchModel" valuePropName="checked"
                tooltip={{title: '使用批模式', icon: <InfoCircleOutlined/>}}
              >
                <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                />
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
}))(StudioSetting);
