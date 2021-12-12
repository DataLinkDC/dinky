import type {Effect, Reducer} from "umi";
import {
   handleAddOrUpdate
} from "@/components/Common/crud";
import type {SqlMetaData} from "@/components/Studio/StudioEvent/data";

export type ClusterType = {
  id: number,
  name: string,
  alias: string,
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
  alias: string,
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
  alias: string,
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

export type TaskType = {
  id?: number,
  catalogueId?: number,
  name?: string,
  alias?: string,
  type?: string,
  checkPoint?: number,
  savePointStrategy?: number,
  savePointPath?: string,
  parallelism?: number,
  fragment?: boolean,
  statementSet?: boolean,
  config?: [],
  clusterId?: any,
  clusterName?: string,
  clusterConfigurationId?: number,
  clusterConfigurationName?: string,
  jarId?: number,
  note?: string,
  enabled?: boolean,
  createTime?: Date,
  updateTime?: Date,
  statement?: string,
  session: string;
  maxRowNum: number;
  jobName: string;
  useResult: boolean;
  useSession: boolean;
  useRemote: boolean;
};

export type ConsoleType = {
  result: {};
}

export type TabsItemType = {
  title: string;
  key: number,
  value: string;
  closable: boolean;
  path: string[];
  task?: TaskType;
  console: ConsoleType;
  monaco?: any;
  isModified: boolean;
  sqlMetaData?: SqlMetaData;
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
    useRemote?: boolean;
    clusterId?: number;
    clusterName?: string;
    address?: string;
  }
  createUser?: string;
  createTime?: string;
  connectors: ConnectorType[];
}

export type StateType = {
  toolHeight?: number;
  toolRightWidth?: number;
  toolLeftWidth?: number;
  cluster?: ClusterType[];
  sessionCluster?: ClusterType[];
  clusterConfiguration?: ClusterConfigurationType[];
  database?: DataBaseType[];
  currentSession?: SessionType;
  current?: TabsItemType;
  sql?: string;
  monaco?: any;
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
    saveToolHeight: Reducer<StateType>;
    saveToolRightWidth: Reducer<StateType>;
    saveToolLeftWidth: Reducer<StateType>;
    saveSql: Reducer<StateType>;
    saveCurrentPath: Reducer<StateType>;
    saveMonaco: Reducer<StateType>;
    saveSqlMetaData: Reducer<StateType>;
    saveTabs: Reducer<StateType>;
    changeActiveKey: Reducer<StateType>;
    saveTaskData: Reducer<StateType>;
    saveSession: Reducer<StateType>;
    showRightClickMenu: Reducer<StateType>;
    refreshCurrentSession: Reducer<StateType>;
    quitCurrentSession: Reducer<StateType>;
    saveResult: Reducer<StateType>;
    saveCluster: Reducer<StateType>;
    saveSessionCluster: Reducer<StateType>;
    saveClusterConfiguration: Reducer<StateType>;
    saveDataBase: Reducer<StateType>;
  };
};

