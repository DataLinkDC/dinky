import {Effect} from "@umijs/max";
import {Reducer} from "@@/plugin-dva/types";
import {getDataBase} from "@/pages/DataStudio/LeftContainer/MetaData/service";
import React, {ReactNode} from "react";

export type SqlMetaData = {
  statement?: string,
  metaData?: MetaData[],
};
export type MetaData = {
  table: string,
  connector: string,
  columns: Column[],
};
export type Column = {
  name: string,
  type: string,
};
export type ClusterType = {
  id: number,
  name: string,
  type: string,
  hosts: string,
  jobManagerHost: string,
  status: number,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
}
export type ClusterConfigurationType = {
  id: number,
  name: string,
  type: string,
  config: any,
  available: boolean,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
}
type TargetKey = React.MouseEvent | React.KeyboardEvent | number;

export type DataBaseType = {
  id: number,
  name: string,
  groupName: string,
  type: string,
  url: string,
  username: string,
  password: string,
  note: string,
  dbVersion: string,
  status: boolean,
  healthTime: Date,
  heartbeatTime: Date,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};

export type EnvType = {
  id?: number,
  name?: string,
  fragment?: boolean,
};

export type TaskType = {
  id?: number,
  catalogueId?: number,
  name?: string,
  dialect?: string,
  type?: string,
  checkPoint?: number,
  savePointStrategy?: number,
  savePointPath?: string,
  parallelism?: number,
  fragment?: boolean,
  statementSet?: boolean,
  batchModel?: boolean,
  config?: [],
  clusterId?: any,
  clusterName?: string,
  clusterConfigurationId?: number,
  clusterConfigurationName?: string,
  databaseId?: number,
  databaseName?: string,
  jarId?: number,
  envId?: number,
  jobInstanceId?: number,
  note?: string,
  enabled?: boolean,
  createTime?: Date,
  updateTime?: Date,
  statement?: string,
  session: string;
  maxRowNum: number;
  jobName: string;
  useResult: boolean;
  useChangeLog: boolean;
  useAutoCancel: boolean;
  useSession: boolean;
};

export type ConsoleType = {
  result: {};
  chart: {};
}

export type TabsItemType = {
  label: string;
  key: number,
  value: string;
  icon: any;
  closable: boolean;
  path: string[];
  task?: TaskType;
  console: ConsoleType;
  monaco?: any;
  isModified: boolean;
  sqlMetaData?: SqlMetaData;
  metaStore?: MetaStoreCatalogType[];
  children:ReactNode
}

export type TabsType = {
  activeKey: number;
  panes: TabsItemType[];
}

export type ConnectorType = {
  tablename: string;
}

export type SessionType = {
  session?: string;
  sessionConfig?: {
    type?: string;
    clusterId?: number;
    clusterName?: string;
    address?: string;
  }
  createUser?: string;
  createTime?: string;
  connectors: ConnectorType[];
}

export type MetaStoreCatalogType = {
  name: string;
  databases: MetaStoreDataBaseType[];
}

export type MetaStoreDataBaseType = {
  name: string;
  tables: MetaStoreTableType[];
  views: string[];
  functions: string[];
  userFunctions: string[];
  modules: string[];
}

export type MetaStoreTableType = {
  name: string;
  columns: MetaStoreColumnType[];
}

export type MetaStoreColumnType = {
  name: string;
  type: string;
}

export type container = {
  selectKey: string;
  height: number | string;
  width: number | string;
}

export type StateType = {
  isFullScreen: boolean;
  leftContainer: container;
  rightContainer: container;
  bottomContainer: container;
  database: DataBaseType[];
  tabs: TabsType;
};
export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {};
  reducers: {
    updateSelectLeftKey: Reducer<StateType>;
    updateLeftWidth: Reducer<StateType>;
    updateSelectRightKey: Reducer<StateType>;
    updateRightWidth: Reducer<StateType>;
    updateSelectBottomKey: Reducer<StateType>;
    updateBottomHeight: Reducer<StateType>;
    saveDataBase: Reducer<StateType>;
    updateTabsActiveKey: Reducer<StateType>;
    closeTab: Reducer<StateType>;
    addTab: Reducer<StateType>;
  };
};
const Model: ModelType = {

  namespace: 'Studio',
  state: {
    isFullScreen: false,
    leftContainer: {
      selectKey: 'project',
      height: "100%",
      width: 500,
    },
    rightContainer: {
      selectKey: 'jobConfig',
      height: "100%",
      width: 500,
    },
    bottomContainer: {
      selectKey: 'console',
      height: 400,
      width: "100%",
    }
    , database: []
    , tabs: {
      activeKey: 0,
      panes: [],
    },
  },
  effects: {},
  reducers: {
    updateSelectLeftKey(state, {payload}) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          selectKey: payload,
        }
      };
    }, updateLeftWidth(state, {payload}) {
      return {
        ...state,
        leftContainer: {
          ...state.leftContainer,
          width: payload,
        }
      };
    },
    updateSelectRightKey(state, {payload}) {
      return {
        ...state,
        rightContainer: {
          ...state.rightContainer,
          selectKey: payload,
        }
      };
    }, updateRightWidth(state, {payload}) {
      return {
        ...state,
        rightContainer: {
          ...state.rightContainer,
          width: payload,
        }
      };
    }
    , updateSelectBottomKey(state, {payload}) {
      return {
        ...state,
        bottomContainer: {
          ...state.bottomContainer,
          selectKey: payload,
        }
      };
    }, updateBottomHeight(state, {payload}) {
      return {
        ...state,
        bottomContainer: {
          ...state.bottomContainer,
          height: payload,
        }
      };
    },
    saveDataBase(state, {payload}) {
      return {
        ...state,
        database: [...payload],
      };
    },
    updateTabsActiveKey(state, {payload}) {
      return {
        ...state,
        tabs: {
          ...state.tabs,
          activeKey: payload
        },
      };
    } ,
    closeTab(state, {payload }) {
      const targetKey=payload as  TargetKey;
      const {tabs:{panes,activeKey}}= state;

      const targetIndex = panes.findIndex((pane) => pane.key === targetKey);
      const newPanes = panes.filter((pane) => pane.key !== targetKey);
      // if ( && targetKey === activeKey) {
      //   const {key} = newPanes[targetIndex === newPanes.length ? targetIndex - 1 : targetIndex];
      //   updateActiveKey(key)
      // }
      return {
        ...state,
        tabs: {
          panes:newPanes,
          activeKey: newPanes.length-1
        },
      };
    },
    addTab(state, {payload }) {
      const node=payload as  TabsItemType;
      node.key=state.tabs.panes.length
      return {
        ...state,
        tabs: {
          panes:[...state.tabs.panes,node],
          activeKey: state.tabs.panes.length
        },
      };
    }

  }
}
export default Model;
