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
  id: number,
  catalogueId: number,
  name: string,
  alias: string,
  type: string,
  checkPoint: number,
  savePointPath: string,
  parallelism: number,
  fragment: boolean,
  clusterId: number,
  clusterName: string,
  note: string,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
  statement: string,
};

export type TabsItemType = {
  title: string;
  key: number ,
  value:string;
  closable: boolean;
  task?:TaskType;
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
  };
};

const getClusters = async () => {
  try {
    const msg = await postAll('api/cluster/listEnabledAll');
    let data:any= [];
    msg.then(value=>{
      data = value.datas;
    });
    console.log(data);
    return data;
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
      }],
    },
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
      /*let newCurrent = state.current;
      for(let i=0;i<payload.tabs.panes.length;i++){
        if(payload.tabs.panes[i].key==payload.tabs.activeKey){
          newCurrent=payload.tabs.panes[i];
        }
      }*/
      return {
        ...state,
        current:{
          ...payload.current,
        },
        tabs:{
          ...payload.tabs,
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
          newTabs.panes[i]={
            ...payload
          };
        }
      }
      return {
        ...state,
        tabs:{
          ...newTabs,
        },
      };
    },
  },
};

export default Model;
