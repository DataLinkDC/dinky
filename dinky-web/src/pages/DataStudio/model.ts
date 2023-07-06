import { Effect } from "@umijs/max";
import { Reducer } from "react";

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
  title: string;
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
  metaStore?: MetaStoreCatalogType[]
}

export type TabsType = {
  activeKey: number;
  panes?: TabsItemType[];
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

export type StateType = {
  isFullScreen: boolean;
  toolHeight?: number;
  toolRightWidth?: number;
  toolLeftWidth?: number;
  cluster?: ClusterType[];
  sessionCluster?: ClusterType[];
  clusterConfiguration?: ClusterConfigurationType[];
  database?: DataBaseType[];
  env?: EnvType[];
  currentSession?: SessionType;
  current?: TabsItemType;
  sql?: string;
  // monaco?: any;
  currentPath?: string[];
  tabs?: TabsType;
  session?: SessionType[];
  result?: {};
  rightClickMenu?: boolean;
  refs?: {
    history: any;
  };
};
export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {
    saveTask: Effect;
  };
  reducers: {

  };
};
const Model: ModelType = {

  namespace: 'Studio',
  state: {
    isFullScreen: false,
    toolHeight: 400,
    toolRightWidth: 300,
    toolLeftWidth: 300,
    cluster: [],
    sessionCluster: [],
    clusterConfiguration: [],
    database: [],
    env: [],
    currentSession: {
      connectors: [],
    },
    current: undefined,
    sql: '',
    // monaco: {},
    currentPath: ['Guide Page'],
    tabs: {
      activeKey: 0,
      panes: [],
    },
    session: [],
    result: {},
    rightClickMenu: false,
    refs: {
      history: {},
    }
  }
}

export default Model;
