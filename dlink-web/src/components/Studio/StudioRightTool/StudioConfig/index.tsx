import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {
  Form, InputNumber, Input, Switch, Select, Tag, Row, Col, Divider, Tooltip, Button, Badge,
  Typography
} from "antd";
import {InfoCircleOutlined,PlusOutlined,MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect, useState} from "react";
import {showTables} from "@/components/Studio/StudioEvent/DDL";

const { Option } = Select;
const { Text } = Typography;

const StudioConfig = (props: any) => {

  const {current,form,dispatch,tabs,currentSession} = props;

  form.setFieldsValue(current.task);


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
      onValuesChange={onValuesChange}
    >
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
        {
          currentSession.session?
            (currentSession.sessionConfig&&currentSession.sessionConfig.useRemote?(<><Badge status="success"/><Text type="success">已启用</Text></>):(<><Badge status="error"/><Text type="danger">已禁用</Text></>)
            ):(<Switch checkedChildren="启用" unCheckedChildren="禁用"/>)
        }
      </Form.Item>
    </Form>
      </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
  current: Studio.current,
  currentSession: Studio.currentSession,
  tabs: Studio.tabs,
}))(StudioConfig);
