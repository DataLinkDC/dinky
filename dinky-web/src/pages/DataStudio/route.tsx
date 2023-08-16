/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


import {
  ApartmentOutlined,
  ArrowsAltOutlined,
  BarChartOutlined,
  CalendarOutlined,
  ConsoleSqlOutlined,
  DatabaseOutlined,
  DesktopOutlined,
  FolderOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  MonitorOutlined,
  PlayCircleOutlined, PlusCircleOutlined, PlusCircleTwoTone,
  ReloadOutlined,
  RightSquareOutlined,
  SettingOutlined, ShrinkOutlined,
  TableOutlined,
  ToolOutlined
} from "@ant-design/icons";
import React, {ReactNode} from "react";
import MetaData from "@/pages/DataStudio/LeftContainer/MetaData";
import Project from "@/pages/DataStudio/LeftContainer/Project";
import Console from "@/pages/DataStudio/BottomContainer/Console";
import JobConfig from "@/pages/DataStudio/RightContainer/JobConfig";
import ExecuteConfig from "@/pages/DataStudio/RightContainer/ExecuteConfig";
import SavePoints from "@/pages/DataStudio/RightContainer/SavePoints";
import HistoryVersion from "@/pages/DataStudio/RightContainer/HistoryVersion";
import JobInfo from "@/pages/DataStudio/RightContainer/JobInfo";
import {l} from "@/utils/intl";
import {TabsPageType} from "@/pages/DataStudio/model";
import {CircleButtonProps} from "@/components/CallBackButton/CircleBtn";
import {DiffEditor} from "@monaco-editor/react";
import Result from "@/pages/DataStudio/BottomContainer/Result";

export const LeftSide = [
  {
    key: 'menu.datastudio.project',
    icon: <ConsoleSqlOutlined />,
    label: l('menu.datastudio.project'),
    children: <Project/>
  },
  {
    key: 'menu.datastudio.structure',
    icon: <TableOutlined/>,
    label: l('menu.datastudio.structure'),
    children: <div>structure</div>
  },
  {
    key: 'menu.datastudio.metadata',
    icon: <DatabaseOutlined/>,
    label: l('menu.datastudio.metadata'),
    children: <MetaData/>
  }
];

export const RightSide:TabProp[] = [
  {
    key: 'menu.datastudio.jobConfig',
    icon: <SettingOutlined/>,
    label: l('menu.datastudio.jobConfig'),
    children: <JobConfig/>,
    isShow: (type,subType) => type === TabsPageType.project&&TabsPageType.flinkSql===subType,
  },
  {
    key: 'menu.datastudio.executeConfig',
    icon: <PlayCircleOutlined/>,
    label: l('menu.datastudio.executeConfig'),
    children: <ExecuteConfig/>,
    isShow: (type,subType) => type === TabsPageType.project&&TabsPageType.flinkSql===subType,
  },
  {
    key: 'menu.datastudio.savePoint',
    icon: <FolderOutlined/>,
    label: l('menu.datastudio.savePoint'),
    children: <SavePoints/>,
    isShow: (type,subType) => type === TabsPageType.project&&TabsPageType.flinkSql===subType,
  },
  {
    key: 'menu.datastudio.historyVision',
    icon: <HistoryOutlined/>,
    label: l('menu.datastudio.historyVision'),
    children: <HistoryVersion/>,
    isShow: (type,subType) => type === TabsPageType.project&&TabsPageType.flinkSql===subType,
  }, {
    key: 'menu.datastudio.jobInfo',
    icon: <InfoCircleOutlined/>,
    label: l('menu.datastudio.jobInfo'),
    children: <JobInfo/>,
    isShow: type => type !== TabsPageType.None,
  }
];

export const LeftBottomSide = [
  {
    key: 'menu.datastudio.console',
    icon: <RightSquareOutlined/>,
    label: l('menu.datastudio.console'),
    children: <Console/>
  },
  {
    key: 'menu.datastudio.result',
    icon: <MonitorOutlined />,
    label: l('menu.datastudio.result'),
    children: <Result/>
  },
  {
    key: 'menu.datastudio.bi',
    icon: <BarChartOutlined />,
    label: l('menu.datastudio.bi'),
  },
  {
    key: 'menu.datastudio.lineage',
    icon: <ApartmentOutlined />,
    label: l('menu.datastudio.lineage'),
  },
  {
    key: 'menu.datastudio.process',
    icon: <DesktopOutlined />,
    label: l('menu.datastudio.process'),
  },
  {
    key: 'menu.datastudio.history',
    icon: <CalendarOutlined/>,
    label: l('menu.datastudio.history'),
  },
  {
    key: 'menu.datastudio.table-data',
    icon: <TableOutlined />,
    label: l('menu.datastudio.table-data'),
  },
  {
    key: 'menu.datastudio.tool',
    icon: <ToolOutlined />,
    label: l('menu.datastudio.tool'),
  }
]

export const LeftBottomMoreTabs:{[c:string]:TabProp[]} = {
  'menu.datastudio.tool':[
    {
      key: 'menu.datastudio.tool.text-comparison',
      icon: <ToolOutlined />,
      label: l('menu.datastudio.tool.text-comparison'),
      children: <DiffEditor height={"100%"} options={{
        readOnly: false,
        originalEditable: true,
        selectOnLineNumbers: true,
        lineDecorationsWidth: 20,
        mouseWheelZoom: true,
        automaticLayout:true,
        scrollBeyondLastLine: false,
        scrollbar: {
          useShadows: false,
          verticalScrollbarSize: 8,
          horizontalScrollbarSize: 8,
          arrowSize: 30,
        }
      }} language={"text"}  />
    },{
      key: 'menu.datastudio.tool.datax2',
      icon: <ToolOutlined />,
      label: ('menu.datastudio.tool.datax2'),
      children: <div>datax2</div>
    },
  ] , 'menu.datastudio.tool2':[
    {
      key: 'menu.datastudio.tool.text-comparison',
      icon: <ToolOutlined />,
      label: ('menu.datastudio.tool.text-comparison'),
      children: <DiffEditor height={"95%"} options={{
        readOnly: true,
        selectOnLineNumbers: true,
        lineDecorationsWidth: 20,
        mouseWheelZoom: true,
        automaticLayout:true,
      }} language={"sql"} original={""} modified={""}/>
    },{
      key: 'menu.datastudio.tool.datax2',
      icon: <ToolOutlined />,
      label: ('menu.datastudio.tool.datax2'),
      children: <div>datax2</div>
    },
  ]
}

// btn route
export const BtnRoute: { [c:string]:CircleButtonProps[] } = {
  'menu.datastudio.metadata':[
    {
      icon: <ReloadOutlined />,
      title: l('button.refresh'),
      onClick: ()=>{}
    }
  ],
  'menu.datastudio.project':[
    {
      icon: <PlusCircleOutlined />,
      title: l('right.menu.createRoot'),
      key: 'right.menu.createRoot',
      onClick: ()=>{}
    },
    {
      icon: <ArrowsAltOutlined />,
      title: l('button.expand-all'),
      key: 'button.expand-all',
      onClick: ()=>{}
    },
    {
      icon: <ShrinkOutlined />,
      title: l('button.collapse-all'),
      key: 'button.collapse-all',
      onClick: ()=>{}
    }
  ]
}

export type TabProp ={
  key:string;
  icon: ReactNode;
  label: string;
  children: ReactNode;
  isShow?:(type:TabsPageType,subType?:string)=>boolean;
}
