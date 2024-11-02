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
  DataStudioTabsItemType,
  EnvType,
  FooterType,
  JobRunningMsgType,
  MetadataTabsItemType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC,
  TabsItemType,
  TabsPageType,
  TaskDataType
} from '@/pages/DataStudio/model';
import {CONFIG_MODEL_ASYNC} from '@/pages/SettingCenter/GlobalSetting/model';
import {DIALECT} from '@/services/constants';
import {UserBaseInfo} from '@/types/AuthCenter/data.d';
import {Cluster, DataSources} from '@/types/RegCenter/data';
import {TaskOwnerLockingStrategy} from '@/types/SettingCenter/data.d';
import {l} from '@/utils/intl';
import {Dispatch} from '@@/plugin-dva/types';
import {Col, Row} from 'antd';

export const mapDispatchToProps = (dispatch: Dispatch) => ({
  updateToolContentHeight: (key: number) =>
    dispatch({
      type: STUDIO_MODEL.updateToolContentHeight,
      payload: key
    }),
  updateCenterContentHeight: (key: number) =>
    dispatch({
      type: STUDIO_MODEL.updateCenterContentHeight,
      payload: key
    }),
  updateSelectLeftKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectLeftKey,
      payload: key
    }),
  updateLeftWidth: (width: number) =>
    dispatch({
      type: STUDIO_MODEL.updateLeftWidth,
      payload: width
    }),
  updateSelectRightKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectRightKey,
      payload: key
    }),
  updateRightWidth: (width: number) =>
    dispatch({
      type: STUDIO_MODEL.updateRightWidth,
      payload: width
    }),
  updateSelectBottomKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomKey,
      payload: key
    }),
  updateSelectBottomSubKey: (key: string) =>
    dispatch({
      type: STUDIO_MODEL.updateSelectBottomSubKey,
      payload: key
    }),
  updateBottomHeight: (height: number) =>
    dispatch({
      type: STUDIO_MODEL.updateBottomHeight,
      payload: height
    }),
  saveDataBase: (data: DataSources.DataSource[]) =>
    dispatch({
      type: STUDIO_MODEL.saveDataBase,
      payload: data
    }),
  queryDatabaseList: () =>
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryDatabaseList
    }),
  queryTaskData: (payload: any) => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryTaskData,
      ...payload
    });
  },
  queryTaskSortTypeData: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryTaskSortTypeData
    });
  },
  querySessionData: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.querySessionData
    });
  },
  queryEnv: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryEnv
    });
  },
  queryClusterConfigurationData: () => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryClusterConfigurationData
    });
  },
  queryUserData: (params: {}) => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryUserData,
      payload: params
    });
  },

  saveProject: (data: any[]) =>
    dispatch({
      type: STUDIO_MODEL.saveProject,
      payload: data
    }),
  updateBottomConsole: (data: string) =>
    dispatch({
      type: STUDIO_MODEL.updateBottomConsole,
      payload: data
    }),
  saveSession: (data: Cluster.Instance[]) =>
    dispatch({
      type: STUDIO_MODEL.saveSession,
      payload: data
    }),
  saveEnv: (data: EnvType[]) =>
    dispatch({
      type: STUDIO_MODEL.saveEnv,
      payload: data
    }),
  saveTabs: (data: TabsItemType[]) =>
    dispatch({
      type: STUDIO_MODEL.saveTabs,
      payload: data
    }),
  saveClusterConfiguration: (data: Cluster.Config[]) =>
    dispatch({
      type: STUDIO_MODEL.saveClusterConfiguration,
      payload: data
    }),
  updateJobRunningMsg: (data: JobRunningMsgType) =>
    dispatch({
      type: STUDIO_MODEL.updateJobRunningMsg,
      payload: data
    }),
  queryDsConfig: (data: string) =>
    dispatch({
      type: CONFIG_MODEL_ASYNC.queryDsConfig,
      payload: data
    }),
  queryTaskOwnerLockingStrategy: (data: string) =>
    dispatch({
      type: CONFIG_MODEL_ASYNC.queryTaskOwnerLockingStrategy,
      payload: data
    })
});

export function isDataStudioTabsItemType(
  item: DataStudioTabsItemType | MetadataTabsItemType | TabsItemType | undefined
): item is DataStudioTabsItemType {
  return item?.type === TabsPageType.project;
}

