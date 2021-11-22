import {Effect, Reducer} from "umi";

export type SettingsStateType = {
  sqlSubmitJarPath:string,
  sqlSubmitJarParas:string,
  sqlSubmitJarMainAppClass:string,
  useRestAPI:boolean,
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
    useRestAPI:true,
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
