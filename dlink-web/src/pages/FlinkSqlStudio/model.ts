import {Effect, Reducer} from "umi";
import {executeSql} from "./service";
import {addOrUpdateData, handleAddOrUpdate, postAll, queryData} from "@/components/Common/crud";
import {Form} from "antd";

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

export type TaskType = {
  id?: number,
  catalogueId?: number,
  name?: string,
  alias?: string,
  type?: string,
  checkPoint?: number,
  savePointPath?: string,
  parallelism?: number,
  fragment?: boolean,
  clusterId?: any,
  clusterName?: string,
  note?: string,
  enabled?: boolean,
  createTime?: Date,
  updateTime?: Date,
  statement?: string,
  session:string;
  maxRowNum:number;
};

export type ConsoleType = {
  result: [];
}

export type TabsItemType = {
  title: string;
  key: number ,
  value:string;
  closable: boolean;
  task?:TaskType;
  console:ConsoleType;
}

export type TabsType = {
  activeKey: number;
  panes?: TabsItemType[];
}

export type StateType = {
  cluster?:ClusterType[];
  current: TabsItemType;
  sql?: string;
  currentPath?: string[];
  tabs:TabsType;
  session:string[];
};

export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {
    saveTask: Effect;
  };
  reducers: {
    saveSql: Reducer<StateType>;
    saveCurrentPath: Reducer<StateType>;
    saveTabs: Reducer<StateType>;
    changeActiveKey: Reducer<StateType>;
    saveTaskData: Reducer<StateType>;
    saveSession: Reducer<StateType>;
  };
};

const getClusters = async () => {
  try {
    const {datas} = await postAll('api/cluster/listEnabledAll');
    return datas;
  } catch (error) {
    console.error('获取Flink集群失败');
    return [];
  }
};


const Model: ModelType = {
  namespace: 'Studio',
  state: {
    cluster:getClusters(),
    current: {
      title: '草稿',
      key: 0 ,
      value:'',
      closable: false,
      task:{
        checkPoint: 0,
        savePointPath: '',
        parallelism: 1,
        fragment: true,
        clusterId: '0',
        maxRowNum: 100,
        session:'admin',
      },
      console:{
        result:[],
      }
    },
    sql: '',
    currentPath: [],
    tabs:{
      activeKey: 0,
      panes: [{
        title: '草稿',
        key: 0 ,
        value:'',
        closable: false,
        task:{
          checkPoint: 0,
          savePointPath: '',
          parallelism: 1,
          fragment: true,
          clusterId: '0',
          session:'admin',
          maxRowNum: 100,
        },
        console:{
          result:[],
        }
      }],
    },
    session:['admin'],
  },

  effects: {
    *saveTask({ payload }, { call, put }) {
      yield call(handleAddOrUpdate,'api/task', payload);
      yield put({
        type: 'saveTaskData',
        payload,
      });
    },
  },

  reducers: {
    saveSql(state, { payload }) {
      const tabs = state.tabs;
      let newCurrent = state.current;
      newCurrent.value=payload;
      for(let i=0;i<tabs.panes.length;i++){
        if(tabs.panes[i].key==tabs.activeKey){
          tabs.panes[i].value=payload;
          tabs.panes[i].task&&(tabs.panes[i].task.statement=payload);
        }
      }
      return {
        ...state,
        current:{
          ...newCurrent
        },
        tabs:{
          ...tabs
        },
      };
    },
    saveCurrentPath(state, { payload }) {
      return {
        ...state,
        currentPath:payload,
      };
    },
    saveTabs(state, { payload }) {
      let newCurrent = state.current;
      for(let i=0;i<payload.panes.length;i++){
        if(payload.panes[i].key==payload.activeKey){
          newCurrent=payload.panes[i];
        }
      }
      console.log(newCurrent);
      return {
        ...state,
        current:{
          ...newCurrent,
        },
        tabs:{
          ...payload,
        },
      };
    },
    changeActiveKey(state, { payload }) {
      let tabs = state.tabs;
      tabs.activeKey = payload;
      let newCurrent = state.current;
      for(let i=0;i<tabs.panes.length;i++){
        if(tabs.panes[i].key==tabs.activeKey){
          newCurrent=tabs.panes[i];
        }
      }
      return {
        ...state,
        current:{
          ...newCurrent,
        },
        tabs:{
          ...tabs,
        },
      };
    },
    saveTaskData(state, { payload }) {
      let newTabs = state.tabs;
      for(let i=0;i<newTabs.panes.length;i++){
        if(newTabs.panes[i].key==newTabs.activeKey){
          newTabs.panes[i].task=payload;
        }
      }
      return {
        ...state,
        tabs:{
          ...newTabs,
        },
      };
    },
    saveSession(state, { payload }) {
      let newSession = state.session;
      for(let i=0;i<newSession.length;i++){
        if(newSession[i].key==payload){
          return {};
        }
      }
      newSession.push(payload);
      console.log(newSession);
      return {
        ...state,
        session:newSession,
      };
    },
  },
};

export default Model;