export function isMetadataTabsItemType(
  item: DataStudioTabsItemType | MetadataTabsItemType | TabsItemType | undefined
): item is MetadataTabsItemType {
  return item?.type === TabsPageType.metadata;
}

export function getCurrentTab(
  panes: TabsItemType[],
  activeKey: string
): DataStudioTabsItemType | MetadataTabsItemType | undefined {
  const item = panes.find((item) => item.key === activeKey);
  switch (item?.type) {
    case 'project':
      return item as DataStudioTabsItemType;
    case 'metadata':
      return item as MetadataTabsItemType;
    default:
      return undefined;
  }
}

export function isProjectTabs(panes: TabsItemType[], activeKey: string): boolean {
  const item = panes.find((item) => item.key === activeKey);
  switch (item?.type) {
    case 'project':
      return true;
    default:
      return false;
  }
}

export function isShowRightTabsJobConfig(dialect: string): boolean {
  return assert(
    dialect,
    [DIALECT.JAVA, DIALECT.PYTHON_LONG, DIALECT.SCALA, DIALECT.FLINKSQLENV],
    true,
    'includes'
  );
}

export function getTabByTaskId(
  panes: TabsItemType[],
  id: number
): DataStudioTabsItemType | MetadataTabsItemType | undefined {
  const item = panes.find((item) => item.treeKey === id);
  switch (item?.type) {
    case 'project':
      return item as DataStudioTabsItemType;
    case 'metadata':
      return item as MetadataTabsItemType;
    default:
      return undefined;
  }
}

export const getCurrentData = (
  panes: TabsItemType[],
  activeKey: string
): TaskDataType | undefined => {
  const item = getCurrentTab(panes, activeKey);
  return isDataStudioTabsItemType(item) ? item.params.taskData : undefined;
};

export const getFooterValue = (panes: any, activeKey: string): Partial<FooterType> => {
  const currentTab = getCurrentTab(panes, activeKey);
  return isDataStudioTabsItemType(currentTab)
    ? {
      codePosition: [1, 1],
      codeType: currentTab.subType
    }
    : {};
};

export const getUserName = (id: Number, users: UserBaseInfo.User[] = []) => {
  let name = '';
  const user = users.find((user: UserBaseInfo.User) => user.id === id);
  if (user && user.username) {
    name = user.username;
  }
  return name;
};

/**
 * 构建责任人
 * @param id
 * @param users
 */
export const showFirstLevelOwner = (id: number, users: UserBaseInfo.User[] = []) => {
  return getUserName(id, users);
};

/**
 * 构建维护人
 * @param ids
 * @param users
 */
export const showSecondLevelOwners = (ids: number[], users: UserBaseInfo.User[] = []) => {
  return ids
    ?.map((id: Number) => {
      return getUserName(id, users);
    })
    ?.join();
};

/**
 * 构建所有责任人 用于悬浮提示
 * @param id
 * @param ids
 * @param users
 */
export const showAllOwners = (id: number, ids: number[], users: UserBaseInfo.User[] = []) => {
  const firstLevelOwnerLabel = l('pages.datastudio.label.jobInfo.firstLevelOwner');
  const secondLevelOwnersLabel = l('pages.datastudio.label.jobInfo.secondLevelOwners');
  const firstLevelOwner = showFirstLevelOwner(id, users);
  const secondLevelOwners = showSecondLevelOwners(ids, users);
  return (
    <Row>
      {/*理论上责任人必填, 无需判断*/}
      <Col span={24}>
        {firstLevelOwnerLabel}：{firstLevelOwner}
      </Col>
      {
        <Col span={24}>
          {secondLevelOwnersLabel}：
          {secondLevelOwners && secondLevelOwners.length > 0 ? secondLevelOwners : '-'}
        </Col>
      }
    </Row>
  );
};

export const lockTask = (
  firstLevelOwner: number,
  secondLevelOwners: number[] = [],
  currentUser: UserBaseInfo.User,
  taskOwnerLockingStrategy: TaskOwnerLockingStrategy
) => {
  if (currentUser?.superAdminFlag) {
    return false;
  }
  const isOwner = currentUser?.id == firstLevelOwner;
  switch (taskOwnerLockingStrategy) {
    case TaskOwnerLockingStrategy.OWNER:
      return !isOwner;
    case TaskOwnerLockingStrategy.OWNER_AND_MAINTAINER:
      return !isOwner && !secondLevelOwners?.includes(currentUser?.id);
    case TaskOwnerLockingStrategy.ALL:
      return false;
    default:
      return false;
  }
};

