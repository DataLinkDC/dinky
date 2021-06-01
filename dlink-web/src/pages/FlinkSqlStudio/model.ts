import {Effect, Reducer} from "umi";
import {executeSql} from "./service";
import {message} from "antd";
import {getInfoById, handleInfo, queryData, removeData} from "@/components/Common/crud";

export type CatalogueType = {
  id?: number;
  taskId?: number;
  sql?: string;
  clusterId?: number;
}

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
  current?: number;
  cluster?:ClusterType[];
  catalogue: CatalogueType[];
  sql?: string;
  currentPath?: string[];
  tabs:TabsType;
};

export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {
    executeSql: Effect;
  };
  reducers: {
    saveSql: Reducer<StateType>;
  };
};

const getClusters = async () => {
  try {
    const msg = await queryData('api/cluster');
    return msg.data;
  } catch (error) {
    console.error('获取Flink集群失败');
    return [];
  }
};


const Model: ModelType = {
  namespace: 'Studio',
  state: {
    current: 0,
    cluster:getClusters(),
    catalogue: [{
      sql: '',
    }],
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
    }
  },

  effects: {
    *executeSql({ payload }, { call, put }) {
      yield call(executeSql, payload);
      yield put({
        type: 'saveStepFormData',
        payload,
      });
      yield put({
        type: 'saveCurrentStep',
        payload: 'result',
      });
    },
  },

  reducers: {
    saveSql(state, { payload }) {
      const catalogues = state.catalogue;
      for(let i=0;i<catalogues.length;i++){
        if(catalogues[i].id==payload.id){
          catalogues[i].sql=payload.sql;
        }
      }
      return {
        ...state,
        catalogue:{
          ...catalogues
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
      return {
        ...state,
        tabs:{
          ...payload
        },
      };
    },
    changeActiveKey(state, { payload }) {
      let tabs = state.tabs;
      tabs.activeKey = payload;
      return {
        ...state,
        tabs:{
          ...tabs,
        },
      };
    },
  },
};

export default Model;
