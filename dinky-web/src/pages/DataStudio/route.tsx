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
    key: 'jobConfig',
    icon: <SettingOutlined/>,
    label: '作业配置',
  },
  {
    key: 'executeConfig',
    icon: <PlayCircleOutlined/>,
    label: '执行配置',
  },
  {
    key: 'savePoint',
    icon: <FolderOutlined/>,
    label: '保存点',
  },
  {
    key: 'versionHistory',
    icon: <HistoryOutlined/>,
    label: '版本历史',
  }, {
    key: 'jobInfo',
    icon: <InfoCircleOutlined/>,
    label: '作业信息',
  }
]
export const LeftBottomSide = [
  {
    key: 'console',
    icon: <RightSquareOutlined/>,
    label: '控制台',
  }
]
