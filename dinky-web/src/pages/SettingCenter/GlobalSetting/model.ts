/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import {
  queryConfigByKeyword,
  queryDsConfig,
  queryResourceConfig
} from '@/pages/SettingCenter/GlobalSetting/service';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { createModelTypes } from '@/utils/modelUtils';
import { Effect } from '@@/plugin-dva/types';
import { Reducer } from 'umi';

const SYS_CONFIG = 'SysConfig';

export type SysConfigStateType = {
  dsConfig: BaseConfigProperties[];
  metricConfig: BaseConfigProperties[];
  resourceConfig: BaseConfigProperties[];
  enabledDs: boolean;
  enableMetricMonitor: boolean;
  enableResource: boolean;
};

export type ConfigModelType = {
  namespace: string;
  state: SysConfigStateType;
  effects: {
    queryDsConfig: Effect;
    queryMetricConfig: Effect;
    queryResourceConfig: Effect;
  };
  reducers: {
    saveDsConfig: Reducer<SysConfigStateType>;
    saveMetricConfig: Reducer<SysConfigStateType>;
    saveResourceConfig: Reducer<SysConfigStateType>;
    updateEnabledDs: Reducer<SysConfigStateType>;
    updateEnableMetricMonitor: Reducer<SysConfigStateType>;
    updateEnableResource: Reducer<SysConfigStateType>;
  };
};

const ConfigModel: ConfigModelType = {
  namespace: SYS_CONFIG,
  state: {
    dsConfig: [],
    metricConfig: [],
    resourceConfig: [],
    enabledDs: false,
    enableMetricMonitor: false,
    enableResource: false
  },

  effects: {
    *queryDsConfig({ payload }, { call, put }) {
      const response: BaseConfigProperties[] = yield call(queryDsConfig, payload);
      yield put({
        type: 'saveDsConfig',
        payload: response || []
      });
      if (response && response.length > 0) {
        const enabledDs = response.some(
          (item: BaseConfigProperties) =>
            item.key === 'sys.dolphinscheduler.settings.enable' && item.value === true
        );
        yield put({
          type: 'updateEnabledDs',
          payload: enabledDs
        });
      }
    },

    *queryMetricConfig({ payload }, { call, put }) {
      console.log(payload);
      const response: BaseConfigProperties[] = yield call(queryConfigByKeyword, payload);
      yield put({
        type: 'saveMetricConfig',
        payload: response || []
      });
      console.log(response);
      if (response && response.length > 0) {
        const enableResource = response.some(
          (item: BaseConfigProperties) =>
            item.key === 'sys.metrics.settings.sys.enable' && item.value === true
        );
        yield put({
          type: 'updateEnableMetricMonitor',
          payload: enableResource
        });
      }
    },

    *queryResourceConfig({ payload }, { call, put }) {
      const response: BaseConfigProperties[] = yield call(queryResourceConfig, payload);
      yield put({
        type: 'saveResourceConfig',
        payload: response || []
      });
      if (response && response.length > 0) {
        const enableResource = response.some(
          (item: BaseConfigProperties) =>
            item.key === 'sys.resource.settings.base.enable' && item.value === true
        );
        yield put({
          type: 'updateEnableResource',
          payload: enableResource
        });
      }
    }
  },

  reducers: {
    saveDsConfig(state, { payload }) {
      return {
        ...state,
        dsConfig: payload
      };
    },
    saveMetricConfig(state, { payload }) {
      return {
        ...state,
        metricConfig: payload
      };
    },
    saveResourceConfig(state, { payload }) {
      return {
        ...state,
        resourceConfig: payload
      };
    },
    updateEnableMetricMonitor(state, { payload }) {
      return {
        ...state,
        enableMetricMonitor: payload
      };
    },
    updateEnabledDs(state, { payload }) {
      return {
        ...state,
        enabledDs: payload
      };
    },
    updateEnableResource(state, { payload }) {
      return {
        ...state,
        enableResource: payload
      };
    }
  }
};
export const [CONFIG_MODEL, CONFIG_MODEL_ASYNC] = createModelTypes(ConfigModel);

export default ConfigModel;
