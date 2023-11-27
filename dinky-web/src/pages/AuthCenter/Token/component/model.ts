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
  buildToken,
  showRoleList,
  showTenantList,
  showUserList
} from '@/pages/AuthCenter/Token/component/service';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { createModelTypes } from '@/utils/modelUtils';
import { Effect } from '@@/plugin-dva/types';
import { Dispatch } from '@umijs/max';
import { Reducer } from 'umi';

export type TokenStateType = {
  users: UserBaseInfo.User[];
  roles: UserBaseInfo.Role[];
  tenants: UserBaseInfo.Tenant[];
  token: string;
};

export type TokenModelType = {
  namespace: string;
  state: TokenStateType;
  effects: {
    queryUsers: Effect;
    queryRoles: Effect;
    queryTenants: Effect;
    tokenValue: Effect;
  };
  reducers: {
    saveUsers: Reducer<TokenStateType>;
    saveRoles: Reducer<TokenStateType>;
    saveTenants: Reducer<TokenStateType>;
    saveBuildToken: Reducer<TokenStateType>;
  };
};

const TokenModel: TokenModelType = {
  namespace: 'Token',
  state: {
    users: [],
    roles: [],
    tenants: [],
    token: ''
  },

  effects: {
    *queryUsers({}, { call, put }) {
      const data: UserBaseInfo.User = yield call(showUserList);
      yield put({ type: 'saveUsers', payload: data });
    },
    *queryRoles({ payload }, { call, put }) {
      const data: UserBaseInfo.Role = yield call(showRoleList, payload);
      yield put({ type: 'saveRoles', payload: data });
    },
    *queryTenants({ payload }, { call, put }) {
      const data: UserBaseInfo.Tenant = yield call(showTenantList, payload);

      yield put({ type: 'saveTenants', payload: data });
    },
    *tokenValue({ payload }, { call, put }) {
      const data: string = yield call(buildToken);
      yield put({ type: 'saveBuildToken', payload: data });
    }
  },

  reducers: {
    saveUsers(state, { payload }) {
      return {
        ...state,
        users: payload
      };
    },
    saveRoles(state, { payload }) {
      return {
        ...state,
        roles: payload
      };
    },
    saveTenants(state, { payload }) {
      return {
        ...state,
        tenants: payload
      };
    },
    saveBuildToken(state, { payload }) {
      return {
        ...state,
        token: payload
      };
    }
  }
};

export const [TOKEN_MODEL, TOKEN_MODEL_ASYNC] = createModelTypes(TokenModel);

export default TokenModel;

export const mapDispatchToProps = (dispatch: Dispatch) => ({
  queryUsers: () => dispatch({ type: TOKEN_MODEL_ASYNC.queryUsers }),
  queryRoles: (userId: number) => dispatch({ type: TOKEN_MODEL_ASYNC.queryRoles, payload: userId }),
  queryTenants: (userId: number) =>
    dispatch({ type: TOKEN_MODEL_ASYNC.queryTenants, payload: userId }),
  buildToken: () => dispatch({ type: TOKEN_MODEL_ASYNC.tokenValue })
});
