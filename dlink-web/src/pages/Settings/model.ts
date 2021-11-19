import {Effect, Reducer} from "umi";

export type SettingsStateType = {
  sqlSubmitJarPath:string,
  sqlSubmitJarParas:string,
  sqlSubmitJarMainAppClass:string,
};

export type ModelType = {
  namespace: string;
  state: SettingsStateType;
  effects: {
  };
  reducers: {
    saveSettings: Reducer<SettingsStateType>;
  };
};

const SettingsModel: ModelType = {
  namespace: 'Settings',
  state: {
    sqlSubmitJarPath:'',
    sqlSubmitJarParas:'',
    sqlSubmitJarMainAppClass:'',
  },

  effects: {

  },

  reducers: {
    saveSettings(state, {payload}) {
      return {
        ...state,
        ...payload,
      };
    },

  },
};

export default SettingsModel;
