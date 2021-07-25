import {Tabs, Empty} from "antd";
import {BarsOutlined,DatabaseOutlined,AppstoreOutlined,ClusterOutlined,MessageOutlined,FireOutlined,FunctionOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import StudioTree from "../StudioTree";
import StudioConnector from "./StudioConnector";
import React from "react";
import StudioDataBase from "./StudioDataBase";
import StudioCluster from "./StudioCluster";

const { TabPane } = Tabs;

const StudioLeftTool = (props:any) => {

  return (
    <Tabs defaultActiveKey="1" size="small" tabPosition="left" style={{ height: "100%",border: "1px solid #f0f0f0"}}>
      <TabPane tab={<span><BarsOutlined/> 目录</span>} key="StudioTree" >
        <StudioTree/>
      </TabPane>
      <TabPane tab={<span><MessageOutlined /> 会话</span>} key="Connectors" >
        <StudioConnector />
      </TabPane>
      <TabPane tab={<span><ClusterOutlined /> 集群</span>} key="Cluster" >
        <StudioCluster />
      </TabPane>
      <TabPane tab={<span><DatabaseOutlined /> 数据源</span>} key="DataSource" >
        <StudioDataBase />
      </TabPane>
      <TabPane tab={<span><AppstoreOutlined /> 元数据</span>} key="MetaData" >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane tab={<span><FunctionOutlined /> 函数</span>} key="Function" >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(StudioLeftTool);
