import {Effect, Reducer} from "umi";
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";

export type AlertStateType = {
  instance:AlertInstanceTableListItem[],
};

export type AlertModelType = {
  namespace: string;
  state: AlertStateType;
  effects: {
  };
  reducers: {
    saveInstance: Reducer<AlertStateType>;
  };
};

const AlertModel: AlertModelType = {
  namespace: 'Alert',
  state: {
    instance:[],
  },

  effects: {

  },

  reducers: {
    saveInstance(state, {payload}) {
      return {
        ...state,
        instance: payload,
      };
    },

  },
};

export default AlertModel;
