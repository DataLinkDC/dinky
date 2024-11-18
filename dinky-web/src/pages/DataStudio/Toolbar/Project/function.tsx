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

import { DIALECT } from '@/services/constants';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';
import { TaskOwnerLockingStrategy } from '@/types/SettingCenter/data.d';
import { Catalogue } from '@/types/Studio/data.d';
import { searchTreeNode } from '@/utils/function';
import { l } from '@/utils/intl';
import { LockTwoTone, UnlockTwoTone } from '@ant-design/icons';
import { Badge, Divider, Space, Tooltip } from 'antd';
import { Key } from 'react';
import { getTabIcon, lockTask, showAllOwners } from '@/pages/DataStudio/function';
import { assert } from '@/pages/DataStudio/utils';

/**
 * generate list of tree node from data
 * @param data
 * @param list
 */
export const generateList = (data: any, list: any[]) => {
  for (const element of data) {
    const node = element;
    const { name, id, parentId, level } = node;
    list.push({ name, id, key: id, title: name, parentId, level });
    if (node.children) {
      generateList(node.children, list);
    }
  }
  return list;
};

/**
 * get parent key of tree node by key
 * @param key
 * @param tree
 */
export const getParentKey = (key: number | string, tree: any): any => {
  let parentKey;
  for (const element of tree) {
    const node = element;
    if (node.children) {
      if (node.children.some((item: any) => item.id === key)) {
        parentKey = node.id;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};

/**
 * Obtain all parent node IDs based on a key
 * Note: Note that the data structure returned through the native backend is relatively simple, so here we directly use recursive object traversal to obtain all parent node IDs
 * @param key
 * @param data
 */
export const getAllParentIdsByOneKey = (
  key: number | string,
  data: any[] = []
): (number | string)[] => {
  let result: (number | string)[] = [];
  data.forEach((item) => {
    if (item.id === key) {
      result.push(item.id);
    } else if (item.children) {
      const parentIds = getAllParentIdsByOneKey(key, item.children);
      if (parentIds.length > 0) {
        result = result.concat(parentIds, item.id);
      }
    }
  });
  // Invert the result array so that the parent nodes are in order from the root to the direct parent node
  return result.reverse();
};

/**
 * search in tree node
 * @param tree data
 * @param data
 * @param searchValue
 * @param assetType search type 'equal' or 'contain'
 */
export function searchInTree(
  tree: any[] = [],
  data: any[],
  searchValue: string | number,
  assetType: 'equal' | 'contain'
): string[] {
  const foundKeys: string[] = [];

  /**
   * Depth-first search algorithm to find the key of the node that meets the search criteria
   * @param node tree node
   * @param path path of the node
   */
  function dfs(node: any, path: string[]) {
    if (assetType === 'equal') {
      if (node?.key && node.key === searchValue) {
        const allParentIdsByOneKey = getAllParentIdsByOneKey(node.key, data);
        allParentIdsByOneKey.forEach((key) => {
          foundKeys.push(key as string);
        });
      }
    }
    if (assetType === 'contain') {
      if (node?.name?.indexOf(searchValue) > -1) {
        const allParentIdsByOneKey = getAllParentIdsByOneKey(node.key, data);
        allParentIdsByOneKey.forEach((key) => {
          foundKeys.push(key as string);
        });
      }
    }
    if (node.children) {
      for (const child of node.children) {
        dfs(child, [...path, node.key]);
      }
    }
  }

  // Traverse all nodes in the tree to find the key of the node that meets the search criteria
  for (const node of tree) {
    dfs(node, [node.key]);
  }
  // Remove duplicate keys from the result
  return Array.from(new Set(foundKeys));
}

/**
 * get leaf key list from tree
 * @param tree
 */
export const getLeafKeyList = (tree: any[]): Key[] => {
  let leafKeyList: Key[] = [];
  for (const node of tree) {
    if (!node.isLeaf) {
      // 目录节点 || is a directory node
      leafKeyList.push(node.id); // 目录节点不需要递归 || directory nodes do not need to be recursive
      if (node.children) {
        // 目录节点的子节点需要递归 || the child nodes of the directory node need to be recursive
        leafKeyList = leafKeyList.concat(getLeafKeyList(node.children)); // 递归 || recursive
      }
    } else {
      // 非目录节点 | is not a directory node
      leafKeyList = leafKeyList.concat(getLeafKeyList(node.children)); // 递归 || recursive
    }
  }
  return leafKeyList;
};

export const buildStepValue = (step: number) => {
  // "success", "processing", "error", "default", "warning"
  switch (step) {
    case 1:
      return {
        title: l('global.table.lifecycle.dev'),
        status: 'processing',
        color: 'cyan'
      };
    case 2:
      return {
        title: l('global.table.lifecycle.online'),
        status: 'success',
        color: 'purple'
      };
    default:
      return {
        title: l('global.table.lifecycle.dev'),
        status: 'default',
        color: 'cyan'
      };
  }
};

export const showBadge = (type: string) => {
  if (!type) {
    return false;
  }
  switch (type.toLowerCase()) {
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.PHOENIX:
    case DIALECT.DORIS:
    case DIALECT.HIVE:
    case DIALECT.STARROCKS:
    case DIALECT.PRESTO:
    case DIALECT.FLINK_SQL:
    case DIALECT.FLINKJAR:
      return true;
    default:
      return false;
  }
};

/**
 * build Catalogue tree
 * @param {Catalogue[]} data
 * @param {string} searchValue
 * @param path
 * @param currentUser
 * @param taskOwnerLockingStrategy
 * @param users
 * @returns {any}
 */

export const buildProjectTree = (
  data: Catalogue[] = [],
  searchValue: string = '',
  path: string[] = [],
  currentUser: UserBaseInfo.User,
  taskOwnerLockingStrategy: TaskOwnerLockingStrategy,
  users: UserBaseInfo.User[] = []
): any =>
  data
    ? data.map((item: Catalogue) => {
        const currentPath = path ? [...path, item.name] : [item.name];
        // 构造生命周期的值
        const stepValue = buildStepValue(item.task?.step);
        // 渲染生命周期的 标记点
        const renderPreFixState = item.isLeaf && showBadge(item.type) && (
          <>
            <Badge
              title={stepValue.title}
              color={stepValue.color}
              // status={(stepValue.status as PresetStatusColorType) ?? 'default'}
            />
          </>
        );

        // 总渲染 title
        const renderTitle = (
          <Space align={'baseline'} size={2}>
            {searchTreeNode(item.name, searchValue)}
          </Space>
        );

        const toolTipTitle = item?.isLeaf ? (
          showAllOwners(item?.task?.firstLevelOwner, item?.task?.secondLevelOwners, users)
        ) : (
          <></>
        );

        // 悬浮延迟时间 1.25 秒
        const mouseEnterDelay = 1.25;

        // 渲染后缀图标
        const renderSuffixIcon = (
          <>
            {lockTask(
              item?.task?.firstLevelOwner,
              item?.task?.secondLevelOwners,
              currentUser,
              taskOwnerLockingStrategy
            ) ? (
              <Tooltip
                mouseEnterDelay={mouseEnterDelay}
                placement={'right'}
                title={
                  <p style={{ margin: 0 }}>
                    {l('global.operation.unable')}
                    <Divider style={{ margin: 0 }} type={'horizontal'} />
                    {toolTipTitle}
                  </p>
                }
              >
                <LockTwoTone twoToneColor={'red'} />
              </Tooltip>
            ) : (
              <Tooltip
                mouseEnterDelay={mouseEnterDelay}
                placement={'right'}
                title={
                  <p style={{ margin: 0 }}>
                    {l('global.operation.able')}
                    <Divider style={{ margin: 0 }} type={'horizontal'} />
                    {toolTipTitle}
                  </p>
                }
              >
                <UnlockTwoTone twoToneColor='gray' />
              </Tooltip>
            )}
          </>
        );

        return {
          isLeaf: item.isLeaf,
          name: item.name,
          parentId: item.parentId,
          label: searchTreeNode(item.name, searchValue),
          icon: item.type && item.children.length === 0 && (
            <Space size={'small'}>
              {renderPreFixState}
              {getTabIcon(item.type, 20)}
            </Space>
          ),
          value: item.id,
          path: currentPath,
          type: item.type,
          title: (
            <>
              {item.isLeaf && showBadge(item.type) && <>{'\u00A0'.repeat(2)}</>}
              <Space style={{ marginLeft: item.isLeaf ? 4 : 0 }} align={'baseline'} size={'small'}>
                {renderTitle}
                {item.isLeaf && renderSuffixIcon}
              </Space>
            </>
          ),
          fullInfo: item,
          key: item.id,
          id: item.id,
          taskId: item.taskId,
          children: buildProjectTree(
            item.children,
            searchValue,
            currentPath,
            currentUser,
            taskOwnerLockingStrategy,
            users
          )
        };
      })
    : [];

export const isUDF = (jobType: string): boolean => {
  return assert(jobType, [DIALECT.SCALA, DIALECT.PYTHON_LONG, DIALECT.JAVA], true, 'includes');
};

export const isFlinkJob = (jobType: string): boolean => {
  return assert(jobType, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes');
};
