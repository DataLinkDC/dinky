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

import { LeftMenuKey } from '@/pages/DataStudio/data.d';
import { getCurrentTab } from '@/pages/DataStudio/function';
import {
  buildProjectTree,
  generateList,
  getLeafKeyList,
  searchInTree
} from '@/pages/DataStudio/LeftContainer/Project/function';
import {
  StateType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC,
  TabsItemType,
  TabsPageType,
  TreeVo
} from '@/pages/DataStudio/model';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { SortAscendingOutlined } from '@ant-design/icons';
import { Key } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Divider, MenuProps } from 'antd';
import { Button, Dropdown, Empty, Space, Tree } from 'antd';
import type { ButtonType } from 'antd/es/button/buttonHelpers';
import Search from 'antd/es/input/Search';
import { ItemType } from 'rc-menu/es/interface';
import React, { useEffect, useState } from 'react';
import { BtnRoute, useTasksDispatch } from '../../BtnContext';
import { CodeTwoTone } from '@ant-design/icons';
import { DIALECT } from '@/services/constants';

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
    selectCatalogueSortTypeData,
    onNodeClick,
    style,
    height,
    leftContainerWidth,
    onRightClick,
    selectKeyChange,
    onExpand,
    dispatch,
    taskOwnerLockingStrategy,
    users
  } = props;

  const { initialState, setInitialState } = useModel('@@initialState');
  const [searchValue, setSearchValueValue] = useState('');
  const [sortState, setSortState] = useState<{
    sortIconType: ButtonType;
    selectedSortValue: string[];
  }>({
    sortIconType: 'text' as ButtonType,
    selectedSortValue: []
  });

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
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryProject,
      payload: { ...selectCatalogueSortTypeData }
    });
  }, [selectCatalogueSortTypeData]);

  function buildSortTreeOptions(trees: TreeVo[] = []): ItemType[] {
    return trees.map((tree) => {
      return {
        key: tree.value,
        label: tree.name,
        children: tree?.children && buildSortTreeOptions(tree.children)
      };
    });
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
      setSortState((prevState) => ({
        ...prevState,
        selectedSortValue: [initialSortValue]
      }));
    }
  }, []);

  // when sorted, change the icon display
  useEffect(() => {
    if (selectCatalogueSortTypeData?.sortValue && selectCatalogueSortTypeData?.sortType) {
      setSortState((prevState) => ({
        ...prevState,
        sortIconType: 'link'
      }));
    } else {
      setSortState((prevState) => ({
        ...prevState,
        sortIconType: 'text'
      }));
    }
  }, [selectCatalogueSortTypeData]);

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
    const expandedKeys: string[] = searchInTree(
      generateList(data, []),
      data,
      String(value).trim(),
      'contain'
    );
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: expandedKeys
    });
    setSearchValueValue(value);
  };

  const expandAll = () => {
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: getLeafKeyList(projectData)
    });
  };
  const openTerminal = () => {
    dispatch({
      type: STUDIO_MODEL.addTab,
      payload: {
        icon: DIALECT.TERMINAL,
        // id: selectDatabaseId + schemaName + tableName,
        breadcrumbLabel: 'SQL CLI Terminal',
        label: 'SQL CLI Terminal',
        type: TabsPageType.terminal
      }
    });
    dispatch({
      type: STUDIO_MODEL.updateSelectRightKey,
      payload: ''
    });
  };
  const currentTabName = LeftMenuKey.PROJECT_KEY;
  const btnEvent = [...BtnRoute[currentTabName]];
  const positionKey = (panes: TabsItemType[], activeKey: string) => {
    const treeKey = getCurrentTab(panes, activeKey)?.treeKey;
    if (treeKey) {
      const expandedKeys: string[] = searchInTree(generateList(data, []), data, treeKey, 'equal');
      dispatch({
        type: STUDIO_MODEL.updateProjectExpandKey,
        payload: expandedKeys
      });
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
      setSortState((prevState) => ({
        ...prevState,
        selectedSortValue: []
      }));
      dispatch({
        type: STUDIO_MODEL.saveTaskSortTypeData,
        payload: {
          sortValue: '',
          sortType: ''
        }
      });
    } else {
      setSortState((prevState) => ({
        ...prevState,
        selectedSortValue: [selectSortValue]
      }));
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
      <Space direction='horizontal' size={8}>
        <Search
          style={{ margin: '8px 0px', width: leftContainerWidth - 60 }}
          placeholder={l('global.search.text')}
          onChange={onChangeSearch}
          allowClear={true}
        />
        <Dropdown
          menu={{
            items: buildSortTreeOptions(catalogueSortTypeData),
            mode: 'horizontal',
            selectable: true,
            onClick: onClick,
            selectedKeys: sortState.selectedSortValue
          }}
          placement='bottomLeft'
        >
          <Button icon={<SortAscendingOutlined />} type={sortState.sortIconType}></Button>
        </Dropdown>
      </Space>

      <div style={{ padding: '3px', background: '#F5F5F5' }} onClick={() => openTerminal()}>
        <CodeTwoTone /> <a>SQL Cli Terminal</a>
      </div>
      <Divider style={{ margin: 3 }} />

      {data.length ? (
        <DirectoryTree
          style={{ ...style, overflowY: 'auto' }}
          className={'treeList'}
          onSelect={(_, info) => onNodeClick(info)}
          onRightClick={onRightClick}
          expandedKeys={expandKeys}
          expandAction={'doubleClick'}
          selectedKeys={selectKey}
          onExpand={onExpand}
          treeData={data}
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
    leftContainerWidth: Studio.leftContainer.width,
    project: Studio.project,
    taskOwnerLockingStrategy: SysConfig.taskOwnerLockingStrategy,
    users: Studio.users,
    catalogueSortType: Studio.catalogueSortType,
    selectCatalogueSortTypeData: Studio.selectCatalogueSortTypeData
  })
)(JobTree);
