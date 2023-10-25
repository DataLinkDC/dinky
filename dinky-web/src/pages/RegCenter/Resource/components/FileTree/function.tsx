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

import { ResourceInfo } from '@/types/RegCenter/data';
import { parseByteStr, renderIcon } from '@/utils/function';
import { l } from '@/utils/intl';

const buildTitleLabel = (item: ResourceInfo) => {
  return (
    <>
      {item.fileName}
      {!item.isDirectory && (
        <span style={{ color: 'gray' }}>
          {' '}
          &nbsp;&nbsp;{l('global.size', '', { size: parseByteStr(item.size) })}
        </span>
      )}
    </>
  );
};

export const buildResourceTreeData = (data: ResourceInfo[]): any =>
  data.map((item: ResourceInfo) => {
    return {
      isLeaf: !item.isDirectory,
      name: item.fileName,
      parentId: item.pid,
      label: item.fullName + '/' + item.fileName,
      icon: renderIcon(item.fileName, '.', item.isDirectory),
      path: item.fullName,
      title: buildTitleLabel(item),
      fullInfo: item,
      key: item.id,
      id: item.id,
      children: item.children && buildResourceTreeData(item.children)
    };
  });
