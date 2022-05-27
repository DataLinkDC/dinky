import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {Form, InputNumber, Input, Select, Tag, Row, Col, Badge, Tooltip, Button, Space} from "antd";
import {InfoCircleOutlined, PlusOutlined, MinusSquareOutlined, MinusCircleOutlined,PaperClipOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect} from "react";
import {JarStateType} from "@/pages/Jar/model";
import {EnvStateType} from "@/pages/DevOps/model";
import {ConfigurationStateType} from "@/pages/ClusterConfiguration/model";
import {Scrollbars} from "react-custom-scrollbars";
import {RUN_MODE} from "@/components/Studio/conf";

const {Option} = Select;

const StudioJarSetting = (props: any) => {

  const {clusterConfiguration, current, form, dispatch, tabs, jars,env, toolHeight} = props;

  const getClusterConfigurationOptions = () => {
    const itemList = [];
    for (const item of clusterConfiguration) {
      const tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const getJarOptions = () => {
    const itemList = [];
    for (const item of jars) {
      const tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
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

export default connect(({Studio, Jar,Env,Configuration}: { Studio: StateType, Jar: JarStateType,Env:EnvStateType,Configuration:ConfigurationStateType }) => ({
  sessionCluster: Studio.sessionCluster,
  clusterConfiguration: Configuration.clusterConfiguration,
  current: Studio.current,
  tabs: Studio.tabs,
  session: Studio.session,
  currentSession: Studio.currentSession,
  toolHeight: Studio.toolHeight,
  jars: Jar.jars,
  env: Env.env,
}))(StudioJarSetting);
