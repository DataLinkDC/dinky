import {
  AppstoreAddOutlined,
  DatabaseOutlined, FolderOutlined, HistoryOutlined, InfoCircleOutlined,
  PlayCircleOutlined, RightSquareOutlined,
  SettingOutlined,
  TableOutlined
} from "@ant-design/icons";
import React from "react";
import MetaData from "@/pages/DataStudio/LeftContainer/MetaData";

export const LeftSide = [
  {
    key: 'menu.datastudio.project',
    icon: <AppstoreAddOutlined/>,
    label: 'menu.datastudio.project',
    children: <div>project</div>
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
  }
]
