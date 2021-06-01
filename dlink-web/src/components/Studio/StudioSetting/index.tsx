import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {Form, InputNumber,Input,Switch,Select,Tag} from "antd";
import {InfoCircleOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect, useState} from "react";

const { Option } = Select;

const StudioSetting = (props: any) => {

  const [form] = Form.useForm();
  const {cluster} = props;
  const [clusterOption, setClusterOption] = useState<[]>();


  const getCluster = ()=>{
    cluster.then(value=>{
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

  useEffect(() => {
    getCluster();
  }, []);

  const localOption = (<><Tag color="default">Local</Tag>本地环境</>);
  return (
    <Form
      form={form}
      layout="vertical"
      className={styles.form_setting}
      initialValues={{}}
    >
      <Form.Item label="Flink集群" tooltip="选择Flink集群进行远程提交任务"
      className={styles.form_item}>
        <Select
          //mode="multiple"
          style={{ width: '100%' }}
          placeholder="选择Flink集群"
          defaultValue={['0']}
          optionLabelProp="label"
        >
          <Option value="0" label={localOption}>
            <Tag color="default">Local</Tag>
             本地环境
          </Option>
          {clusterOption}
        </Select>
      </Form.Item>
      <Form.Item label="CheckPoint" tooltip="设置Flink任务的检查点步长，0 代表不启用"
      className={styles.form_item}>
        <InputNumber min={0} max={999999} defaultValue={0}/>
      </Form.Item>
      <Form.Item
        label="Parallelism" className={styles.form_item}
        tooltip="设置Flink任务的并行度，最小为 1"
      >
        <InputNumber min={1} max={9999} defaultValue={1}/>
      </Form.Item>
      <Form.Item
        label="Fragment" className={styles.form_item}
        tooltip={{ title: '【增强特性】 开启FlinkSql片段机制，使用“:=”进行定义（以“;”结束），“${}”进行调用', icon: <InfoCircleOutlined /> }}
      >
        <Switch checkedChildren="启用" unCheckedChildren="禁用"
               // defaultChecked={formVals.enabled}
        />
      </Form.Item>
      <Form.Item
        label="SavePointPath" className={styles.form_item}
        tooltip='从SavePointPath恢复Flink任务'
      >
        <Input placeholder="hdfs://..." />
      </Form.Item>
    </Form>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
  sql: Studio.sql,
}))(StudioSetting);
