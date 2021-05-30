import {Effect, Reducer} from "umi";
import {executeSql} from "./service";

export type CatalogueType = {
  id?: number;
  taskId?: number;
  sql?: string;
  clusterId?: number;
}

export type StateType = {
  current?: number;
  catalogue: CatalogueType[];
  sql?: string;
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



const Model: ModelType = {
  namespace: 'Studio',
  state: {
    current: 0,
    catalogue: [{
      sql: '',
    }],
    sql: '',
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
        catalogue:catalogues,
      };
    },
  },
};

export default Model;