/**
 * 断言 断言类型值是否在断言类型值列表中 | assert whether the assertion type value is in the assertion type value list
 * @param needAssertTypeValue
 * @param assertTypeValueList
 * @param needAssertTypeValueLowerCase
 * @param assertType
 */
export const assert = (
  needAssertTypeValue: string = '',
  assertTypeValueList: string[] | string = [],
  needAssertTypeValueLowerCase = false,
  assertType: 'notIncludes' | 'includes' | 'notEqual' | 'equal' = 'includes'
): boolean => {
  // 如果 needAssertTypeValue 为空, 则直接返回 false 不需要断言 | if needAssertTypeValue is empty, return false directly
  if (isEmpty(needAssertTypeValue)) {
    return false;
  }
  // 判断 assertTypeValueList 是字符串还是数组 | judge whether assertTypeValueList is a string or an array
  if (!Array.isArray(assertTypeValueList)) {
    assertTypeValueList = [assertTypeValueList];
  }
  // 如果是 assertType 是 notEqual 或 equal, 则 assertTypeValueList 只能有一个值 | if assertType is notEqual or equal, assertTypeValueList can only have one value
  if (assertType === 'notEqual' || assertType === 'equal') {
    assertTypeValueList = assertTypeValueList.slice(0, 1);
  }
  // 判断需要断言的值是否需要转小写 | determine whether the value to be asserted needs to be converted to lowercase
  if (needAssertTypeValueLowerCase) {
    needAssertTypeValue = needAssertTypeValue.toLowerCase();
    assertTypeValueList = assertTypeValueList.map((item) => item.toLowerCase());
  }
  if (assertType === 'notIncludes') {
    return !assertTypeValueList.includes(needAssertTypeValue);
  }
  if (assertType === 'includes') {
    return assertTypeValueList.includes(needAssertTypeValue);
  }
  if (assertType === 'notEqual') {
    return assertTypeValueList.every((item) => item !== needAssertTypeValue);
  }
  if (assertType === 'equal') {
    return assertTypeValueList.every((item) => item === needAssertTypeValue);
  }
  return false;
};

/**
 * 判断 不为空或者不为 undefined | determine whether it is not empty or not undefined
 * @param value
 */
export const isNotEmpty = (value: any): boolean => {
  return value !== '' && value !== undefined && value !== null;
};

/**
 * 判断为空或者为 undefined | determine whether it is empty or undefined
 * @param value
 */
export const isEmpty = (value: any): boolean => {
  return !isNotEmpty(value);
};

/**
 * 转换多表的SelectResult
 * @param data
 */
export const convertMockResultToList = (data: any): any [] => {
  const rowDataResults: any[] = [];
  // 对于每个MockResult的Column，一个元素代表一个表信息
  data.columns.forEach((columnString: string) => {
    // 当前表的column信息
    let columnArr: string[] = [];
    // 当前表的row data信息
    const rowDataArr: string[] = [];
    // 表名
    let tableName: string = '';
    //解析当前表单信息
    const columnJsonInfo = JSON.parse(columnString);
    // 提取column信息
    if (columnJsonInfo['dinkySinkResultColumnIdentifier']) {
      columnArr = columnJsonInfo['dinkySinkResultColumnIdentifier']
    }
    // 提取表名
    if (columnJsonInfo['dinkySinkResultTableIdentifier']) {
      tableName = columnJsonInfo['dinkySinkResultTableIdentifier'];
    }
    // 遍历column信息
    data.rowData.forEach((rowDataElement: any) => {
      if (rowDataElement.dinkySinkResultTableIdentifier == tableName) {
        rowDataArr.push(rowDataElement);
      }
    })
    // 构建constant对象
    const rowDataResult = {
      'tableName': tableName, columns: columnArr, rowData: rowDataArr
    };
    rowDataResults.push(rowDataResult);
  });

  console.log(rowDataResults)
  return rowDataResults;
};
