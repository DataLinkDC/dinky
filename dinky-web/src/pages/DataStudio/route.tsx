import {
  ApartmentOutlined,
  AppstoreAddOutlined, BarChartOutlined, CalendarOutlined, ConsoleSqlOutlined,
  DatabaseOutlined, DesktopOutlined, FolderOutlined, HistoryOutlined, InfoCircleOutlined,
  LoadingOutlined, MonitorOutlined,
  PlayCircleOutlined, RightSquareOutlined,
  SettingOutlined,
  TableOutlined,
  ToolOutlined
} from "@ant-design/icons";
import React from "react";
import MetaData from "@/pages/DataStudio/LeftContainer/MetaData";
import Project from "@/pages/DataStudio/LeftContainer/Project";
import Console from "@/pages/DataStudio/BottomContainer/Console";

export const LeftSide = [
  {
    key: 'menu.datastudio.project',
    // icon: <AppstoreAddOutlined/>,
    icon: <ConsoleSqlOutlined />,
    label: 'menu.datastudio.project',
    children: <Project/>
  },
  {
    key: 'menu.datastudio.structure',
    icon: <TableOutlined/>,
    label: 'menu.datastudio.structure',
    children: <div>structure</div>
  },
  {
    key: 'menu.datastudio.metadata',
    icon: <DatabaseOutlined/>,
    label: 'menu.datastudio.metadata',
    children: <MetaData/>
  }
]
export const RightSide = [
  {
    key: 'menu.datastudio.jobConfig',
    icon: <SettingOutlined/>,
    label: 'menu.datastudio.jobConfig',
  },
  {
    key: 'menu.datastudio.executeConfig',
    icon: <PlayCircleOutlined/>,
    label: 'menu.datastudio.executeConfig',
  },
  {
    key: 'menu.datastudio.savePoint',
    icon: <FolderOutlined/>,
    label: 'menu.datastudio.savePoint',
  },
  {
    key: 'menu.datastudio.historyVision',
    icon: <HistoryOutlined/>,
    label: 'menu.datastudio.historyVision',
  }, {
    key: 'menu.datastudio.jobInfo',
    icon: <InfoCircleOutlined/>,
    label: 'menu.datastudio.jobInfo',
  }
]
export const LeftBottomSide = [
  {
    key: 'menu.datastudio.console',
    icon: <RightSquareOutlined/>,
    label: 'menu.datastudio.console',
    children: <Console/>
  },
  {
    key: 'menu.datastudio.result',
    icon: <MonitorOutlined />,
    label: 'menu.datastudio.result',
  },
  {
    key: 'menu.datastudio.bi',
    icon: <BarChartOutlined />,
    label: 'menu.datastudio.bi',
  },
  {
    key: 'menu.datastudio.lineage',
    icon: <ApartmentOutlined />,
    label: 'menu.datastudio.lineage',
  },
  {
    key: 'menu.datastudio.process',
    icon: <DesktopOutlined />,
    label: 'menu.datastudio.process',
  },
  {
    key: 'menu.datastudio.history',
    icon: <CalendarOutlined/>,
    label: 'menu.datastudio.history',
  },
  {
    key: 'menu.datastudio.table-data',
    icon: <TableOutlined />,
    label: 'menu.datastudio.table-data',
  },
  {
    key: 'menu.datastudio.tool',
    icon: <ToolOutlined />,
    label: 'menu.datastudio.tool',
  }
]
