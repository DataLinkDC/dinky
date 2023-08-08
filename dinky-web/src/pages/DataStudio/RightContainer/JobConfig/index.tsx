/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {InfoCircleOutlined, MinusCircleOutlined, PaperClipOutlined, PlusOutlined} from "@ant-design/icons";
import {Badge, Button, Col, Form, Input, InputNumber, Row, Select, Space, Switch, Tag, Typography} from "antd";
import {useEffect} from "react";
import {l} from "@/utils/intl";
import {RUN_MODE} from "@/services/constants";
import {connect} from "umi";
import {SessionType, StateType} from "@/pages/DataStudio/model";
import {AlertStateType} from "@/pages/RegCenter/Alert/AlertInstance/model";
import {getCurrentData} from "@/pages/DataStudio/function";
import {useForm} from "antd/es/form/Form";

const {Option} = Select;
const {Text} = Typography;

const JobConfig = (props: any) => {
  const {
    sessionCluster,
    clusterConfiguration,
    dispatch,
    tabs: {panes, activeKey},
    env,
    group,
  } = props;
  const current = getCurrentData(panes, activeKey);
  const currentSession: SessionType = {
    connectors: [], sessionConfig: {
      clusterId: current.clusterId,
      clusterName: current.clusterName,
    }
  }
  const [form] = useForm();
  form.setFieldsValue(current);

  const getClusterOptions = () => {
    const itemList = [];
    for (const item of sessionCluster) {
      const tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.name}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const getClusterConfigurationOptions = () => {
    const itemList = [];
    for (const item of clusterConfiguration) {
      if (current.type.search(item.type.toLowerCase()) === -1) {
        continue;
      }
      const tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.name}</>);
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
        {item.fragment ? <PaperClipOutlined/> : undefined}{item.name}</>);
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

  const onValuesChange = (change: any, all: any) => {
    for (let i = 0; i < panes.length; i++) {
      if (panes[i].key === activeKey) {
        for (const key in change) {
          panes[i].params.taskData[key] = all[key];
        }
        break;
      }
    }
    dispatch({
      type: "Studio/saveTabs",
      payload: {...props.tabs},
    });
  };

  const onChangeClusterSession = () => {
    //todo 这里需要验证

    // showTables(currentSession.session, dispatch);
  };

  return (
    <Form
      initialValues={{
        name: RUN_MODE.LOCAL,
        envId: 0,
        parallelism: 1,
        savePointStrategy: 0,
        alertGroupId: 0,
      }}
      className={"data-studio-form"}
      style={{paddingInline: '10px'}}
      form={form}
      layout="vertical"
      onValuesChange={onValuesChange}
    >
      <Form.Item
        label={l('global.table.execmode')} name="type"
        tooltip={l('pages.datastudio.label.jobConfig.execmode.tip')}
      >
        <Select value={RUN_MODE.LOCAL}>
          <Option value={RUN_MODE.LOCAL}>Local</Option>
          <Option value={RUN_MODE.STANDALONE}>Standalone</Option>
          <Option value={RUN_MODE.YARN_SESSION}>Yarn Session</Option>
          <Option value={RUN_MODE.YARN_PER_JOB}>Yarn Per-Job</Option>
          <Option value={RUN_MODE.YARN_APPLICATION}>Yarn Application</Option>
          <Option value={RUN_MODE.KUBERNETES_SESSION}>Kubernetes Session</Option>
          <Option value={RUN_MODE.KUBERNETES_APPLICATION}>Kubernetes Application</Option>
          <Option value={RUN_MODE.KUBERNETES_APPLICATION_OPERATOR}>Kubernetes Operator Application</Option>
        </Select>
      </Form.Item>
      {(current.type === RUN_MODE.YARN_SESSION || current.type === RUN_MODE.KUBERNETES_SESSION || current.type === RUN_MODE.STANDALONE) ? (
        <Row>
          <Col span={24}>
            <Form.Item label={l('pages.datastudio.label.jobConfig.cluster')}
                       tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                         type: current.type
                       })}
                       name="clusterId"
            >
              {
                currentSession.session ?
                  (currentSession.sessionConfig && currentSession.sessionConfig.clusterId ?
                      (<><Badge status="success"/><Text
                        type="success">{currentSession.sessionConfig.clusterName}</Text></>)
                      : (<><Badge status="error"/><Text type="danger">{l('pages.devops.jobinfo.localenv')}</Text></>)
                  ) : (<Select
                    style={{width: '100%'}}
                    placeholder={l('pages.datastudio.label.jobConfig.cluster.tip')}
                    optionLabelProp="label"
                    onChange={onChangeClusterSession}
                  >
                    {getClusterOptions()}
                  </Select>)
              }
            </Form.Item>
          </Col>
        </Row>) : undefined}
      {(current.type === RUN_MODE.YARN_PER_JOB || current.type === RUN_MODE.YARN_APPLICATION
        || current.type === RUN_MODE.KUBERNETES_APPLICATION || current.type === RUN_MODE.KUBERNETES_APPLICATION_OPERATOR) ? (
        <Row>
          <Col span={24}>
            <Form.Item label={l('pages.datastudio.label.jobConfig.clusterConfig')}
                       tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                         type: current.type
                       })}
                       name="clusterConfigurationId"
            >
              <Select
                style={{width: '100%'}}
                placeholder={l('pages.datastudio.label.jobConfig.clusterConfig.tip2')}
                optionLabelProp="label"
              >
                {getClusterConfigurationOptions()}
              </Select>
            </Form.Item>
          </Col>
        </Row>) : undefined}
      <Form.Item label={l('pages.datastudio.label.jobConfig.flinksql.env')}
                 tooltip={l('pages.datastudio.label.jobConfig.flinksql.env.tip1')}
                 name="envId">
        <Select
          style={{width: '100%'}}
          placeholder={l('pages.datastudio.label.jobConfig.flinksql.env.tip2')}
          allowClear
          optionLabelProp="label"
          value={0}
        >
          {getEnvOptions()}
        </Select>
      </Form.Item>
      <Row>
        <Col span={12}>
          <Form.Item
            label={l('pages.datastudio.label.jobConfig.parallelism')}
            name="parallelism"
            tooltip={l('pages.datastudio.label.jobConfig.parallelism.tip')}
          >
            <InputNumber min={1} max={9999}/>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            label={l('pages.datastudio.label.jobConfig.insert')} name="statementSet"
            valuePropName="checked"
            tooltip={{
              title: l('pages.datastudio.label.jobConfig.insert.tip'),
              icon: <InfoCircleOutlined/>
            }}
          >
            <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
            />
          </Form.Item>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
          <Form.Item
            label={l('pages.datastudio.label.jobConfig.fragment')} name="fragment"
            valuePropName="checked"
            tooltip={{
              title: l('pages.datastudio.label.jobConfig.fragment.tip'),
              icon: <InfoCircleOutlined/>
            }}
          >
            <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
            />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            label={l('pages.datastudio.label.jobConfig.batchmode')} name="batchModel"
            valuePropName="checked"
            tooltip={{title: l('pages.datastudio.label.jobConfig.batchmode.tip'), icon: <InfoCircleOutlined/>}}
          >
            <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
            />
          </Form.Item>
        </Col>
      </Row>
      <Form.Item
        label={l('pages.datastudio.label.jobConfig.savePointStrategy')}
        name="savePointStrategy"
        tooltip={l('pages.datastudio.label.jobConfig.savePointStrategy.tip')}
      >
        <Select>
          <Option value={0}>{l('global.savepoint.strategy.disabled')}</Option>
          <Option value={1}>{l('global.savepoint.strategy.latest')}</Option>
          <Option value={2}>{l('global.savepoint.strategy.earliest')}</Option>
          <Option value={3}>{l('global.savepoint.strategy.custom')}</Option>
        </Select>
      </Form.Item>
      {current.savePointStrategy === 3 ?
        (<Form.Item
          label={l('pages.datastudio.label.jobConfig.savePointpath')}
          name="savePointPath"
          tooltip={l('pages.datastudio.label.jobConfig.savePointpath.tip1')}
        >
          <Input placeholder={l('pages.datastudio.label.jobConfig.savePointpath.tip2')}/>
        </Form.Item>) : ''
      }
      <Row>
        <Col span={24}>
          <Form.Item label={l('pages.datastudio.label.jobConfig.alertGroup')} name="alertGroupId"
          >
            <Select
              style={{width: '100%'}}
              placeholder={l('pages.datastudio.label.jobConfig.alertGroup.tip')}
              optionLabelProp="label"
            >
              {getGroupOptions()}
            </Select>
          </Form.Item>
        </Col>
      </Row>
      <Form.Item
        label={l('pages.datastudio.label.jobConfig.other')}
        tooltip={{title: l('pages.datastudio.label.jobConfig.other.tip'), icon: <InfoCircleOutlined/>}}
      >
        {/*todo 这里需要优化，有有异常抛出*/}
        <Form.List name="config"
        >
          {(fields, {add, remove}) => (
            <>
              {fields.map(({key, name, ...restField}) => (
                <Space key={key} style={{display: 'flex'}} align="baseline">
                  <Form.Item
                    {...restField}
                    name={[name, 'key']}
                    style={{marginBottom: '5px'}}
                  >
                    <Input placeholder={l('pages.datastudio.label.jobConfig.addConfig.params')}/>
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'value']}
                    style={{marginBottom: '5px'}}
                  >
                    <Input placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}/>
                  </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(name)}/>
                </Space>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                  {l('pages.datastudio.label.jobConfig.addConfig')}
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
      </Form.Item>
    </Form>
  );
};

export default connect(({Studio, Alert}: { Studio: StateType, Alert: AlertStateType }) => ({
  sessionCluster: Studio.sessionCluster,
  clusterConfiguration: Studio.clusterConfiguration,
  tabs: Studio.tabs,
  env: Studio.env,
  group: Alert.group,
}))(JobConfig);
