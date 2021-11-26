import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {Form, InputNumber, Input, Switch, Select, Tag, Row, Col, Badge, Tooltip, Button, Typography, Space} from "antd";
import {InfoCircleOutlined, PlusOutlined, MinusSquareOutlined, MinusCircleOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect, useState} from "react";
import {showTables} from "@/components/Studio/StudioEvent/DDL";
import {JarStateType} from "@/pages/Jar/model";
import {Scrollbars} from 'react-custom-scrollbars';

const {Option} = Select;
const {Text} = Typography;

const StudioSetting = (props: any) => {

  const {sessionCluster, clusterConfiguration, current, form, dispatch, tabs, currentSession, jars, toolHeight} = props;

  const getClusterOptions = () => {
    let itemList = [(<Option key={0} value={0} label={(<><Tag color="default">Local</Tag>本地环境</>)}>
      <Tag color="default">Local</Tag>
      本地环境
    </Option>)];
    for (let item of sessionCluster) {
      let tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const getClusterConfigurationOptions = () => {
    let itemList = [];
    for (let item of clusterConfiguration) {
      let tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const getJarOptions = () => {
    let itemList = [];
    for (let item of jars) {
      let tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  useEffect(() => {
    form.setFieldsValue(current.task);
  }, [current.task]);


  const onValuesChange = (change: any, all: any) => {
    let newTabs = tabs;
    for (let i = 0; i < newTabs.panes.length; i++) {
      if (newTabs.panes[i].key == newTabs.activeKey) {
        for (let key in change) {
          newTabs.panes[i].task[key] = all[key];
        }
        break;
      }
    }
    dispatch && dispatch({
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
            tooltip='指定 Flink 任务的执行模式，默认为 yarn-session'
          >
            <Select defaultValue="yarn-session" value="yarn-session">
              <Option value="standalone">standalone</Option>
              <Option value="yarn-session">yarn-session</Option>
              <Option value="yarn-per-job">yarn-per-job</Option>
              <Option value="yarn-application">yarn-application</Option>
            </Select>
          </Form.Item>
          {(current.task.type == 'yarn-session' || current.task.type == 'standalone') ? (
            <Row>
              <Col span={24}>
                <Form.Item label="Flink集群" tooltip={`选择Flink集群进行 ${current.task.type} 模式的远程提交任务`} name="clusterId"
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
                        defaultValue={0}
                        optionLabelProp="label"
                        onChange={onChangeClusterSession}
                      >
                        {getClusterOptions()}
                      </Select>)
                  }
                </Form.Item>
              </Col>
            </Row>) : ''}
          {(current.task.type == 'yarn-per-job' || current.task.type == 'yarn-application') ? (
            <Row>
              <Col span={24}>
                <Form.Item label="Flink集群配置" tooltip={`选择Flink集群配置进行 ${current.task.type} 模式的远程提交任务`}
                           name="clusterConfigurationId"
                           className={styles.form_item}>
                  <Select
                    style={{width: '100%'}}
                    placeholder="选择Flink集群配置"
                    defaultValue={0}
                    optionLabelProp="label"
                  >
                    {getClusterConfigurationOptions()}
                  </Select>
                </Form.Item>
              </Col>
            </Row>) : ''}
          {(current.task.type == 'yarn-application') ? (
            <Row>
              <Col span={24}>
                <Form.Item label="可执行 Jar"
                           tooltip={`选择可执行 Jar 进行 ${current.task.type} 模式的远程提交 Jar 任务。当该参数项存在值时，将只提交可执行 Jar.`}
                           name="jarId"
                           className={styles.form_item}>
                  <Select
                    style={{width: '100%'}}
                    placeholder="选择可执行Jar，非必填"
                    allowClear
                    optionLabelProp="label"
                  >
                    {getJarOptions()}
                  </Select>
                </Form.Item>
              </Col>
            </Row>) : ''}
          <Form.Item
            label="作业名" className={styles.form_item} name="jobName"
            tooltip='设置任务名称，默认为作业名'
          >
            <Input placeholder="自定义作业名"/>
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
          <Row>
            <Col span={12}>
              <Form.Item
                label="Fragment" className={styles.form_item} name="fragment" valuePropName="checked"
                tooltip={{title: '【增强特性】 开启FlinkSql片段机制，使用“:=”进行定义（以“;”结束），“${}”进行调用', icon: <InfoCircleOutlined/>}}
              >
                <Switch checkedChildren="启用" unCheckedChildren="禁用"
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="启用语句集" className={styles.form_item} name="statementSet" valuePropName="checked"
                tooltip={{
                  title: '【增强特性】 开启语句集机制，将把多个 Insert 语句合成一个 JobGraph 再进行提交，Select 语句无效',
                  icon: <InfoCircleOutlined/>
                }}
              >
                <Switch checkedChildren="启用" unCheckedChildren="禁用"
                />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="SavePoint策略" className={styles.form_item} name="savePointStrategy"
            tooltip='指定 SavePoint策略，默认为禁用'
          >
            <Select defaultValue={0}>
              <Option value={0}>禁用</Option>
              <Option value={1}>最近一次</Option>
              <Option value={2}>最早一次</Option>
              <Option value={3}>自定义</Option>
            </Select>
          </Form.Item>
          {current.task.savePointStrategy == 3 ?
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
                        // fieldKey={[fieldKey, 'key']}
                        style={{marginBottom: '5px'}}
                      >
                        <Input placeholder="参数"/>
                      </Form.Item>
                      <Form.Item
                        {...restField}
                        name={[name, 'value']}
                        // fieldKey={[fieldKey, 'value']}
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
}))(StudioSetting);
