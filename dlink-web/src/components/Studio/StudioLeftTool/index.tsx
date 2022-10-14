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


import {Empty, Tabs} from "antd";
import {
  AppstoreOutlined,
  BarsOutlined,
  CloudOutlined,
  ClusterOutlined,
  DatabaseOutlined,
  FunctionOutlined,
  InsertRowAboveOutlined,
  MessageOutlined
} from "@ant-design/icons";
import {StateType} from "@/pages/DataStudio/model";
import {connect,useIntl} from "umi";
import StudioTree from "../StudioTree";
import StudioConnector from "./StudioConnector";
import StudioDataBase from "./StudioDataBase";
import StudioCluster from "./StudioCluster";
import StudioMetaData from "./StudioMetaData";
import StudioMetaStore from "./StudioMetaStore";
import StudioFragment from "@/components/Studio/StudioLeftTool/StudioFragment";

const {TabPane} = Tabs;

const StudioLeftTool = (props: any) => {
  const intl = useIntl();
  const {toolHeight} = props;

  return (
    <Tabs defaultActiveKey="1" size="small" tabPosition="left" style={{height: toolHeight}}>
      <TabPane tab={<span><BarsOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.directory', defaultMessage: '目录',})}</span>} key="StudioTree">
        <StudioTree/>
      </TabPane>
      <TabPane tab={<span><InsertRowAboveOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.structure', defaultMessage: '结构',})}</span>} key="MetaStore">
        <StudioMetaStore/>
      </TabPane>
      <TabPane tab={<span><MessageOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.session', defaultMessage: '会话',})}</span>} key="Connectors">
        <StudioConnector/>
      </TabPane>
      <TabPane tab={<span><ClusterOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.cluster', defaultMessage: '集群',})}</span>} key="Cluster">
        <StudioCluster/>
      </TabPane>
      <TabPane tab={<span><DatabaseOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.datasource', defaultMessage: '数据源',})}</span>} key="DataSource">
        <StudioDataBase/>
      </TabPane>
      <TabPane tab={<span><AppstoreOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.mate', defaultMessage: '元数据',})}</span>} key="MetaData">
        <StudioMetaData/>
      </TabPane>
      <TabPane tab={<span><CloudOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.globalvariable', defaultMessage: '全局变量',})}</span>} key="fragment">
        <StudioFragment/>
      </TabPane>
      <TabPane tab={<span><FunctionOutlined/> {intl.formatMessage({id: 'pages.datastadio.lable.function', defaultMessage: '函数',})}</span>} key="Function">
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
      </TabPane>
    </Tabs>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  sql: Studio.sql,
  toolHeight: Studio.toolHeight,
}))(StudioLeftTool);
