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

import { queryDsConfig } from '@/pages/SettingCenter/GlobalSetting/service';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { createModelTypes } from '@/utils/modelUtils';
import { Effect } from '@@/plugin-dva/types';
import { Reducer } from 'umi';

export type ConfigStateType = {
  dsConfig: BaseConfigProperties[];
};

export type ConfigModelType = {
  namespace: string;
  state: ConfigStateType;
  effects: {
    queryDsConfig: Effect;
  };
  reducers: {
    saveDsConfig: Reducer<ConfigStateType>;
  };
};

const ConfigModel: ConfigModelType = {
  namespace: 'Config',
  state: {
    dsConfig: []
  },

  effects: {
    *queryDsConfig({ payload }, { call, put }) {
      const response: BaseConfigProperties[] = yield call(queryDsConfig, payload);
      yield put({
        type: 'saveDsConfig',
        payload: response || []
      });
    }
  },

  reducers: {
    saveDsConfig(state, { payload }) {
      return {
        ...state,
        dsConfig: payload
      };
    }
  }
};

export const [CONFIG_MODEL, CONFIG_MODEL_ASYNC] = createModelTypes(ConfigModel);

export default ConfigModel;
