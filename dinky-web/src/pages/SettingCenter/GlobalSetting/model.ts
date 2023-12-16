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

import { queryDsConfig } from '@/pages/SettingCenter/GlobalSetting/service';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { createModelTypes } from '@/utils/modelUtils';
import { Effect } from '@@/plugin-dva/types';
import { Reducer } from 'umi';

const SYS_CONFIG = 'SysConfig';

export type SysConfigStateType = {
  dsConfig: BaseConfigProperties[];
  enabledDs: boolean;
};

export type ConfigModelType = {
  namespace: string;
  state: SysConfigStateType;
  effects: {
    queryDsConfig: Effect;
  };
  reducers: {
    saveDsConfig: Reducer<SysConfigStateType>;
    updateEnabledDs: Reducer<SysConfigStateType>;
  };
};

const ConfigModel: ConfigModelType = {
  namespace: SYS_CONFIG,
  state: {
    dsConfig: [],
    enabledDs: false
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
    }
  },

  reducers: {
    saveDsConfig(state, { payload }) {
      return {
        ...state,
        dsConfig: payload
      };
    },
    updateEnabledDs(state, { payload }) {
      return {
        ...state,
        enabledDs: payload
      };
    }
  }
};

export const [CONFIG_MODEL, CONFIG_MODEL_ASYNC] = createModelTypes(ConfigModel);

export default ConfigModel;
