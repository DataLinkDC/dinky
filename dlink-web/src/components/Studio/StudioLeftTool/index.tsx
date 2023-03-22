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

import {Tabs} from "antd";
import {AppstoreOutlined, BarsOutlined, InsertRowAboveOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import StudioTree from "../StudioTree";
import StudioMetaData from "./StudioMetaData";
import StudioMetaStore from "./StudioMetaStore";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const StudioLeftTool = (props: any) => {

  const {toolHeight} = props;

  return (
    <Tabs defaultActiveKey="1" size="small" tabPosition="left" style={{height: toolHeight}}>
      <TabPane tab={<span><BarsOutlined/> {l('pages.datastudio.label.directory')}</span>} key="StudioTree">
        <StudioTree/>
      </TabPane>
      <TabPane tab={<span><InsertRowAboveOutlined/> {l('pages.datastudio.label.structure')}</span>}
               key="MetaStore">
        <StudioMetaStore/>
      </TabPane>
      <TabPane tab={<span><AppstoreOutlined/> {l('pages.datastudio.label.meta')}</span>} key="MetaData">
        <StudioMetaData/>
      </TabPane>
    </Tabs>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  sql: Studio.sql,
  toolHeight: Studio.toolHeight,
}))(StudioLeftTool);
