import type { Effect, Reducer } from 'umi';


export type StateType = {
  value?: string;
};

export type ModelType = {
  namespace: string;
  state: StateType;
  effects: {
    updateSql: Effect;
  };
  reducers: {
    saveValue: Reducer<StateType>;
  };
};

const Model: ModelType = {
  namespace: 'sqlEditorValue',

  state: {
    value: 'select ',
  },

  effects: {
    *updateSql({ payload }, { call, put }) {
      yield put({
        type: 'saveValue',
        payload,
      });
    },
  },

  reducers: {
    saveValue(state, { payload }) {
      return {
        ...state,
        value: payload,
      };
    },
  },
};

export default Model;
