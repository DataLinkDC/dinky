/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { getMetricsLayout } from '@/pages/DevOps/JobDetail/JobMetrics/service';
import { Effect, Reducer } from '@@/plugin-dva/types';

export type MetricsType = {
  jobMetricsTarget: Record<string, JobMetricsItem[]>;
  layoutName: string;
};

export type DevopsType = {
  jobInfoDetail: any;
  metrics: MetricsType;
};

export type DevopsModelType = {
  namespace: string;
  state: DevopsType;
  effects: {
    queryMetricsTarget: Effect;
  };

  reducers: {
    updateMetricsTarget: Reducer;
    setMetricsTarget: Reducer;
    setJobInfoDetail: Reducer;
  };
};

const Devops: DevopsModelType = {
  namespace: 'Devops',

  state: {
    jobInfoDetail: {},
    metrics: {
      jobMetricsTarget: {},
      layoutName: ''
    }
  },

  effects: {
    *queryMetricsTarget({ payload }, { call, put }) {
      const data: JobMetricsItem[] = yield call(getMetricsLayout, payload);
      yield put({ type: 'setMetricsTarget', payload: data });
    }
  },

  reducers: {
    updateMetricsTarget(state, { payload }) {
      state.metrics.jobMetricsTarget[payload.verticeId] = payload.data;
      return {
        ...state
      };
    },

    setMetricsTarget(state, { payload }) {
      const data: JobMetricsItem[] = payload;
      const jobMetricsTarget: any = {};
      data.forEach((metrics) => {
        if (!(metrics.vertices in jobMetricsTarget)) {
          jobMetricsTarget[metrics.vertices] = [];
        }
        jobMetricsTarget[metrics.vertices].push(metrics);
      });
      state.metrics.jobMetricsTarget = jobMetricsTarget;
      return {
        ...state
      };
    },

    setJobInfoDetail(state, { jobDetail }) {
      return {
        ...state,
        jobInfoDetail: jobDetail,
        metrics: {
          ...state.metrics,
          layoutName: `${jobDetail.instance.name}-${jobDetail.instance.taskId}`
        }
      };
    }
  }
};

export default Devops;
