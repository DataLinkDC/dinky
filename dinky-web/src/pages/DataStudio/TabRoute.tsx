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

import { onAdd } from '@/pages/DataStudio/BottomContainer/TableData';
import { LeftBottomKey } from '@/pages/DataStudio/data.d';
import { TaskDataType } from '@/pages/DataStudio/model';
import { Tab } from '@/pages/DataStudio/route';
import React from 'react';

export type BottomTabProps = {
  icon?: React.ReactNode;
  onAdd: (tabs: Tab[], key: string, data: TaskDataType | undefined, operator: any) => Promise<void>;
  onRemove?: (
    tabs: Tab[],
    key: string,
    data: TaskDataType | undefined,
    tabKey: string,
    refresh: any
  ) => Promise<void>;
  key: string;
};
export const BottomTabRoute: { [c: string]: BottomTabProps } = {
  [LeftBottomKey.TABLE_DATA_KEY]: {
    key: 'add table data',
    onAdd: async (tabs, key, data, operator) => {
      await onAdd(tabs, key, data, operator);
    }
  }
};
