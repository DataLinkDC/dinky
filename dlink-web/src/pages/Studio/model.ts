import {Effect, Reducer} from "umi";
import {fakeSubmitForm} from "@/pages/Demo/FormStepForm/service";

export type StateType = {
  current?: string;
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
    current: 'info',
    sql: '',
  },

  effects: {
    *executeSql({ payload }, { call, put }) {
      yield call(fakeSubmitForm, payload);
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
      return {
        ...state,
        sql: payload,
      };
    },
  },
};

export default Model;
