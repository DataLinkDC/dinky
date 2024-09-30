import {ToolbarRoute} from "@/pages/DataStudioNew/Toolbar/data.d";
import {
  ApartmentOutlined,
  ConsoleSqlOutlined,
  DatabaseOutlined,
  FolderOutlined,
  FunctionOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  InsertRowRightOutlined,
  MonitorOutlined,
  RightSquareOutlined,
  SettingOutlined,
  TableOutlined
} from "@ant-design/icons";
import React from "react";
import Project from "@/pages/DataStudio/LeftContainer/Project";
import Catalog from "@/pages/DataStudio/LeftContainer/Catalog";
import DataSource from '@/pages/DataStudio/LeftContainer/DataSource';
import GlobalVariable from "@/pages/DataStudio/LeftContainer/GlobaleVar";


export const toolbarRoutes: ToolbarRoute[] =
  [{
    key: 'project',
    title: '项目',
    icon: <ConsoleSqlOutlined/>,
    position: 'leftTop',
    content: <Project/>
  }, {
    key: 'catalog',
    title: 'Catalog',
    icon: <TableOutlined/>,
    position: 'leftTop',
    content: <Catalog/>
  }
    , {
    key: 'datasource',
    title: '数据源',
    icon: <DatabaseOutlined/>,
    position: 'leftTop',
    content: <DataSource/>
  }, {
    key: 'function',
    title: '函数',
    icon: <FunctionOutlined/>,
    position: 'leftTop',
    content: <GlobalVariable/>
  }, {
    key: 'jobConfig',
    title: '作业配置',
    icon: <SettingOutlined/>,
    position: 'right',
    content: <>这是测试界面</>
  }, {
    key: 'previewConfig',
    title: '预览配置',
    icon: <InsertRowRightOutlined/>,
    position: 'right',
    content: <>这是测试界面</>

  }, {
    key: 'savePoint',
    title: '保存点',
    icon: <FolderOutlined/>,
    position: 'right',
    content: <>这是测试界面</>
  }, {
    key: 'history',
    title: '历史版本',
    icon: <HistoryOutlined/>,
    position: 'right',
    content: <>这是测试界面</>
  }, {
    key: 'jobInfo',
    title: '作业信息',
    icon: <InfoCircleOutlined/>,
    position: 'right',
    content: <>这是测试界面</>
  }, {
    key: 'console',
    title: '控制台',
    icon: <RightSquareOutlined/>,
    position: 'leftBottom',
    content: <>这是测试界面</>
  }, {
    key: 'result',
    title: '结果',
    icon: <MonitorOutlined/>,
    position: 'leftBottom',
    content: <>这是测试界面</>
  }, {
    key: 'lineage',
    title: '血缘',
    icon: <ApartmentOutlined/>,
    position: 'leftBottom',
    content: <>这是测试界面</>
  }
  ]

export const leftDefaultShowTab:ToolbarRoute = toolbarRoutes[0]
