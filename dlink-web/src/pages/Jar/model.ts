import {Effect, Reducer} from "umi";
import {JarTableListItem} from "@/pages/Jar/data";

export type JarStateType = {
  jars:JarTableListItem[],
};

export type ModelType = {
  namespace: string;
  state: JarStateType;
  effects: {
  };
  reducers: {
    saveJars: Reducer<JarStateType>;
  };
};

const JarModel: ModelType = {
  namespace: 'Jar',
  state: {
    jars:[],
  },

  effects: {

  },

  reducers: {
    saveJars(state, {payload}) {
      return {
        ...state,
        jars: payload,
      };
    },

  },
};

export default JarModel;
