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

import { getCurrentTab } from '@/pages/DataStudio/function';
import {
  buildProjectTree,
  generateList,
  getLeafKeyList,
  getParentKey
} from '@/pages/DataStudio/LeftContainer/Project/function';
import {
  StateType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC,
  TabsItemType,
  TreeVo
} from '@/pages/DataStudio/model';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { SortAscendingOutlined } from '@ant-design/icons';
import { Key } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import type { MenuProps } from 'antd';
import { Button, Dropdown, Empty, Space, Tree } from 'antd';
import Search from 'antd/es/input/Search';
import { ItemType } from 'rc-menu/es/interface';
import React, { useEffect, useState } from 'react';
import { BtnRoute, useTasksDispatch } from '../../BtnContext';

const { DirectoryTree } = Tree;

/**
 * props
 */
type TreeProps = {
  onNodeClick: (info: any) => void;
  onRightClick: (info: any) => void;
  style?: React.CSSProperties;
  selectKeyChange: (keys: Key[]) => void;
  onExpand: (expandedKeys: Key[]) => void;
};

const JobTree: React.FC<TreeProps & connect> = (props) => {
  const {
    project: { data: projectData, expandKeys, selectKey },
    catalogueSortType: { data: catalogueSortTypeData },
    selectCatalogueSortTypeData: { data: selectCatalogueSortTypeData },
    onNodeClick,
    style,
    height,
    onRightClick,
    selectKeyChange,
    onExpand,
    dispatch,
    taskOwnerLockingStrategy,
    users
  } = props;

  const { initialState, setInitialState } = useModel('@@initialState');
  const [searchValue, setSearchValueValue] = useState('');
  const [sortIconType, setSortIconType] = useState('');
  const [selectedSortValue, setSelectedSortValue] = useState<string[]>([]);
  const [data, setData] = useState<any[]>(
    buildProjectTree(
      projectData,
      searchValue,
      [],
      initialState?.currentUser?.user,
      taskOwnerLockingStrategy,
      users
    )
  );
  const btnDispatch = useTasksDispatch();

  useEffect(() => {
    setData(
      buildProjectTree(
        projectData,
        searchValue,
        [],
        initialState?.currentUser?.user,
        taskOwnerLockingStrategy,
        users
      )
    );
  }, [searchValue, projectData, taskOwnerLockingStrategy]);

  useEffect(() => {
    dispatch({ type: STUDIO_MODEL_ASYNC.queryProject, payload: selectCatalogueSortTypeData });
  }, [selectCatalogueSortTypeData]);

  function renameTrees(trees: TreeVo[]): ItemType[] {
    const menuItems: ItemType[] = [];
    for (let tree of trees) {
      let menuItem = {
        key: tree['value'],
        label: tree['name']
      };
      if (tree['children']) {
        const subTrees = renameTrees(tree['children']);
        menuItem['children'] = subTrees;
      }
      menuItems.push(menuItem);
    }
    return menuItems;
  }

  // set sort default value
  useEffect(() => {
    if (
      selectCatalogueSortTypeData &&
      selectCatalogueSortTypeData.sortValue != '' &&
      selectCatalogueSortTypeData.sortType != ''
    ) {
      const initialSortValue =
        selectCatalogueSortTypeData.sortValue + '_' + selectCatalogueSortTypeData.sortType;
      setSelectedSortValue([initialSortValue]);
    }
  }, []);

  // when sorted, change the icon display
  useEffect(() => {
    if (selectCatalogueSortTypeData.sortValue && selectCatalogueSortTypeData.sortType) {
      setSortIconType('link');
    } else {
      setSortIconType('text');
    }
  }, [selectCatalogueSortTypeData]);

  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const onChangeSearch = (e: any) => {
    let { value } = e.target;
    if (!value) {
      dispatch({
        type: STUDIO_MODEL.updateProjectExpandKey,
        payload: []
      });
      setSearchValueValue(value);
      return;
    }
    value = String(value).trim();
    const expandList: any[] = generateList(data, []);
    let expandedKeys: any = expandList
      .map((item: any) => {
        if (item?.name.indexOf(value) > -1) {
          return getParentKey(item.key, data);
        }
        return null;
      })
      .filter((item: any, i: number, self: any) => item && self.indexOf(item) === i);
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: expandedKeys
    });
    setSearchValueValue(value);
    setAutoExpandParent(true);
  };

  const expandAll = () => {
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: getLeafKeyList(projectData)
    });
  };

  const currentTabName = 'menu.datastudio.project';
  const btnEvent = [...BtnRoute[currentTabName]];
  const positionKey = (panes: TabsItemType[], activeKey: string) => {
    const treeKey = getCurrentTab(panes, activeKey)?.treeKey;
    if (treeKey) {
      const expandList: any[] = generateList(data, []);
      let expandedKeys: any = expandList
        .map((item: any) => {
          if (item?.key === treeKey) {
            return getParentKey(item.key, data);
          }
          return null;
        })
        .filter((item: any, i: number, self: any) => item && self.indexOf(item) === i);
      dispatch({
        type: STUDIO_MODEL.updateProjectExpandKey,
        payload: expandedKeys
      });
      setAutoExpandParent(true);
      selectKeyChange([treeKey]);
    }
  };

  btnEvent[1].onClick = expandAll;

  btnEvent[2].onClick = () =>
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: []
    });
  btnEvent[3].onClick = positionKey;
  btnDispatch({
    type: 'change',
    selectKey: currentTabName,
    payload: btnEvent
  });

  const onClick: MenuProps['onClick'] = (e) => {
    const selectSortValue = e.key;
    const sortField: string = selectSortValue.substring(0, selectSortValue.lastIndexOf('_'));
    const sortType: string = selectSortValue.substring(selectSortValue.lastIndexOf('_') + 1);
    if (
      sortField == selectCatalogueSortTypeData.sortValue &&
      sortType == selectCatalogueSortTypeData.sortType
    ) {
      setSelectedSortValue([]);
      dispatch({
        type: STUDIO_MODEL.saveTaskSortTypeData,
        payload: {
          sortValue: '',
          sortType: ''
        }
      });
    } else {
      setSelectedSortValue([selectSortValue]);
      dispatch({
        type: STUDIO_MODEL.saveTaskSortTypeData,
        payload: {
          sortValue: sortField,
          sortType: sortType
        }
      });
    }
  };

  return (
    <>
      <Space direction='horizontal'>
        <Search
          style={{ margin: '8px 0px' }}
          placeholder={l('global.search.text')}
          onChange={onChangeSearch}
          allowClear={true}
        />
        <Dropdown
          menu={{
            items: renameTrees(catalogueSortTypeData),
            mode: 'horizontal',
            selectable: true,
            onClick: onClick,
            selectedKeys: selectedSortValue
          }}
          placement='bottomLeft'
        >
          <Button icon={<SortAscendingOutlined />} type={sortIconType}></Button>
        </Dropdown>
      </Space>

      {data.length ? (
        <DirectoryTree
          style={{ ...style, height: height - 40 - 16, overflowY: 'auto' }}
          className={'treeList'}
          onSelect={(_, info) => onNodeClick(info)}
          onRightClick={onRightClick}
          expandedKeys={expandKeys}
          expandAction={'doubleClick'}
          selectedKeys={selectKey}
          onExpand={onExpand}
          treeData={data}
          // autoExpandParent={autoExpandParent}
        />
      ) : (
        <Empty
          className={'code-content-empty'}
          description={l('datastudio.project.create.folder.tip')}
        />
      )}
    </>
  );
};

export default connect(
  ({ Studio, SysConfig }: { Studio: StateType; SysConfig: SysConfigStateType }) => ({
    height: Studio.toolContentHeight,
    project: Studio.project,
    taskOwnerLockingStrategy: SysConfig.taskOwnerLockingStrategy,
    users: Studio.users,
    catalogueSortType: Studio.catalogueSortType,
    selectCatalogueSortTypeData: Studio.selectCatalogueSortTypeData
  })
)(JobTree);
