import {Effect, Reducer} from "umi";

export type StateType = {
  current?: string;
  data?: {
    sql: string;
  };
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
