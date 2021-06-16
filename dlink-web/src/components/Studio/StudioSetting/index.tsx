import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {Form, InputNumber,Input,Switch,Select,Tag,Row,Col,Divider,Tooltip,Button} from "antd";
import {InfoCircleOutlined,PlusOutlined,MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect, useState} from "react";

const { Option } = Select;

const StudioSetting = (props: any) => {

  const {cluster,current,form,dispatch,tabs,session} = props;
  const [clusterOption, setClusterOption] = useState<[]>();
  const [newSesstion, setNewSesstion] = useState<string>('');


  const getCluster = ()=>{
    cluster&&cluster.then(value=>{
      let itemList = [];
      for(let item of value){
        let tag =(<><Tag color={item.enabled?"processing":"error"}>{item.type}</Tag>{item.alias}</>);
        itemList.push(<Option value={item.id} label={tag}>
          {tag}
        </Option>)
      }
      setClusterOption(itemList);
    });
  };

  form.setFieldsValue(current.task);

  useEffect(() => {
    getCluster();
  }, []);

  const addSession = ()=>{
    if(newSesstion!='') {
      dispatch && dispatch({
        type: "Studio/saveSession",
        payload: newSesstion,
      });
      setNewSesstion('');
    }
  };

  const onValuesChange = (change:any,all:any)=>{
    let newTabs = tabs;
    for(let i=0;i<newTabs.panes.length;i++){
      if(newTabs.panes[i].key==newTabs.activeKey){
        for(let key in change){
          newTabs.panes[i].task[key]=change[key];
        }
        break;
      }
    }

    dispatch&&dispatch({
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
                icon={<MinusSquareOutlined />}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
    <Form
      form={form}
      layout="vertical"
      className={styles.form_setting}
      //initialValues={current.task}
      onValuesChange={onValuesChange}
    >
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
        <InputNumber min={1} max={9999} defaultValue={1} />
      </Form.Item>
        </Col>
      </Row>
      <Row>
        <Col span={12}>
      <Form.Item
        label="Fragment" className={styles.form_item} name="fragment" valuePropName="checked"
        tooltip={{ title: '【增强特性】 开启FlinkSql片段机制，使用“:=”进行定义（以“;”结束），“${}”进行调用', icon: <InfoCircleOutlined /> }}
      >
        <Switch checkedChildren="启用" unCheckedChildren="禁用"
        />
      </Form.Item>
        </Col>
          <Col span={12}>
      <Form.Item
        label="MaxRowNum" className={styles.form_item} name="maxRowNum"
        tooltip='预览数据的最大行数'
      >
        <InputNumber min={1} max={9999} defaultValue={100} />
      </Form.Item>
        </Col>
      </Row>
      <Form.Item
        label="SavePointPath" className={styles.form_item} name="savePointPath"
        tooltip='从SavePointPath恢复Flink任务'
      >
        <Input placeholder="hdfs://..." />
      </Form.Item>
      <Row>
        <Col span={11}>
      <Form.Item label="Flink集群" tooltip="选择Flink集群进行远程提交任务" name="clusterId"
                 className={styles.form_item}>
        <Select
          style={{ width: '100%' }}
          placeholder="选择Flink集群"
          defaultValue={0}
          optionLabelProp="label"
        >
          <Option value={0} label={(<><Tag color="default">Local</Tag>本地环境</>)}>
            <Tag color="default">Local</Tag>
            本地环境
          </Option>
          {clusterOption}
        </Select>
      </Form.Item>
        </Col>
          <Col span={11} offset={1}>
      <Form.Item
        label="共享会话" tooltip="选择会话进行 Flink Catalog 的共享" name="session"
        className={styles.form_item}>
        <Select
          placeholder="选择会话"
          // defaultValue='admin'
          allowClear
          dropdownRender={menu => (
            <div>
              {menu}
              <Divider style={{ margin: '4px 0' }} />
              <div style={{ display: 'flex', flexWrap: 'nowrap', padding: 8 }}>
                <Input style={{ flex: 'auto' }}
                  onChange={(e)=>{
                    setNewSesstion(e.target.value);
                  }}
                />
                <a
                  style={{ flex: 'none', padding: '8px', display: 'block', cursor: 'pointer' }}
                  onClick={addSession}
                >
                  <PlusOutlined />
                </a>
              </div>
            </div>
          )}
        >
          {session.map(item => (
            <Option key={item}>{item}</Option>
          ))}
        </Select>
      </Form.Item>
        </Col>
      </Row>
    </Form>
      </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
  current: Studio.current,
  tabs: Studio.tabs,
  session: Studio.session,
}))(StudioSetting);
