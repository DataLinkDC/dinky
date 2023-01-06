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


import {NameSpaceTableListItem, RoleTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {Reducer} from "@@/plugin-dva/connect";

export type NameSpaceStateType = {
  role:RoleTableListItem[],
  nameSpaces: NameSpaceTableListItem[],
};
export type NameSpaceModelType = {
  namespace: string;
  state: NameSpaceStateType;
  effects: {
  };
  reducers: {
    saveRole: Reducer<NameSpaceStateType>;
    saveNameSpace: Reducer<NameSpaceStateType>;
  };
};

const NameSpaceModel: NameSpaceModelType = {
  namespace: 'NameSpace',
  state: {
    role:[],
    nameSpaces:[],
  },

  effects: {

  },

  reducers: {
    saveRole(state, {payload}) {
      return {
        ...state,
        role: payload,
      };
    },
    saveNameSpace(state, {payload}) {
      return {
        ...state,
        nameSpaces: payload,
      };
    },
  },
};

export default NameSpaceModel;
