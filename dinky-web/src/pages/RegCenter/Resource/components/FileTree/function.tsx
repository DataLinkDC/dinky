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

import { TagAlignCenter } from '@/components/StyledComponents';
import { ResourceInfo } from '@/types/RegCenter/data';
import { parseByteStr, renderIcon } from '@/utils/function';
import { l } from '@/utils/intl';
import { Typography } from 'antd';

const buildTitleLabel = (showCopy = false, item: ResourceInfo) => {
  const fillValue = `ADD FILE 'rs:${item.fullName}';`;

  return (
    <>
      {!item.isDirectory && showCopy ? (
        <Typography.Text
          copyable={{ text: fillValue, tooltips: l('rc.resource.copy', '', { fillValue }) }}
        >
          {item.fileName}
        </Typography.Text>
      ) : (
        item.fileName
      )}
      {!item.isDirectory && (
        <span style={{ color: 'gray' }}>
          {' '}
          &nbsp;&nbsp;{l('global.size', '', { size: parseByteStr(item.size) })}
        </span>
      )}
    </>
  );
};

/**
 * 判断目录是否为空
 * @param directory
 */
function isDirectoryEmpty(directory: ResourceInfo): boolean {
  if (!directory.children) {
    return true;
  }
  for (let child of directory.children) {
    if (child.isDirectory) {
      if (!isDirectoryEmpty(child)) {
        return false;
      }
    } else {
      return false;
    }
  }
  return true;
}

function filterEmpty(
  isFilterEmptyChildren: boolean,
  item: ResourceInfo,
  filterSuffixList: string[]
) {
  if (isFilterEmptyChildren) {
    if (item.isDirectory) {
      // 如果是目录，则递归遍历看最深处是否是空目录，是的话过滤掉
      return !isFilterEmptyChildren || !isDirectoryEmpty(item);
    } else {
      // 如果是文件，则判断该文件末尾后缀是不是 .jar 文件，是的话留着，不是的话仍然过滤
      // 获取文件后缀 , 如果没有后缀则返回空 false
      const suffix = item.fileName.split('.').reverse().pop();
      if (suffix) {
        // 如果有后缀，则判断是否在过滤列表中
        return !filterSuffixList.includes(suffix);
      }
    }
  }
  return true;
}

export const buildResourceTreeData = (
  data: ResourceInfo[] = [],
  isFilterEmptyChildren = false,
  filterSuffixList: string[] = [],
  showCopy: boolean = false
): any =>
  data
    .filter((item: ResourceInfo) => filterEmpty(isFilterEmptyChildren, item, filterSuffixList))
    .map((item: ResourceInfo) => {
      return {
        isLeaf: !item.isDirectory,
        name: item.fileName,
        parentId: item.pid,
        label: item.fileName,
        icon: <TagAlignCenter>{renderIcon(item.fileName, '.', item.isDirectory)}</TagAlignCenter>,
        path: item.fullName,
        title: buildTitleLabel(showCopy, item),
        fullInfo: item,
        key: item.id,
        id: item.id,
        children:
          item.children &&
          buildResourceTreeData(item.children, isFilterEmptyChildren, filterSuffixList, showCopy)
      };
    });
