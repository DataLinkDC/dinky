import {ChartData, JobMetrics} from "@/pages/Metrics/Job/data";
import {Effect, Reducer} from "@@/plugin-dva/types";
import {getMetricsLayout} from "@/pages/DevOps/JobDetail/JobMonitor/service";

export type MonitorType = {
  chartDataList: Record<string, ChartData[]>
  jobMetricsLayout: JobMetrics[]
}

export type MonitorModelType = {
  namespace: string;
  state: MonitorType;
  effects: {
    queryMonitorLayout: Effect;
  };

  reducers: {
    updateChartDatas: Reducer;
    updateMetricsLayout: Reducer;
  }
}

const Monitor: MonitorModelType = {
  namespace: 'monitors',

  state: {
    chartDataList: {},
    jobMetricsLayout: []
  },

  effects: {
    * queryMonitorLayout({payload}, {call, put}) {
      const data: [] = yield call(getMetricsLayout, payload);
      yield put({type: 'updateMetricsLayout', payload: data});
    },
  },

  reducers: {
    updateChartDatas(state, {payload}) {
      // console.log(payload)
      if (!(payload.key in state.chartDataList)) {
        state.chartDataList[payload.key] = []
      }
      state.chartDataList[payload.key].push(payload.data)
      return state;
    },

    updateMetricsLayout(state, {payload}) {
      // console.log(payload)
      return {
        ...state,
        jobMetricsLayout: payload
      };
    },

  },

  // test(state) {
  //   console.log('test---------------------------------------');
  //   return state;
  // },
};

export default Monitor;
