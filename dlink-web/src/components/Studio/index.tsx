import React, {useEffect, useState} from "react";
import {connect} from "umi";
import styles from './index.less';

import {BarsOutlined,SettingOutlined,AuditOutlined,ScheduleOutlined,AppstoreOutlined,ApiOutlined,DashboardOutlined,
  FireOutlined,ClusterOutlined,DatabaseOutlined,FunctionOutlined} from "@ant-design/icons";

import StudioMenu from "./StudioMenu";
import {Row, Col, Card, Empty, Tabs, Form,BackTop} from "antd";
import StudioTree from "./StudioTree";
import StudioTabs from "./StudioTabs";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import StudioConsole from "./StudioConsole";
import StudioSetting from "./StudioSetting";
import StudioEdit from "./StudioEdit";
import StudioConnector from "./StudioConnector";

const {TabPane} = Tabs;

type StudioProps = {
  // sql: StateType['sql'];
  rightClickMenu:StateType['rightClickMenu'];
  dispatch:any;
};

const Studio: React.FC<StudioProps> = (props) => {

  const {rightClickMenu,dispatch} = props;
  const [form] = Form.useForm();

  /*useEffect(() => {
    setSqls(sql);
  }, [sql]);*/

  const onClick=()=>{
    if(rightClickMenu){
      dispatch&&dispatch({
        type: "Studio/showRightClickMenu",
        payload: false,
      });
    }
  };

  return (
    <div onClick={onClick}>
      <StudioMenu form={form}/>
      <Card bordered={false} className={styles.card} size="small" id="studio_card">
        <Row>
          <Col span={4} className={styles["vertical-tabs"]}>
            <Tabs defaultActiveKey="1" size="small" tabPosition="left" style={{ height: "100%",border: "1px solid #f0f0f0"}}>
              <TabPane tab={<span><BarsOutlined/> 目录</span>} key="StudioTree" >
                <StudioTree/>
              </TabPane>
              <TabPane tab={<span><DatabaseOutlined /> 数据源</span>} key="DataSource" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span><AppstoreOutlined /> 元数据</span>} key="MetaData" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span><ClusterOutlined /> 集群</span>} key="Cluster" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span><ApiOutlined /> 连接器</span>} key="Connectors" >
                <StudioConnector />
              </TabPane>
              <TabPane tab={<span><FireOutlined /> 任务</span>} key="FlinkTask" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span><FunctionOutlined /> 函数</span>} key="FlinkTask" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
            </Tabs>
          </Col>
          <Col span={16}>
            <StudioTabs/>
            <StudioEdit/>
          </Col>
          <Col span={4} className={styles["vertical-tabs"]}>
            <Tabs defaultActiveKey="1" size="small" tabPosition="right"  style={{ height: "100%",border: "1px solid #f0f0f0"}}>
              <TabPane tab={<span><SettingOutlined /> 配置</span>} key="1" >
                <StudioSetting form={form} />
              </TabPane>
              <TabPane tab={<span><ScheduleOutlined /> 详情</span>} key="2" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span><AuditOutlined /> 审计</span>} key="3" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
            </Tabs>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <StudioConsole/>
          </Col>
        </Row>
      </Card>
      <BackTop />
    </div>
  )
};

export default connect(({Studio}: { Studio: StateType }) => ({
  rightClickMenu: Studio.rightClickMenu,
}))(Studio);
