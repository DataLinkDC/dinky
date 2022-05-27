import type {Effect, Reducer} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {handleAddOrUpdate} from "@/components/Common/crud";
import {AnyAction} from "redux";

export type TaskType = {
  id?: number,
  catalogueId?: number,
  name?: string,
  alias?: string,
  dialect?: string,
  type?: string,
  checkPoint?: number,
  savePointStrategy?: number,
  savePointPath?: string,
  parallelism?: number,
  fragment?: boolean,
  statementSet?: boolean,
  batchModel?: boolean,
  config?: [],
  clusterId?: any,
  clusterName?: string,
  clusterConfigurationId?: number,
  clusterConfigurationName?: string,
  databaseId?: number,
  databaseName?: string,
  jarId?: number,
  envId?: number,
  jobInstanceId?: number,
  note?: string,
  enabled?: boolean,
  createTime?: Date,
  updateTime?: Date,
  statement?: string,
  session: string;
  maxRowNum: number;
  jobName: string;
  useResult: boolean;
  useChangeLog: boolean;
  useAutoCancel: boolean;
  useSession: boolean;
};

export type TaskModelType = {
  namespace: string;
  effects: {
    saveTask: Effect;
  };
  reducers: {
    changeTaskStep: Reducer<StateType>;
    saveTaskData: Reducer<StateType>;
  };
};

const TaskModel:TaskModelType = {
  namespace: 'Task',
  state: {
    env: [],
  },
  effects: {
    *saveTask({payload}, {call, put}) {
      const para = payload;
      para.configJson = JSON.stringify(payload.config);
      yield call(handleAddOrUpdate, 'api/task', para);
      yield put({
        type: 'saveTaskData',
        payload,
      });
    },
  },

  reducers: {
    saveTaskData(state, {payload}) {
      const newTabs = state.tabs;
      let newCurrent = state.current;
      for (let i = 0; i < newTabs.panes.length; i++) {
        if (newTabs.panes[i].key == payload.key) {
          newTabs.panes[i].task = payload;
          newTabs.panes[i].isModified = false;
          if(newCurrent.key == payload.key){
            newCurrent = newTabs.panes[i];
          }
        }
      }
      return {
        ...state,
        current: {...newCurrent},
        tabs: {...newTabs},
      };
    },

    changeTaskStep(state, {payload}) {
      const newTabs:any = state!.tabs;
      let newCurrent:any = state!.current;
      for (let i = 0; i < newTabs.panes.length; i++) {
        if (newTabs.panes[i].task.id == payload.id) {
          newTabs.panes[i].task.step = payload.step;
          if(newCurrent.key == newTabs.panes[i].key){
            newCurrent = newTabs.panes[i];
          }
        }
      }
      return {
        ...state,
        current: {...newCurrent},
        tabs: {...newTabs},
      };
    },
  },
}

export default TaskModel;
