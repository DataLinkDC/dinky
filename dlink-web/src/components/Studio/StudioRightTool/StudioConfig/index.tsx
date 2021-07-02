import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {Form, InputNumber,Input,Switch,Select,Tag,Row,Col,Divider,Tooltip,Button} from "antd";
import {InfoCircleOutlined,PlusOutlined,MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect, useState} from "react";
import {showTables} from "@/components/Studio/StudioEvent/DDL";

const { Option } = Select;

const StudioConfig = (props: any) => {

  const {current,form,dispatch,tabs,session} = props;
  const [newSesstion, setNewSesstion] = useState<string>('');

  form.setFieldsValue(current.task);

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

  const onChangeClusterSession = ()=>{
    showTables(current.task,dispatch);
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
      onValuesChange={onValuesChange}
    >
      <Form.Item
        label="作业名" className={styles.form_item} name="jobName"
        tooltip='设置任务名称，默认为作业名'
      >
        <Input placeholder="自定义作业名" />
      </Form.Item>
      <Row>
        <Col span={10}>
          <Form.Item
            label="共享会话" className={styles.form_item} name="useSession" valuePropName="checked"
            tooltip={{ title: '开启共享会话，将进行 Flink Catalog 的共享', icon: <InfoCircleOutlined /> }}
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用"
            />
          </Form.Item>
        </Col>
        <Col span={14}>
          <Form.Item
            label="会话 Key" tooltip="设置共享会话的 Key" name="session"
            className={styles.form_item}>
            <Select
              placeholder="选择会话"
              allowClear
              onChange={onChangeClusterSession}
              dropdownRender={menu => (
                <div>
                  {menu}
                  <Divider style={{ margin: '4px 0' }} />
                  <div style={{ display: 'flex', flexWrap: 'nowrap', padding: 8 }}>
                    <Input style={{ flex: 'auto' }} value={newSesstion}
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
      <Row>
        <Col span={12}>
          <Form.Item
            label="预览结果" className={styles.form_item} name="useResult" valuePropName="checked"
            tooltip={{ title: '开启预览结果，将同步运行并返回数据结果', icon: <InfoCircleOutlined /> }}
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用"
            />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            label="最大行数" className={styles.form_item} name="maxRowNum"
            tooltip='预览数据的最大行数'
          >
            <InputNumber min={1} max={9999} defaultValue={100} />
          </Form.Item>
        </Col>
      </Row>
      <Form.Item
        label="远程执行" className={styles.form_item} name="useRemote" valuePropName="checked"
        tooltip={{ title: '开启远程执行，将在远程集群进行任务执行', icon: <InfoCircleOutlined /> }}
      >
        <Switch checkedChildren="启用" unCheckedChildren="禁用"
        />
      </Form.Item>
    </Form>
      </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
  current: Studio.current,
  tabs: Studio.tabs,
  session: Studio.session,
}))(StudioConfig);
