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

import { folderSeparator, renderIcon } from '@/utils/function';

/**
 * build tree data
 * @param data
 * @returns {any}
 */
export const buildTreeData = (data: any): any =>
  data?.map((item: any) => {
    // build key
    let buildKey = item.path + folderSeparator() + item.name;

    // if has children , recursive build
    if (item.children) {
      return {
        isLeaf: !item.leaf,
        name: item.name,
        parentId: item.path,
        icon: renderIcon(item.name, '.', item.leaf),
        content: item.content,
        path: item.path,
        title: item.name,
        key: buildKey,
        children: buildTreeData(item.children)
      };
    }
    return {
      isLeaf: !item.leaf,
      name: item.name,
      parentId: item.path,
      icon: renderIcon(item.name, '.', item.leaf),
      content: item.content,
      path: item.path,
      title: item.name,
      key: buildKey
    };
  });
