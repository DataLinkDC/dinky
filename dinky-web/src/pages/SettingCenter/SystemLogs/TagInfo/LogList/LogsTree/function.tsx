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

import { LogInfo } from '@/types/SettingCenter/data';
import { parseByteStr, renderIcon } from '@/utils/function';
import { l } from '@/utils/intl';

const buildTitleLabel = (item: LogInfo) => {
  return (
    <>
      {item.name}
      {!item.leaf && (
        <span style={{ color: 'gray' }}>
          {' '}
          &nbsp;&nbsp;{l('global.size', '', { size: parseByteStr(item.size) })}
        </span>
      )}
    </>
  );
};

export const buildLogInfoTreeData = (data: LogInfo[]): any =>
  data.map((item: LogInfo) => {
    return {
      isLeaf: !item.leaf,
      name: item.name,
      parentId: item.parentId,
      label: item.path,
      icon: renderIcon(item.name, '.', item.leaf),
      path: item.path,
      title: buildTitleLabel(item),
      fullInfo: item,
      key: item.id ?? item.name,
      id: item.id,
      children: buildLogInfoTreeData(item.children)
    };
  });
