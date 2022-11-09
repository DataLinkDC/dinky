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
  ApartmentOutlined,
  BarChartOutlined,
  CalendarOutlined,
  CodeOutlined,
  DesktopOutlined,
  FunctionOutlined,
  TableOutlined
} from "@ant-design/icons";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import StudioMsg from "./StudioMsg";
import StudioTable from "./StudioTable";
import StudioHistory from "./StudioHistory";
import StudioFX from "./StudioFX";
import StudioCA from "./StudioCA";
import StudioProcess from "./StudioProcess";
import {Scrollbars} from 'react-custom-scrollbars';
import Chart from "@/components/Chart";
import {useState} from "react";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const StudioConsole = (props: any) => {



  const {height, current} = props;
  let consoleHeight = (height - 37.6);
  const [activeKey, setActiveKey] = useState<string>("StudioMsg");

  const onTabsChange = (key: string) => {
    setActiveKey(key);
  }

  return (
    <Tabs defaultActiveKey="StudioMsg" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0", height: height, margin: "0 32px"
    }} onChange={onTabsChange}>
      <TabPane
        tab={
          <span>
          <CodeOutlined/>
            {l('pages.datastudio.label.info')}
        </span>
        }
        key="StudioMsg"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioMsg height={consoleHeight} isActive={activeKey === "StudioMsg"}/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <TableOutlined/>
            {l('pages.datastudio.label.result')}
        </span>
        }
        key="StudioTable"
      >
        <Scrollbars style={{height: consoleHeight}}>
          {current ? <StudioTable/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <BarChartOutlined/>
          BI
        </span>
        }
        key="StudioChart"
      >
        <Scrollbars style={{height: consoleHeight}}>
          {current ? <Chart height={consoleHeight}/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <ApartmentOutlined/>
            {l('pages.datastudio.label.lineage')}
        </span>
        }
        key="StudioConsanguinity"
      >
        <Scrollbars style={{height: consoleHeight}}>
          {current ? <StudioCA/> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <DesktopOutlined/>
            {l('pages.datastudio.label.process')}
        </span>
        }
        key="StudioProcess"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioProcess/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <CalendarOutlined/>
            {l('pages.datastudio.label.history')}
        </span>
        }
        key="StudioHistory"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioHistory/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <FunctionOutlined/>
            {l('pages.datastudio.label.function')}
        </span>
        }
        key="StudioFX"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioFX/>
        </Scrollbars>
      </TabPane>
    </Tabs>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  sql: Studio.sql,
  current: Studio.current,
}))(StudioConsole);
