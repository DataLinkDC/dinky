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

import { showAlertGroup, showAlertInstance } from '@/pages/RegCenter/Alert/AlertGroup/service';
import { Alert } from '@/types/RegCenter/data.d';
import { Reducer } from 'umi';

import { createModelTypes } from '@/utils/modelUtils';
import { Effect } from '@@/plugin-dva/types';

export type AlertStateType = {
  instance: Alert.AlertInstance[];
  group: Alert.AlertGroup[];
};

export type AlertModelType = {
  namespace: string;
  state: AlertStateType;
  effects: {
    queryInstance: Effect;
    queryAlertGroup: Effect;
  };
  reducers: {
    saveInstance: Reducer<AlertStateType>;
    saveGroup: Reducer<AlertStateType>;
  };
};

const AlertModel: AlertModelType = {
  namespace: 'Alert',
  state: {
    instance: [],
    group: []
  },

  effects: {
    *queryInstance({}, { call, put }) {
      const data: Alert.AlertInstance[] = yield call(showAlertInstance);
      yield put({ type: 'saveInstance', payload: data });
    },
    *queryAlertGroup({}, { call, put }) {
      const data: Alert.AlertGroup[] = yield call(showAlertGroup);
      yield put({ type: 'saveGroup', payload: data });
    }
  },

  reducers: {
    saveInstance(state, { payload }) {
      return {
        ...state,
        instance: payload
      };
    },
    saveGroup(state, { payload }) {
      return {
        ...state,
        group: payload
      };
    }
  }
};

export const [ALERT_MODEL, ALERT_MODEL_ASYNC] = createModelTypes(AlertModel);

export default AlertModel;
