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

import { CircleDataStudioButtonProps } from '@/components/CallBackButton/CircleBtn';
import { l } from '@/utils/intl';
import {
  ArrowsAltOutlined,
  EnvironmentOutlined,
  PlusCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
  ShrinkOutlined
} from '@ant-design/icons';
import { createContext, useContext, useReducer } from 'react';

export const BtnRoute: { [c: string]: CircleDataStudioButtonProps[] } = {
  'menu.datastudio.datasource': [
    {
      icon: <PlusOutlined />,
      title: l('button.create'),
      onClick: () => {}
    },
    {
      icon: <ReloadOutlined />,
      title: l('button.refresh'),
      onClick: () => {}
    }
  ],
  'menu.datastudio.catalog': [
    {
      icon: <ReloadOutlined />,
      title: l('button.refresh'),
      onClick: () => {}
    }
  ],
  'menu.datastudio.project': [
    {
      icon: <PlusCircleOutlined />,
      title: l('right.menu.createRoot'),
      key: 'right.menu.createRoot',
      onClick: () => {}
    },
    {
      icon: <ArrowsAltOutlined />,
      title: l('button.expand-all'),
      key: 'button.expand-all',
      onClick: () => {}
    },
    {
      icon: <ShrinkOutlined />,
      title: l('button.collapse-all'),
      key: 'button.collapse-all',
      onClick: () => {}
    },
    {
      icon: <EnvironmentOutlined />,
      title: l('button.position'),
      key: 'button.position',
      onClick: () => {}
    }
  ]
};

type BtnAction = {
  type: 'change';
  selectKey: string;
  payload: CircleDataStudioButtonProps[];
};

export const BtnContext = createContext(BtnRoute);

const BtnDispatchContext = createContext((params: BtnAction) => {});

export function useTasksDispatch() {
  return useContext(BtnDispatchContext);
}

function BtnReducer(state = BtnRoute, action: BtnAction) {
  switch (action.type) {
    case 'change': {
      return { ...state, [action.selectKey]: action.payload };
    }
    default:
      return { ...state };
  }
}

// @ts-ignore
export function BtnProvider({ children }) {
  const [btn, dispatch] = useReducer(BtnReducer, BtnRoute);

  return (
    <BtnContext.Provider value={btn}>
      <BtnDispatchContext.Provider value={dispatch}>{children}</BtnDispatchContext.Provider>
    </BtnContext.Provider>
  );
}
