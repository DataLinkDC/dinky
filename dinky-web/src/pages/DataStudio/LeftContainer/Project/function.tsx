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

import { getTabIcon } from '@/pages/DataStudio/MiddleContainer/function';
import { DIALECT } from '@/services/constants';
import { Catalogue } from '@/types/Studio/data.d';
import { searchTreeNode } from '@/utils/function';
import { l } from '@/utils/intl';
import { Badge, Space } from 'antd';
import { PresetStatusColorType } from 'antd/es/_util/colors';
import { Key } from 'react';

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
/**
 * build Catalogue tree
 * @param {Catalogue[]} data
 * @param {string} searchValue
 * @param path
 * @returns {any}
 */

export const buildProjectTree = (
  data: Catalogue[],
  searchValue: string = '',
  path: string[] = []
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
              status={(stepValue.status as PresetStatusColorType) ?? 'default'}
            />
          </>
        );

        // 总渲染 title
        const renderTitle = (
          <>
            <Space align={'baseline'} size={2}>
              {searchTreeNode(item.name, searchValue)}
            </Space>
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
              {item.isLeaf && showBadge(item.type) && <>{'\u00A0'.repeat(2)}</>} {renderTitle}
            </>
          ),
          fullInfo: item,
          key: item.id,
          id: item.id,
          taskId: item.taskId,
          children: buildProjectTree(item.children, searchValue, currentPath)
        };
      })
    : [];

export const isUDF = (jobType: string): boolean => {
  return jobType === 'Scala' || jobType === 'Python' || jobType === 'Java';
};

export const buildStepValue = (step: number) => {
  // "success", "processing", "error", "default", "warning"
  // todo: 生命周期正在重构 后续在优化
  switch (step) {
    case 0:
      return {
        title: l('global.table.lifecycle.unknown'),
        status: 'default',
        color: '#b0aeae'
      };
    case 1:
      return {
        title: l('global.table.lifecycle.dev'),
        status: 'processing',
        color: '#1890ff'
      };
    case 2:
      return {
        title: l('global.table.lifecycle.online'),
        status: 'success',
        color: '#52c41a'
      };
    case 3:
      return {
        title: l('global.table.lifecycle.stopped'),
        status: 'error',
        color: '#f5222d'
      };
    case 4:
      return {
        title: l('global.table.lifecycle.offline'),
        status: 'warning',
        color: '#faad14'
      };
    default:
      return {
        title: l('global.table.lifecycle.dev'),
        status: 'default',
        color: '#1890ff'
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
