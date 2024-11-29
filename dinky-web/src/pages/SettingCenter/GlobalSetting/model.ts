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
  queryDsConfig,
  queryResourceConfig,
  queryTaskOwnerLockingStrategy
} from '@/pages/SettingCenter/GlobalSetting/service';
import {
  BaseConfigProperties,
  GLOBAL_SETTING_KEYS,
  TaskOwnerLockingStrategy
} from '@/types/SettingCenter/data.d';
import { createModelTypes } from '@/utils/modelUtils';
import { Effect } from '@@/plugin-dva/types';
import { Reducer } from 'umi';

const SYS_CONFIG = 'SysConfig';

export type SysConfigStateType = {
  dsConfig: BaseConfigProperties[];
  enabledDs: boolean;
  enableResource: boolean;
  resourcePhysicalDelete: boolean;
  taskOwnerLockingStrategy: TaskOwnerLockingStrategy;
};

export type ConfigModelType = {
  namespace: string;
  state: SysConfigStateType;
  effects: {
    queryDsConfig: Effect;
    queryResourceConfig: Effect;
    queryTaskOwnerLockingStrategy: Effect;
  };
  reducers: {
    saveDsConfig: Reducer<SysConfigStateType>;
    updateEnabledDs: Reducer<SysConfigStateType>;
    updateResourcePhysicalDelete: Reducer<SysConfigStateType>;
    updateEnableResource: Reducer<SysConfigStateType>;
    updateTaskOwnerLockingStrategy: Reducer<SysConfigStateType>;
  };
};

const ConfigModel: ConfigModelType = {
  namespace: SYS_CONFIG,
  state: {
    dsConfig: [],
    enabledDs: false,
    enableResource: false,
    resourcePhysicalDelete: false,
    taskOwnerLockingStrategy: TaskOwnerLockingStrategy.ALL
  },

  effects: {
    *queryTaskOwnerLockingStrategy({}, { call, put }) {
      const response: BaseConfigProperties[] = yield call(queryTaskOwnerLockingStrategy);
      if (response && response.length > 0) {
        const taskOwnerLockingStrategy = response.find(
          (item) => item.key === GLOBAL_SETTING_KEYS.SYS_ENV_SETTINGS_TASK_OWNER_LOCK_STRATEGY
        );

        yield put({
          type: 'updateTaskOwnerLockingStrategy',
          payload: taskOwnerLockingStrategy
            ? taskOwnerLockingStrategy.value
            : TaskOwnerLockingStrategy.ALL
        });
      }
    },
    *queryDsConfig({}, { call, put }) {
      const response: BaseConfigProperties[] = yield call(queryDsConfig);
      yield put({
        type: 'saveDsConfig',
        payload: response || []
      });
      if (response && response.length > 0) {
        const enabledDs = response.some(
          (item: BaseConfigProperties) =>
            item.key === GLOBAL_SETTING_KEYS.SYS_DOLPHINSETTINGS_ENABLE && item.value == true
        );
        yield put({
          type: 'updateEnabledDs',
          payload: enabledDs
        });
      }
    },
    *queryResourceConfig({}, { call, put }) {
      const response: BaseConfigProperties[] = yield call(queryResourceConfig);
      yield put({
        type: 'saveDsConfig',
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

        const physicalDelete = response.some(
          (item: BaseConfigProperties) =>
            item.key === GLOBAL_SETTING_KEYS.SYS_RESOURCE_SETTINGS_BASE_PHYSICAL_DELETION &&
            item.value == true
        );
        yield put({
          type: 'updateResourcePhysicalDelete',
          payload: physicalDelete
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
    },
    updateTaskOwnerLockingStrategy(state, { payload }) {
      return {
        ...state,
        taskOwnerLockingStrategy: payload
      };
    },
    updateEnableResource(state, { payload }) {
      return {
        ...state,
        enableResource: payload
      };
    },
    updateResourcePhysicalDelete(state, { payload }) {
      return {
        ...state,
        resourcePhysicalDelete: payload
      };
    }
  }
};

export const [CONFIG_MODEL, CONFIG_MODEL_ASYNC] = createModelTypes(ConfigModel);

export default ConfigModel;
