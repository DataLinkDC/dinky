import {Effect, Reducer} from "umi";
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";
import {AlertGroupTableListItem} from "@/pages/AlertGroup/data";

export type AlertStateType = {
  instance:AlertInstanceTableListItem[],
  group:AlertGroupTableListItem[]
};

export type AlertModelType = {
  namespace: string;
  state: AlertStateType;
  effects: {
  };
  reducers: {
    saveInstance: Reducer<AlertStateType>;
    saveGroup: Reducer<AlertStateType>;
  };
};

const AlertModel: AlertModelType = {
  namespace: 'Alert',
  state: {
    instance:[],
    group:[],
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
    saveGroup(state, {payload}) {
      return {
        ...state,
        group: payload,
      };
    },
  },
};

export default AlertModel;
