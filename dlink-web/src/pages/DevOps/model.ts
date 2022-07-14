import type { Reducer} from "umi";

export type EnvType = {
  id?: number,
  name?: string,
  alias?: string,
  fragment?: boolean,
};

export type EnvStateType = {
  env?: EnvType[];
}

export type EnvModelType = {
  namespace: string;
  state: EnvStateType;
  effects: {
  };
  reducers: {
    saveEnv: Reducer<EnvStateType>;
  };
};

const EnvModel: EnvModelType = {
  namespace: 'Env',
  state: {
    env: [],
  },

  effects: {

  },

  reducers: {
    saveEnv(state, {payload}) {
      return {
        ...state,
        env: [...payload],
      };
    },
  },
};

export default EnvModel;
