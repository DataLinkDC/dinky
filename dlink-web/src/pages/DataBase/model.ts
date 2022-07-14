import type { Reducer} from "umi";

export type DataBaseType = {
  id: number,
  name: string,
  alias: string,
  groupName: string,
  type: string,
  url: string,
  username: string,
  password: string,
  note: string,
  dbVersion: string,
  status: boolean,
  healthTime: Date,
  heartbeatTime: Date,
  enabled: boolean,
  createTime: Date,
  updateTime: Date,
};

export type DataStateType = {
  database?: DataBaseType[];
}

export type DataBaseModelType = {
  namespace: string;
  state: DataStateType;
  effects: {
  };
  reducers: {
    saveDataBase: Reducer<DataStateType>;
  };
};

const DataBaseModel: DataBaseModelType = {
  namespace: 'DataBase',
  state: {
    database:[],
  },

  effects: {

  },

  reducers: {
    saveDataBase(state, {payload}) {
      return {
        ...state,
        database: [...payload],
      };
    },

  },
};

export default DataBaseModel;