const Model: ModelType = {
  namespace: 'Studio',
  state: {
    toolHeight: 400,
    toolRightWidth: 300,
    toolLeftWidth: 300,
    cluster: [],
    sessionCluster: [],
    clusterConfiguration: [],
    database: [],
    currentSession: {
      connectors: [],
    },
    current: {
      title: '草稿',
      key: 0,
      value: '',
      closable: false,
      path: ['草稿'],
      isModified: false,
      task: {
        jobName: '草稿',
        // type: 'standalone',
        checkPoint: 0,
        savePointStrategy: 0,
        savePointPath: '',
        parallelism: 1,
        fragment: true,
        statementSet: false,
        clusterId: 0,
        clusterName: "本地环境",
        clusterConfigurationId:undefined,
        clusterConfigurationName:undefined,
        jarId:undefined,
        maxRowNum: 100,
        config: [],
        session: '',
        alias: '草稿',
        useResult:true,
        useSession:false,
        useRemote:false,
      },
      console: {
        result: {},
      },
      monaco: {},
      sqlMetaData: undefined,
    },
    sql: '',
    monaco: {},
    currentPath: ['草稿'],
    tabs: {
      activeKey: 0,
      panes: [{
        title: '草稿',
        key: 0,
        value: '',
        closable: false,
        isModified: false,
        path: ['草稿'],
        task: {
          jobName: '草稿',
          // type: 'standalone',
          checkPoint: 0,
          savePointStrategy: 0,
          savePointPath: '',
          parallelism: 1,
          fragment: true,
          statementSet: false,
          clusterId: 0,
          clusterName: "本地环境",
          clusterConfigurationId:undefined,
          clusterConfigurationName:undefined,
          jarId:undefined,
          session: '',
          config: [],
          maxRowNum: 100,
          alias: '草稿',
          useResult:true,
          useSession:false,
          useRemote:false,
        },
        console: {
          result: {},
        },
        monaco: {},
        sqlMetaData: undefined,
      }],
    },
    session: [],
    result:{},
    rightClickMenu: false,
    refs:{
      history:{},
    }
  },

  effects: {
    * saveTask({payload}, {call, put}) {
      const para = payload;
      para.configJson = JSON.stringify(payload.config);
      yield call(handleAddOrUpdate, 'api/task', para);
      yield put({
        type: 'saveTaskData',
        payload,
      });
    },
  },

  reducers: {
    saveToolHeight(state, {payload}) {
      return {
        ...state,
        toolHeight: payload,
      };
    },saveToolRightWidth(state, {payload}) {
      return {
        ...state,
        toolRightWidth: payload,
      };
    },saveToolLeftWidth(state, {payload}) {
      return {
        ...state,
        toolLeftWidth: payload,
      };
    },
    saveSql(state, {payload}) {
      const {tabs} = state;
      const newCurrent = state.current;
      newCurrent.value = payload;
      for (let i = 0; i < tabs.panes.length; i++) {
        if (tabs.panes[i].key == tabs.activeKey) {
          tabs.panes[i].value = payload;
          tabs.panes[i].task && (tabs.panes[i].task.statement = payload);
        }
      }
      return {
        ...state,
        current: {
          ...newCurrent
        },
        tabs: {
          ...tabs
        },
      };
    },
    saveCurrentPath(state, {payload}) {
      return {
        ...state,
        currentPath: payload,
      };
    },
    saveMonaco(state, {payload}) {
      return {
        ...state,
        monaco: {
          ...payload
        },
      };
    },
    saveSqlMetaData(state, {payload}) {
      const newCurrent = state.current;
      const newTabs = state.tabs;
      if(newCurrent.key == payload.activeKey){
        newCurrent.sqlMetaData = payload.sqlMetaData;
        newCurrent.isModified = payload.isModified;
      }
      for (let i = 0; i < newTabs.panes.length; i++) {
        if (newTabs.panes[i].key == payload.activeKey) {
          newTabs.panes[i].sqlMetaData = payload.sqlMetaData;
          newTabs.panes[i].isModified = payload.isModified;
          break;
        }
      }
      return {
        ...state,
        current: newCurrent,
        tabs: newTabs,
      };
    },
    saveTabs(state, {payload}) {
      let newCurrent = state.current;
      for (let i = 0; i < payload.panes.length; i++) {
        if (payload.panes[i].key == payload.activeKey) {
          newCurrent = payload.panes[i];
        }
      }
      return {
        ...state,
        current: {
          ...newCurrent,
          isModified:false,
        },
        tabs: {
          ...payload,
        },
      };
    },
    deleteTabByKey(state, {payload}) {
      const newTabs = state.tabs;
      for (let i = 0; i < newTabs.panes.length; i++) {
        if (newTabs.panes[i].key == payload) {
          newTabs.panes.splice(i, 1);
          break;
        }
      }
      const newCurrent = newTabs.panes[newTabs.panes.length - 1];
      if (newTabs.activeKey == payload) {
        newTabs.activeKey = newCurrent.key;
      }
      return {
        ...state,
        current: {
          ...newCurrent,
        },
        tabs: {
          ...newTabs,
        },
      };
    },
    changeActiveKey(state, {payload}) {
      const {tabs} = state;
      tabs.activeKey = payload;
      let newCurrent = state.current;
      for (let i = 0; i < tabs.panes.length; i++) {
        if (tabs.panes[i].key == tabs.activeKey) {
          newCurrent = tabs.panes[i];
        }
      }
      return {
        ...state,
        current: {
          ...newCurrent,
        },
        tabs: {
          ...tabs,
        },
        currentPath: newCurrent.path,
      };
    },
    saveTaskData(state, {payload}) {
      const newTabs = state.tabs;
      for (let i = 0; i < newTabs.panes.length; i++) {
        if (newTabs.panes[i].key == newTabs.activeKey) {
          newTabs.panes[i].task = payload;
        }
      }
      return {
        ...state,
        tabs: {
          ...newTabs,
        },
      };
    },
    saveSession(state, {payload}) {
      return {
        ...state,
        session: [...payload],
      };
    },
    showRightClickMenu(state, {payload}) {
      return {
        ...state,
        rightClickMenu: payload,
      };
    },
    refreshCurrentSession(state, {payload}) {
      return {
        ...state,
        currentSession: {
          ...state?.currentSession,
          ...payload
        }
      };
    },
    quitCurrentSession(state) {
      return {
        ...state,
        currentSession: {
          connectors: [],
        }
      };
    },
    saveResult(state, {payload}) {
      return {
        ...state,
        result: {
          ...payload
        },
      };
    },
    saveCluster(state, {payload}) {
      return {
        ...state,
        cluster: payload,
      };
    },saveSessionCluster(state, {payload}) {
      return {
        ...state,
        sessionCluster: payload,
      };
    },saveClusterConfiguration(state, {payload}) {
      return {
        ...state,
        clusterConfiguration: payload,
      };
    },saveDataBase(state, {payload}) {
      return {
        ...state,
        database: payload,
      };
    },
  },
};

export default Model;
